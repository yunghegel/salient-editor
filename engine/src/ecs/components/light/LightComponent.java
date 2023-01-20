package ecs.components.light;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.math.Vector3;

abstract public class LightComponent implements Component {

    Vector3 color;
    float intensity;
    boolean castShadows;
    Vector3 direction;
    Vector3 position;
    public BaseLight light;
    public boolean processed = false;
    public boolean selected = false;

    public abstract void setLight(BaseLight light);

    public final static ComponentMapper<LightComponent> mapper = ComponentMapper.getFor(LightComponent.class);

    public LightComponent() {

    }

    public void setProcessed(){
        processed = true;

    }
}

