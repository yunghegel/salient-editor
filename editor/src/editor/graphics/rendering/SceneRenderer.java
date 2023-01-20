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
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import editor.Context;
import core.systems.BulletPhysicsSystem;
import core.systems.GizmoSystem;

import editor.graphics.rendering.Renderer;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
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
    DirectionalShadowLight shadowLight;
    Environment environment;
    private FrameBuffer depthBuffer;
    private TextureRegion shadowRegion;
    private Texture shadowTexture;
    private TextureRegion fboRegion;
    private Texture fboTexture;
    private Texture depthTexture;
    private TextureRegion depthRegion;
    VisWindow window;
    VisImage image;
    VisTable table;

    public SceneRenderer(SceneManager sceneManager , Camera camera) {
        this.sceneManager = sceneManager;
        this.camera = camera;
        Log.info("SceneRenderer" , "Created SceneRenderer");
        batch = new ModelBatch(new PBRShaderProvider(new PBRShaderConfig()));
        compass = ModelUtils.buildCompassModel();
        compassInstance = new ModelInstance(compass);
        DepthShaderProvider depthShaderProvider = new DepthShaderProvider();
        DepthShader.Config config = new DepthShader.Config();
        depthBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        depthBatch = new ModelBatch(depthShaderProvider);
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        config.numDirectionalLights = 6;
        config.numPointLights = 16;
        config.numSpotLights = 16;
        config.numBones = 128;
        spriteBatch = new SpriteBatch();

        shadowBatch = new ModelBatch(new DepthShaderProvider());
        shadowLight = new DirectionalShadowLight(512, 512, 100, 100, 1f, 300f);
        shadowLight.set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f);
        environment = new Environment();
        environment.shadowMap = shadowLight;
        environment.add(new DirectionalLightEx().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        environment.add(shadowLight);
        environment.set(new ColorAttribute(PBRColorAttribute.AmbientLight , 0.5f , 0.5f , 0.5f , 1f));



    }

    public void setContext(Context context) {
        this.context = context;

    }

    public void setCamera(Camera camera) {
        sceneManager.setCamera(camera);
    }

    @Override
    public void render(Camera cam) {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClearColor(0, 0, 0, 0);

        ScreenUtils.clear(0, 0, 0, 1);
        context.getCameraController().update();
        context.orthoCamController.update();

        sceneManager.update(delta);


//        depthBuffer.begin();
//        sceneManager.renderDepth();
//        depthBuffer.end();
//        depthBuffer.bind();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);




//        spriteBatch.begin();
//        spriteBatch.draw(frameBuffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        spriteBatch.end();
        sceneManager.render();








        Gdx.gl.glDisable(Gdx.gl20.GL_DEPTH_TEST);


        context.getSceneSystem().renderShapes();
        context.lightsSystem.render(cam);

        if (BulletPhysicsSystem.debugDraw) {
            context.bulletWorld.render(cam);
        }
        if (GizmoSystem.transformToolEnabled) {

            context.gizmoSystem.drawSelectedArrow();
        }
        context.gizmoSystem.translateTool.render(delta);
        context.gizmoSystem.scaleTool.render();

        Gdx.gl.glEnable(Gdx.gl20.GL_DEPTH_TEST);

//        spriteBatch.begin();
//        spriteBatch.draw(depthRegion , 0 , 0);
//        spriteBatch.end();
    //renderCompass(cam);

    }
    public void renderCompass(Camera cam){
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        Matrix4 tmp = new Matrix4();
        tmp.setToLookAt(new Vector3(0, 0, 0), new Vector3(0, 0, 1), new Vector3(0, 1, 0));
        tmp.set(cam.invProjectionView);
        compassInstance.transform.set(tmp);
       Vector3 unproj = new Vector3(300,300,1);
       Vector3 pos =camera.unproject(unproj);



        batch.begin(cam);


        //compassInstance.transform.setToLookAt(cam.direction , Vector3.Y);
        compassInstance.transform.setTranslation(pos);
        compassInstance.transform.rotate(Vector3.Y, 180);


        batch.render(compassInstance);
        batch.end();

//        texture = frameBuffer.getColorBufferTexture();
//        spriteBatch = new SpriteBatch();
//        spriteBatch.begin();
//
//
//        spriteBatch.draw(texture , 100 , 100 , 100 , 100);
//        spriteBatch.end();


    }
}

