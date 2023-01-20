package ecs.components.light;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.math.Vector3;
import sys.io.ComponentRegistry;

public class SpotLightComponent extends LightComponent {
    public final static ComponentMapper<SpotLightComponent> mapper = ComponentMapper.getFor(SpotLightComponent.class);

    public float intensity;
    public Vector3 position;
    public Color color;
    public Vector3 direction;
    public SpotLight spotLight;
    public boolean processed=false;
    public boolean selected = false;

    @Override
    public void setLight(BaseLight light) {
        this.spotLight = (SpotLight) light;
        color = light.color;
        direction = ((SpotLight) light).direction;
        position = ((SpotLight) light).position;

    }

    public SpotLightComponent() {
        ComponentRegistry.register(this);
    }
}
