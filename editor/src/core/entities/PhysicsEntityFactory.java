package core.entities;

import com.badlogic.ashley.core.Entity;
import core.components.BulletComponent;

public class PhysicsEntityFactory
{

    public static Entity createPhysicsEntity(int flags) {
        Entity entity = new Entity();
        entity.add(new BulletComponent(flags));
        return entity;
    }

    public static Entity addPhysicsComponent(Entity entity , int flags) {
        entity.add(new BulletComponent(flags));
        return entity;
    }

    public static BulletComponent getPhysicsComponent(Entity entity) {
        return entity.getComponent(BulletComponent.class);
    }

}


