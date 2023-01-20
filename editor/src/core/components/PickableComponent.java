package core.components;

import backend.data.ComponentRegistry;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class PickableComponent implements Component
{

    public final static ComponentMapper<PickableComponent> mapper = ComponentMapper.getFor(PickableComponent.class);

    public boolean isPickable = true;
    public BoundingBox boundingBox = new BoundingBox();
    public Vector3 position = new Vector3();
    public boolean selected = false;
    public Vector3 intersection = new Vector3();

    public PickableComponent() {
        ComponentRegistry.register(this);
    }

    public void setPosition(Vector3 pos) {
        BoundingBox boundingBox = new BoundingBox(new Vector3(pos.x - 0.5f , pos.y - 0.5f , pos.z - 0.5f) , new Vector3(pos.x + 0.5f , pos.y + 0.5f , pos.z + 0.5f));
        setBoundingBox(boundingBox);
        this.position = pos;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

}
