package editor.tools;

import backend.tools.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import core.components.SceneComponent;
import core.systems.GizmoSystem;
import editor.Context;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import ui.UserInterface;
import ui.tools.AbstractTool;
import ui.widgets.RenderWidget;
import util.MaterialUtils;
import utils.ModelUtils;

import static util.StringUtils.trimFloat;
import static util.StringUtils.trimVector3;

public class TranslateTool extends AbstractTool implements InputProcessor
{

    private static final float ARROW_THIKNESS = 0.3f;
    private static final float ARROW_CAP_SIZE = 0.08f;
    private static final int ARROW_DIVISIONS = 12;
    protected static Color COLOR_X = Color.RED;
    protected static Color COLOR_Y = Color.GREEN;
    protected static Color COLOR_Z = Color.BLUE;
    protected static Color COLOR_XZ = Color.CYAN;
    protected static Color COLOR_XYZ = Color.LIGHT_GRAY;
    protected static Color COLOR_SELECTED = Color.YELLOW;
    public boolean selected;
    public boolean hovered = false;
    public boolean dragging;
    public boolean enabled = false;
    public Scene transformReferenceScene;
    public ModelInstance transformReferenceModelInstance;
    public ModelInstance xHandle;
    public ModelInstance yHandle;
    public ModelInstance zHandle;
    public ModelInstance xzPlaneHandle;
    public ModelInstance xHandleOutline;
    public ModelInstance yHandleOutline;
    public ModelInstance zHandleOutline;
    public ModelInstance xzPlaneHandleOutline;
    public BoundingBox xArrowBoundingBox;
    public BoundingBox yArrowBoundingBox;
    public BoundingBox zArrowBoundingBox;
    public BoundingBox xzPlaneBoundingBox;
    public TransformMode mode;
    Model xHandleModel;
    Model yHandleModel;
    Model zHandleModel;
    Model xzPlaneHandleModel;
    public static Vector3 currentPos = new Vector3();
    Vector3 mouseDownPos = new Vector3();

    Vector3 mouseUpPos = new Vector3();
    Vector3 mousePos = new Vector3();
    Vector3 mousePos2 = new Vector3();
    boolean translateX = false;
    boolean translateY = false;
    boolean translateZ = false;
    Plane xPlane, yPlane, zPlane;
    Vector3 endPointX = new Vector3();
    Vector3 endPointY = new Vector3();
    Vector3 endPointZ = new Vector3();
    Vector3 tmp = new Vector3();
    Matrix4 tmpMatrix = new Matrix4();
    Matrix4 tmpMatrix2 = new Matrix4();
    float distanceX, distanceY, distanceZ;
    float deltaX, deltaY, deltaZ;
    ModelBatch batch;
    PerspectiveCamera camera;
    SceneManager sceneManager;
    private Vector3 xPlaneIntersection = new Vector3();
    private Vector3 yPlaneIntersection = new Vector3();
    private Vector3 zPlaneIntersection = new Vector3();
    private Vector3 xPlaneIntersection2 = new Vector3();
    private Vector3 yPlaneIntersection2 = new Vector3();
    private Vector3 zPlaneIntersection2 = new Vector3();
    Ray yPosRay = new Ray();
    Ray zPosRay = new Ray();
    Ray xPosRay = new Ray();
    Ray xNegRay = new Ray();
    Ray yNegRay = new Ray();
    Ray zNegRay = new Ray();
    private Vector3 position = new Vector3();
    private Vector3 intersection = new Vector3();
    private float distanceToOrigin;
    private SceneComponent selectedComponent;
    MousePickingTool picker;

    public TransformState state = TransformState.NONE;
    private final Vector3 temp0 = new Vector3();
    private final Vector3 temp1 = new Vector3();
    private final Matrix4 tempMat0 = new Matrix4();
    private final Vector3 lastPos = new Vector3();
    public boolean initTranslate = true;
    private Ray ray;

