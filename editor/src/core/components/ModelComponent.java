package core.components;

import backend.data.ComponentRegistry;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;

public class ModelComponent implements Component
{

    public final static ComponentMapper<ModelComponent> mapper = ComponentMapper.getFor(ModelComponent.class);
    public String modelPath;
    Matrix4 transform;

    public ModelComponent() {
        ComponentRegistry.register(this);

    }

    public ModelComponent(Model model) {

    }

}

