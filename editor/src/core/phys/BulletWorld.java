package core.phys;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Disposable;

public class BulletWorld implements Disposable
{

    public final btDynamicsWorld dynamicsWorld;
    public final btBroadphaseInterface broadphase;
    public final btAxisSweep3 sweep;
    private final btDispatcher dispatcher;
    private final btCollisionConfiguration collisionConfig;
    private final btConstraintSolver constraintSolver;
    private final DebugDrawer debugDrawer;
    private final btGhostPairCallback ghostPairCallback;
    private final float fixedTimeStep = 1f / 60f;
    private final Vector3 gravity = new Vector3(0 , -5f , 0);
    private final Vector3 lastRayFrom = new Vector3();
    private final Vector3 lastRayTo = new Vector3();
    private final Vector3 rayColor = new Vector3(1 , 0 , 1);

    public BulletWorld() {
        Bullet.init();
        //   log.info("Bullet initialized; creating system...");
        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        sweep = new btAxisSweep3(new Vector3(-1000 , -1000 , -1000) , new Vector3(1000 , 1000 , 1000));

        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher , broadphase , constraintSolver , collisionConfig);
        dynamicsWorld.setGravity(gravity);
        ghostPairCallback = new btGhostPairCallback();
        sweep.getOverlappingPairCache().setInternalGhostPairCallback(ghostPairCallback);
        debugDrawer = new DebugDrawer();
        debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_DrawWireframe);
        //  log.info("debug drawer set; mode DBG.DrawWireframe");

        dynamicsWorld.setDebugDrawer(debugDrawer);
        //  log.info("dynamics world created & bullet system inititialized.");
    }

    public void update(float delta) {
        dynamicsWorld.stepSimulation(delta , 1 , fixedTimeStep);
    }

    public void addBody(btRigidBody body) {
        dynamicsWorld.addRigidBody(body);
        //     log.info("body added to dynamics world; registered with flags: " + body.getCollisionFlags() + " and group: " + body.getBroadphaseProxy().getCollisionFilterGroup() + " and mask: " + body.getBroadphaseProxy().getCollisionFilterMask());
    }

    public void render(Camera camera) {
        debugDrawer.begin(camera);
        debugDrawer.drawLine(lastRayFrom , lastRayTo , rayColor);
        dynamicsWorld.debugDrawWorld();
        debugDrawer.end();
    }

    public void raycast(Vector3 from , Vector3 to , RayResultCallback callback) {
        lastRayFrom.set(from).sub(0 , 5f , 0f);

        dynamicsWorld.rayTest(from , to , callback);

        if (callback.hasHit() && callback instanceof ClosestRayResultCallback) {
            // Use interpolation to determine the hitpoint where the ray hit the object
            // This is what bullet does behind the scenes as well
            lastRayTo.set(from);
            lastRayTo.lerp(to , callback.getClosestHitFraction());
        }
        else {
            lastRayTo.set(to);
        }
    }

    /**
     * Exposes the collision world for more simple collision-based queries;
     *
     * @param object - the object we'd like to preform collision operations on
     */

    //TODO: Method for registering a new PhysicsComponent which wraps a BulletEntity

    //TODO: Method for registering EventListeners for particular types of collision events
    public void addCollisionObject(btCollisionObject object) {
        dynamicsWorld.addCollisionObject(object);
    }

    /**
     * Remove a collision object from the world.
     *
     * @param object = the object we'd like to remove from the world
     */
    public void removeCollisionObject(btCollisionObject object) {
        dynamicsWorld.removeCollisionObject(object);
    }

    /**
     * Add a collision object to the world.
     *
     * @param object - our collision object we'd like to preform collision operations on
     * @param group  - filter group, for filtering collisions
     * @param mask   - filter mask, for filtering collisions
     *               <p>
     *               only objects with matching group and mask will collide
     */

    public void addCollisionObject(btCollisionObject object , short group , short mask) {
        dynamicsWorld.addCollisionObject(object , group , mask);
    }

    @Override
    public void dispose() {
        dynamicsWorld.dispose();
        constraintSolver.dispose();
        broadphase.dispose();
        dispatcher.dispose();
        collisionConfig.dispose();
        ghostPairCallback.dispose();
        sweep.dispose();
        //       log.info("memory mngmt; bullet system disposed.");
    }

    public void setToAxisSweep() {
        dynamicsWorld.setBroadphase(sweep);
    }

    public void setToDbvt() {
        dynamicsWorld.setBroadphase(broadphase);
    }

    public void setGravity(Vector3 gravity) {
        dynamicsWorld.setGravity(gravity);
    }

    public void setGhostPairCallback(btGhostPairCallback ghostPairCallBack) {
        dynamicsWorld.getBroadphase().getOverlappingPairCache().setInternalGhostPairCallback(ghostPairCallBack);
    }

    public void sweep() {
        dynamicsWorld.getBroadphase().calculateOverlappingPairs(dynamicsWorld.getDispatcher());

    }

}