    public TranslateTool(SceneManager sceneManager , PerspectiveCamera camera) {
        this.sceneManager = sceneManager;
        this.camera = camera;

        SceneAsset transformReferenceAsset = new GLTFLoader().load(Gdx.files.internal("models/transform_reference.gltf"));
        Model refModel = transformReferenceAsset.scene.model;
        transformReferenceModelInstance = new ModelInstance(refModel);
        transformReferenceScene = new Scene(transformReferenceAsset.scene);

        ModelBuilder modelBuilder = new ModelBuilder();

        xHandleModel = modelBuilder.createArrow(0 , 0 , 0 , 1 , 0 , 0 , ARROW_CAP_SIZE , ARROW_THIKNESS , ARROW_DIVISIONS , GL20.GL_TRIANGLES , new Material(PBRColorAttribute.createEmissive(COLOR_X) , PBRColorAttribute.createSpecular(COLOR_X) , PBRTextureAttribute.createBaseColorTexture(new Texture("dev_mat/RED.png"))) , VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal |VertexAttributes.Usage.TextureCoordinates);
        yHandleModel = modelBuilder.createArrow(0 , 0 , 0 , 0 , 1 , 0 , ARROW_CAP_SIZE , ARROW_THIKNESS , ARROW_DIVISIONS , GL20.GL_TRIANGLES , new Material(PBRColorAttribute.createEmissive(COLOR_Y) , PBRColorAttribute.createSpecular(COLOR_Y) , PBRTextureAttribute.createBaseColorTexture(new Texture("dev_mat/GREEN.png"))) , VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal|VertexAttributes.Usage.TextureCoordinates);
        zHandleModel = modelBuilder.createArrow(0 , 0 , 0 , 0 , 0 , 1 , ARROW_CAP_SIZE , ARROW_THIKNESS , ARROW_DIVISIONS , GL20.GL_TRIANGLES , new Material(PBRColorAttribute.createEmissive(COLOR_Z) , PBRColorAttribute.createSpecular(COLOR_Z) , PBRTextureAttribute.createBaseColorTexture(new Texture("dev_mat/BLUE.png"))) , VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal|VertexAttributes.Usage.TextureCoordinates);

        xzPlaneHandleModel = modelBuilder.createSphere(.25f , .25f , .25f , 20 , 20 , new Material(PBRTextureAttribute.createBaseColorTexture(new Texture("dev_mat/CYAN.png")),PBRColorAttribute.createSpecular(COLOR_XZ),PBRColorAttribute.createEmissive(COLOR_XZ)) , VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal|VertexAttributes.Usage.TextureCoordinates);
        xzPlaneHandle = new ModelInstance(xzPlaneHandleModel);
        xHandle = new ModelInstance(xHandleModel);
        yHandle = new ModelInstance(yHandleModel);
        zHandle = new ModelInstance(zHandleModel);
        MaterialUtils.replaceTexture(xHandle , "dev_mat/RED.png");
        MaterialUtils.replaceTexture(yHandle , "dev_mat/GREEN.png");
        MaterialUtils.replaceTexture(zHandle , "dev_mat/BLUE.png");
        MaterialUtils.replaceTexture(xzPlaneHandle , "dev_mat/CYAN.png");


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

//        xHandle.materials.get(0).clear();
//        yHandle.materials.get(0).clear();
//        zHandle.materials.get(0).clear();

        xHandle.materials.add(MaterialUtils.createGenericBDSFMateral(Color.RED));
        yHandle.materials.add(MaterialUtils.createGenericBDSFMateral(Color.GREEN));
        zHandle.materials.add(MaterialUtils.createGenericBDSFMateral(Color.BLUE));
        xzPlaneHandle.materials.add(MaterialUtils.createGenericBDSFMateral(Color.CYAN));

        float alpha =1;
//        xHandle.materials.get(0).set(PBRColorAttribute.createBaseColorFactor(Color.RED));
//        yHandle.materials.get(0).set(PBRColorAttribute.createBaseColorFactor(Color.BLUE));
//        zHandle.materials.get(0).set(PBRColorAttribute.createBaseColorFactor(Color.GREEN));
//        xzPlaneHandle.materials.get(0).set(PBRColorAttribute.createBaseColorFactor(Color.CYAN));


        batch = new ModelBatch(new PBRShaderProvider(new PBRShaderConfig()));

    }

    public void setSelectedComponent(SceneComponent selectedComponent) {
        this.selectedComponent = selectedComponent;
        Vector3 boundingBoxCorner1 = new Vector3();
        Vector3 componentOffset = new Vector3();
        Matrix4 translationMatrix = new Matrix4();
        selectedComponent.update();
        selectedComponent.boundingBox.getCorner000(boundingBoxCorner1);
        boundingBoxCorner1.x -= .1f;
        boundingBoxCorner1.y += .2f;
        boundingBoxCorner1.z -= .1f;
        currentPos.set(selectedComponent.center);

        translationMatrix.set(selectedComponent.scene.modelInstance.transform);


        //        selectedComponent.boundingBoxScene.modelInstance.transform.set(translationMatrix);

    }

