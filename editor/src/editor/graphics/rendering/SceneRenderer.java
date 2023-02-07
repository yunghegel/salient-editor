package editor.graphics.rendering;

import backend.tools.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;

import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.FxaaEffect;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import core.systems.SceneSystem;
import editor.Context;
import core.systems.BulletPhysicsSystem;
import core.systems.GizmoSystem;

import editor.tools.RotateTool;
import editor.tools.ScaleTool;
import editor.tools.TranslateTool;

import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import ui.widgets.RenderWidget;
import util.ModelUtils;

public class SceneRenderer implements Renderer
{

    private SceneManager sceneManager;
    private Camera camera;
    private Context context;
    FrameBuffer frameBuffer;
    SpriteBatch spriteBatch;
    Texture texture;
    TextureRegionDrawable drawable;
    ModelBatch batch;
    ModelBatch depthBatch;
    private Model compass;
    private ModelInstance compassInstance;
    ModelBatch shadowBatch;
    private DirectionalShadowLight shadowLight;
    Environment environment;
    private FrameBuffer depthBuffer;
    private TextureRegion shadowRegion;
    private Texture shadowTexture;
    private TextureRegion fboRegion;
    private Texture fboTexture;
    private Texture depthTexture;
    private TextureRegion depthRegion;
    private Camera cam;
    public boolean drawAxes=true;
    VisWindow window;
    VisImage image;
    VisTable table;
    ShaderProgram outlineShaderProgram;
    ModelInstance axes1;
    ModelInstance axes2dot5;
    ModelInstance axes5;
    ModelInstance gridLines;
    private VfxManager vfxManager;
    private FxaaEffect fxaaEffect;

    {
        axes1 = ModelUtils.createGrid(1f , 0.1f);
        axes2dot5 = ModelUtils.createGrid(2.5f , 0.3f);
        axes5 = ModelUtils.createGrid(5f , 0.5f);
        gridLines = ModelUtils.createAxisLines(0.8f);
    }

    public SceneRenderer(SceneManager sceneManager , Camera camera) {
        this.sceneManager = sceneManager;
        this.camera = camera;
        outlineShaderProgram = new ShaderProgram(Gdx.files.internal("shaders/object_outline.vert").readString(), Gdx.files.internal("shaders/object_outline.frag").readString());
        if (!outlineShaderProgram.isCompiled()) {
            System.out.println(outlineShaderProgram.getLog());
        }
        outlineShaderProgram.bind();
       // outlineShaderProgram.setUniformf("u_dst",1);
        Log.info("SceneRenderer" , "Created SceneRenderer");
        batch = new ModelBatch(new PBRShaderProvider(new PBRShaderConfig()));
        compass = ModelUtils.buildCompassModel();
        compassInstance = new ModelInstance(compass);
        DepthShaderProvider depthShaderProvider = new DepthShaderProvider();

        //config.depthBufferOnly = true;
        depthBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        depthBatch = new ModelBatch(new PBRShaderProvider(new PBRShaderConfig()));
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);


        spriteBatch = new SpriteBatch();

