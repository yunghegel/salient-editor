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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import core.components.light.LightComponent;
import core.components.light.PointLightComponent;
import core.components.light.SpotLightComponent;
import editor.Context;
import core.components.SceneComponent;
import editor.graphics.scene.GameObject;
import editor.graphics.scene.MeshInfo;
import ui.UserInterface;
import ui.widgets.RenderWidget;

public class ObjectPickingSystem extends IteratingSystem

{
    Ray ray;
    ComponentMapper<SceneComponent> pm = ComponentMapper.getFor(SceneComponent.class);
    ComponentMapper<LightComponent> lm = ComponentMapper.getFor(LightComponent.class);
    public Vector3 intersection = new Vector3();
    GameObject selection;
    GameObject lastSelection;
    GameObject hoveredComponent;
    Context context;
    public float dst = 0;


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

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            deselectObject();
        }
        }



    @Override
    protected void processEntity(Entity entity , float deltaTime) {
        SceneComponent sceneComponent = pm.get(entity);
        LightComponent lightComponent = lm.get(entity);
        dst=0;
        intersection.set(0,0,0);
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
        sceneComponent.hovered = false;
        Vector3 position = context.camera.position;
        if (sceneComponent.boundingBox.contains(position)) {
            return;
        }

        if (!sceneComponent.pickable) return;

        if (Intersector.intersectRayBoundsFast(ray, sceneComponent.boundingBox)) {
            sceneComponent.hovered = true;
            hoveredComponent = sceneComponent;
            //if camera position is within bounding box, don't select

        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)){
            if (selection != null) {
                deselectObject();
                if (GizmoSystem.rotateToolEnabled) {
                    context.gizmoSystem.rotateTool.disable();
                    GizmoSystem.rotateToolEnabled = false;
                }
                if (GizmoSystem.scaleToolEnabled) {
                    context.gizmoSystem.scaleTool.disable();
                    GizmoSystem.scaleToolEnabled = false;
                }
                if (GizmoSystem.translateToolEnabled) {
                    context.gizmoSystem.translateTool.disable();
                    GizmoSystem.translateToolEnabled = false;
                }
            }
        }

        if (!sceneComponent.hovered) return;

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
//            lastSelection = selection;
//            selection = sceneComponent;
//            setSelectedObject(sceneComponent);
            for (MeshInfo meshInfo : sceneComponent.meshInfos) {
               if (pickFromMeshTriangles(meshInfo,intersection,dst)) {
                   lastSelection = selection;
                   selection = sceneComponent;
                   setSelectedObject(sceneComponent);
                   Log.info("ObjectPickingSystem","MeshInfo selected: "+selection);
                   break;
               }
            }


        }


    }

    public GameObject getHoveredComponent(){
        return selection;
    }

    public void setSelectedObject(SceneComponent sceneComponent){
        sceneComponent.selected = true;
        selection = sceneComponent;
        SceneSystem.selectedSceneComponent = (SceneComponent) selection;
        context.gizmoSystem.translateTool.setSelectedComponent((SceneComponent) selection);
        UserInterface.getInstance().componentInspector.setSelectedComponent((SceneComponent) selection);
        UserInterface.getInstance().componentInspector.pack();

        Log.info("ObjectPickingSystem" , "Selected component: " + sceneComponent.id);
    }

    public void deselectObject(){
        SceneSystem.selectedSceneComponent = null;
        GizmoSystem.translateToolEnabled = false;
        GizmoSystem.scaleToolEnabled = false;
        if (context.gizmoSystem.translateTool.getSelectedComponent()!=null){
            context.gizmoSystem.translateTool.disable();
            context.gizmoSystem.translateTool.update();
            context.gizmoSystem.translateTool.enabled = false;
        }
        if (context.gizmoSystem.scaleTool.getSelectedComponent()!=null){
            context.gizmoSystem.scaleTool.disable();
            context.gizmoSystem.scaleTool.update();
            context.gizmoSystem.scaleTool.enabled = false;
        }
        if (context.gizmoSystem.rotateTool.getSelectedComponent()!=null){
            context.gizmoSystem.rotateTool.disable();
            context.gizmoSystem.rotateTool.update();
            context.gizmoSystem.rotateTool.enabled = false;
        }

        context.gizmoSystem.scaleTool.enabled = false;
        UserInterface.getInstance().componentInspector.deselectComponent();
    }

    public boolean pickFromMeshTriangles(MeshInfo meshInfo,Vector3 intersectionOut,float dstOut) {
        ray = RenderWidget.viewport.getPickRay(Gdx.input.getX() , Gdx.input.getY());
        Vector3 intersection = new Vector3();
        if (Intersector.intersectRayTriangles(ray , meshInfo.vertices , meshInfo.indices , meshInfo.vertexSize , intersection)) {
            dstOut = ray.origin.dst(intersection);
            intersectionOut.set(intersection);
            return true;
        }
        return false;
    }

    public boolean pickMeshFromTriangles(MeshInfo meshInfo, Vector3 intersectionOut, float dstOut, Matrix4 transform) {
        ray = RenderWidget.viewport.getPickRay(Gdx.input.getX() , Gdx.input.getY());
        Vector3 intersection = new Vector3();
        float[] vertices = new float[meshInfo.vertices.length];
        vertices= meshInfo.multiplyMatrix(transform);


        if (Intersector.intersectRayTriangles(ray , vertices , meshInfo.indices , meshInfo.vertexSize , intersection)) {
            dstOut = ray.origin.dst(intersection);
            intersectionOut.set(intersection);
            return true;
        }
        return false;

    }}


