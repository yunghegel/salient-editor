package core.entities;

import backend.EditorIO;
import backend.tools.Log;
import backend.data.ObjectRegistry;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.Vector3;
import editor.Context;
import core.components.PlayerComponent;
import core.components.SceneComponent;
import input.ThirdPersonPlayerController;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import util.MiscUtils;

public class SceneEntityFactory
{

    private static SceneEntityFactory instance = new SceneEntityFactory();
    Engine engine;

    private SceneEntityFactory() {

    }

    public static SceneEntityFactory getInstance() {
        return instance;
    }

    public static Entity createSceneEntity(String path , String id) {
        SceneAsset sceneAsset = EditorIO.io().loadGLTF(path , id);

        core.components.SceneComponent sceneComponent = new core.components.SceneComponent(id , path);
        sceneComponent.create(sceneAsset);
        sceneComponent.update();
        sceneComponent.populateArrays();

        core.components.TransformComponent transformComponent = new core.components.TransformComponent();
        transformComponent.transform = sceneComponent.model.transform;

        Entity entity = new Entity();
        entity.add(sceneComponent);
        entity.add(transformComponent);

        ObjectRegistry.addSceneComponent(sceneComponent);
        Log.info("EntityFactory" , "Created SceneEntity from filepath: " + path + " with ID " + id);
        return entity;
    }

    public static Entity createSceneEntity(SceneAsset sceneAsset,String path,String id){

        Entity entity = new Entity();
        core.components.SceneComponent sceneComponent = new core.components.SceneComponent(id , path);
        sceneComponent.create(sceneAsset);
        sceneComponent.update();
        sceneComponent.populateArrays();
        sceneComponent.scene.modelInstance.transform.setToTranslation((float) Math.random() * 10 - 5, 0, (float) Math.random() * 10 - 5);

        core.components.TransformComponent transformComponent = new core.components.TransformComponent();
        transformComponent.transform = sceneComponent.model.transform;
        entity.add(sceneComponent);
        entity.add(transformComponent);
        ObjectRegistry.addSceneComponent(sceneComponent);
        Log.info("EntityFactory" , "Created SceneEntity from SceneAsset with ID " + id);
        return entity;
    }

    public static Entity createSceneEntity(ModelInstance model , String id) {

        core.components.SceneComponent sceneComponent = new core.components.SceneComponent(id);
        sceneComponent.create(model);
        sceneComponent.update();
        sceneComponent.populateArrays();


        core.components.TransformComponent transformComponent = new core.components.TransformComponent();
        transformComponent.transform = sceneComponent.model.transform;

        Entity entity = new Entity();
        entity.add(sceneComponent);
        entity.add(transformComponent);
        sceneComponent.translate(MiscUtils.getRandomVector3(-20f , 20f));
        sceneComponent.rotate(MiscUtils.getRandomVector3(-20f , 20f));
        Log.info("EntityFactory" , "Created SceneEntity from ModelInstance with ID " + id);
        ObjectRegistry.addSceneComponent(sceneComponent);
        return entity;
    }

    public static Entity createPlayerEntity(String path) {
        Entity entity = new Entity();

        SceneAsset sceneAsset = EditorIO.io().loadGLTF(path , "player");
        core.components.SceneComponent sceneComponent = new SceneComponent("player" , path);
        sceneComponent.create(sceneAsset);
        sceneComponent.update();
        sceneComponent.populateArrays();

        PlayerComponent playerComponent = new PlayerComponent();
        playerComponent.setPlayerScene(sceneComponent.scene);
        playerComponent.setState(ThirdPersonPlayerController.AnimState.STAND);

        entity.add(sceneComponent);
        entity.add(playerComponent);

        Log.info("EntityFactory" , "Created PlayerEntity from filepath: " + path);
        ObjectRegistry.addSceneComponent(sceneComponent);

        return entity;
    }

    public static Entity createPlayerEntity(SceneAsset sceneAsset,String path){
        Entity entity = new Entity();
        core.components.SceneComponent sceneComponent = new SceneComponent("player" , path);
        sceneComponent.create(sceneAsset);
        sceneComponent.update();
        sceneComponent.populateArrays();
        PlayerComponent playerComponent = new PlayerComponent();
        playerComponent.setPlayerScene(sceneComponent.scene);
        playerComponent.setState(ThirdPersonPlayerController.AnimState.STAND);
        entity.add(sceneComponent);
        entity.add(playerComponent);
        Log.info("EntityFactory" , "Created PlayerEntity from SceneAsset");
        ObjectRegistry.addSceneComponent(sceneComponent);
        return entity;
    }

