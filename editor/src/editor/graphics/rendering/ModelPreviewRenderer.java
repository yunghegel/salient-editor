package editor.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.VisImage;
import core.systems.SceneSystem;
import editor.graphics.rendering.Renderer;
import net.mgsx.gltf.scene3d.attributes.FogAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;
import ui.widgets.ModelPreviewWidget;
import util.GeometryUtils;
import util.ModelUtils;

public class ModelPreviewRenderer implements Renderer
{
    public ModelBatch batch;
    public SceneManager sceneManager;
    private SceneAsset sceneAsset;
    private Scene scene;
    public ModelInstance modelInstance;
    ModelInstance axes1;
    ModelInstance axes2dot5;
    ModelInstance axes5;
    public PerspectiveCamera camera;
    private Cubemap diffuseCubemap;
    private Cubemap environmentCubemap;
    private Cubemap specularCubemap;
    private Texture brdfLUT;
    private float time;
    Stage stage;
    MenuBar menuBar;
    private SceneSkybox skybox;
    private DirectionalLightEx light;
    private FirstPersonCameraController cameraController;
    public InputMultiplexer inputMultiplexer;
    public FrameBuffer frameBuffer;
    public Texture frameBufferTexture;
    public DirectionalShadowLight shadowLight;
    Table rootTable;
    SpriteBatch spriteBatch;
    TextureRegion textureRegion;
    public CameraInputController cameraInputController;
    public VisImage image;
    public PBRShaderConfig config;
    public DepthShaderProvider depthShaderProvider;
    ModelPreviewWidget renderWidget;
    Table root;
    Environment environment;
    ShapeRenderer shapeRenderer = new ShapeRenderer();
    public boolean drawBbox = true;
    public boolean enable= true;



    public ModelPreviewRenderer ()
    {

        image = new VisImage();
        root = new Table();

        axes1 = ModelUtils.createGrid(1f, 0.1f);
        axes2dot5 = ModelUtils.createGrid(2.5f, 0.3f);
        axes5 = ModelUtils.createGrid(5f, 0.5f);


        ModelUtils.createAlphaAttribute(axes1.model, 0.1f);
        Scene scene = new Scene(axes1);
        root.setFillParent(true);


        spriteBatch = new SpriteBatch();

        config = new PBRShaderConfig();
        config.numBones = 128;
        config.numDirectionalLights = 12;
        config.numPointLights = 16;
        config.numSpotLights = 12;

        depthShaderProvider = new DepthShaderProvider();
        depthShaderProvider.config.numBones = 128;
        depthShaderProvider.config.numDirectionalLights = 12;
        depthShaderProvider.config.numPointLights = 16;
        depthShaderProvider.config.numSpotLights = 12;
        batch = new ModelBatch(new PBRShaderProvider(config));
        sceneManager = new SceneManager(new PBRShaderProvider(config) , depthShaderProvider);


        sceneManager.addScene(scene);
        light = new DirectionalLightEx();
        light.direction.set(1 , -3 , 1).nor();
        light.color.set(Color.WHITE);
//        shadowLight = new DirectionalShadowLight(1024 , 1024 , 10f , 10f , 1f , 300f);
//        shadowLight.setViewport(0 , 0 , 1024 , 1024);
//        shadowLight.set(1f , 1f , 1f , 1f , 1f , 1f);
        float near = 1f;
        float far = 100f;
        float exponent = 1f;
//
//        shadowLight.direction.set(light.direction);
//        sceneManager.environment.add(shadowLight);
        sceneManager.environment.set(new ColorAttribute(ColorAttribute.Fog , new Color(Color.valueOf("7f7f7f4d"))));
        sceneManager.environment.set(new ColorAttribute(PBRColorAttribute.AmbientLight , 0.5f , 0.5f , 0.5f , 1f));
        sceneManager.environment.set(new FogAttribute(FogAttribute.FogEquation).set(near , far , exponent));
        sceneManager.environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
        environmentCubemap = iblBuilder.buildEnvMap(1024);
        diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        sceneManager.setAmbientLight(1f);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture , brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

        sceneManager.environment.add(light);
        camera = new PerspectiveCamera(25f, 500, 500);
        sceneManager.setCamera(camera);
        camera.position.set(0, 5f, 6f);
        camera.lookAt(0, 0, 0);
        //ensure camera Y axis is up
        camera.up.set(0, 1, 0);

//        renderWidget = new ModelPreviewWidget(Context.getInstance().getStage(),camera);
//        renderWidget.setRenderer(this);
//        root.add(renderWidget).grow().fill().expand();

    }

