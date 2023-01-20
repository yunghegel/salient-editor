package ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneModel;
import sys.io.ComponentRegistry;
import utils.ModelUtils;

public class SceneComponent implements Component {
    public ModelInstance modelInstance;
    public Model model;
    public Matrix4 transform=new Matrix4();
    public BoundingBox boundingBox=new BoundingBox();
    public ModelInstance boundingBoxInstance;
    public Scene boundingBoxScene;
    public Scene gltfScene;
    public SceneModel sceneModel;
    public SceneAsset sceneAsset;
    public String path;
    public String id = "GLTF Scene";
    public boolean boundingBoxVisible = true;
    public Vector3 center = new Vector3();
    public boolean selected= false;
    public boolean parsed = false;


    public final static ComponentMapper<SceneComponent> mapper = ComponentMapper.getFor(SceneComponent.class);

    public SceneComponent(){
        ComponentRegistry.register(this);

    }

    public void calculateBoundingBoxInfo(){
        boundingBox = gltfScene.modelInstance.calculateBoundingBox(boundingBox);
        boundingBox.getCenter(center);


    }

    public void update(){


        boundingBoxInstance = ModelUtils.createBoundingBoxRenderable(boundingBox);
        boundingBoxScene = new Scene(boundingBoxInstance);
        center = boundingBox.getCenter(center);
        boundingBoxScene.modelInstance.transform.set(gltfScene.modelInstance.transform);

    }

    public void addToScene(SceneManager scene){
        scene.addScene(boundingBoxScene);
    }
    public void removeFromScene(SceneManager scene){
        scene.removeScene(boundingBoxScene);
    }
}