    public static Entity create3DInputProcessingEntity() {
        Entity entity = new Entity();
        PerspectiveCamera perspectiveCamera = new PerspectiveCamera(67 , Gdx.graphics.getWidth() , Gdx.graphics.getHeight());

        perspectiveCamera.position.set(0 , 10 , 0);
        perspectiveCamera.lookAt(0 , 10 , 0);
        perspectiveCamera.near = 1f;
        perspectiveCamera.far = 500f;
        perspectiveCamera.update();

        FirstPersonCameraController cameraController = new FirstPersonCameraController(perspectiveCamera)
        {
            @Override
            public boolean touchDragged(int screenX , int screenY , int pointer) {
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                    //drag pan camera
                    float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
                    float deltaY = -Gdx.input.getDeltaY() * degreesPerPixel;

                    perspectiveCamera.position.add(0 , 0 , deltaY).dot(Vector3.Y);
                    perspectiveCamera.position.add(-deltaX , 0 , 0).dot(Vector3.X);

                    return true;
                }

                if (Gdx.input.isButtonPressed(1)) {
                    return super.touchDragged(screenX , screenY , pointer);

                }
                return false;
            }

            @Override
            public boolean scrolled(float amountX , float amountY) {
                //move camera forward and backward with intensity varying with distance from camera to target
                float intensity = 0.1f * ( perspectiveCamera.position.dst(perspectiveCamera.direction) );
                perspectiveCamera.translate(perspectiveCamera.direction.x * amountY * intensity , perspectiveCamera.direction.y * amountY * intensity , perspectiveCamera.direction.z * amountY * intensity);
                return true;

            }

        };

        cameraController.setVelocity(20);
        core.components.InputProcessorComponent inputProcessorComponent = new core.components.InputProcessorComponent(perspectiveCamera , cameraController);
        entity.add(inputProcessorComponent);
        return entity;
    }

    public static void addStaticBulletComponent(Entity entity) {
        core.components.BulletComponent bulletComponent = new core.components.BulletComponent(true);
        bulletComponent.isStatic = true;
        entity.add(bulletComponent);

    }

    public static void addCrateEntity() {
        Entity entity = SceneEntityFactory.createSceneEntity("models/crate.gltf" , "crate" , new Vector3(0 , 10 , 0) , true);
        SceneEntityFactory.addDynamicBulletComponent(entity);
        Context.getInstance().engine.addEntity(entity);
    }

    public static Entity createSceneEntity(String path , String id , Vector3 position , boolean pickable) {
        SceneAsset sceneAsset = EditorIO.io().loadGLTF(path , id);

        core.components.SceneComponent sceneComponent = new core.components.SceneComponent(id , path);
        sceneComponent.create(sceneAsset);
        sceneComponent.update();
        sceneComponent.populateArrays();
        sceneComponent.setPickable(pickable);
        sceneComponent.setPosition(position);

        core.components.TransformComponent transformComponent = new core.components.TransformComponent();
        transformComponent.transform = sceneComponent.model.transform;

        Entity entity = new Entity();
        entity.add(sceneComponent);
        entity.add(transformComponent);

        ObjectRegistry.addSceneComponent(sceneComponent);
        Log.info("EntityFactory" , "Created SceneEntity from filepath: " + path + " with ID " + id);
        return entity;
    }

    public static void addDynamicBulletComponent(Entity entity) {
        core.components.BulletComponent bulletComponent = new core.components.BulletComponent(false);
        bulletComponent.isStatic = false;
        entity.add(bulletComponent);

    }

    public static SceneComponent createSceneComponent(String id , String path) {
        SceneAsset sceneAsset = EditorIO.io().loadGLTF(path , id);
        core.components.SceneComponent sceneComponent = new core.components.SceneComponent(id , path);
        sceneComponent.create(sceneAsset);
        sceneComponent.update();
        sceneComponent.populateArrays();
        ObjectRegistry.addSceneComponent(sceneComponent);
        Log.info("EntityFactory" , "Created SceneComponent from filepath: " + path + " with ID " + id);
        return sceneComponent;
    }

    public void createAllEntities() {

    }

}