        shadowBatch = new ModelBatch(new DepthShaderProvider());
        shadowLight = new DirectionalShadowLight(512, 512, 100, 100, 1f, 300f);
        shadowLight.set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f);
        environment = new Environment();
        //environment.shadowMap = shadowLight;
        //environment.add(new DirectionalLightEx().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        //environment.add(shadowLight);
        //environment.set(new ColorAttribute(PBRColorAttribute.AmbientLight , 0.5f , 0.5f , 0.5f , 1f));
        float lum = 0.1f;
        sceneManager.environment.set(new ColorAttribute(ColorAttribute.AmbientLight , 0.01f, 0.01f, 0.01f, 1f));
        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
        fxaaEffect = new FxaaEffect();
        vfxManager.addEffect(fxaaEffect);

    }

    public void setContext(Context context) {
        this.context = context;

    }

    public void setCamera(Camera camera) {
        sceneManager.setCamera(camera);
    }

    GenFrameBuffer genFrameBuffer = new GenFrameBuffer(true);
    GenFrameBuffer genFrameBuffer2 = new GenFrameBuffer(true);
    GenFrameBuffer gizmoFrameBuffer = new GenFrameBuffer(true);
    GenFrameBuffer shadowFrameBuffer = new GenFrameBuffer(false);

    @Override
    public void render(Camera cam) {
        this.cam = cam;

        //Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        clear();
        update();

        sceneManager.getRenderableProviders().removeValue(axes1,true);
        sceneManager.getRenderableProviders().removeValue(axes2dot5,true);
        sceneManager.getRenderableProviders().removeValue(axes5,true);
        sceneManager.getRenderableProviders().removeValue(gridLines,true);


        // Begin render to an off-screen buffer.



        drawShadows();
        drawGizmos();




        //sceneManager.getRenderableProviders().add(axes1,axes2dot5,axes5,gridLines);

        genFrameBuffer.begin(RenderWidget.viewport);
        //Gdx.gl.glEnable(GL20.GL_BLEND);
        //Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.begin(cam);
        context.getSceneSystem().renderShapes();
        //sceneManager.renderColors();

        sceneManager.renderColors();
        if (drawAxes){
            drawAxis(cam);
        }
        renderWithoutDepth();

        batch.end();
        //Gdx.gl.glDisable(GL20.GL_BLEND);
        genFrameBuffer.end();


        genFrameBuffer2.begin(RenderWidget.viewport);
        depthBatch.begin(cam);
        Gdx.gl.glClearColor(0, 0, 0, 0f);
//        context.gizmoSystem.translateTool.render(Gdx.graphics.getDeltaTime());
//        context.gizmoSystem.scaleTool.render();
//        context.gizmoSystem.rotateTool.render();
        if (SceneSystem.selectedSceneComponent != null) {
            depthBatch.render(SceneSystem.selectedSceneComponent.model, sceneManager.environment);
        }
        depthBatch.end();
        genFrameBuffer2.end();




        spriteBatch.begin();
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);


        spriteBatch.setShader(null);
        spriteBatch.draw(genFrameBuffer.getFboTexture() , 0 , 0 , Gdx.graphics.getWidth() , Gdx.graphics.getHeight());




        spriteBatch.setShader(null);
