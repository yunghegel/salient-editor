package ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import ecs.components.BulletComponent;
import ecs.components.ModelComponent;
import ecs.components.SceneComponent;
import physics.BulletWorld;
import physics.MotionState;
import ecs.World;
import utils.BulletUtils;

public class PhysicsSystem extends IteratingSystem {

    PerspectiveCamera camera;

    public BulletWorld bulletWorld;
    public static boolean debugDraw = true;
    public World world;
    public ImmutableArray<Entity> entities;
    public static Vector3 gravity = new Vector3(0, -10, 0);
    private ComponentMapper<SceneComponent> pm = ComponentMapper.getFor(SceneComponent.class);
    public final static ComponentMapper<BulletComponent> bc = ComponentMapper.getFor(BulletComponent.class);

    public PhysicsSystem() {
        super(Family.all(BulletComponent.class, SceneComponent.class).get());

        bulletWorld = new BulletWorld();

        Bullet.init();
        sys.Log.info("PhysicsSystem", "Bullet initialized");
        sys.Log.info("PhysicsSystem", "System added to engine");
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setCamera(PerspectiveCamera camera){

    }

    public BulletWorld getBulletWorld() {
        return bulletWorld;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(Family.all(BulletComponent.class, ModelComponent.class).get());

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        bulletWorld.update(deltaTime);

        if (debugDraw) {
            bulletWorld.render(camera);
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        BulletComponent bulletComponent = bc.get(entity);
        SceneComponent sceneComponent = pm.get(entity);

        if (!bulletComponent.initialized) {
            bulletComponent.initialized = true;
            if (bulletComponent.isStatic) {
                createStaticPhysicsEntity(bulletComponent, sceneComponent);
                sys.Log.info("PhysicsSystem", "Created static physics entity");
            } else {
                createDyanamicPhysicsEntity(sceneComponent, bulletComponent);
                sys.Log.info("PhysicsSystem", "Created dynamic physics entity");

            }

        }

    }

    void createDyanamicPhysicsEntity(SceneComponent sceneComponent, BulletComponent bulletComponent) {
        ModelInstance modelInstance = sceneComponent.gltfScene.modelInstance;
        btCollisionShape shape = BulletUtils.createGImpactMeshShape(modelInstance.model);
        float mass = bulletComponent.mass;
        shape.calculateLocalInertia(mass, bulletComponent.inertia);
        MotionState motionState = new MotionState(modelInstance.transform);
        bulletComponent.motionState = motionState;

        btRigidBody.btRigidBodyConstructionInfo constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, bulletComponent.motionState, shape, bulletComponent.inertia);
        btRigidBody body = new btRigidBody(constructionInfo);
        body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
        bulletWorld.addBody(body);

    }

    public void createStaticPhysicsEntity(BulletComponent bulletComponent, SceneComponent sceneComponent) {
        ModelInstance modelInstance = sceneComponent.gltfScene.modelInstance;
        btCollisionShape shape = BulletUtils.createGImpactMeshShape(modelInstance.model);
        float mass = 0;
        shape.calculateLocalInertia(mass, bulletComponent.inertia);
        MotionState motionState = new MotionState(modelInstance.transform);
        bulletComponent.motionState = motionState;

        btRigidBody.btRigidBodyConstructionInfo constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, bulletComponent.motionState, shape, bulletComponent.inertia);
        btRigidBody body = new btRigidBody(constructionInfo);
        body.setWorldTransform(modelInstance.transform);
        body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
        body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_DISABLE_VISUALIZE_OBJECT);

        bulletWorld.addBody(body);
    }

    public void createStaticNodePhysicsEntity(BulletComponent bulletComponent,SceneComponent sceneComponent){
        sys.Log.info("PhysicsSystem","Registering level collision in btCollisionShape object...");
        ModelInstance modelInstance = sceneComponent.gltfScene.modelInstance;

        for(Node node :modelInstance.model.nodes){
            //if(node.id.contains("collision")){
            sys.Log.info("Found collision node: "+node.id);
            BoundingBox boundingBox = new BoundingBox();
            node.calculateBoundingBox(boundingBox);
            btCollisionShape collisionShape = Bullet.obtainStaticNodeShape(node,false);
            MotionState motionState = new MotionState(node.localTransform);
            bulletComponent.motionState = motionState;

            float mass = 0f;
            Vector3 localInertia = new Vector3();

            if(collisionShape != null){
                collisionShape.calculateLocalInertia(mass, localInertia);

                btRigidBody.btRigidBodyConstructionInfo playerInfo=new btRigidBody.btRigidBodyConstructionInfo(mass, motionState, collisionShape, localInertia);
                btRigidBody nodeRigidBody = new btRigidBody(playerInfo);

                nodeRigidBody.setAngularFactor(Vector3.Y);
                nodeRigidBody.setDamping(0.75f,0.99f);
                nodeRigidBody.setActivationState(Collision.DISABLE_DEACTIVATION);
                nodeRigidBody.setCollisionFlags(nodeRigidBody.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
                //Disable debug drawing for this object
                nodeRigidBody.setCollisionFlags(nodeRigidBody.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_DISABLE_VISUALIZE_OBJECT);

                bulletWorld.addBody(nodeRigidBody);
                sys.Log.info("PhysicsSystem","Registered collision node: "+node.id); }
        }

    }

    public void setGravity(float gravity)
    {
        bulletWorld.setGravity(new Vector3(0, -gravity, 0));
    }
}
