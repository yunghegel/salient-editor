package ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import sys.io.ComponentRegistry;

public class ModelComponent implements Component {
    public final static ComponentMapper<ModelComponent> mapper = ComponentMapper.getFor(ModelComponent.class);

    Matrix4 transform;
    public String modelPath;

    public ModelComponent() {
        ComponentRegistry.register(this);

    }

    public ModelComponent(Model model){

    }



}

