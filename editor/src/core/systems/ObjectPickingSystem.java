package core.systems;

import backend.tools.Log;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import core.components.light.LightComponent;
import core.components.light.PointLightComponent;
import core.components.light.SpotLightComponent;
import editor.Context;
import core.components.SceneComponent;
import editor.graphics.scene.GameObject;
import ui.UserInterface;
import ui.widgets.RenderWidget;

public class ObjectPickingSystem extends IteratingSystem

{
    Ray ray;
    ComponentMapper<SceneComponent> pm = ComponentMapper.getFor(SceneComponent.class);
    ComponentMapper<LightComponent> lm = ComponentMapper.getFor(LightComponent.class);
    Vector3 intersection = new Vector3();
    GameObject selection;
    GameObject lastSelection;
    GameObject hoveredComponent;
    Context context;


    public ObjectPickingSystem() {
        super(Family.one(SceneComponent.class, LightComponent.class).get());
    }

    public void setContext(Context context){
        this.context = context;
    }

    @Override
    public void addedToEngine(Engine engine) {
        Log.info("ObjectPickingSystem","ObjectPickingSystem added to engine");
        super.addedToEngine(engine);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)){
            SceneSystem.selectedSceneComponent = null;
            GizmoSystem.transformToolEnabled = false;
            GizmoSystem.scaleToolEnabled = false;
            if (context.gizmoSystem.translateTool.getSelectedComponent()!=null){
            context.gizmoSystem.translateTool.disable();
            context.gizmoSystem.translateTool.update();
            context.gizmoSystem.translateTool.enabled = false;
            }
            if (context.gizmoSystem.scaleTool.getSelectedComponent()!=null){
            context.gizmoSystem.scaleTool.disable();
            context.gizmoSystem.scaleTool.update();}
            context.gizmoSystem.scaleTool.enabled = false;
            }

        }



    @Override
    protected void processEntity(Entity entity , float deltaTime) {
        SceneComponent sceneComponent = pm.get(entity);
        LightComponent lightComponent = lm.get(entity);
        ray = RenderWidget.viewport.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        if (lightComponent instanceof PointLightComponent)
        {
         if (Intersector.intersectRayBounds(ray, ( (PointLightComponent) lightComponent ).boundingBox, intersection))
         {

                selection = lightComponent;
                Log.info("ObjectPickingSystem", "PointLightComponent selected: " + selection);


         }
        }

        if (lightComponent instanceof SpotLightComponent)
        {
            if (Intersector.intersectRayBounds(ray, ( (SpotLightComponent) lightComponent ).boundingBox, intersection))
            {
                if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT))
                {
                        selection = lightComponent;
                        Log.info("ObjectPickingSystem","SpotLightComponent selected: "+selection);
                }
            }

        }

        BoundingBox boundingBox = new BoundingBox();
        sceneComponent.hovered = false;
        Vector3 position = context.camera.position;
        if (sceneComponent.boundingBox.contains(position)) {
            return;
        }
        if (Intersector.intersectRayBoundsFast(ray, sceneComponent.boundingBox)) {
            sceneComponent.hovered = true;
            hoveredComponent = sceneComponent;
            //if camera position is within bounding box, don't select

        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)&&sceneComponent.hovered) {
            lastSelection = selection;
            selection = sceneComponent;
            if (lastSelection==selection){
                return;
            }
            updateSelection(sceneComponent);
        }

    }

    public GameObject getHoveredComponent(){
        return selection;
    }

    public void updateSelection(SceneComponent sceneComponent){
        selection = sceneComponent;
        SceneSystem.selectedSceneComponent = (SceneComponent) selection;
        UserInterface.getInstance().componentInspector.populateTables(sceneComponent);
        UserInterface.getInstance().componentInspector.pack();

        Log.info("ObjectPickingSystem" , "Selected component: " + sceneComponent.id);
    }

}
