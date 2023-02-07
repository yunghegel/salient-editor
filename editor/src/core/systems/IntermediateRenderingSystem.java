package core.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

import core.components.SceneComponent;
import editor.Context;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import ui.widgets.RenderWidget;

public class IntermediateRenderingSystem extends IteratingSystem
{
    ComponentMapper<core.components.SceneComponent> pm = ComponentMapper.getFor(core.components.SceneComponent.class);
    Context context;
    ModelBatch modelBatch;
    FrameBuffer fbo;
    ModelBatch shadowBatch;
    FrameBuffer shadowBuffer;
    SpriteBatch spriteBatch;
    ShaderProgram shader;
    SceneManager sceneManager;
    Texture fboTexture;
    Texture shadowTexture;
    TextureRegion fboRegion;
    TextureRegion shadowRegion;
    Environment environment;
    DirectionalShadowLight shadowLight;
    PerspectiveCamera camera;
    ShaderProgram outlineShaderProgram;

    boolean captureAll = false;




    RenderableProvider renderableProvider;

    public IntermediateRenderingSystem() {
        super(Family.one(SceneComponent.class).get());
        init();
    }

    public void init(){
        outlineShaderProgram = new ShaderProgram(Gdx.files.internal("shaders/object_outline.vert").readString(), Gdx.files.internal("shaders/object_outline.frag").readString());

        shadowBatch = new ModelBatch(new DepthShaderProvider());
        modelBatch = new ModelBatch();
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, 1024, 1024, true);
        shadowBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 1024, 1024, false);

        spriteBatch = new SpriteBatch();
        //shader = new ShaderProgram(Gdx.files.internal("shaders/outline.vert"), Gdx.files.internal("shaders/outline.frag"));
        environment = new Environment();
        shadowLight = new DirectionalShadowLight(1024, 1024, 100f, 100f, 1f, 300f);
        //environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1.0f, 1f, .6f, 1f));
        environment.add((shadowLight = new DirectionalShadowLight(1024, 1024, 60f, 60f, .1f, 50f))
                                .set(1f, 1f, 1f, 40.0f, -35f, -35f));
        environment.shadowMap = shadowLight;

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, 6, 2);
        camera.direction.set(0, 0, -4).sub(camera.position).nor();
        camera.near = 1;
        camera.far = 300;
        camera.update();

//        if (!shader.isCompiled()) {
//            System.out.println(shader.getLog());
//        }
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        sceneManager = context.sceneManager;

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);



    }

    public void renderShadows(SceneComponent sceneComponent){
        Gdx.gl.glViewport((int)RenderWidget.vec.x, (int)RenderWidget.vec.y, (int)RenderWidget.width, (int)RenderWidget.height);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        if (shadowLight == null) return;
        //shadowBuffer.bind();
        shadowBuffer.begin();
        shadowLight.begin(Vector3.Zero, camera.direction);
        shadowBatch.begin(shadowLight.getCamera());
        shadowBatch.render(sceneComponent.model);
        shadowBatch.end();
        shadowLight.end();
        shadowBuffer.end();
        //shadowBuffer.getColorBufferTexture().bind(1);
        shadowTexture = shadowBuffer.getColorBufferTexture();
        shadowRegion = new TextureRegion(shadowTexture);
        shadowRegion.flip(false, true);



    }

    public void renderScene(SceneComponent sceneComponent){
       //Gdx.gl.glClearColor(0, 0, 0, 1);

        fbo.bind();
        fbo.begin();
        Gdx.gl.glCullFace(GL20.GL_BACK);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);
        //Gdx.gl.glDepthMask(true);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        modelBatch.begin(camera);
        modelBatch.render(sceneComponent.scene,environment);  //environment has shadowMap!
        modelBatch.end();
        fbo.end();



    }

    @Override
    protected void processEntity(Entity entity , float deltaTime) {
        core.components.SceneComponent sceneComponent = pm.get(entity);


//        renderShadows(sceneComponent);
//        renderScene(sceneComponent);
    }

    public void setContext(Context context){
        this.context = context;
    }

    public void setShader(ShaderProgram shader) {
        this.shader = shader;
    }

    public void setRenderableProvider(RenderableProvider renderableProvider) {
        this.renderableProvider = renderableProvider;
    }
}
