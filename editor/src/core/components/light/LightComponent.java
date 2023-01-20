package core.components.light;

import backend.annotations.Storable;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.math.Vector3;
import editor.graphics.scene.GameObject;

@Storable(value = "LightComponent", auto = true)
abstract public class LightComponent extends GameObject implements Component
{

    public final static ComponentMapper<LightComponent> mapper = ComponentMapper.getFor(LightComponent.class);
    public BaseLight light;
    public boolean processed = false;
    public boolean selected = false;
    Vector3 color;
    float intensity;
    boolean castShadows;
    Vector3 direction;
    Vector3 position;

    public LightComponent() {

    }

    public abstract void setLight(BaseLight light);

    public void setProcessed() {
        processed = true;

    }

}

