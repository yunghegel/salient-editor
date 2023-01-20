package physics;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;


import java.util.logging.Logger;

public class BulletEntity {
    private final btRigidBody body;
    private final ModelInstance modelInstance;


    public enum Type {
        STATIC, KINEMATIC,GHOST,CHARACTER
    }
    public enum Filter {
        NONE, ALL, STATIC, DYNAMIC, KINEMATIC
    }

    public BulletEntity(btRigidBody body, ModelInstance modelInstance) {
        this.body = body;
        this.modelInstance = modelInstance;
    }

    public BulletEntity (btRigidBody body, ModelInstance modelInstance, Type type){
        this.body=body;
        this.modelInstance=modelInstance;
        setType(type);
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public btRigidBody getBody() {
        return body;
    }

    public void setType(Type type) {
        switch (type) {
            case STATIC:
                body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
                break;
            case GHOST:
                body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);

                break;
            case KINEMATIC:
                body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
                body.setActivationState(Collision.DISABLE_DEACTIVATION);
                break;
            case CHARACTER:
                body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
                break;
        }
    }
    public void setDebugDrawing(boolean debugDrawing) {
        if (debugDrawing) {
            body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_DISABLE_VISUALIZE_OBJECT);
        } else {
            body.setCollisionFlags(body.getCollisionFlags() & ~btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        }
    }
}