//        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        spriteBatch.setShader(outlineShaderProgram);
        spriteBatch.draw(gizmoFrameBuffer.getFboTexture() , 0 , 0 , Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
        spriteBatch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);


        spriteBatch.draw(genFrameBuffer2.getFboTexture() , 0 , 0 , Gdx.graphics.getWidth() , Gdx.graphics.getHeight());

        spriteBatch.setShader(null);
        spriteBatch.draw(shadowFrameBuffer.getFboTexture() , RenderWidget.vec.x , RenderWidget.vec.y , Gdx.graphics.getWidth() , Gdx.graphics.getHeight());

        spriteBatch.end();



        //sceneManager.render();

       // renderWithoutDepth();
        //Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawAxes() {
        Gdx.gl.glDisable(GL20.GL_BLEND);
        if (drawAxes){

            sceneManager.getRenderableProviders().add(axes1,axes2dot5,axes5,gridLines);
//            batch.render(axes1, sceneManager.environment);
//            batch.render(axes2dot5, sceneManager.environment);
//            batch.render(axes5, sceneManager.environment);
//            batch.render(gridLines, sceneManager.environment);

        }
        Gdx.gl.glEnable(GL20.GL_BLEND);
    }

    private void drawShadows(){
        shadowFrameBuffer.begin(RenderWidget.viewport);

        sceneManager.renderShadows();
        shadowFrameBuffer.end();


    }

    private void drawGizmos(){
        gizmoFrameBuffer.begin(RenderWidget.viewport);



        // Apply the effects chain to the captured frame.
        // In our case, only one effect (gaussian blur) will be applied.

        depthBatch.begin(cam);
        Gdx.gl.glClearColor(0, 0, 0, 0f);
        if (context.gizmoSystem.rotateTool.rotationState == RotateTool.RotationState.ROTATE_X) {
            depthBatch.render(context.gizmoSystem.rotateTool.xHandleOutline, sceneManager.environment);
        }

        if (context.gizmoSystem.rotateTool.rotationState == RotateTool.RotationState.ROTATE_Y) {
            depthBatch.render(context.gizmoSystem.rotateTool.yHandleOutline,sceneManager.environment);
        }
        if (context.gizmoSystem.rotateTool.rotationState == RotateTool.RotationState.ROTATE_Z) {
            depthBatch.render(context.gizmoSystem.rotateTool.zHandleOutline,sceneManager.environment);
        }

        if (context.gizmoSystem.translateTool.state == TranslateTool.TransformState.TRANSLATE_X) {
            depthBatch.render(context.gizmoSystem.translateTool.xHandle,sceneManager.environment);
            depthBatch.render(context.gizmoSystem.translateTool.xzPlaneHandle,sceneManager.environment);
        }
        if (context.gizmoSystem.translateTool.state == TranslateTool.TransformState.TRANSLATE_Y) {
            depthBatch.render(context.gizmoSystem.translateTool.yHandle,sceneManager.environment);
            depthBatch.render(context.gizmoSystem.translateTool.xzPlaneHandle,sceneManager.environment);
        }
        if (context.gizmoSystem.translateTool.state == TranslateTool.TransformState.TRANSLATE_Z) {
            depthBatch.render(context.gizmoSystem.translateTool.zHandle,sceneManager.environment);
            depthBatch.render(context.gizmoSystem.translateTool.xzPlaneHandle,sceneManager.environment);
        }

        if (context.gizmoSystem.scaleTool.scaleState == ScaleTool.ScaleState.SCALE_X) {
            depthBatch.render(context.gizmoSystem.scaleTool.xHandle,sceneManager.environment);
            depthBatch.render(context.gizmoSystem.scaleTool.xyzHandle,sceneManager.environment);
        }
        if (context.gizmoSystem.scaleTool.scaleState == ScaleTool.ScaleState.SCALE_Y) {
            depthBatch.render(context.gizmoSystem.scaleTool.yHandle,sceneManager.environment);
            depthBatch.render(context.gizmoSystem.scaleTool.xyzHandle,sceneManager.environment);
        }
        if (context.gizmoSystem.scaleTool.scaleState == ScaleTool.ScaleState.SCALE_Z) {
            depthBatch.render(context.gizmoSystem.scaleTool.zHandle,sceneManager.environment);
            depthBatch.render(context.gizmoSystem.scaleTool.xyzHandle,sceneManager.environment);
        }
        if (context.gizmoSystem.scaleTool.scaleState == ScaleTool.ScaleState.SCALE_XYZ) {
            depthBatch.render(context.gizmoSystem.scaleTool.xyzHandle,sceneManager.environment);
        }
        depthBatch.end();



        gizmoFrameBuffer.end();
    }

    private void renderWithoutDepth(){

        Gdx.gl.glDisable(Gdx.gl20.GL_DEPTH_TEST);


        context.lightsSystem.render(cam);

        if (BulletPhysicsSystem.debugDraw) {
            context.bulletWorld.render(cam);
        }
        if (GizmoSystem.translateToolEnabled) {

            context.gizmoSystem.drawSelectedArrow();
        }
        context.gizmoSystem.translateTool.render(Gdx.graphics.getDeltaTime());
        context.gizmoSystem.scaleTool.render();
        context.gizmoSystem.rotateTool.render();
        context.wireframeRenderSystem.render();
        Gdx.gl.glEnable(Gdx.gl20.GL_DEPTH_TEST);
    }

    private void update(){
        context.getCameraController().update();
        context.orthoCamController.update();
        sceneManager.update(Gdx.graphics.getDeltaTime());
        if (SceneSystem.selectedSceneComponent!=null){
            float dst = context.camera.position.dst(SceneSystem.selectedSceneComponent.center);
            //outlineShaderProgram.setUniformf("u_dst", dst);
        } else {
            float dst = 1f;
            //outlineShaderProgram.setUniformf("u_dst", dst);
        }


    }

    private void clear(){
        Gdx.gl.glClearColor(.2f, .2f, .2f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    public void resize(){
        genFrameBuffer.resize(RenderWidget.viewport);
    }

    public void toggleAxes() {
        drawAxes = !drawAxes;
    }
    ModelBatch axisBatch = new ModelBatch();
    public void drawAxis(Camera cam){
        axisBatch.begin(cam);

//        axisBatch.render(axes1, sceneManager.environment);
//        axisBatch.render(axes2dot5, sceneManager.environment);
        axisBatch.render(axes5, sceneManager.environment);
        axisBatch.render(gridLines, sceneManager.environment);
        axisBatch.end();
    }

}

