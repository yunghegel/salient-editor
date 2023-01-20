package editor.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import editor.Context;
import core.components.SceneComponent;
import core.systems.GizmoSystem;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import org.lwjgl.opengl.GL40;
import ui.tools.AbstractTool;
import util.ModelUtils;

public class ScaleTool extends AbstractTool implements InputProcessor
{
    protected static Color COLOR_X = Color.RED;
    protected static Color COLOR_Y = Color.GREEN;
    protected static Color COLOR_Z = Color.BLUE;
    protected static Color COLOR_XYZ = Color.CYAN;

    public enum ScaleState
    {
        NONE,
        SCALE_X,
        SCALE_Y,
        SCALE_Z,
        SCALE_XYZ
    }

    private final Vector3 temp0 = new Vector3();
    private final Vector3 temp1 = new Vector3();
    private final Vector3 tempScale = new Vector3();
    private final Vector3 tempScaleDst = new Vector3();

    public ScaleState scaleState = ScaleState.NONE;

    private ModelInstance xHandle;
    private ModelInstance yHandle;
    private ModelInstance zHandle;
    private ModelInstance xyzHandle;

    public ModelInstance xHandleOutline;
    public ModelInstance yHandleOutline;
    public ModelInstance zHandleOutline;
    public ModelInstance xyzPlaneHandleOutline;

    public BoundingBox xHandleBoundingBox;
    public BoundingBox yHandleBoundingBox;
    public BoundingBox zHandleBoundingBox;
    public BoundingBox xyzHandleBoundingBox;

    public ModelBatch batch;
    public PerspectiveCamera camera;
    public SceneManager sceneManager;

    public SceneComponent getSelectedComponent() {
        return selectedComponent;
    }

    private SceneComponent selectedComponent;

    public Vector3 currentPos = new Vector3();
    public Vector3 lastPos = new Vector3();
    public boolean initScale = true;
    public boolean enabled = false;



    public ScaleTool(SceneManager sceneManager , PerspectiveCamera camera)
    {
        this.sceneManager = sceneManager;
        this.camera = camera;
        batch = new ModelBatch();

        ModelBuilder modelBuilder = new ModelBuilder();

        Material matX = new Material(PBRColorAttribute.createEmissive(COLOR_X) , PBRColorAttribute.createSpecular(COLOR_X) , PBRTextureAttribute.createBaseColorTexture(new Texture("dev_mat/RED.png")));
        Material matY = new Material(PBRColorAttribute.createEmissive(COLOR_Y) , PBRColorAttribute.createSpecular(COLOR_Y) , PBRTextureAttribute.createBaseColorTexture(new Texture("dev_mat/GREEN.png")));
        Material matZ = new Material(PBRColorAttribute.createEmissive(COLOR_Z) , PBRColorAttribute.createSpecular(COLOR_Z) , PBRTextureAttribute.createBaseColorTexture(new Texture("dev_mat/BLUE.png")));
        Material matXYZ = new Material(PBRColorAttribute.createEmissive(COLOR_XYZ) , PBRColorAttribute.createSpecular(COLOR_XYZ) , PBRTextureAttribute.createBaseColorTexture(new Texture("dev_mat/CYAN.png")));

        Model xPlaneHandleModel = ModelUtils.createArrowStub(matX,
                                                             Vector3.Zero, new Vector3(1, 0, 0));
        Model yPlaneHandleModel = ModelUtils.createArrowStub(matY,
                                                              Vector3.Zero, new Vector3(0, 1, 0));
        Model zPlaneHandleModel = ModelUtils.createArrowStub(matZ,
                                                              Vector3.Zero, new Vector3(0, 0, 1));
        Model xyzPlaneHandleModel = modelBuilder.createBox(0.16f, 0.16f, 0.16f,
                                                           matXYZ,
                                                           VertexAttributes.Usage.Position|VertexAttributes.Usage.Normal);
        xHandle = new ModelInstance(xPlaneHandleModel);
        yHandle = new ModelInstance(yPlaneHandleModel);
        zHandle = new ModelInstance(zPlaneHandleModel);
        xyzHandle = new ModelInstance(xyzPlaneHandleModel);
        Model xHandleCopy;
        Model yHandleCopy;
        Model zHandleCopy;
        Model xyzHandleCopy;

        xHandleCopy = ModelUtils.createArrowStub(matX,
                                                 Vector3.Zero, new Vector3(1, 0, 0));
        yHandleCopy = ModelUtils.createArrowStub(matY,
                                                 Vector3.Zero, new Vector3(0, 1, 0));
        zHandleCopy = ModelUtils.createArrowStub(matZ,
                                                 Vector3.Zero, new Vector3(0, 0, 1));
        xyzHandleCopy = modelBuilder.createBox(0.16f, 0.16f, 0.16f,
                                               matXYZ,
                                               VertexAttributes.Usage.Position|VertexAttributes.Usage.Normal);

//        utils.ModelUtils.createOutlineModel(xHandleCopy , Color.WHITE , 0.02f);
//        utils.ModelUtils.createOutlineModel(yHandleCopy , Color.WHITE , 0.02f);
//        utils.ModelUtils.createOutlineModel(zHandleCopy , Color.WHITE , 0.02f);
//        utils.ModelUtils.createOutlineModel(xyzHandleCopy , Color.WHITE , 0.02f);

        //scale outline
        float alpha = 0.95f;
        ModelUtils.createAlphaAttribute(xHandleCopy, alpha);
        ModelUtils.createAlphaAttribute(yHandleCopy, alpha);
        ModelUtils.createAlphaAttribute(zHandleCopy, alpha);
        ModelUtils.createAlphaAttribute(xyzHandleCopy, alpha);

        float scale = 1.1f;

        xHandleOutline = new ModelInstance(xHandleCopy);
        yHandleOutline = new ModelInstance(yHandleCopy);
        zHandleOutline = new ModelInstance(zHandleCopy);
        xyzPlaneHandleOutline = new ModelInstance(xyzHandleCopy);

        xHandleOutline.transform.scale(scale, scale, scale);
        yHandleOutline.transform.scale(scale, scale, scale);
        zHandleOutline.transform.scale(scale, scale, scale);
        xyzPlaneHandleOutline.transform.scale(scale, scale, scale);
    }

