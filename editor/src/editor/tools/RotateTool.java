package editor.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.math.collision.Sphere;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.components.SceneComponent;
import core.systems.GizmoSystem;
import editor.Context;
import editor.graphics.scene.MeshInfo;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import ui.tools.AbstractTool;
import ui.widgets.RenderWidget;
import util.MaterialUtils;
import util.ModelUtils;

public class RotateTool extends AbstractTool implements InputProcessor
{

    ShapeRenderer shapeRenderer = new ShapeRenderer();

    public enum RotationState
    {
        NONE, ROTATE_X, ROTATE_Y, ROTATE_Z
    }

    public double dst = 0;

    public RotationState rotationState = RotationState.NONE;

    public ModelInstance xHandle, yHandle, zHandle;
    public ModelInstance xHandleOutline, yHandleOutline, zHandleOutline;
    public ModelInstance gizmo;
    public ModelBatch batch;
    public MeshInfo xHandleMesh, yHandleMesh, zHandleMesh;
    public MeshInfo gizmoMesh;

    public SceneManager sceneManager;
    private SceneComponent selectedComponent;

    public boolean enabled = false;
    public boolean initRotate = true;
    public float previousRot = 0;
    public float currentRot = 0;

    public Vector3 intersectionPoint = new Vector3();

    public RotateTool(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        batch = new ModelBatch(new PBRShaderProvider(new PBRShaderConfig()));
        SceneAsset x = new GLTFLoader().load(Gdx.files.internal("models/rotate_gizmo_x.gltf"));
        SceneAsset y = new GLTFLoader().load(Gdx.files.internal("models/rotate_gizmo_y.gltf"));
        SceneAsset z = new GLTFLoader().load(Gdx.files.internal("models/rotate_gizmo_z.gltf"));
        SceneAsset gizmoasset = new GLTFLoader().load(Gdx.files.internal("models/rotate_gizmo.gltf"));
        Scene xScene = new Scene(x.scene);
        Scene yScene = new Scene(y.scene);
        Scene zScene = new Scene(z.scene);
        Scene gizmoScene = new Scene(gizmoasset.scene);
        gizmo = gizmoScene.modelInstance;
        xHandle = xScene.modelInstance;
        yHandle = yScene.modelInstance;
        zHandle = zScene.modelInstance;

        yHandleMesh = new MeshInfo(yHandle.model.meshes.get(0));
        xHandleMesh = new MeshInfo(xHandle.model.meshes.get(0));
        zHandleMesh = new MeshInfo(zHandle.model.meshes.get(0));
        xHandleMesh.setModel(gizmo);
        yHandleMesh.setModel(gizmo);
        zHandleMesh.setModel(gizmo);

        //gizmoMesh = new MeshInfo(gizmo.model.meshes.get(0));
        // gizmoMesh.setModel(gizmo);
        //
        //        xHandle.model.nodes.get(0).inheritTransform = true;
        //        yHandle.model.nodes.get(0).inheritTransform = true;
        //        zHandle.model.nodes.get(0).inheritTransform = true;

        //        xHandleMesh.setPosition(1,0,0);
        //        xHandleMesh.updateMesh();

        Model model = new Model();

        xHandleOutline = new ModelInstance(xHandle);
        yHandleOutline = new ModelInstance(yHandle);
        zHandleOutline = new ModelInstance(zHandle);

        //       xHandleOutline.materials.get(0).clear();
        //        yHandleOutline.materials.get(0).clear();
        //       zHandleOutline.materials.get(0).clear();
        //
        //xHandleOutline.materials.add(MaterialUtils.createGenericBDSFMateral(Color.RED));
        //yHandleOutline.materials.add(MaterialUtils.createGenericBDSFMateral(Color.GREEN));
        //       zHandleOutline.materials.add(MaterialUtils.createGenericBDSFMateral(Color.BLUE));

        zHandleOutline.materials.get(0).set(PBRColorAttribute.createBaseColorFactor(Color.RED));
        yHandleOutline.materials.get(0).set(PBRColorAttribute.createBaseColorFactor(Color.GREEN));
        xHandleOutline.materials.get(0).set(PBRColorAttribute.createBaseColorFactor(Color.BLUE));
        //        xHandleOutline.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLUE));
        //        zHandleOutline.materials.get(0).set(ColorAttribute.createSpecular(Color.RED));
        //        yHandleOutline.materials.get(0).set(ColorAttribute.createSpecular(Color.GREEN));
        //        xHandleOutline.materials.get(0).set(ColorAttribute.createSpecular(Color.BLUE));
        //        zHandleOutline.materials.get(0).set(ColorAttribute.createAmbient(Color.RED));
        //        yHandleOutline.materials.get(0).set(ColorAttribute.createAmbient(Color.GREEN));
        //        xHandleOutline.materials.get(0).set(ColorAttribute.createAmbient(Color.BLUE));

        float alpha = 0.6f;
        xHandle.materials.get(0).set(PBRColorAttribute.createBaseColorFactor(new Color(1 , 0 , 0 , alpha)));
        yHandle.materials.get(0).set(PBRColorAttribute.createBaseColorFactor(new Color(0 , 1 , 0 , alpha)));
        zHandle.materials.get(0).set(PBRColorAttribute.createBaseColorFactor(new Color(0 , 0 , 1 , alpha)));
        xHandle.materials.get(0).set(ColorAttribute.createDiffuse(new Color(1 , 0 , 0 , alpha)));
        yHandle.materials.get(0).set(ColorAttribute.createDiffuse(new Color(0 , 1 , 0 , alpha)));
        zHandle.materials.get(0).set(ColorAttribute.createDiffuse(new Color(0 , 0 , 1 , alpha)));
        xHandle.materials.get(0).set(ColorAttribute.createSpecular(new Color(1 , 0 , 0 , alpha)));
        yHandle.materials.get(0).set(ColorAttribute.createSpecular(new Color(0 , 1 , 0 , alpha)));
        zHandle.materials.get(0).set(ColorAttribute.createSpecular(new Color(0 , 0 , 1 , alpha)));
        xHandle.materials.get(0).set(ColorAttribute.createAmbient(new Color(1 , 0 , 0 , alpha)));
        yHandle.materials.get(0).set(ColorAttribute.createAmbient(new Color(0 , 1 , 0 , alpha)));
        zHandle.materials.get(0).set(ColorAttribute.createAmbient(new Color(0 , 0 , 1 , alpha)));

        Material handleMaterial = new Material();
        Color color;

        transform2 = new Matrix4();
        transform2.setToOrtho2D(0 , 0 , Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setProjectionMatrix(transform2);

        ModelUtils.createOutlineModel(xHandleOutline.model , Color.WHITE , 0.01f);
        ModelUtils.createOutlineModel(yHandleOutline.model , Color.WHITE , 0.01f);
        ModelUtils.createOutlineModel(zHandleOutline.model , Color.WHITE , 0.01f);
    }

    public void update() {

        scaleHandle(1);
        dst = selectedComponent.center.dst(Context.getInstance().camera.position);
        dst = Math.sqrt(dst) / 1.1;
        gizmo.transform.setTranslation(selectedComponent.center);
        gizmo.transform.setToTranslationAndScaling(selectedComponent.center , new Vector3((float) dst , (float) dst , (float) dst));
        //        shapeRenderer.getProjectionMatrix().scl((float)dst);
        zHandleOutline.transform.set(gizmo.transform);
        yHandleOutline.transform.set(gizmo.transform);
        xHandleOutline.transform.set(gizmo.transform);
    }

    Vector3 a = new Vector3();
    Vector3 b = new Vector3();
    Matrix4 transform2 = new Matrix4();

    public void setSelectedComponent(SceneComponent selectedComponent) {
        this.selectedComponent = selectedComponent;
    }

    public SceneComponent getSelectedComponent() {
        return selectedComponent;
    }

    public void render() {

        if (enabled) {
            Gdx.gl.glLineWidth(1);
            Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA , GL20.GL_ONE_MINUS_SRC_ALPHA);
            batch.begin(sceneManager.camera);
            Vector3 center = selectedComponent.center;
            batch.render(gizmo , sceneManager.environment);
            batch.end();
            calculateAngle();

            //get bounding sphere of gizmo

            BoundingBox boundingBox = new BoundingBox();
            gizmo.calculateBoundingBox(boundingBox);
            Vector3 center2 = new Vector3();
            boundingBox.getCenter(center2);
            Vector3 dimensions = new Vector3();
            float radius = boundingBox.getDimensions(dimensions).len() / 2;

            Sphere sphere = new Sphere(center2 , radius);
            sphere.center.set(center2);
            System.out.println("center: " + center);
            System.out.println("center2: " + center2);
            System.out.println("radius: " + radius);

            if (rotationState == RotationState.ROTATE_X) {
                batch.render(xHandleOutline);

                //                Gdx.input.setCursorCatched(true);
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Crosshair);
                shapeRenderer.begin();
                shapeRenderer.setColor(Color.BLUE);
                shapeRenderer.rectLine(a.x , a.y , b.x , b.y , 1);
                shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.circle(a.x , a.y , 10 , 30);
                shapeRenderer.setColor(Color.BLACK);
                //shapeRenderer.circle(b.x,b.y, 15,30);
                shapeRenderer.end();
            }
            if (rotationState == RotationState.ROTATE_Y) {
                batch.render(yHandleOutline);
                //                Gdx.input.setCursorCatched(true);
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Crosshair);
                shapeRenderer.begin();
                shapeRenderer.setColor(Color.GREEN);
                shapeRenderer.rectLine(a.x , a.y , b.x , b.y , 1);
                shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.circle(a.x , a.y , 10 , 30);
                shapeRenderer.setColor(Color.BLACK);
                //shapeRenderer.circle(b.x,b.y, 15,30);
                shapeRenderer.end();
            }
            if (rotationState == RotationState.ROTATE_Z) {
                batch.render(zHandleOutline);
                //                Gdx.input.setCursorCatched(true);
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Crosshair);
                shapeRenderer.begin();
                shapeRenderer.setColor(Color.RED);

