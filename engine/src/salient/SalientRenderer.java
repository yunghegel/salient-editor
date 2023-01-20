package salient;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import com.kotcrab.vis.ui.widget.VisWindow;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;
import ui.widgets.RenderWidget;
import utils.ModelUtils;

public class SalientRenderer implements RenderWidget.Renderer {
    public SceneManager sceneManager;
    private SceneAsset sceneAsset;
    private Scene scene;
    public static PerspectiveCamera camera;
    private Cubemap diffuseCubemap;
    private Cubemap environmentCubemap;
    private Cubemap specularCubemap;
    private Texture brdfLUT;
    private float time;
    private SceneSkybox skybox;
    public DirectionalLightEx light;
    private FirstPersonCameraController cameraController;
    ModelInstance axes;
    Scene axesScene;
    Ray ray;
    Scene rayScene;
    Table table;
    Stage stage;
    Table bufferTable;
    InputMultiplexer inputMultiplexer;
    VisSplitPane middlePane;
    VisImage image;
    FrameBuffer frameBuffer;
    Texture texture;
    TextureRegion textureRegion;
    SpriteBatch batch;
    Table leftTable;
    Table rightTable;
    ModelBatch modelBatch;
    VisWindow window;
    Vector3 fromRay = new Vector3();
    Vector3 toRay = new Vector3();
    Viewport viewport;
    public DirectionalShadowLight shadowLight;

    public void create(Stage stage){
        sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/Duck.gltf"));
        scene = new Scene(sceneAsset.scene);
        sceneManager = new SceneManager();
        sceneManager.addScene(scene);


        // setup camera (The BoomBox model is very small so you may need to adapt camera settings for your scene)
        camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()-180);
        float d = .02f;
        camera.near =.1f;
        camera.far = 2000;
        sceneManager.setCamera(camera);
        camera.position.set(0,0.5f, 4f);

        cameraController = new FirstPersonCameraController(camera);
        Gdx.input.setInputProcessor(cameraController);

        // setup light
        light = new DirectionalLightEx();
        light.direction.set(1, -3, 1).nor();
        light.color.set(Color.WHITE);
        sceneManager.environment.add(light);
        shadowLight = new DirectionalShadowLight(1024, 1024, 10f, 10f, 1f, 300f);
        shadowLight.setViewport(0, 0, 1024, 1024);
        shadowLight.set(1f, 1f, 1f, 1f, 1f, 1f);

        shadowLight.direction.set(light.direction);
        sceneManager.environment.add(shadowLight);
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

        // setup skybox
        skybox = new SceneSkybox(environmentCubemap);
        sceneManager.setSkyBox(skybox);


        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        modelBatch = new ModelBatch();
        batch = new SpriteBatch();
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(cameraController);
        textureRegion = new TextureRegion();
        texture = new Texture("icons/icon.png");

    }

    @Override
    public void render(Camera camera) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        float delta = Gdx.graphics.getDeltaTime();

        cameraController.update();
        sceneManager.update(delta);
        sceneManager.render();
        processInput();
        modelBatch.begin(camera);
        modelBatch.render(sceneManager.getRenderableProviders(), sceneManager.environment);
        modelBatch.end();

        batch.begin();
        batch.draw(texture, 0, 0);
        batch.end();





    }

    public PerspectiveCamera getCamera(){
        return camera;
    }

    public void processInput(){
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){

            ray = RenderWidget.viewport.getPickRay(Gdx.input.getX(), Gdx.input.getY());
            fromRay.set(ray.origin);
            toRay.set(ray.direction).scl(100f).add(ray.origin);
            rayDebugDraw(fromRay, toRay);
            BoundingBox box = new BoundingBox();
            scene.modelInstance.calculateBoundingBox(box);
            if(Intersector.intersectRayBoundsFast(ray,box)){
                System.out.println("Intersected");
            }

        }
    }

    public void rayDebugDraw(Vector3 rayOrigin, Vector3 rayDirection) {

        if (rayScene != null) {
            sceneManager.removeScene(rayScene);
        }

        ModelInstance ray = ModelUtils.createRayModelInstance(rayOrigin, rayDirection, 100, Color.BLUE);
        rayScene = new Scene(ray);

        sceneManager.addScene(rayScene);

    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public FirstPersonCameraController getCameraController() {
        return cameraController;
    }
}