    public void setSelectedComponent(SceneComponent sceneComponent)
    {
        this.selectedComponent = sceneComponent;
    }

    public void update(){
        double dst;
        scaleHandle(1);
        dst = selectedComponent.center.dst(Context.getInstance().camera.position);
        dst = Math.sqrt(dst);

        xHandle.transform.setTranslation(selectedComponent.center);
        yHandle.transform.setTranslation(selectedComponent.center);
        zHandle.transform.setTranslation(selectedComponent.center);
        xyzHandle.transform.setTranslation(selectedComponent.center);
        xHandle.transform.scl((float) dst);
        yHandle.transform.scl((float) dst);
        zHandle.transform.scl((float) dst);
        xyzHandle.transform.scl((float) dst);

        xHandleBoundingBox = new BoundingBox();
        yHandleBoundingBox = new BoundingBox();
        zHandleBoundingBox = new BoundingBox();
        xyzHandleBoundingBox = new BoundingBox();

        xHandle.calculateBoundingBox(xHandleBoundingBox);
        yHandle.calculateBoundingBox(yHandleBoundingBox);
        zHandle.calculateBoundingBox(zHandleBoundingBox);
        xyzHandle.calculateBoundingBox(xyzHandleBoundingBox);

        xHandleBoundingBox.mul(xHandle.transform);
        yHandleBoundingBox.mul(yHandle.transform);
        zHandleBoundingBox.mul(zHandle.transform);
        xyzHandleBoundingBox.mul(xyzHandle.transform);

        xHandleOutline.transform.set(xHandle.transform);
        yHandleOutline.transform.set(yHandle.transform);
        zHandleOutline.transform.set(zHandle.transform);
        xyzPlaneHandleOutline.transform.set(xyzHandle.transform);
        xHandleOutline.transform.scale(1.005f, 1.5f, 1.5f);
        yHandleOutline.transform.scale(1.5f, 1.005f, 1.5f);
        zHandleOutline.transform.scale(1.5f, 1.5f, 1.005f);
        xyzPlaneHandleOutline.transform.scale(1.1f, 1.1f, 1.1f);

        xyzPlaneHandleOutline.transform.set(xyzHandle.transform);

    }

