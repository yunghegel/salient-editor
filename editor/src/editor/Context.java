package editor;

import backend.DefaultAssets;
import backend.serialization.SceneSerializer;
import backend.tools.Log;
import backend.tools.Perf;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.strongjoshua.console.Console;
import com.strongjoshua.console.GUIConsole;
import core.attributes.PhysicsAttributes;
import core.components.BulletComponent;
import core.entities.SceneEntityFactory;
import core.systems.*;
import editor.graphics.rendering.SceneRenderer;
import input.EditorPerspectiveCameraController;
import net.mgsx.gltf.scene3d.attributes.FogAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import core.phys.BulletWorld;
import core.phys.MotionState;
import ui.UserInterface;
import util.MiscUtils;
import util.ModelUtils;
import util.SceneUtils;
import utils.EnvironmentUtils;

public class Context
{

    static {
        Gdx.gl.glLineWidth(2);
        //Gdx.gl.glEnable(GL.GL_LINE_SMOOTH);
    }

    private static Context instance;
    //ecs
    public BulletWorld bulletWorld;
    public InputMultiplexer inputMultiplexer;
    public boolean drawAxes = true;
    public SceneSerializer sceneSerializer;
    private GLProfiler profiler;
    private UserInterface ui;
    public Console console;

    Array<ModelInstance> instances = new Array<>();

    public Context() {

        if (!VisUI.isLoaded()) VisUI.load(new Skin(Gdx.files.internal("skin/tixel.json")));
        instance = this;
        profiler = new GLProfiler(Gdx.graphics);
        //VisUI.load(new Skin(Gdx.files.internal("skin/tixel.json")));
        console = new GUIConsole(VisUI.getSkin() , false , 0 , VisWindow.class , VisTable.class , "default-pane" , TextField.class , VisTextButton.class , VisLabel.class , VisScrollPane.class);
        ui = new UserInterface();
        bulletWorld = new BulletWorld();
        stage = new Stage(new ScreenViewport())
        {

            @Override
            public boolean mouseMoved(int screenX , int screenY) {
                if (console.hitsConsole(screenX , screenY)) {
                    Log.info("Console hit");

                }
                return super.mouseMoved(screenX , screenY);
            }
        };

        createScene();
        createECS();
        createEntities();
        inputMultiplexer = new InputMultiplexer();

        //inputMultiplexer.addProcessor(glfwInputAdapter);
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(cameraController);
        inputMultiplexer.addProcessor(console.getInputProcessor());
        Stage consoleStage = (Stage) console.getInputProcessor();
        consoleStage.setViewport(stage.getViewport());
        inputMultiplexer.addProcessor(consoleStage);

        Gdx.input.setInputProcessor(inputMultiplexer);

    }

    private Cubemap diffuseCubemap;
    private Cubemap environmentCubemap;
    private Cubemap specularCubemap;
    private Texture brdfLUT;
    private SceneSkybox skybox;
    private SceneAsset sceneAsset;
    private Scene scene;
    private Scene axesScene;
    public OrthographicCamera orthoCam;
    public PerspectiveCamera camera;
    public PerspectiveCamera orthoPerspectiveCamera;
    public SceneManager sceneManager;
    public Stage stage;
    public SceneRenderer sceneRenderer;
    public FirstPersonCameraController cameraController;
    public CameraInputController orthoCamController;
    public static DirectionalLightEx light;
    public static DirectionalShadowLight shadowLight;
    public PBRShaderConfig config;
    public DepthShaderProvider depthShaderProvider;