    public SceneComponent getSelectedComponent() {
        return this.selectedComponent;
    }

    public void deselectComponent() {
        this.selectedComponent = null;
    }

    InputMultiplexer inputMultiplexer = new InputMultiplexer();
    {

        inputMultiplexer.addProcessor(this);
        inputMultiplexer.addProcessor(Context.getInstance().stage);
        inputMultiplexer.addProcessor(Context.getInstance().cameraController);
    }

    @Override
    public void enable() {
        Gdx.input.setInputProcessor(inputMultiplexer);
        Context.getInstance().inputMultiplexer.addProcessor(this);
        //Gdx.input.setInputProcessor(this);
        //RenderWidget.renderWidgetMultiplexer.addProcessor(this);
        update();

        sceneManager.getRenderableProviders().add(xHandle);
        sceneManager.getRenderableProviders().add(yHandle);
        sceneManager.getRenderableProviders().add(zHandle);
        sceneManager.getRenderableProviders().add(xzPlaneHandle);

        batch = new ModelBatch();

    }

    {
        xArrowBoundingBox = new BoundingBox();
        yArrowBoundingBox = new BoundingBox();
        zArrowBoundingBox = new BoundingBox();
        xzPlaneBoundingBox = new BoundingBox();
    }

    public void update() {
        double dst;
        scaleHandle(1);
        if (selectedComponent!=null) {
            dst = selectedComponent.center.dst(Context.getInstance().camera.position);
            dst = Math.sqrt(dst);

            xHandle.transform.setTranslation(selectedComponent.center);
            yHandle.transform.setTranslation(selectedComponent.center);
            zHandle.transform.setTranslation(selectedComponent.center);
            xzPlaneHandle.transform.setTranslation(selectedComponent.center);
            xHandle.transform.scl((float) dst);
            yHandle.transform.scl((float) dst);
            zHandle.transform.scl((float) dst);
            xzPlaneHandle.transform.scl((float) dst / 2);

            xArrowBoundingBox = new BoundingBox();
            yArrowBoundingBox = new BoundingBox();
            zArrowBoundingBox = new BoundingBox();
            xzPlaneBoundingBox = new BoundingBox();

            xHandle.calculateBoundingBox(xArrowBoundingBox);
            yHandle.calculateBoundingBox(yArrowBoundingBox);
            zHandle.calculateBoundingBox(zArrowBoundingBox);
            xzPlaneHandle.calculateBoundingBox(xzPlaneBoundingBox);

            xArrowBoundingBox.mul(xHandle.transform);
            yArrowBoundingBox.mul(yHandle.transform);
            zArrowBoundingBox.mul(zHandle.transform);
            xzPlaneBoundingBox.mul(xzPlaneHandle.transform);

            xHandleOutline.transform.set(xHandle.transform);
            yHandleOutline.transform.set(yHandle.transform);
            zHandleOutline.transform.set(zHandle.transform);
            xzPlaneHandleOutline.transform.set(xzPlaneHandle.transform);

            //        transformReferenceModelInstance.transform.setToTranslation(selectedComponent.center);
            //        transformReferenceModelInstance.transform.scl(.5f);

            xPosRay.origin.set(selectedComponent.center);
            xPosRay.direction.set(1 , 0 , 0);
            yPosRay.origin.set(selectedComponent.center);
            yPosRay.direction.set(0 , 1 , 0);
            zPosRay.origin.set(selectedComponent.center);
            zPosRay.direction.set(0 , 0 , 1);
            xNegRay.origin.set(selectedComponent.center);
            xNegRay.direction.set(-1 , 0 , 0);
            yNegRay.origin.set(selectedComponent.center);
            yNegRay.direction.set(0 , -1 , 0);
            zNegRay.origin.set(selectedComponent.center);
            zNegRay.direction.set(0 , 0 , -1);
        }
    }

    @Override
    public void disable() {
        Gdx.input.setInputProcessor(Context.getInstance().inputMultiplexer);
        Context.getInstance().inputMultiplexer.removeProcessor(this);
        //RenderWidget.renderWidgetMultiplexer.removeProcessor(this);
        state = TransformState.NONE;
        sceneManager.getRenderableProviders().removeValue(xHandle , true);
        sceneManager.getRenderableProviders().removeValue(yHandle , true);
        sceneManager.getRenderableProviders().removeValue(zHandle , true);
        sceneManager.getRenderableProviders().removeValue(xzPlaneHandle , true);
        batch.dispose();

    }

