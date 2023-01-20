package ecs.entities;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.Vector3;

import ecs.components.InputProcessorComponent;
import ecs.components.SceneComponent;
import ecs.components.TransformComponent;
import net.mgsx.gltf.scene3d.scene.Scene;
import sys.io.EditorIO;
import sys.io.ResourceRegistry;


public class EntityFactory {
    private static EntityFactory instance = new EntityFactory();
    public static EntityFactory getInstance() {return instance;}
    Engine engine;



    private EntityFactory(){

    }

    public void createAllEntities(){

    }

    public static Entity createSceneComponent(String path, String id) {
        Entity entity = new Entity();
        SceneComponent sceneComponent = new SceneComponent();
        TransformComponent transformComponent = new TransformComponent();
        /*SceneAsset sceneAsset = new GLTFLoader().load(Gdx.files.internal(path));*/

        sceneComponent.sceneAsset= EditorIO.io().loadGLTF(path,id);
        sceneComponent.sceneModel = sceneComponent.sceneAsset.scene;
        sceneComponent.gltfScene = new Scene(sceneComponent.sceneModel);
        sceneComponent.model = sceneComponent.sceneModel.model;
        sceneComponent.modelInstance = new ModelInstance(sceneComponent.model);
        sceneComponent.modelInstance.calculateBoundingBox(sceneComponent.boundingBox);
        sceneComponent.id = id;
        sceneComponent.path = path;
        transformComponent.transform = sceneComponent.modelInstance.transform;

        entity.add(sceneComponent);
        entity.add(transformComponent);
        ResourceRegistry.addSceneComponent(sceneComponent);


        sys.Log.info("EntityFactory","Created SceneComponent from filepath: "+path+" with ID "+id);
        return entity;
    }
    public static void dispose() {

    }

    public static Entity create3DInputProcessingEntity() {
        Entity entity = new Entity();
        PerspectiveCamera perspectiveCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        perspectiveCamera.position.set(0, 10, 0);
        perspectiveCamera.lookAt(0, 10, 0);
        perspectiveCamera.near = 1f;
        perspectiveCamera.far = 3000f;
        perspectiveCamera.update();

        FirstPersonCameraController cameraController = new FirstPersonCameraController(perspectiveCamera) {
            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)&&Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                    //drag pan camera
                    float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
                    float deltaY = -Gdx.input.getDeltaY() * degreesPerPixel;

                    perspectiveCamera.position.add(0, 0, deltaY).dot(Vector3.Y);
                    perspectiveCamera.position.add(-deltaX, 0, 0).dot(Vector3.X);


                    return true;
                }



                if (Gdx.input.isButtonPressed(1)) {
                    return super.touchDragged(screenX, screenY, pointer);

                }
                return false;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                //move camera forward and backward with intensity varying with distance from camera to target
                float intensity = 0.1f * (perspectiveCamera.position.dst(perspectiveCamera.direction));
                perspectiveCamera.translate(perspectiveCamera.direction.x * amountY * intensity, perspectiveCamera.direction.y * amountY * intensity, perspectiveCamera.direction.z * amountY * intensity);
                return true;

            }

        };

        cameraController.setVelocity(20);
        InputProcessorComponent inputProcessorComponent = new InputProcessorComponent(perspectiveCamera, cameraController);
        entity.add(inputProcessorComponent);
        return entity;
    }
}

