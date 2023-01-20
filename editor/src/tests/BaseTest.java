package tests;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.MenuItem;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;
import util.ModelUtils;

public class BaseTest extends ScreenAdapter
{
    public SceneManager sceneManager;
    private SceneAsset sceneAsset;
    public Scene scene;
    public ModelInstance modelInstance;
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
    public FirstPersonCameraController cameraController;
    public InputMultiplexer inputMultiplexer;
    public FrameBuffer frameBuffer;
    public Texture frameBufferTexture;
    public ModelBatch batch;
    Table rootTable;
    SpriteBatch spriteBatch;
    TextureRegion textureRegion;
    CameraInputController cameraInputController;
    public boolean captureFrameBuffer = false;


    public BaseTest(Game game) {
        // create scene

        stage = new Stage();
        menuBar = new MenuBar();
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.align(Align.topLeft);
        rootTable.add(menuBar.getTable()).expandX().fillX().growX().row();
        stage.addActor(rootTable);
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        Menu menu = new Menu("Menu");
        menuBar.addMenu(menu);
        MenuItem selectionScreen = new MenuItem("Selection Screen");
        menu.addItem(selectionScreen);

        selectionScreen.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event , Actor actor) {
                game.setScreen(new TestSelectionScreen(game));
                dispose();
            }
        });

        sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/Duck.gltf"));
        scene = new Scene(sceneAsset.scene);
        sceneManager = new SceneManager();
        sceneManager.addScene(scene);
        modelInstance=scene.modelInstance;

        ModelInstance axes = ModelUtils.createAxes();
        Scene axesScene = new Scene(axes);

        // setup camera (The BoomBox model is very small so you may need to adapt camera settings for your scene)
        camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float d = .02f;
        camera.near =  .1f;
        camera.far = 2000;
        sceneManager.setCamera(camera);
        camera.position.set(0,0.5f, 4f);

        cameraController = new FirstPersonCameraController(camera);
        cameraController.setVelocity(40);
        cameraInputController = new CameraInputController(camera);
        inputMultiplexer.addProcessor(cameraController);
        Gdx.input.setInputProcessor(inputMultiplexer);


        // setup light
        light = new DirectionalLightEx();
        light.direction.set(1, -3, 1).nor();
        light.color.set(Color.WHITE);
        sceneManager.environment.add(light);

        // setup quick IBL (image based lighting)
        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
        environmentCubemap = iblBuilder.buildEnvMap(1024);
        diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        // This texture is provided by the library, no need to have it in your assets.
        brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        sceneManager.setAmbientLight(1f);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        spriteBatch = new SpriteBatch();

        // setup skybox
        skybox = new SceneSkybox(environmentCubemap);
       // sceneManager.setSkyBox(skybox);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(.3f, .3f, .3f, 1f);
        float deltaTime = Gdx.graphics.getDeltaTime();
        time += deltaTime;

        cameraController.update();
        //scene.modelInstance.transform.rotate(Vector3.Y, 10f * deltaTime);
        sceneManager.update(delta);
        sceneManager.render();
//        frameBuffer.bind();
//        frameBuffer.begin();
//        Gdx.gl.glCullFace(GL20.GL_BACK);
//        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
//        Gdx.gl.glEnable(GL20.GL_BLEND);
//        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);
//        Gdx.gl.glDepthMask(true);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//        if (captureFrameBuffer) {
//            sceneManager.render();
//        }
//
//        frameBuffer.end();
//        frameBufferTexture = frameBuffer.getColorBufferTexture();
//        textureRegion = new TextureRegion(frameBufferTexture);
//        textureRegion.flip(false, true);
//
//        if(captureFrameBuffer) {
//            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//            spriteBatch.begin();
//            spriteBatch.draw(textureRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//            spriteBatch.end();
//        }else {
//            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//            sceneManager.render();
//        }



        stage.act(delta);
        stage.draw();
    }


    @Override
    public void dispose() {
        super.dispose();
    }
    @Override
    public void resize(int width , int height) {
        sceneManager.updateViewport(width, height);

    }

    public Texture getFrameBufferColor() {
        frameBufferTexture = frameBuffer.getColorBufferTexture();
        return frameBufferTexture;
    }

}
