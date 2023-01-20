package core.components.light;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import sys.io.ComponentRegistry;

public class DirectionalLightComponent extends LightComponent
{

    public final static ComponentMapper<DirectionalLightComponent> mapper = ComponentMapper.getFor(DirectionalLightComponent.class);
    public Vector3 direction;
    public Color color;
    public DirectionalLight directionalLight;
    public float intensity;
    public boolean processed = false;
    public boolean selected = false;

    public DirectionalLightComponent() {
        ComponentRegistry.register(this);
    }

    @Override
    public void setLight(BaseLight light) {
        this.directionalLight = (DirectionalLight) light;
        color = light.color;
        direction = ( (DirectionalLight) light ).direction;

    }

}
