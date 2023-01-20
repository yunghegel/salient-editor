package core.components.light;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import sys.io.ComponentRegistry;

public class SpotLightComponent extends LightComponent
{

    public final static ComponentMapper<SpotLightComponent> mapper = ComponentMapper.getFor(SpotLightComponent.class);

    public float intensity;
    public Vector3 position;
    public Color color;
    public Vector3 direction;
    public SpotLight light;
    public boolean processed = false;
    public boolean selected = false;
    public BoundingBox boundingBox = new BoundingBox();

    public SpotLightComponent() {
        ComponentRegistry.register(this);
    }

    @Override
    public void setLight(BaseLight light) {
        this.light = (SpotLight) light;
        color = light.color;
        direction = ( (SpotLight) light ).direction;
        position = ( (SpotLight) light ).position;

    }

}
