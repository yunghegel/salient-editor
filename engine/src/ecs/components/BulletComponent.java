package ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector3;
import physics.BulletEntity;
import physics.MotionState;
import sys.io.ComponentRegistry;

public class BulletComponent implements Component {
    public final static ComponentMapper<BulletComponent> bc = ComponentMapper.getFor(BulletComponent.class);

    public BulletEntity bulletEntity;
    public MotionState motionState;
    public boolean isStatic = false;
    public float mass;
    public Vector3 inertia = new Vector3();
    public boolean initialized = false;

    public BulletComponent(boolean isStatic){
        ComponentRegistry.register(this);
        this.isStatic = isStatic;
        if (isStatic) {
            mass = 0;
        } else {
            mass = 1;
        }


    }


}
