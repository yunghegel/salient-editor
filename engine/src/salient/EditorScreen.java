package salient;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import com.kotcrab.vis.ui.widget.VisWindow;
import ecs.World;
import ecs.systems.ObjectPickingSystem;
import ecs.systems.PhysicsSystem;
import ecs.systems.RenderSystem;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import ui.SalientUI;
import ui.widgets.RenderWidget;

public class EditorScreen implements Screen {
    Game game;
    private SceneManager sceneManager;
    private SceneAsset sceneAsset;
    private Scene scene;
    private PerspectiveCamera camera;
    private Cubemap diffuseCubemap;
    private Cubemap environmentCubemap;
    private Cubemap specularCubemap;
    private Texture brdfLUT;
    private float time;
    private SceneSkybox skybox;
    private DirectionalLightEx light;
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
    SalientUI salientUI;
    Vector3 fromRay = new Vector3();
    Vector3 toRay = new Vector3();
    Viewport viewport;
    RenderWidget renderWidget;
    SalientRenderer salientRenderer;
    World world;
    PhysicsSystem bulletPhysicsSystem;
    ObjectPickingSystem objectPickingSystem;
    RenderSystem renderSystem;
    Engine engine;


    public EditorScreen(EditorGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        salientRenderer = new SalientRenderer();
        renderWidget = new RenderWidget();
        salientRenderer.create(stage);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
        this.sceneManager = salientRenderer.getSceneManager();
        this.camera = salientRenderer.getCamera();
        this.cameraController = salientRenderer.getCameraController();
        inputMultiplexer.addProcessor(cameraController);

        engine = new Engine();











        salientUI = new SalientUI(stage,salientRenderer.sceneManager,salientRenderer.light,salientRenderer.getCamera(),salientRenderer.shadowLight);

        renderWidget.setRenderer(salientRenderer);
        renderWidget.setCam(salientRenderer.getCamera());


        salientUI.setRenderWidget(renderWidget);
        objectPickingSystem = new ObjectPickingSystem();
        renderSystem = new RenderSystem();
        renderSystem.setSceneManager(sceneManager);
        bulletPhysicsSystem = new PhysicsSystem();
        bulletPhysicsSystem.setCamera(camera);
        initEngine();
        SalientAssets.createEntities(engine);

    }

    private void initEngine() {


        engine.addSystem(bulletPhysicsSystem);
        engine.addSystem(objectPickingSystem);
        engine.addSystem(renderSystem);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glViewport(0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        engine.update(delta);
        stage.act(delta);
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);


    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