                shapeRenderer.rectLine(a.x , a.y , b.x , b.y , 1);
                shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.circle(a.x , a.y , 10 , 30);
                shapeRenderer.setColor(Color.BLACK);
                //shapeRenderer.circle(b.x,b.y, 15,30);
                shapeRenderer.end();
            }
            if (rotationState == RotationState.NONE) {
                Gdx.input.setCursorCatched(false);
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);

            }

            // Gdx.input.setCursorPosition((int)b.x,(int)b.y);

        }

        shapeRenderer.end();

        //

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
        //RenderWidget.renderWidgetMultiplexer.addProcessor(this);
        Context.getInstance().inputMultiplexer.addProcessor(this);
        enabled = true;
        GizmoSystem.rotateToolEnabled = true;
        update();
        sceneManager.getRenderableProviders().add(gizmo);
    }

    @Override
    public void disable() {
        Gdx.input.setInputProcessor(Context.getInstance().inputMultiplexer);
        Context.getInstance().inputMultiplexer.removeProcessor(this);
        //RenderWidget.renderWidgetMultiplexer.removeProcessor(this);
        GizmoSystem.rotateToolEnabled = false;
        enabled = false;
        rotationState = RotationState.NONE;
        sceneManager.getRenderableProviders().removeValue(gizmo , true);
    }
    private void scaleHandle(float scl) {
        xHandleOutline.transform.setToScaling(scl, scl, scl);
        yHandleOutline.transform.setToScaling(scl, scl, scl);
        zHandleOutline.transform.setToScaling(scl, scl, scl);
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
        rotationState = RotationState.NONE;
        return false;
    }
    Vector3 angle = new Vector3();
    Quaternion tmpQ = new Quaternion();
    Quaternion lastQuaternion = new Quaternion();
    public float degree=0;
    Vector3 pos = new Vector3();
    Vector3 pos2 = new Vector3();

    @Override
    public boolean touchDragged(int screenX , int screenY , int pointer) {

        pos = new Vector3();
        selectedComponent.getPosition(pos);
        sceneManager.camera.project(pos);

        float delta = Gdx.input.getDeltaX();
        angle = new Vector3();

        dst = pos.dst(sceneManager.camera.position);

        double dst2 = sceneManager.camera.position.dst(pos);
        dst2 = Math.sqrt(dst2);

//angle between the mouse and the center of the object





        Vector2 vec = new Vector2();

        float angleBetween = calculateAngle();

        if (initRotate){
            initRotate = false;
            currentRot = degree - previousRot;
            selectedComponent.transform.getRotation(lastQuaternion);
        }
        boolean rotChanged = false;
        if (rotationState==RotationState.ROTATE_X){
            //batch.render(xHandleOutline);

            tmpQ.setEulerAngles(0,-angleBetween,0);
            selectedComponent.model.transform.rotate(tmpQ);
            //selectedComponent.setRX(selectedComponent.getRX()+(degree*delta)/dst);
            rotChanged = true;

        }
        if (rotationState==RotationState.ROTATE_Y){
            //batch.render(yHandleOutline,sceneManager.environment);
            tmpQ.setEulerAngles(-angleBetween,0,0);
            selectedComponent.model.transform.rotate(tmpQ);
            rotChanged = true;

        }
        if (rotationState==RotationState.ROTATE_Z){
              //batch.render(zHandleOutline,sceneManager.environment);
            tmpQ.setEulerAngles(0,0,-angleBetween);
            selectedComponent.model.transform.rotate(tmpQ);
            rotChanged = true;
        }
        previousRot = degree;


        return false;
    }

    public float calculateAngle(){
        Ray ray = RenderWidget.viewport.getPickRay(Gdx.input.getX() , Gdx.input.getY());
        a = sceneManager.camera.project(selectedComponent.center);
        b = sceneManager.camera.project(ray.origin);
        degree = (float)Math.toDegrees(Math.atan2(b.x-a.x, b.y-a.y));
        return degree/10;
    }

    @Override
    public boolean mouseMoved(int screenX , int screenY) {

        return false;
    }

    @Override
    public boolean scrolled(float amountX , float amountY) {
        return false;
    }

}


