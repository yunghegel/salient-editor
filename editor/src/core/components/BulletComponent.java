package core.components;

import backend.data.ComponentRegistry;
import backend.annotations.Storable;
import backend.tools.Log;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import editor.graphics.scene.GameObject;
import core.attributes.PhysicsAttributes;
import physics.BulletEntity;
import core.phys.MotionState;

@Storable(value = "BulletComponent", auto = true)
public class BulletComponent extends GameObject implements Component
{

    public final static ComponentMapper<BulletComponent> bc = ComponentMapper.getFor(BulletComponent.class);

    public BulletEntity bulletEntity;
    public MotionState motionState;
    public boolean isStatic = false;
    public float mass;
    public Vector3 inertia = new Vector3();
    public boolean initialized = false;
    public btCollisionShape shape;
    public btRigidBody body;

    public int flags = 0;

    PhysicsAttributes attr;

    public BulletComponent(boolean isStatic) {
        ComponentRegistry.register(this);
        this.isStatic = isStatic;

        if (isStatic) {

            mass = 0;
        }
        else {
            mass = 1;
        }
    }

    public BulletComponent(int flags) {
        name = "Physics Component";
        ComponentRegistry.register(this);
        attr = new PhysicsAttributes(flags);

        if (attr.checkFlag(PhysicsAttributes.STATIC)) {
            Log.info("BulletComponent" , "Component registered attribute STATIC");
        }
        if (attr.checkFlag(PhysicsAttributes.DYNAMIC)) {
            Log.info("BulletComponent" , "Component registered attribute DYNAMIC");
        }
        if (attr.checkFlag(PhysicsAttributes.KINEMATIC)) {
            Log.info("BulletComponent" , "Component registered attribute KINEMATIC");
        }
        if (attr.checkFlag(PhysicsAttributes.GHOST_OBJECT)) {
            Log.info("BulletComponent" , "Component registered attribute GHOST_OBJECT");
        }
        if (attr.checkFlag(PhysicsAttributes.DEBUG_DRAW_ENABLED)) {
            Log.info("BulletComponent" , "Component registered attribute DEBUG_DRAW_ENABLED");
        }

    }

}



