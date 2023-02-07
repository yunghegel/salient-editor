package core.systems;

import backend.data.ObjectRegistry;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import editor.Context;
import core.components.SceneComponent;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import backend.tools.Log;
import ui.elements.TransformWindow;
import ui.widgets.RenderWidget;
import utils.ModelUtils;

public class SceneSystem extends IteratingSystem
{

    public static core.components.SceneComponent selectedSceneComponent;
    public static Ray ray = new Ray();
    static Array<Renderable> renderables = new Array<Renderable>();
    static ShapeRenderer shapeRenderer = new ShapeRenderer();
    ComponentMapper<core.components.SceneComponent> pm = ComponentMapper.getFor(core.components.SceneComponent.class);
    Array<core.components.SceneComponent> scenes = new Array<core.components.SceneComponent>();
    BoundingBox boundingBox = new BoundingBox();
    Scene boundingBoxScene = new Scene(new Model());
    Scene rayScene = new Scene(new Model());
    ModelBatch modelBatch = new ModelBatch();
    BoundingBox selectedBoundingBox = new BoundingBox();
    private Context context;
    private PerspectiveCamera camera;
    private SceneManager sceneManager;

    public SceneSystem() {

        super(Family.one(core.components.SceneComponent.class).get());
    }

    public static void addRenderable(Renderable renderable) {
        renderables.add(renderable);
    }

    public static void removeRenderable(Renderable renderable) {
        renderables.removeValue(renderable , true);
    }

    public void setContext(Context context) {
        this.context = context;
        this.camera = context.getCamera();
        this.sceneManager = context.getSceneManager();
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        Log.info("SceneSystem" , "SceneSystem added to engine");

    }

    @Override
    public void update(float deltaTime) {

        modelBatch.begin(camera);
        super.update(deltaTime);
        modelBatch.end();



    }

    @Override
    protected void processEntity(com.badlogic.ashley.core.Entity entity , float deltaTime) {
        core.components.SceneComponent sceneComponent = pm.get(entity);
        sceneComponent.update();
        if (!sceneManager.getRenderableProviders().contains(sceneComponent.scene , false)&&!sceneComponent.initialized) {
            sceneManager.addScene(sceneComponent.scene);
            sceneComponent.initialized = true;
            // sceneManager.addScene(sceneComponent.boundingBoxScene);
            Log.info("RenderSystem" , "New scene identified but not present in scene manager...adding to scene manager");
        }

      //  selectedBoundingBox = sceneComponent.boundingBox;

        if (sceneComponent != selectedSceneComponent) {
            sceneComponent.selected = false;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {

        //    sceneManager.getRenderableProviders().removeValue(sceneComponent.boundingBoxScene , false);

//            sceneComponent.selected = processRayCast(sceneComponent , sceneComponent.boundingBox);
            if (sceneComponent.hovered) {

                selectedSceneComponent = sceneComponent;
                ui.scene.ObjectTree.setWindowTransformValues(selectedSceneComponent);
            }



        }

    }

    public boolean processRayCast(core.components.SceneComponent sceneComponent , BoundingBox bbox) {
        sceneManager.removeScene(rayScene);
        ray = RenderWidget.viewport.getPickRay(Gdx.input.getX() , Gdx.input.getY());
        ModelInstance rayInstance = ModelUtils.createRayModelInstance(ray.origin , ray.direction , 100 , Color.MAGENTA);
        rayScene = new Scene(rayInstance);
        sceneManager.addScene(rayScene);
        sceneComponent.update();
        sceneComponent.selected = false;

        if (sceneComponent == selectedSceneComponent) {
            return true;
        }

        sceneComponent.boundingBox = sceneComponent.scene.modelInstance.calculateBoundingBox(boundingBox);
        boundingBox.set(sceneComponent.boundingBox);
        if (!sceneComponent.pickable) {
            return false;
        }
        sceneComponent.boundingBox.mul(sceneComponent.scene.modelInstance.transform);
        if (Intersector.intersectRayBounds(ray , sceneComponent.boundingBox , new Vector3())) {
            selectedSceneComponent = sceneComponent;
            sceneComponent.selected = true;
            TransformWindow.component = sceneComponent;

            Log.info("SceneSystem" , "Selected scene component: " + selectedSceneComponent.id);

            return true;
        }

        return false;

    }

    public void doBatchComponentProcces() {
        Array<core.components.SceneComponent> sceneComponents = ObjectRegistry.getSceneComponents();
        for (core.components.SceneComponent sceneComponent : sceneComponents) {

            if (!sceneComponent.selected) {
                if (sceneManager.getRenderableProviders().contains(sceneComponent.boundingBoxScene , false)) {
                    sceneManager.getRenderableProviders().removeValue(sceneComponent.boundingBoxScene , false);
                    Log.info("RenderSystem" , "BoundingBoxScene present but component not selected...removing from scene manager");
                }
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                selectedSceneComponent = null;
                sceneComponent.selected = false;
                sceneManager.removeScene(sceneComponent.boundingBoxScene);
                Log.info("RenderSystem" , "Escape key pressed...deselecting component with id: " + sceneComponent.id);
            }

        }
    }

    public void setAllScenesUnselected() {
        Array<core.components.SceneComponent> sceneComponents = ObjectRegistry.getSceneComponents();
        for (SceneComponent sceneComponent : sceneComponents) {
            sceneComponent.update();
            sceneComponent.selected = false;
            sceneManager.removeScene(sceneComponent.boundingBoxScene);
        }
    }

    public void renderShapes() {
        if (selectedSceneComponent != null&&selectedSceneComponent.drawBounds) {

            shapeRenderer.setProjectionMatrix(sceneManager.camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.box(selectedSceneComponent.boundingBox.min.x , selectedSceneComponent.boundingBox.min.y , selectedSceneComponent.boundingBox.min.z + selectedSceneComponent.boundingBox.getDepth() , selectedSceneComponent.boundingBox.getWidth() , selectedSceneComponent.boundingBox.getHeight() , selectedSceneComponent.boundingBox.getDepth());
            shapeRenderer.end();
        }
        context.gizmoSystem.drawSelectedArrow();
    }

    public void dispose() {
        modelBatch.dispose();
        shapeRenderer.dispose();
    }

}