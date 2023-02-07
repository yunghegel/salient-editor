package core.systems;

import backend.tools.Log;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.VisLabel;
import editor.graphics.rendering.Renderer;
import core.components.SceneComponent;
import editor.Context;
import editor.tools.RotateTool;
import net.mgsx.gltf.scene3d.scene.Scene;

import editor.tools.ScaleTool;
import editor.tools.TranslateTool;
import ui.widgets.RenderWidget;
import util.ModelUtils;

public class GizmoSystem extends IteratingSystem implements Renderer
{
    public static boolean translateToolEnabled = false;
    public static boolean scaleToolEnabled = false;
    public static boolean rotateToolEnabled = false;

    public static boolean translateX = false;
    public static boolean translateY = false;
    public static boolean translateZ = false;

    public static boolean scaleX = false;
    public static boolean scaleY = false;
    public static boolean scaleZ = false;
    public static boolean scaleXYZ = false;


    public static VisLabel selectedComponent;

    public TranslateTool translateTool;
    public ScaleTool scaleTool;
    public RotateTool rotateTool;
    ComponentMapper<SceneComponent> pm = ComponentMapper.getFor(SceneComponent.class);
    Ray ray = new Ray();
    ShapeRenderer shapeRenderer = new ShapeRenderer();
    private SceneComponent selectedSceneComponent;
    private Context context;



    public GizmoSystem() {

        super(Family.one(SceneComponent.class).get());

        selectedComponent = new VisLabel("");
        selectedComponent.setAlignment(Align.left);
        selectedComponent.setX(10);
        selectedComponent.setOriginX(0);

        RenderWidget.getInstance().addRenderer(this);

    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        translateTool = new TranslateTool(context.getSceneManager() , context.getCamera());
        scaleTool = new ScaleTool(context.getSceneManager() , context.getCamera());
        rotateTool = new RotateTool(context.getSceneManager());
        Log.info("GizmoSystem" , "GizmoSystem added to engine");
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            toggleTranslateTool();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            toggleScaleTool();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            toggleRotateTool();
        }

