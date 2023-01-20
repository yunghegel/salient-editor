package core.components.light;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import sys.io.ComponentRegistry;

public class PointLightComponent extends LightComponent
{

    public final static ComponentMapper<PointLightComponent> mapper = ComponentMapper.getFor(PointLightComponent.class);

    public float intensity;
    public float cutoffAngle;
    public float exponent;
    public Vector3 position;
    public Color color;
    public PointLight light;
    public boolean processed = false;
    public BoundingBox boundingBox = new BoundingBox();

    public PointLightComponent() {
        ComponentRegistry.register(this);
    }

    @Override
    public void setLight(BaseLight light) {
        this.light = (PointLight) light;
        color = light.color;
    }

}