    public void setModelInstance (ModelInstance modelInstance)
    {
        this.modelInstance = modelInstance;
    }



    public void dispose ()
    {
        batch.dispose();
        frameBuffer.dispose();
    }

    public void setImageDrawable ()
    {
        image.setDrawable(new TextureRegionDrawable(textureRegion));
        //setBackground(new TextureRegionDrawable(textureRegion));
    }

    @Override
    public void render(Camera cam) {
        //frameBuffer.begin();
        Gdx.gl.glEnable(Gdx.gl20.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl20.GL_SRC_ALPHA , Gdx.gl20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glEnable(Gdx.gl20.GL_DEPTH_TEST);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

        BoundingBox boundingBox = new BoundingBox();
        Vector3 center = new Vector3();
        Vector3 position =new Vector3();


        if (SceneSystem.selectedSceneComponent != null&&enable)
        {
            Model model = SceneSystem.selectedSceneComponent.scene.modelInstance.model;
            modelInstance = new ModelInstance(model);
            modelInstance.calculateBoundingBox(boundingBox);
            boundingBox.mul(modelInstance.transform);
            modelInstance.transform.getTranslation(position);
//            SceneSystem.selectedSceneComponent.scene.modelInstance.calculateBoundingBox(boundingBox);
//            boundingBox.mul(SceneSystem.selectedSceneComponent.scene.modelInstance.transform);
            boundingBox.getCenter(center);

            //camera.position.set(center.x , center.y , center.z + 5f);
            if (!sceneManager.getRenderableProviders().contains(modelInstance, true))
            {

                sceneManager.getRenderableProviders().clear();
                sceneManager.getRenderableProviders().add(axes1);
                sceneManager.getRenderableProviders().add(axes2dot5);
                sceneManager.getRenderableProviders().add(axes5);
                sceneManager.getRenderableProviders().add(modelInstance);



               // sceneManager.addScene(SceneSystem.selectedSceneComponent.scene);
            }

            cam.direction.set(center.x - position.x , center.y - position.y , center.z - position.z).nor();
            cam.rotateAround(center, new Vector3(0, 1, 0), 1/5f);
            cam.lookAt(center);

            cameraInputController = new CameraInputController(cam) {
                @Override
                public boolean scrolled(float amountX , float amountY) {
                    cam.translate(cam.direction.x * amountY , cam.direction.y * amountY , cam.direction.z * amountY);

                    return super.scrolled(amountX , amountY);
                }


            };
            //Context.getInstance().inputMultiplexer.addProcessor(cameraInputController);
            cameraInputController.update();


            //scene.modelInstance.transform.rotate(Vector3.Y, 10f * 1/60f);
            sceneManager.update(1/60f);
            sceneManager.render();




        } else {
        reset();
        }

        if (drawBbox)
            GeometryUtils.renderBoundingBox(boundingBox,Color.WHITE,cam);
//        frameBuffer.end();
//        frameBufferTexture = frameBuffer.getColorBufferTexture();
//        textureRegion = new TextureRegion(frameBufferTexture);

//        sceneManager.render();

    }
    //write a method that animates a modelinstance, oscillating between two values
    public void animateModel(float min, float max, float speed, ModelInstance modelInstance) {
        float time = MathUtils.sinDeg(Gdx.graphics.getDeltaTime() * speed);
        float value = MathUtils.lerp(min, max, time);
        modelInstance.transform.setToTranslationAndScaling(value, 0, 0, 1, 1, 1);
    }

    public void reset(){
        camera.position.set(0, 5f, 6f);
        camera.lookAt(0, 0, 0);
        //ensure camera Y axis is up
        camera.up.set(0, 1, 0);
    }



}
