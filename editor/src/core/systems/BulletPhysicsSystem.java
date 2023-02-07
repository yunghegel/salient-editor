package core.systems;

import backend.tools.Log;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Array;
import core.components.BulletComponent;
import core.components.ModelComponent;
import editor.Context;
import core.components.SceneComponent;
import core.phys.BulletWorld;

import core.phys.MotionState;
import utils.BulletUtils;

public class BulletPhysicsSystem extends IteratingSystem
{

    public final static ComponentMapper<BulletComponent> bc = ComponentMapper.getFor(BulletComponent.class);
    public static BulletWorld bulletWorld;
    public static boolean debugDraw = false;
    public static Vector3 gravity = new Vector3(0 , -10 , 0);
    public static Ray ray = new Ray();
    static Array<btRigidBody> staticBodies = new Array<btRigidBody>();
    public ImmutableArray<Entity> entities;
    public ClosestRayResultCallback rayCallback;
    public Vector3 rayFrom = new Vector3();
    public Vector3 rayTo = new Vector3();
    PerspectiveCamera camera;
    Array<btRigidBody> dynamicBodies = new Array<btRigidBody>();
    private Context context;
    private ComponentMapper<core.components.SceneComponent> pm = ComponentMapper.getFor(core.components.SceneComponent.class);

    public BulletPhysicsSystem() {
        super(Family.all(BulletComponent.class , core.components.SceneComponent.class).get());

        bulletWorld = Context.getInstance().bulletWorld;

        Bullet.init();
        Log.info("PhysicsSystem" , "Bullet initialized");
        Log.info("PhysicsSystem" , "System added to engine");
    }

    public static void toggleStaticBodyDebugDraw() {
        for (btRigidBody body : staticBodies) {
            if (body.getCollisionFlags() == btCollisionObject.CollisionFlags.CF_DISABLE_VISUALIZE_OBJECT) {
                body.setCollisionFlags(body.getCollisionFlags() & ~btCollisionObject.CollisionFlags.CF_DISABLE_VISUALIZE_OBJECT);
            }
            else {
                body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_DISABLE_VISUALIZE_OBJECT);
            }
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setCamera(PerspectiveCamera camera) {

    }

    public BulletWorld getBulletWorld() {
        return bulletWorld;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(Family.all(BulletComponent.class , ModelComponent.class).get());
        rayCallback = new ClosestRayResultCallback(new Vector3() , new Vector3());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            doPhysicsRayCast();

        }
       // bulletWorld.update(deltaTime);

        //        if (debugDraw) {
        //            bulletWorld.render(RenderWidget.cam);
        //        }
    }

    @Override
    protected void processEntity(Entity entity , float deltaTime) {

        BulletComponent bulletComponent = bc.get(entity);
        core.components.SceneComponent sceneComponent = pm.get(entity);

        if (!bulletComponent.initialized) {
            bulletComponent.initialized = true;
            if (bulletComponent.isStatic) {
                createStaticPhysicsEntity(bulletComponent , sceneComponent);
                Log.info("PhysicsSystem" , "Created static physics entity from component " + sceneComponent.id);
            }
            else {
                createDyanamicPhysicsEntity(sceneComponent , bulletComponent);
                Log.info("PhysicsSystem" , "Created dynamic physics entity from component " + sceneComponent.id);

            }

        }
        if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {

        }
    }

