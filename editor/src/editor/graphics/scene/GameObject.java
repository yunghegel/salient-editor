package editor.graphics.scene;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;

public class GameObject
{

    public String name;
    Matrix4 transform = new Matrix4();
    Array<GameObject> children = new Array<GameObject>();

    public GameObject(String name) {
        this.name = name;
    }

    public GameObject() {

    }

    public void addChild(GameObject child) {
        children.add(child);
    }

    public void setTransform(Matrix4 transform) {
        transform.mul(transform);
        for (GameObject child : children) {
            child.setTransform(transform);
        }

    }

    public void setTransformLocal(Matrix4 transform) {
        this.transform.mul(transform);
    }

}
