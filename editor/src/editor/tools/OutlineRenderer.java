package editor.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import core.components.SceneComponent;
import editor.Context;
import editor.graphics.rendering.Renderer;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import tests.shaders.CustomDepthShaderProvider;
import ui.widgets.RenderWidget;

public class OutlineRenderer implements Renderer
{

    private static OutlineRenderer instance;

    public static OutlineRenderer getInstance() {
        if (instance == null) {
            instance = new OutlineRenderer();
        }

        return instance;
    }

    SceneManager sceneManager;
    public PBRShaderConfig config;
    public DepthShaderProvider depthShaderProvider;
    private ModelBatch depthModelBatch;
    private FrameBuffer depthFbo;
    public FrameBuffer frameBuffer;
    public Texture frameBufferTexture;
    public ModelBatch batch;
    SpriteBatch spriteBatch;
    TextureRegion textureRegion;
    private ShaderProgram outlineShaderProgram;
    CustomDepthShaderProvider customDepthShaderProvider;
    public OutlineRenderer(){

        config = new PBRShaderConfig();
        config.numBones = 128;
        config.numDirectionalLights = 8;
        config.numPointLights = 8;
        config.numSpotLights = 8;

        depthShaderProvider = new DepthShaderProvider();
        depthShaderProvider.config.numBones = 128;
        depthShaderProvider.config.numDirectionalLights = 8;
        depthShaderProvider.config.numPointLights = 8;
        depthShaderProvider.config.numSpotLights = 8;
        outlineShaderProgram = new ShaderProgram(Gdx.files.internal("shaders/object_outline.vert").readString(), Gdx.files.internal("shaders/object_outline.frag").readString());
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        depthFbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        spriteBatch = new SpriteBatch();
        textureRegion = new TextureRegion();
        batch = new ModelBatch();
        customDepthShaderProvider = new CustomDepthShaderProvider();
        depthModelBatch = new ModelBatch(customDepthShaderProvider);

        sceneManager = new SceneManager(new PBRShaderProvider(config) , depthShaderProvider);
        sceneManager.setCamera(Context.getInstance().camera);

    }


    public void captureDepth(Camera camera){
        FrameBuffer depth = depthFbo;
        depthFbo.bind();
        depthFbo.begin();
        Gdx.gl.glCullFace(GL20.GL_BACK);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);
        //Gdx.gl.glDepthMask(true);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        depthModelBatch.begin(camera);
        sceneManager.renderDepth();
        depthModelBatch.end();
        depthFbo.end();
    }

    public void captureScene(){
        frameBuffer.bind();
        frameBuffer.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        sceneManager.render();
        frameBuffer.end();
        frameBufferTexture = frameBuffer.getColorBufferTexture();
        textureRegion = new TextureRegion(frameBufferTexture);
        textureRegion.flip(false, true);
    }

    public void drawWithOutline(){
        TextureRegion depthTextureRegion = new TextureRegion(depthFbo.getColorBufferTexture());
        depthTextureRegion.flip(false, true);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        spriteBatch.begin();
        spriteBatch.setShader(outlineShaderProgram);
        spriteBatch.draw(textureRegion, 0, 0);



        if (RenderWidget.getInstance().region != null) {
            spriteBatch.draw(RenderWidget.getInstance().region , 0, 0);
        }
        spriteBatch.draw(depthTextureRegion, 0, 0);


        spriteBatch.end();
    }

    private void setModelToOutline(Scene scene){
        if(sceneManager.getRenderableProviders().contains(scene, true)){
            return;
        }
        sceneManager.getRenderableProviders().clear();
        sceneManager.addScene(scene);

    }

    public void setSelectedComponent(SceneComponent sceneComponent){
        setModelToOutline(sceneComponent.scene);
    }

    public void clearSelectedComponent(){
        sceneManager.getRenderableProviders().clear();
    }

    @Override
    public void render(Camera cam) {
        //Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(.3f, .3f, .3f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        sceneManager.update(Gdx.graphics.getDeltaTime());


        captureScene();

        captureDepth(cam);

        drawWithOutline();
    }

}
