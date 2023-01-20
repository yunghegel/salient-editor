package tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import editor.tools.BasePickerTool;
import editor.tools.MultiplexedTool;
import util.GeometryUtils;
import util.MaterialUtils;
import utils.ModelUtils;

import java.util.ArrayList;
import java.util.List;

public class TranslateTool extends MultiplexedTool
{private ShapeRenderer shapeRenderer = new ShapeRenderer();
    protected static Color COLOR_X = Color.RED;
    protected static Color COLOR_Y = Color.GREEN;
    protected static Color COLOR_Z = Color.BLUE;
    protected static Color COLOR_XZ = Color.CYAN;
    private static final float ARROW_THIKNESS = 0.4f;
    private static final float ARROW_CAP_SIZE = 0.15f;
    private static final int ARROW_DIVISIONS = 12;
    Model xHandleModel;
    Model yHandleModel;
    Model zHandleModel;
    Model xzPlaneHandleModel;
    public ModelInstance xHandle;
    public ModelInstance yHandle;
    public ModelInstance zHandle;
    public ModelInstance xzPlaneHandle;
    public ModelInstance xHandleOutline;
    public ModelInstance yHandleOutline;
    public ModelInstance zHandleOutline;
    public ModelInstance xzPlaneHandleOutline;
    public ModelBatch batch;
    public BoundingBox xArrowBoundingBox=new BoundingBox();
    public BoundingBox yArrowBoundingBox=new BoundingBox();
    public BoundingBox zArrowBoundingBox=new BoundingBox();
    public BoundingBox xzPlaneBoundingBox=new BoundingBox();
    Vector3 xArrowIntersection = new Vector3();
    Vector3 yArrowIntersection = new Vector3();
    Vector3 zArrowIntersection = new Vector3();
    boolean xArrowHovered = false;
    boolean yArrowHovered = false;
    boolean zArrowHovered = false;

    SceneManager  sceneManager;
    Camera camera;
    Ray ray;
    Vector3 triangleIntersection = new Vector3();

    boolean xSelected = false;
    boolean ySelected = false;
    boolean zSelected = false;
    boolean objectSelected=false;


    public TranslateTool(InputMultiplexer inputMultiplexer, Camera camera, SceneManager sceneManager)
    {
        super(inputMultiplexer);
        this.camera = camera;
        this.sceneManager = sceneManager;
        createModels();
        inputMultiplexer.addProcessor(this);
    }