    public void render(){
        batch.begin(sceneManager.camera);
        if (GizmoSystem.scaleToolEnabled)
        {
            Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
            batch.render(xHandle, sceneManager.environment);
            batch.render(yHandle, sceneManager.environment);
            batch.render(zHandle, sceneManager.environment);
            batch.render(xyzHandle, sceneManager.environment);
        }
        if (scaleState== ScaleState.SCALE_X)
        {
            //cull front faces
//            GL40.glEnable(GL40.GL_CULL_FACE);
//            GL40.glCullFace(GL40.GL_FRONT);

            batch.render(xHandleOutline, sceneManager.environment);
        }
        else if (scaleState== ScaleState.SCALE_Y)
        {
            //cull front faces
//            GL40.glEnable(GL40.GL_CULL_FACE);
//            GL40.glCullFace(GL40.GL_FRONT);

            batch.render(yHandleOutline, sceneManager.environment);
        }
        else if (scaleState== ScaleState.SCALE_Z)
        {
            //cull front faces
//            GL40.glEnable(GL40.GL_CULL_FACE);
//            GL40.glCullFace(GL40.GL_FRONT);
            batch.render(zHandleOutline, sceneManager.environment);
        }
        else if (scaleState== ScaleState.SCALE_XYZ)
        {
            //cull front faces
//            GL40.glEnable(GL40.GL_CULL_FACE);
//            GL40.glCullFace(GL40.GL_FRONT);
            batch.render(xyzPlaneHandleOutline, sceneManager.environment);
        }
        //disable culling
        GL40.glCullFace(GL40.GL_BACK);


        batch.end();
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
        return false;
    }

    @Override
    public boolean touchUp(int screenX , int screenY , int pointer , int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX , int screenY , int pointer) {
        Ray ray = MousePickingTool.getMousePicker().ray;
        Vector3 rayEnd = selectedComponent.model.transform.getTranslation(temp0);
        Vector3 currentScale = new Vector3();

        selectedComponent.model.transform.getScale(currentScale);
        float dst = camera.position.dst(rayEnd);

        rayEnd = ray.getEndPoint(rayEnd , dst);
        selectedComponent.scene.modelInstance.transform.getTranslation(currentPos);
        Vector3 vec = new Vector3();

        if (initScale) {
            initScale = false;
            lastPos.set(rayEnd);
        }

        boolean modified = false;
        if (scaleState==ScaleState.SCALE_X)
        {
            vec.set(1+rayEnd.x - lastPos.x, 1, 1);
            modified = true;
        }

        else if (scaleState==ScaleState.SCALE_Y)
        {
            vec.set(1, 1+rayEnd.y - lastPos.y, 1);
            modified = true;
        }
        else if (scaleState==ScaleState.SCALE_Z)
        {
            vec.set(1, 1, 1+rayEnd.z - lastPos.z);
            modified = true;
        }
        else if (scaleState==ScaleState.SCALE_XYZ)
        {
            vec.set(1+rayEnd.x - lastPos.x, 1+rayEnd.y - lastPos.y, 1+rayEnd.z - lastPos.z);
            float avg = (vec.x+vec.y+vec.z)/3;
            vec.set(avg, avg, avg);
            modified = true;
        }

        //ensure not 0
        if (vec.x==0)
            vec.x = 1;

        if (modified) {
            selectedComponent.scene.modelInstance.transform.scl(vec.x, vec.y, vec.z);

        }

        lastPos.set(rayEnd);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX , int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX , float amountY) {
        return false;
    }

    @Override
    public void enable() {
        Context.getInstance().inputMultiplexer.addProcessor(this);
        update();
        enabled = true;


        sceneManager.getRenderableProviders().add(xHandle);
        sceneManager.getRenderableProviders().add(yHandle);
        sceneManager.getRenderableProviders().add(zHandle);
        sceneManager.getRenderableProviders().add(xyzHandle);
    }

    @Override
    public void disable() {
        Context.getInstance().inputMultiplexer.removeProcessor(this);
        scaleState = ScaleState.NONE;
        enabled = false;
        sceneManager.getRenderableProviders().removeValue(xHandle , true);
        sceneManager.getRenderableProviders().removeValue(yHandle , true);
        sceneManager.getRenderableProviders().removeValue(zHandle , true);
        sceneManager.getRenderableProviders().removeValue(xyzHandle , true);
    }

    public void scaleHandle(float scale) {
        xHandle.transform.setToScaling(scale , scale , scale);
        yHandle.transform.setToScaling(scale , scale , scale);
        zHandle.transform.setToScaling(scale , scale , scale);
        xyzHandle.transform.setToScaling(scale , scale , scale);
    }

}
