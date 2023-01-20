package ecs.components.light;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import sys.io.ComponentRegistry;

public class PointLightComponent extends LightComponent {
    public final static ComponentMapper<PointLightComponent> mapper = ComponentMapper.getFor(PointLightComponent.class);

    public float intensity;
    public float cutoffAngle;
    public float exponent;
    public Vector3 position;
    public Color color;
    public PointLight light;
    public boolean processed=false;
    @Override
    public void setLight(BaseLight light) {
        this.light = (PointLight) light;
        color = light.color;
    }

    public PointLightComponent() {
        ComponentRegistry.register(this);
    }
}