        if (SceneSystem.selectedSceneComponent != null) {
            translateTool.setSelectedComponent(SceneSystem.selectedSceneComponent);
            scaleTool.setSelectedComponent(SceneSystem.selectedSceneComponent);
            rotateTool.setSelectedComponent(SceneSystem.selectedSceneComponent);
            scaleTool.update();
            translateTool.update();
            rotateTool.update();
            selectedSceneComponent = SceneSystem.selectedSceneComponent;
        }

    }

    @Override
    protected void processEntity(Entity entity , float deltaTime) {
        SceneComponent sceneComponent = pm.get(entity);

        if (sceneComponent.selected) {
            selectedSceneComponent = sceneComponent;
            translateTool.setSelectedComponent(SceneSystem.selectedSceneComponent);
            scaleTool.setSelectedComponent(SceneSystem.selectedSceneComponent);
            rotateTool.setSelectedComponent(SceneSystem.selectedSceneComponent);
            translateTool.update();
            rotateTool.update();
            scaleTool.update();
        }
        if (Gdx.input.isButtonJustPressed(0) && translateToolEnabled) {
            pickTranslateGizmo();
        }
        if (Gdx.input.isButtonJustPressed(0) && scaleToolEnabled) {
            pickScaleGizmo();
        }
        if (Gdx.input.isButtonJustPressed(0) && rotateToolEnabled) {
            pickRotateGizmo();
        }
    }

    private void pickTranslateGizmo() {
        ray = RenderWidget.viewport.getPickRay(Gdx.input.getX() , Gdx.input.getY());
        Vector3 intersection = new Vector3();
        setTranslateNone();
        translateTool.state = TranslateTool.TransformState.NONE;
        if (Intersector.intersectRayBounds(ray , translateTool.xArrowBoundingBox , intersection)) {
            setTranslateX();
            translateTool.state = TranslateTool.TransformState.TRANSLATE_X;
            translateTool.initTranslate = true;
            Log.info("GizmoSystem" , "Picked X arrow");
        }
        if (Intersector.intersectRayBounds(ray , translateTool.yArrowBoundingBox , intersection)) {
            setTranslateY();
            translateTool.state = TranslateTool.TransformState.TRANSLATE_Y;
            translateTool.initTranslate = true;
            Log.info("GizmoSystem" , "Picked Y arrow");
        }
        if (Intersector.intersectRayBounds(ray , translateTool.zArrowBoundingBox , intersection)) {
            setTranslateZ();
            translateTool.state = TranslateTool.TransformState.TRANSLATE_Z;
            translateTool.initTranslate = true;
            Log.info("GizmoSystem" , "Picked Z arrow");
        }

    }

    private void pickScaleGizmo() {
        ray = RenderWidget.viewport.getPickRay(Gdx.input.getX() , Gdx.input.getY());
        Vector3 intersection = new Vector3();
        setScaleNone();
        scaleTool.scaleState = ScaleTool.ScaleState.NONE;
        if (Intersector.intersectRayBounds(ray , scaleTool.xHandleBoundingBox , intersection)) {
            setScaleX();
            scaleTool.scaleState = ScaleTool.ScaleState.SCALE_X;
            scaleTool.initScale = true;
            Log.info("GizmoSystem" , "Picked X handle");
        }
        if (Intersector.intersectRayBounds(ray , scaleTool.yHandleBoundingBox , intersection)) {
            setScaleY();
            scaleTool.scaleState = ScaleTool.ScaleState.SCALE_Y;
            scaleTool.initScale = true;
            Log.info("GizmoSystem" , "Picked Y handle");
        }
        if (Intersector.intersectRayBounds(ray , scaleTool.zHandleBoundingBox , intersection)) {
            setScaleZ();
            scaleTool.scaleState = ScaleTool.ScaleState.SCALE_Z;
            scaleTool.initScale = true;
            Log.info("GizmoSystem" , "Picked Z handle");
        }
        if (Intersector.intersectRayBounds(ray , scaleTool.xyzHandleBoundingBox , intersection)) {
            setScaleXYZ();
            scaleTool.scaleState = ScaleTool.ScaleState.SCALE_XYZ;
            scaleTool.initScale = true;
            Log.info("GizmoSystem" , "Picked XYZ handle");
        }
    }

    private void pickRotateGizmo(){
        ray = RenderWidget.viewport.getPickRay(Gdx.input.getX() , Gdx.input.getY());
        Vector3 intersection = new Vector3();
        float dst = 0;
        rotateTool.rotationState = RotateTool.RotationState.NONE;
        BoundingBox boundingBox = new BoundingBox();
        rotateTool.gizmo.calculateBoundingBox(boundingBox);
        boundingBox.mul(rotateTool.gizmo.transform);
        if (!Intersector.intersectRayBounds(ray , boundingBox , intersection)) {
            return;
        }

        if (context.objectPickingSystem.pickMeshFromTriangles(rotateTool.xHandleMesh,rotateTool.intersectionPoint,dst,rotateTool.gizmo.transform)){
            rotateTool.rotationState = RotateTool.RotationState.ROTATE_X;
            rotateTool.initRotate = true;
            Log.info("GizmoSystem" , "Picked X handle");
            return;
        }



        if (context.objectPickingSystem.pickMeshFromTriangles(rotateTool.yHandleMesh,rotateTool.intersectionPoint,dst,rotateTool.gizmo.transform)){
            rotateTool.rotationState = RotateTool.RotationState.ROTATE_Y;
            rotateTool.initRotate = true;
            Log.info("GizmoSystem" , "Picked Y handle");
            return;
        }
         if (context.objectPickingSystem.pickMeshFromTriangles(rotateTool.zHandleMesh,rotateTool.intersectionPoint,dst,rotateTool.gizmo.transform)){
            rotateTool.rotationState = RotateTool.RotationState.ROTATE_Z;
            rotateTool.initRotate = true;
            Log.info("GizmoSystem" , "Picked Z handle");
            return;
        }



    }

    public void toggleTranslateTool() {
        translateToolEnabled = !translateToolEnabled;
        scaleToolEnabled = false;
        rotateToolEnabled = false;
        scaleTool.disable();
        rotateTool.disable();
        if (translateToolEnabled) {
            translateTool.enable();

        }
        else {
            translateTool.disable();
            translateTool.update();
            setTranslateNone();
        }

        Log.info("GizmoSystem" , "Transform tool enabled: " + translateToolEnabled);
    }

    public void toggleScaleTool() {
        scaleToolEnabled = !scaleToolEnabled;
        translateToolEnabled = false;
        rotateToolEnabled = false;
        translateTool.disable();
        rotateTool.disable();
        if (scaleToolEnabled) {
            scaleTool.enable();
        }
        else {
            scaleTool.disable();
            scaleTool.update();
            setScaleNone();
        }
        Log.info("GizmoSystem" , "Scale tool enabled: " + scaleToolEnabled);
    }

    public void toggleRotateTool(){
        if (rotateTool.getSelectedComponent() == null) return;
        rotateToolEnabled = !rotateToolEnabled;
        scaleToolEnabled = false;
        translateToolEnabled = false;
        scaleTool.disable();
        translateTool.disable();
        if (rotateToolEnabled) {
            rotateTool.enable();
        }
        else {
            rotateTool.disable();
            rotateTool.update();
        }

        Log.info("GizmoSystem" , "Rotate tool enabled: " + rotateToolEnabled);
    }

    public void setTranslateX() {
        translateX = true;
        translateY = false;
        translateZ = false;

    }

    public void setTranslateY() {
        translateY = true;
        translateX = false;
        translateZ = false;
    }

    public void setTranslateZ() {
        translateZ = true;
        translateX = false;
        translateY = false;
    }

    public void setTranslateNone() {
        translateX = false;
        translateY = false;
        translateZ = false;
    }

    public void setScaleX() {
        scaleX = true;
        scaleY = false;
        scaleZ = false;
        scaleXYZ = false;
    }

    public void setScaleY() {
        scaleY = true;
        scaleX = false;
        scaleZ = false;
        scaleXYZ = false;
    }

    public void setScaleZ() {
        scaleZ = true;
        scaleX = false;
        scaleY = false;
        scaleXYZ = false;
    }

    public void setScaleXYZ() {
        scaleXYZ = true;
        scaleX = false;
        scaleY = false;
        scaleZ = false;
    }

    public void setScaleNone() {
        scaleX = false;
        scaleY = false;
        scaleZ = false;
        scaleXYZ = false;
    }

    public void drawSelectedArrow() {
        //Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        Vector3 center = new Vector3();
        if (translateX) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.line(translateTool.xArrowBoundingBox.getCenter(center) , translateTool.xArrowBoundingBox.getCenter(center).add(1 , 0 , 0));
            shapeRenderer.end();
        }
        if (translateY) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.line(translateTool.yArrowBoundingBox.getCenter(center) , translateTool.yArrowBoundingBox.getCenter(center).add(0 , 1 , 0));
            shapeRenderer.end();
        }
        if (translateZ) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.line(translateTool.zArrowBoundingBox.getCenter(center) , translateTool.zArrowBoundingBox.getCenter(center).add(0 , 0 , 1));
            shapeRenderer.end();
        }
    }

    public void dispose() {
        shapeRenderer.dispose();

        translateTool.dispose();
    }

    @Override
    public void render(Camera cam) {


    }



}
