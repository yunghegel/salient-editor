package core.components;

import backend.data.ComponentRegistry;
import backend.annotations.Storable;
import backend.tools.Log;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import editor.graphics.scene.GameObject;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneModel;
import util.ModelUtils;

@Storable(value = "SceneComponent", auto = true)

public class SceneComponent extends GameObject implements Component
{

    public final static ComponentMapper<SceneComponent> mapper = ComponentMapper.getFor(SceneComponent.class);
    //flags as mask
    public static final int MASK = 1; // 0001
    public static final int PICKABLE = 2; // 0010
    public static final int PHYSICS_ENABLED = 4; // 0100
    public ModelInstance model;
    public Model mdl;
    public BoundingBox boundingBox = new BoundingBox();
    public ModelInstance boundingBoxInstance;
    public Scene boundingBoxScene;
    public Scene scene;
    public SceneModel sceneModel;
    public SceneAsset sceneAsset;
    public Array<Mesh> meshes = new Array<Mesh>();
    public Array<Material> materials = new Array<Material>();
    public Array<Node> nodes = new Array<Node>();
    public Array<Animation> animations = new Array<Animation>();
    public Matrix4 transform = new Matrix4();
    public Vector3 center = new Vector3();
    public boolean selected = false;
    public boolean parsed = false;
    public boolean hovered = false;
    public boolean arraysPopulated = false;
    public boolean boundingBoxVisible = false;
    public boolean pickable = true;
    public String path;
    public String id;
    private Matrix4 tmp = new Matrix4();
    private Vector3 position;
    private Vector3 rotation;
    private Vector3 scale;
    private float[] vertices;
    private short[] indices;
    private Model outlinedModel;
    public ModelInstance outlinedModelInstance;

    public SceneComponent(String id , String path) {
        ComponentRegistry.register(this);
        position = new Vector3();
        rotation = new Vector3();
        scale = new Vector3();

        this.id = id;
        this.name = id;
        this.path = path;

    }

    public SceneComponent(String id) {
        ComponentRegistry.register(this);
        this.id = id;
        this.path = "null";
    }