    private void createScene() {

        config = new PBRShaderConfig();
        config.numBones = 128;
        config.numDirectionalLights = 8;
        config.numPointLights = 16;
        config.numSpotLights = 16;

        depthShaderProvider = new DepthShaderProvider();
        depthShaderProvider.config.numBones = 128;
        depthShaderProvider.config.numDirectionalLights = 8;
        depthShaderProvider.config.numPointLights = 16;
        depthShaderProvider.config.numSpotLights = 12;

        sceneManager = new SceneManager(new PBRShaderProvider(config) , depthShaderProvider);

        camera = new PerspectiveCamera(67 , Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
        camera.near = 1f;
        camera.far = 300;
        camera.update();
        camera.position.set(5f , 2.5f , 5f);
        camera.lookAt(0 , 2.5f , 0);
        orthoCam = new OrthographicCamera(Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
        orthoCam.far = 1000;
        orthoCam.lookAt(.25f , .25f , -1);
        orthoCam.direction.set(-.25f , -.25f , -.25f);
        orthoCam.update();

        sceneRenderer = new SceneRenderer(sceneManager , camera);
        orthoCamController = new CameraInputController(orthoCam);
        cameraController = new EditorPerspectiveCameraController(camera);

        sceneManager.setCamera(camera);

        //ModelInstance axes = util.ModelUtils.createAxes();
        ModelInstance axes1 = ModelUtils.createGrid(1f , 0.1f);
        ModelInstance axes2dot5 = ModelUtils.createGrid(2.5f , 0.3f);
        ModelInstance axes5 = ModelUtils.createGrid(5f , 0.5f);
        ModelInstance gridLines = ModelUtils.createAxisLines(0.8f);
        ModelInstance floor = ModelUtils.createFloor(25f , .1f , 25f);
        sceneManager.getRenderableProviders().add(axes1 , axes2dot5 , axes5 , gridLines);

        Scene sceneFloor = new Scene(floor);
        sceneFloor.modelInstance.transform.setTranslation(0 , -10f , 0);
        sceneManager.addScene(sceneFloor);
        btCollisionShape floorShape = new btBoxShape(new Vector3(2000 / 2f , .1f / 2f , 2000 / 2f));
        MotionState floorMotionState = new MotionState(floor.transform);
        float mass = 0;
        Vector3 localInertia = new Vector3();
        floorShape.calculateLocalInertia(mass , localInertia);
        btRigidBody.btRigidBodyConstructionInfo floorRigidBodyCI = new btRigidBody.btRigidBodyConstructionInfo(mass , floorMotionState , floorShape , localInertia);
        btRigidBody floorRigidBody = new btRigidBody(floorRigidBodyCI);
        bulletWorld.addBody(floorRigidBody);

        int count = 3;

        for (int i = 0; i < count; i++) {
            Model model = util.ModelUtils.createSphere(MiscUtils.getRandomFloat(.5f , 2f));
            ModelInstance instance = new ModelInstance(model);
            instance.transform.setToTranslation(MiscUtils.getRandomFloat(-10f , 10f) , MiscUtils.getRandomFloat(-10f , 10f) , MiscUtils.getRandomFloat(-10f , 10f));

            instances.add(instance);
        }

        for (int i = 0; i < count; i++) {
            Model model = util.ModelUtils.createCube(MiscUtils.getRandomFloat(.5f , 2f));
            ModelInstance instance = new ModelInstance(model);
            instance.transform.setToTranslation(MiscUtils.getRandomFloat(-10f , 10f) , MiscUtils.getRandomFloat(-10f , 10f) , MiscUtils.getRandomFloat(-10f , 10f));

            instances.add(instance);
        }

        light = new DirectionalLightEx();

        light.color.set(Color.WHITE);
        sceneManager.environment.add(light);
        //sceneManager.environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        shadowLight = new DirectionalShadowLight(512 , 512 , 100 , 100f , 1f , 100f);

        shadowLight.direction.set(0 , -.5f , 1);
        //shadowLight.setCenter(0,50,0);
        shadowLight.color.set(Color.WHITE);
        //sceneManager.environment.add((shadowLight).set(0.8f, 0.8f, 0.8f, -.4f, -.4f, -.4f));
        EnvironmentUtils.setupCamera(camera , sceneManager);
        EnvironmentUtils.setupLight(shadowLight , sceneManager);
        EnvironmentUtils.setupIBL(sceneManager , shadowLight , skybox);
        //sceneManager.environment.add((shadowLight));
        sceneManager.environment.shadowMap = shadowLight;
        //sceneManager.environment.add(light);
        sceneManager.environment.set(new ColorAttribute(ColorAttribute.AmbientLight , 0.2f , 0.2f , 0.2f , 0.5f));

        float near = 1f;
        float far = 100f;
        float exponent = 1f;

        //shadowLight.direction.set(light.direction);

        sceneManager.environment.set(new ColorAttribute(ColorAttribute.Fog , new Color(Color.valueOf("7f7f7f4d"))));
        sceneManager.environment.set(new ColorAttribute(PBRColorAttribute.AmbientLight , 0.5f , 0.5f , 0.5f , 1f));
        sceneManager.environment.set(new FogAttribute(FogAttribute.FogEquation).set(near , far , exponent));

        //        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(shadowLight);
        //        environmentCubemap = iblBuilder.buildEnvMap(1024);
        //        diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        //        specularCubemap = iblBuilder.buildRadianceMap(10);
        //        iblBuilder.dispose();
        //
        //        brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        //        sceneManager.setAmbientLight(1f);
        //        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture , brdfLUT));
        //        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        //        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

        Cubemap customSkyboxCubemap = SceneUtils.createCubemapDirectionFormat("editor");
        skybox = new SceneSkybox(customSkyboxCubemap);
        sceneManager.setSkyBox(skybox);
    }

    public Engine engine;
    public LightsSystem lightsSystem;
    public BulletPhysicsSystem bulletPhysicsSystem;
    public SceneSystem sceneSystem;
    public GizmoSystem gizmoSystem;
    public PlayerSystem playerSystem;
    public ObjectPickingSystem objectPickingSystem;
    public IntermediateRenderingSystem intermediateRenderingSystem;

    private void createECS() {
        int ecs = Perf.start("create_ECS");

        int systemsCreate = Perf.start("create_systems");
        sceneSystem = new SceneSystem();
        lightsSystem = new LightsSystem();
        bulletPhysicsSystem = new BulletPhysicsSystem();
        gizmoSystem = new GizmoSystem();
        playerSystem = new PlayerSystem();
        objectPickingSystem = new ObjectPickingSystem();
        intermediateRenderingSystem = new IntermediateRenderingSystem();
        Perf.end(systemsCreate);

        int assignContext = Perf.start("assign_system_context");
        sceneSystem.setContext(this);
        lightsSystem.setContext(this);
        bulletPhysicsSystem.setContext(this);
        gizmoSystem.setContext(this);
        playerSystem.setContext(this);
        objectPickingSystem.setContext(this);
        intermediateRenderingSystem.setContext(this);
        Perf.end(assignContext);

        int createEngine = Perf.start("create_engine");
        engine = new Engine();
        Perf.end(createEngine);

        int addSystems = Perf.start("add_systems");
        engine.addSystem(sceneSystem);
        engine.addSystem(lightsSystem);
        engine.addSystem(bulletPhysicsSystem);
        engine.addSystem(gizmoSystem);
        engine.addSystem(playerSystem);
        engine.addSystem(objectPickingSystem);
        engine.addSystem(intermediateRenderingSystem);
        Perf.end(addSystems);
    }

    private void createEntities() {
        if (!DefaultAssets.i.loaded) {
            ThreadUtils.yield();
        }
        int createEntities = Perf.start("create_entities");

        int createSceneEntities = Perf.start("create_scene_entities");
        Entity scene = SceneEntityFactory.createSceneEntity(DefaultAssets.i.crateSceneAsset , "models/crate.gltf" , "crate_scene");
        SceneEntityFactory.addStaticBulletComponent(scene);
        //engine.addEntity(scene);

        Entity riggedModelScene = SceneEntityFactory.createSceneEntity(DefaultAssets.i.riggedCharacterSceneAsset , "models/RiggedFigure.gltf" , "rigged_model_scene");
        SceneEntityFactory.addStaticBulletComponent(riggedModelScene);
        //engine.addEntity(riggedModelScene);

        Entity map = SceneEntityFactory.createSceneEntity(DefaultAssets.i.staticMapScene , "models/transmission_test/TransmissionTest.gltf" , "map_scene");
        //SceneEntityFactory.addStaticBulletComponent(map);

        engine.addEntity(map);

        Entity player = SceneEntityFactory.createPlayerEntity(DefaultAssets.i.playerSceneAsset , "models/Player.gltf");
        //engine.addEntity(player);
        Perf.end(createSceneEntities);

        BulletComponent bulletComponent = new BulletComponent(PhysicsAttributes.STATIC | PhysicsAttributes.DEBUG_DRAW_ENABLED);

        for (ModelInstance instance : instances) {
            instance.transform.setTranslation(MiscUtils.getRandomVector3(-10f , 10f));
            Entity entity = SceneEntityFactory.createSceneEntity(instance , instance.getClass().getSimpleName());
            // SceneEntityFactory.addDynamicBulletComponent(entity);
            engine.addEntity(entity);
        }

        //        SceneComponent sceneComponent = SceneEntityFactory.createSceneComponent("industrial_map","models/map_industrial/map_industrial.gltf");
        //
        //        Entity mapEntity = new Entity();
        //        mapEntity.add(sceneComponent);
        //        engine.addEntity(mapEntity);

        sceneSerializer = new SceneSerializer();
        sceneSerializer.serializeComponentRegistry();
    }

    public static Context getInstance() {
        return instance;
    }

    public Scene getAxesScene() {
        return axesScene;
    }

    public Console getConsole() {
        return console;
    }

    public void dispose() {
        sceneManager.dispose();
        sceneAsset.dispose();
        bulletWorld.dispose();
        sceneSystem.dispose();
        lightsSystem.dispose();
        gizmoSystem.dispose();
        bulletPhysicsSystem.dispose();
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public Stage getStage() {
        return stage;
    }

    public SceneSystem getSceneSystem() {
        return sceneSystem;
    }

    public FirstPersonCameraController getCameraController() {
        return cameraController;
    }

    public OrthographicCamera getOrthoCam() {
        return orthoCam;
    }

    public GLProfiler getProfiler() {
        return profiler;
    }

}