    public void render(float delta) {

        if (GizmoSystem.translateToolEnabled) {
            batch.begin(sceneManager.camera);
            Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
            //batch.render(transformReferenceModelInstance , sceneManager.environment);

            batch.render(xHandle , sceneManager.environment);
            batch.render(yHandle , sceneManager.environment);
            batch.render(zHandle , sceneManager.environment);
            batch.render(xzPlaneHandle , sceneManager.environment);

            if (GizmoSystem.translateX) {
//                batch.render(xHandleOutline , sceneManager.environment);
//                batch.render(xzPlaneHandleOutline , sceneManager.environment);

            }
            if (GizmoSystem.translateY) {
//                batch.render(yHandleOutline , sceneManager.environment);
//                batch.render(xzPlaneHandleOutline , sceneManager.environment);
            }
            if (GizmoSystem.translateZ) {
//                batch.render(zHandleOutline , sceneManager.environment);
//                batch.render(xzPlaneHandleOutline , sceneManager.environment);

            }
            batch.end();
        }

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
        clearTmp();
        if (selectedComponent == null) {
            return false;
        }

        mouseDownPos.set(screenX , screenY , 0);
        camera.unproject(mouseDownPos);

        selectedComponent.scene.modelInstance.transform.getTranslation(currentPos);

        tmp = new Vector3();
        tmp = currentPos.cpy();
        tmpMatrix = new Matrix4();
        tmpMatrix.set(selectedComponent.scene.modelInstance.transform);
        tmpMatrix2.set(selectedComponent.scene.modelInstance.transform);
        tmpMatrix.setTranslation(tmp);

        ray = MousePickingTool.getMousePicker().ray;

        distanceX = -ray.origin.x / ray.direction.x;
        distanceY = -ray.origin.y / ray.direction.y;
        distanceZ = -ray.origin.z / ray.direction.z;

        distanceToOrigin = currentPos.dst(Vector3.Zero);

        return false;
    }

    private void clearTmp() {
        xPlaneIntersection.setZero();
        yPlaneIntersection.setZero();
        zPlaneIntersection.setZero();
        xPlaneIntersection2.setZero();
        yPlaneIntersection2.setZero();
        zPlaneIntersection2.setZero();
        tmp.setZero();
        tmpMatrix.idt();
    }

    @Override
    public boolean touchUp(int screenX , int screenY , int pointer , int button) {
        if (selectedComponent == null) {
            return false;
        }
        mouseUpPos.set(screenX , screenY , 0);
        camera.unproject(mouseUpPos);

        selectedComponent.scene.modelInstance.transform.getTranslation(currentPos);

        tmp = new Vector3();
        tmp = currentPos.cpy();
        tmpMatrix = new Matrix4();
        tmpMatrix.set(selectedComponent.scene.modelInstance.transform);
        tmpMatrix2.set(selectedComponent.scene.modelInstance.transform);
        tmpMatrix.setTranslation(tmp);
        //selectedComponent.scene.modelInstance.transform.setTranslation(tmp);

        state = TransformState.NONE;

        clearTmp();

        tmp.setZero();
        return false;
    }