    public void createModels(){
        ModelBuilder modelBuilder = new ModelBuilder();

        xHandleModel = modelBuilder.createArrow(0 , 0 , 0 , 1 , 0 , 0 , ARROW_CAP_SIZE , ARROW_THIKNESS , ARROW_DIVISIONS , GL20.GL_TRIANGLES , new Material(PBRColorAttribute.createEmissive(COLOR_X) , PBRColorAttribute.createSpecular(COLOR_X) , PBRTextureAttribute.createBaseColorTexture(new Texture("dev_mat/RED.png"))) , VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked);
        yHandleModel = modelBuilder.createArrow(0 , 0 , 0 , 0 , 1 , 0 , ARROW_CAP_SIZE , ARROW_THIKNESS , ARROW_DIVISIONS , GL20.GL_TRIANGLES , new Material(PBRColorAttribute.createBaseColorFactor(COLOR_Y) , PBRColorAttribute.createEmissive(COLOR_Y) , PBRColorAttribute.createSpecular(COLOR_Y) , PBRTextureAttribute.createBaseColorTexture(new Texture("dev_mat/GREEN.png"))) , VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        zHandleModel = modelBuilder.createArrow(0 , 0 , 0 , 0 , 0 , 1 , ARROW_CAP_SIZE , ARROW_THIKNESS , ARROW_DIVISIONS , GL20.GL_TRIANGLES , new Material(PBRColorAttribute.createBaseColorFactor(COLOR_Z) , PBRColorAttribute.createEmissive(COLOR_Z) , PBRColorAttribute.createSpecular(COLOR_Z) , PBRTextureAttribute.createBaseColorTexture(new Texture("dev_mat/BLUE.png"))) , VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        xzPlaneHandleModel = modelBuilder.createSphere(.25f , .25f , .25f , 20 , 20 , new Material(PBRColorAttribute.createBaseColorFactor(COLOR_XZ) , PBRTextureAttribute.createBaseColorTexture(new Texture("dev_mat/CYAN.png"))) , VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        xHandle = new ModelInstance(xHandleModel);
        yHandle = new ModelInstance(yHandleModel);
        zHandle = new ModelInstance(zHandleModel);
        MaterialUtils.replaceTexture(xHandle , "dev_mat/RED.png");

        xzPlaneHandle = new ModelInstance(xzPlaneHandleModel);
        Model xHandleCopy;
        Model yHandleCopy;
        Model zHandleCopy;
        Model xzPlaneHandleCopy;
        xHandleCopy = modelBuilder.createArrow(0 , 0 , 0 , 1 , 0 , 0 , ARROW_CAP_SIZE , ARROW_THIKNESS , ARROW_DIVISIONS , GL20.GL_TRIANGLES , new Material(PBRColorAttribute.createBaseColorFactor(Color.WHITE) , PBRColorAttribute.createEmissive(Color.WHITE) , PBRColorAttribute.createSpecular(Color.WHITE)) , VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked);
        yHandleCopy = modelBuilder.createArrow(0 , 0 , 0 , 0 , 1 , 0 , ARROW_CAP_SIZE , ARROW_THIKNESS , ARROW_DIVISIONS , GL20.GL_TRIANGLES , new Material(PBRColorAttribute.createBaseColorFactor(Color.WHITE) , PBRColorAttribute.createEmissive(Color.WHITE) , PBRColorAttribute.createSpecular(Color.WHITE)) , VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        zHandleCopy = modelBuilder.createArrow(0 , 0 , 0 , 0 , 0 , 1 , ARROW_CAP_SIZE , ARROW_THIKNESS , ARROW_DIVISIONS , GL20.GL_TRIANGLES , new Material(PBRColorAttribute.createBaseColorFactor(Color.WHITE) , PBRColorAttribute.createEmissive(COLOR_Z) , PBRColorAttribute.createSpecular(COLOR_Z)) , VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        xzPlaneHandleCopy = modelBuilder.createSphere(.25f , .25f , .25f , 20 , 20 , new Material(PBRColorAttribute.createBaseColorFactor(COLOR_XZ)) , VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ModelUtils.createOutlineModel(xHandleCopy , Color.WHITE , 0.02f);
        ModelUtils.createOutlineModel(yHandleCopy , Color.WHITE , 0.02f);
        ModelUtils.createOutlineModel(zHandleCopy , Color.WHITE , 0.02f);
        ModelUtils.createOutlineModel(xzPlaneHandleCopy , Color.WHITE , 0.02f);

        xHandleOutline = new ModelInstance(xHandleCopy);
        yHandleOutline = new ModelInstance(yHandleCopy);
        zHandleOutline = new ModelInstance(zHandleCopy);
        xzPlaneHandleOutline = new ModelInstance(xzPlaneHandleCopy);

        batch = new ModelBatch();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX , int screenY , int pointer , int button) {
        List<Vector3> triangles = new ArrayList<>();
        for (GizmoTest.GameObject gameObject:GizmoTest.gameObjects){


        }

        if (xArrowHovered){
            xSelected=true;
        }
        if (yArrowHovered){
            ySelected=true;
        }
        if (zArrowHovered){
            zSelected=true;
        }
        else {
            xSelected=false;
            ySelected=false;
            zSelected=false;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX , int screenY , int pointer , int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX , int screenY , int pointer) {

        //if (Intersector.intersectRayTriangles(ray,))

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX , int screenY) {
        ray = BasePickerTool.getInstance().ray;
        List<Vector3> triangles = new ArrayList<>();
        if (Intersector.intersectRayBounds(ray,xArrowBoundingBox,xArrowIntersection)) {
            renderBoundingBox(xArrowBoundingBox , camera);
            xArrowHovered=true;
            xSelected=true;
        }
        else xArrowHovered = false;
        if (Intersector.intersectRayBounds(ray,yArrowBoundingBox,yArrowIntersection)){
            yArrowHovered = true;
            ySelected=true;}
        else yArrowHovered = false;
        zArrowHovered = Intersector.intersectRayBounds(ray , zArrowBoundingBox , zArrowIntersection);

        for (GizmoTest.GameObject gameObject:GizmoTest.gameObjects){



        }
        triangles.clear();

        return false;
    }

    @Override
    public boolean scrolled(float amountX , float amountY) {
        return false;
    }

    @Override
    public void update() {
        xArrowBoundingBox.mul(xHandle.transform);
        yArrowBoundingBox.mul(yHandle.transform);
        zArrowBoundingBox.mul(zHandle.transform);
        xHandleOutline.transform.set(xHandle.transform);
        yHandleOutline.transform.set(yHandle.transform);
        zHandleOutline.transform.set(zHandle.transform);
        renderBoundingBox(xArrowBoundingBox,camera);
        xzPlaneHandleOutline.transform.set(xzPlaneHandle.transform);
        xzPlaneBoundingBox.mul(xzPlaneHandle.transform);

    }

    public void renderHandles(){
        for (GizmoTest.GameObject gameObject: GizmoTest.gameObjects)
            if (gameObject.selected){
                setHandlesPosition(gameObject);
                batch.begin(sceneManager.camera);

                Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);



            batch.render(xHandle);
            batch.render(yHandle);
            batch.render(zHandle);
            batch.render(xzPlaneHandle);


        if (xSelected) {
            batch.render(xHandleOutline);
            batch.render(xzPlaneHandleOutline , sceneManager.environment);

        }
        if (ySelected) {
            batch.render(yHandleOutline , sceneManager.environment);
            batch.render(xzPlaneHandleOutline , sceneManager.environment);
        }
        if (zSelected) {
            batch.render(zHandleOutline , sceneManager.environment);
            batch.render(xzPlaneHandleOutline , sceneManager.environment);
        }
        batch.end();
    }}

    public void setHandlesPosition(GizmoTest.GameObject gameObject){
       Vector3 center = new Vector3();
        gameObject.boundingBox.getCenter(center);

        xHandle.transform.setTranslation(center);
        yHandle.transform.setTranslation(center);
        zHandle.transform.setTranslation(center);

        xzPlaneHandle.transform.setTranslation(center);

    }

    public static List<Vector3> getModelTriangles(Model model){
        List<Vector3> triangles = new ArrayList<Vector3>();
        for (MeshPart meshPart : model.meshParts) {
            int numVertices = meshPart.size;
            int numIndices = GeometryUtils.getIndicesCount(model);
            int numTriangles = numIndices / 3;
            int numVerticesPerTriangle = 3;
            int numFloatsPerVertex = 3;
            int numFloatsPerTriangle = numFloatsPerVertex * numVerticesPerTriangle;
            int numFloats = numFloatsPerTriangle * numTriangles;
            float[] vertices = new float[numFloats];
            meshPart.mesh.getVertices(vertices);
            for (int i = 0; i < numFloats; i += numFloatsPerTriangle) {
                triangles.add(new Vector3(vertices[i] , vertices[i + 1] , vertices[i + 2]));
            }
        }
        System.out.println(triangles.size());

        return  triangles;
    }

    public static void renderIntersection(Vector3 intersection,Camera camera){
        ShapeRenderer shapeRenderer = new ShapeRenderer();
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Point);
//        shapeRenderer.setProjectionMatrix(camera.combined);
//        shapeRenderer.setColor(Color.RED);
//        shapeRenderer.point(intersection.x , intersection.y , intersection.z);
   //    shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.line(Vector3.Zero , intersection);
        shapeRenderer.end();
    }


    public static void renderBoundingBox(BoundingBox boundingBox,Camera camera){
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        Vector3 center = new Vector3();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.box(boundingBox.min.x , boundingBox.min.y , boundingBox.min.z + boundingBox.getDepth() , boundingBox.getWidth() , boundingBox.getHeight() , boundingBox.getDepth());
        shapeRenderer.end();
    }

}