    public void createStaticPhysicsEntity(BulletComponent bulletComponent , core.components.SceneComponent sceneComponent) {
        ModelInstance modelInstance = sceneComponent.scene.modelInstance;
        btCollisionShape shape = BulletUtils.createGImpactMeshShape(modelInstance.model);
        float mass = 0;

        shape.calculateLocalInertia(mass , bulletComponent.inertia);
        MotionState motionState = new MotionState(modelInstance.transform);
        bulletComponent.motionState = motionState;

        btRigidBody.btRigidBodyConstructionInfo constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass , bulletComponent.motionState , shape , bulletComponent.inertia);
        btRigidBody body = new btRigidBody(constructionInfo);
        body.setWorldTransform(modelInstance.transform);
        body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
        body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_DISABLE_VISUALIZE_OBJECT);
        staticBodies.add(body);
        bulletWorld.addBody(body);
    }

    public void createDyanamicPhysicsEntity(core.components.SceneComponent sceneComponent , BulletComponent bulletComponent) {
        ModelInstance modelInstance = sceneComponent.scene.modelInstance;
        btCollisionShape shape = BulletUtils.createTriangleMeshShape(modelInstance.model);

        float mass = 10f;
        shape.setLocalScaling(modelInstance.transform.getScale(new Vector3()));
        shape.calculateLocalInertia(mass , bulletComponent.inertia);

        MotionState motionState = new MotionState(sceneComponent.scene.modelInstance.transform);

        bulletComponent.motionState = motionState;

        btRigidBody.btRigidBodyConstructionInfo constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass , motionState , shape , bulletComponent.inertia);
        btRigidBody body = new btRigidBody(constructionInfo);
        body.setWorldTransform(sceneComponent.scene.modelInstance.transform);

        dynamicBodies.add(body);
        bulletWorld.addBody(body);
        bulletWorld.setToDbvt();

    }

    public void createStaticNodePhysicsEntity(BulletComponent bulletComponent , core.components.SceneComponent sceneComponent) {
        sys.Log.info("PhysicsSystem" , "Registering level collision in btCollisionShape object...");
        ModelInstance modelInstance = sceneComponent.scene.modelInstance;

        for (Node node : modelInstance.model.nodes) {
            //if(node.id.contains("collision")){
            sys.Log.info("Found collision node: " + node.id);
            BoundingBox boundingBox = new BoundingBox();
            node.calculateBoundingBox(boundingBox);
            btCollisionShape collisionShape = Bullet.obtainStaticNodeShape(node , false);
            MotionState motionState = new MotionState(node.localTransform);
            bulletComponent.motionState = motionState;

            float mass = 0f;
            Vector3 localInertia = new Vector3();

            if (collisionShape != null) {
                collisionShape.calculateLocalInertia(mass , localInertia);

                btRigidBody.btRigidBodyConstructionInfo playerInfo = new btRigidBody.btRigidBodyConstructionInfo(mass , motionState , collisionShape , localInertia);
                btRigidBody nodeRigidBody = new btRigidBody(playerInfo);

                nodeRigidBody.setAngularFactor(Vector3.Y);
                nodeRigidBody.setDamping(0.75f , 0.99f);
                nodeRigidBody.setActivationState(Collision.DISABLE_DEACTIVATION);

                //Disable debug drawing for this object
                nodeRigidBody.setCollisionFlags(nodeRigidBody.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_DISABLE_VISUALIZE_OBJECT);
                dynamicBodies.add(nodeRigidBody);
                bulletWorld.addBody(nodeRigidBody);
                sys.Log.info("PhysicsSystem" , "Registered collision node: " + node.id);
            }
        }

    }

    public void setGravity(float gravity) {
        bulletWorld.setGravity(new Vector3(0 , -gravity , 0));
    }

    public void doPhysicsRayCast() {
        Ray ray = SceneSystem.ray;
        rayFrom.set(ray.origin);
        rayTo.set(ray.direction).scl(100f).add(ray.origin).sub(Gdx.input.getX() , Gdx.input.getY() , 0);

        bulletWorld.raycast(ray.origin , ray.direction , rayCallback);
        if (rayCallback.hasHit()) {
            sys.Log.info("PhysicsSystem" , "Raycast hit!");
            btRigidBody body = (btRigidBody) rayCallback.getCollisionObject();
            //body.setLinearVelocity(new Vector3(0 , 0 , 0));
            body.applyCentralImpulse(new Vector3(0 , 1 , 0));
        }
    }

    public void dispose(){
        bulletWorld.dispose();

    }

}
