package ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import ecs.components.BulletComponent;
import ecs.components.PickableComponent;
import ecs.components.SceneComponent;
import net.mgsx.gltf.scene3d.scene.Scene;
import physics.BulletWorld;
import ecs.World;

import salient.SalientRenderer;
import utils.ModelUtils;

public class ObjectPickingSystem extends IteratingSystem {
  public final static ComponentMapper<PickableComponent> mapper = ComponentMapper.getFor(PickableComponent.class);
  public final static ComponentMapper<BulletComponent> bulletMapper = ComponentMapper.getFor(BulletComponent.class);
  ClosestRayResultCallback callback;
  Vector3 fromRay = new Vector3();
    Vector3 toRay = new Vector3();

  BulletWorld bulletWorld;
  World world;
  Ray ray;
  Scene rayScene;
  PerspectiveCamera camera;
    float mouseX = Gdx.input.getX();
    float mouseY = Gdx.input.getY();
    public static Vector3 cameraCenter = new Vector3();
    public static Vector2 viewportOrigin = new Vector2();

  public ObjectPickingSystem() {
    super(Family.one(SceneComponent.class, PickableComponent.class, BulletComponent.class).get());
  }

  public void setWorld(World world) {
    this.world = world;
    camera = SalientRenderer.camera;
    bulletWorld = world.getPhysics().getBulletWorld();
  }

  @Override
  public void addedToEngine(Engine engine) {
    super.addedToEngine(engine);
    sys.Log.info("ObjectPickingSystem","ObjectPickingSystem added to engine");
    callback = new ClosestRayResultCallback( new Vector3(), new Vector3());
    camera = SalientRenderer.camera;

  }

  @Override
  public void update(float deltaTime) {
    if(getEntities().size()>0) {
      super.update(deltaTime);
    }


  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    SceneComponent sceneComponent = SceneComponent.mapper.get(entity);
    BulletComponent bulletComponent = BulletComponent.bc.get(entity);
    PickableComponent pickableComponent = PickableComponent.mapper.get(entity);



    //normalize the coordinates to our camera viewport size
    float screenX = camera.viewportWidth / Gdx.graphics.getWidth();
    float screenY = camera.viewportHeight / Gdx.graphics.getHeight();
    float camY = screenY* camera.viewportHeight;
    float camX = screenX* camera.viewportWidth;





if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)){

    camera.update();
    //apply the coordinates to our camera viewport




    //Ray ray = camera.getPickRay(mouseX, mouseY);
  ray = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());





    //bulletWorld.raycast(fromRay, toRay, callback);
    rayDebugView(fromRay, toRay);

 /*   if (callback.hasHit()) {
      btCollisionObject collisionObject = callback.getCollisionObject();
      if (collisionObject instanceof btRigidBody) {
        // Activate and push the object in the direction of the ray
        collisionObject.activate();
        ((btRigidBody) collisionObject).applyCentralImpulse(ray.direction.scl(50f));
      }
    }*/
  }}





  public void rayDebugView(Vector3 rayOrigin, Vector3 rayDirection) {

    if (rayScene != null) {
      world.getSceneManager().removeScene(rayScene);
    }

    ModelInstance ray = ModelUtils.createRayModelInstance(rayOrigin, rayDirection, 100, Color.BLUE);
    rayScene = new Scene(ray);

    world.getSceneManager().addScene(rayScene);

  }

}