    public void setPickable(boolean pickable) {
        this.pickable = pickable;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setParsed(boolean parsed) {
        this.parsed = parsed;
    }

    public void create(SceneAsset scene) {
        this.sceneAsset = scene;
        this.sceneModel = scene.scene;
        this.scene = new Scene(sceneModel);
        this.mdl = scene.scene.model;
        this.model = this.scene.modelInstance;
        this.model.calculateBoundingBox(boundingBox);



    }

    public void create(ModelInstance model) {
        this.model = model;
        this.mdl = model.model;
        this.model.calculateBoundingBox(boundingBox);
        this.scene = new Scene(mdl);

//        ModelUtils.createOutlineModel(mdl , Color.WHITE, .5f);
//        ModelUtils.createAlphaAttribute(mdl,.5f);
//        outlinedModelInstance = new ModelInstance(outlinedModel);

    }

    @Override
    public void setTransform(Matrix4 transform) {
        scene.modelInstance.transform.set(transform);
        Log.info("SceneComponent" , "Set transform on GameObject of Type SceneComponent with id: " + id);

        super.setTransform(transform);
    }

    @Override
    public void setTransformLocal(Matrix4 transform) {
        scene.modelInstance.transform.set(transform);
        Log.info("SceneComponent" , "Set transform locally on GameObject of Type SceneComponent with id: " + id);
        super.setTransformLocal(transform);
    }

    public void populateArrays() {
        if (!arraysPopulated) {
            loadMeshes();
            loadMaterials();
            loadNodes();
            loadAnimations();
            arraysPopulated = true;
        }
    }

    public void loadMeshes() {
        meshes.addAll(scene.modelInstance.model.meshes);
    }

    public void loadMaterials() {
        materials.addAll(scene.modelInstance.materials);
    }

    public void loadNodes() {
        nodes.addAll(scene.modelInstance.model.nodes);
    }

    public void loadAnimations() {
        animations.addAll(scene.modelInstance.animations);
    }

    public void update() {
        calculateBoundingBoxInfo();
        ensureDataBind();
    }

    public void calculateBoundingBoxInfo() {

        scene.modelInstance.calculateBoundingBox(boundingBox);
        tmp.set(scene.modelInstance.transform);
        boundingBox.mul(tmp);
        boundingBox.getCenter(center);



    }

    private void ensureDataBind() {
        if (model != null) model = scene.modelInstance;
        transform = scene.modelInstance.transform;
    }

    public void calculateIndicesAndVertices() {
        meshes = scene.modelInstance.model.meshes;
        for (Mesh mesh : meshes) {
            VertexAttributes vertexAttributes = mesh.getVertexAttributes();
            int offset = vertexAttributes.getOffset(VertexAttributes.Usage.Position);

            int vertexSize = mesh.getVertexSize() / 4;
            int vertCount = mesh.getNumVertices() * mesh.getVertexSize() / 4;

            vertices = new float[vertCount];
            indices = new short[mesh.getNumIndices()];

            mesh.getVertices(vertices);
            mesh.getIndices(indices);
        }

    }

    public void addToScene(SceneManager scene) {
        scene.addScene(boundingBoxScene);
    }

    public void removeFromScene(SceneManager scene) {
        scene.removeScene(boundingBoxScene);
    }

    public String getProperties() {
        return "id: " + id + " path: " + path + " selected: " + selected;
    }

    public Vector3 getPosition(Vector3 out) {
        return model.transform.getTranslation(out);
    }

    public void setPosition(Vector3 position) {
        this.position = position;
        updateModelTransform();
    }

    public void setPosition(float x , float y , float z) {
        setPosition(new Vector3(x , y , z));
    }

    private void updateModelTransform() {
        ensureDataBind();
        if (model != null) {
            scene.modelInstance.transform.idt();
            scene.modelInstance.transform.set(position , new Quaternion().setEulerAngles(rotation.x , rotation.y , rotation.z) , scale);
            transform = scene.modelInstance.transform;
            //            else {
            //                model.transform.idt().scale(scale.x , scale.y , scale.z).mul(transform);
            //            }
        }
    }

    public float getX() {
        return position.x;
    }

    public void setX(float position) {
        this.position.x = position;
        updateModelTransform();
    }

    public float getY() {
        return position.y;
    }

    public void setY(float position) {
        this.position.y = position;
        updateModelTransform();
    }

    public float getZ() {
        return position.z;
    }

    public void setZ(float position) {
        this.position.z = position;
        updateModelTransform();
    }

    public Vector3 getRotation() {
        return rotation;
    }

    public void setRotation(Vector3 rotation) {
        this.rotation = rotation;
        updateModelTransform();
    }

    public void setRotation(float x , float y , float z) {
        setRotation(new Vector3(x , y , z));
    }

    public float getRX() {
        return rotation.x;
    }

    public void setRX(float rotation) {
        this.rotation.x = rotation;
        updateModelTransform();
    }

    public float getRY() {
        return rotation.y;
    }

    public void setRY(float rotation) {
        this.rotation.x = rotation;
        updateModelTransform();
    }

    public float getRZ() {
        return rotation.z;
    }

    public void setRZ(float rotation) {
        this.rotation.x = rotation;
        updateModelTransform();
    }

    public Vector3 getScale() {
        return scale;
    }

    public void setScale(Vector3 scale) {
        this.scale = scale;
        updateModelTransform();
    }

    public void setScale(float x , float y , float z) {
        setScale(new Vector3(x , y , z));
    }

    public void createOutlineModel(){
        Array<Mesh> meshes = new Array<Mesh>();
        Array<Material> materials = new Array<Material>();


        outlinedModelInstance = scene.modelInstance.copy();

        ModelUtils.createOutlineModel(outlinedModelInstance.copy().model, new Color(1,1,1,.1f), .5f);

        ModelUtils.createAlphaAttribute(outlinedModelInstance.model,.5f);




    }

}
