package ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.viewport.Viewport;
import ecs.components.SceneComponent;

import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import ecs.World;
import utils.ModelUtils;

public class RenderSystem extends IteratingSystem {
    private ComponentMapper<SceneComponent> pm = ComponentMapper.getFor(SceneComponent.class);
    BoundingBox boundingBox = new BoundingBox();
    Scene boundingBoxScene =new Scene(new Model());
    World world;
    SceneManager sceneManager;
    private static Vector2 vec = new Vector2();
    public Viewport viewport;

    public RenderSystem() {
        super(Family.one(SceneComponent.class).get());



    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        sys.Log.info("RenderSystem","RenderSystem added to engine");
        //sceneManager = world.getSceneManager();

    }

    public void addScene(Scene scene){
        sceneManager.addScene(scene);
    }

    public void removeScene(Scene scene){
        sceneManager.removeScene(scene);
    }


    public void setWorld(World world){
        this.world = world;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        /*sceneManager.update(deltaTime);
        sceneManager.render();*/

        removeScene(boundingBoxScene);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SceneComponent sceneComponent = pm.get(entity);

        if(!sceneManager.getRenderableProviders().contains(sceneComponent.gltfScene,false)){
            sceneManager.addScene(sceneComponent.gltfScene);
            // sceneManager.addScene(sceneComponent.boundingBoxScene);
            sys.Log.info("RenderSystem", "New scene identified but not present in scene manager...adding to scene manager");
        }


        if (!sceneComponent.selected){

            return;
        } else {
            sceneComponent.gltfScene.modelInstance.calculateBoundingBox(boundingBox);

            ModelInstance modelInstance = ModelUtils.createBoundingBoxRenderable(boundingBox);
            boundingBoxScene = new Scene(modelInstance);

            addScene(boundingBoxScene);
            modelInstance.transform=sceneComponent.gltfScene.modelInstance.transform;

        }
    }


}

