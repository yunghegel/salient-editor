package ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.scenes.scene2d.Stage;
import ecs.components.BulletComponent;
import ecs.components.PickableComponent;
import ecs.entities.EntityFactory;
import ecs.systems.LightingSystem;
import ecs.systems.ObjectPickingSystem;
import ecs.systems.PhysicsSystem;
import ecs.systems.RenderSystem;
import net.mgsx.gltf.scene3d.scene.SceneManager;

import ui.EditorStage;

public class World {

    Engine engine;
    RenderSystem renderSystem;
    PhysicsSystem bulletPhysicsSystem;
    ObjectPickingSystem objectPickingSystem;
    LightingSystem lightsSystem;



    SceneManager sceneManager;

    Stage stage;
    EditorStage editorStage;
    PerspectiveCamera camera;


    public Engine getEngine(){
        return engine;
    }

    public PhysicsSystem getPhysics(){
        return bulletPhysicsSystem;
    }

    public World(SceneManager sceneManager,  Stage stage, PerspectiveCamera camera, FirstPersonCameraController cameraController){
        this.sceneManager = sceneManager;

        this.stage = stage;
        this.camera = camera;
        lightsSystem = new LightingSystem();
        engine = new Engine();
        renderSystem = new RenderSystem();
        bulletPhysicsSystem = new PhysicsSystem();
        objectPickingSystem = new ObjectPickingSystem();


        lightsSystem.setWorld(this);
        renderSystem.setWorld(this);
        objectPickingSystem.setWorld(this);
        bulletPhysicsSystem.setWorld(this);
        objectPickingSystem.setWorld(this);

        initSystems();

        initEntities();

    }

    public void initSystems(){
        engine.addSystem(renderSystem);
        engine.addSystem(bulletPhysicsSystem);
        engine.addSystem(objectPickingSystem);
        engine.addSystem(lightsSystem);
    }

    public void initEntities(){
        Entity crate = EntityFactory.createSceneComponent("models/RiggedFigure.gltf","Rigged test model");
        crate.add(new BulletComponent(false));
        crate.add(new PickableComponent());

        engine.addEntity(crate);

        Entity world = EntityFactory.createSceneComponent("models/test_map/test_map.gltf","test_map");
        world.add(new BulletComponent(true));
        engine.addEntity(world);

    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public Stage getStage() {
        return stage;
    }

    public EditorStage getEditorStage() {
        return editorStage;
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }
}