    @Override
    public boolean touchDragged(int screenX , int screenY , int pointer) {
        if (selectedComponent == null) {
            return false;
        }

        System.out.println("touchdragged");
        translateX = false;
        translateY = false;
        translateZ = false;
        mousePos = new Vector3(screenX , screenY , 0);
        camera.unproject(mousePos);

        Ray ray = MousePickingTool.getMousePicker().ray;
        Vector3 rayEnd = selectedComponent.model.transform.getTranslation(temp0);
        float dst = camera.position.dst(rayEnd);

        rayEnd = ray.getEndPoint(rayEnd , dst);
        selectedComponent.scene.modelInstance.transform.getTranslation(currentPos);
        //currentPos.nor();
        // rayEnd.sub(currentPos);

        if (initTranslate) {
            initTranslate = false;
            lastPos.set(rayEnd);
        }

        boolean modified = false;
        Vector3 vec = new Vector3();
        if (state == TransformState.TRANSLATE_X) {
            vec.set(rayEnd.x - lastPos.x , 0 , 0);
              Log.info("TranslateX" , "RayEnd: " + rayEnd.toString() + " LastPos: " + lastPos.toString() + " Vec: " + vec.toString());
            modified = true;
        }
        else if (state == TransformState.TRANSLATE_Y) {
            vec.set(0 , rayEnd.y - lastPos.y , 0);
            Log.info("TranslateY" , "RayEnd: " + rayEnd.toString() + " LastPos: " + lastPos.toString() + " Vec: " + vec.toString());
            modified = true;
        }
        else if (state == TransformState.TRANSLATE_Z) {

            vec.set(0 , 0 , rayEnd.z - lastPos.z);
            Log.info("TranslateZ" , "RayEnd: " + rayEnd.toString() + " LastPos: " + lastPos.toString() + " Vec: " + vec.toString());
            modified = true;
        }

        if (modified) {
            selectedComponent.scene.modelInstance.transform.trn(vec);
        selectedComponent.translate(vec);
        currentPos.add(vec);


        }

        lastPos.set(rayEnd);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX , int screenY) {
        if (selectedComponent == null) {
            return false;
        }

        hovered = false;
        mousePos2 = new Vector3(screenX , screenY , 1);
        camera.unproject(mousePos2);
        Ray ray = RenderWidget.viewport.getPickRay(screenX , screenY);
        final float distanceY = -ray.origin.y / ray.direction.y;
        final float distanceX = -ray.origin.x / ray.direction.x;
        final float distanceZ = -ray.origin.z / ray.direction.z;
        endPointY = new Vector3();
        endPointX = new Vector3();
        endPointZ = new Vector3();

        ray.getEndPoint(endPointY , distanceY);
        ray.getEndPoint(endPointX , distanceX);
        ray.getEndPoint(endPointZ , distanceZ);
        xPlane = new Plane(new Vector3(0 , 1 , 0) , distanceToOrigin);
        yPlane = new Plane(new Vector3(1 , 0 , 0) , distanceToOrigin);
        zPlane = new Plane(new Vector3(0 , 0 , 1) , distanceToOrigin);

        if (Intersector.intersectRayBounds(ray , xArrowBoundingBox , intersection)) {
            hovered = true;
        }
        else if (Intersector.intersectRayBounds(ray , yArrowBoundingBox , intersection)) {
            hovered = true;
        }
        else if (Intersector.intersectRayBounds(ray , zArrowBoundingBox , intersection)) {
            hovered = true;
        }
        else if (Intersector.intersectRayBounds(ray , xzPlaneBoundingBox , intersection)) {
            hovered = true;
        }
        if (Intersector.intersectRayPlane(ray , xPlane , xPlaneIntersection))
            UserInterface.debugLabel1.setText("xPlaneIntersection: " + trimVector3(xPlaneIntersection));
        if (Intersector.intersectRayPlane(ray , yPlane , yPlaneIntersection))
            UserInterface.debugLabel2.setText("yPlaneIntersection: " + trimVector3(yPlaneIntersection));
        if (Intersector.intersectRayPlane(ray , zPlane , zPlaneIntersection))
            UserInterface.debugLabel3.setText("zPlaneIntersection: " + trimVector3(zPlaneIntersection));
        UserInterface.debugLabel8.setText("EndPointX: " + trimVector3(endPointX) + "| EndPointY: " + trimVector3(endPointY) + "| EndPointZ: " + trimVector3(endPointZ));
        UserInterface.debugLabel9.setText("WorldX: " + trimFloat(endPointY.x) + "\nWorldY: " + trimFloat(endPointZ.y) + "\nWorldZ: " + trimFloat(endPointX.z));
        UserInterface.debugLabel10.setText("RayIntersection: " + trimVector3(intersection));

        return false;
    }

    @Override
    public boolean scrolled(float amountX , float amountY) {
        return false;
    }

    public void dispose() {
        xHandleModel.dispose();
        yHandleModel.dispose();
        zHandleModel.dispose();

        xzPlaneHandleModel.dispose();
        batch.dispose();
        transformReferenceModelInstance.model.dispose();

    }

    public void scaleHandle(float scale) {
        xHandle.transform.setToScaling(scale , scale , scale);
        yHandle.transform.setToScaling(scale , scale , scale);
        zHandle.transform.setToScaling(scale , scale , scale);
        xzPlaneHandle.transform.setToScaling(scale , scale , scale);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public void setWidgetMode(TransformMode mode) {
        this.mode = mode;
    }

    public enum TransformMode
    {
        TRANSLATE, ROTATE, SCALE
    }

    public enum TransformState
    {
        TRANSLATE_X, TRANSLATE_Y, TRANSLATE_Z, NONE
    }

}
