package core.components.light;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.math.Vector3;
import sys.io.ComponentRegistry;

public class ShadowLightComponent extends LightComponent
{

    public final static ComponentMapper<ShadowLightComponent> mapper = ComponentMapper.getFor(ShadowLightComponent.class);
    public final Vector3 tmpV = new Vector3();
    public float halfDepth;
    public float halfHeight;
    public int shadowMapWidth;
    public int shadowMapHeight;
    public float shadowViewportWidth;
    public float shadowViewportHeight;
    public DirectionalShadowLight light;
    public boolean processed = false;
    public boolean selected = false;

    public ShadowLightComponent() {
        ComponentRegistry.register(this);
    }

    @Override
    public void setLight(BaseLight light) {
        this.light = (DirectionalShadowLight) light;
    }

}
