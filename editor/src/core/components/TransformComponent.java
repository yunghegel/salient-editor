package core.components;

import backend.data.ComponentRegistry;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Matrix4;

public class TransformComponent implements Component
{

    public final static ComponentMapper<TransformComponent> mapper = ComponentMapper.getFor(TransformComponent.class);
    public Matrix4 transform;

    public TransformComponent() {
        transform = new Matrix4();
        ComponentRegistry.register(this);
    }

}
