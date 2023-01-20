package ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Matrix4;
import sys.io.ComponentRegistry;

public class TransformComponent implements Component {
  public Matrix4 transform;
  public final static ComponentMapper<TransformComponent> mapper = ComponentMapper.getFor(TransformComponent.class);

    public TransformComponent() {
        transform = new Matrix4();
        ComponentRegistry.register(this);
    }
}
