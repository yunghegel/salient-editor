package tests;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import tests.shaders.ColorShader;
import tests.shaders.CustomDepthShaderProvider;
import tests.shaders.OutlineShader;

public class ShaderTest extends BaseTest
{
    public RenderContext renderContext;
    public ColorShader shader;
    public OutlineShader outlineShader;

    public Renderable renderable;
    public ShaderProgram outlineShaderProgram;
    public ShaderProgram colorShader;
    public ShaderProgram depthShader;
    public Mesh fullScreenQuad;
    FrameBuffer secondFbo;
    CustomDepthShaderProvider customDepthShaderProvider;
    ModelBatch depthModelBatch;
    FrameBuffer depthFbo;
    Model model;
    Array<Renderable> renderables = new Array<Renderable>();
    Pool<Renderable>    renderablePool = new Pool<Renderable>() {
        @Override
        protected Renderable newObject() {
            return new Renderable();
        }
    };


    public ShaderTest(Game game) {
        super(game);
        renderContext = new RenderContext((new DefaultTextureBinder(DefaultTextureBinder.ROUNDROBIN, 1)));
        colorShader = new ShaderProgram(Gdx.files.internal("shaders/color.vs.vert").readString(), Gdx.files.internal("shaders/color.fs.frag").readString());
        outlineShaderProgram = new ShaderProgram(Gdx.files.internal("shaders/object_outline.vert").readString(), Gdx.files.internal("shaders/object_outline.frag").readString());
        depthShader = new ShaderProgram(Gdx.files.internal("shaders/custom_depth.vs.vert").readString(), Gdx.files.internal("shaders/custom_depth.fs.frag").readString());
     //   fullScreenQuad=createFullScreenQuad();

        secondFbo = new FrameBuffer(Pixmap.Format.RGBA8888 , 1, 1, false, false);
        shader= new ColorShader();
        outlineShader = new OutlineShader();
        outlineShader.init();
        shader.init();
        renderable = new Renderable();
        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createSphere(2f, 2f, 2f, 20, 20,
                                          new Material(),
                                          VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);

        NodePart blockPart = model.nodes.get(0).parts.get(0);
        modelInstance.getRenderables(renderables, renderablePool);
        renderable = new Renderable();
        blockPart.setRenderable(renderable);
//        batch = new ModelBatch(new DefaultShaderProvider() {
//            @Override
//            protected Shader createShader(Renderable renderable) {
//                return new ColorShader();
//            }
//        });
        batch = new ModelBatch();



        customDepthShaderProvider = new CustomDepthShaderProvider();
        depthModelBatch = new ModelBatch(customDepthShaderProvider);
        depthFbo = new FrameBuffer(Pixmap.Format.RGBA8888
                , Gdx.graphics.getWidth()
                , Gdx.graphics.getHeight()
                , true);


    }

    float count = 0;

    @Override
    public void render(float delta) {
   // super.render(delta);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(.3f, .3f, .3f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        cameraController.update();
        scene.modelInstance.transform.rotate(Vector3.Y, 10f * delta);
        sceneManager.update(delta);
        captureScene();

        captureDepth();

        drawWithOutline();
//        frameBuffer.bind();
//        frameBuffer.begin();
//        batch.begin(camera);
//        Gdx.gl.glCullFace(GL20.GL_BACK);
//        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
//        Gdx.gl.glEnable(GL20.GL_BLEND);
//        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//        batch.render(scene,sceneManager.environment);
//        batch.end();
//        frameBuffer.end();
//        frameBufferTexture = frameBuffer.getColorBufferTexture();
//        textureRegion = new TextureRegion(frameBufferTexture);
//        textureRegion.flip(false, true);
//
//        TextureRegion depthTextureRegion = new TextureRegion(depthFbo.getColorBufferTexture());
//        depthTextureRegion.flip(false, true);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//        spriteBatch.begin();
//        spriteBatch.setShader(outlineShaderProgram);
//        spriteBatch.draw(textureRegion, 0, 0);
//        spriteBatch.draw(depthTextureRegion, 0, 0);
//
//
//        spriteBatch.end();



    }

    @Override
    public void dispose() {
        super.dispose();
    }
    public Mesh createFullScreenQuad(){
        float[] verts = new float[20];
        int i = 0;
        verts[i++] = -1.f; // x1
        verts[i++] = -1.f; // y1
        verts[i++] =  0.f; // u1
        verts[i++] =  0.f; // v1
        verts[i++] =  1.f; // x2
        verts[i++] = -1.f; // y2
        verts[i++] =  1.f; // u2
        verts[i++] =  0.f; // v2
        verts[i++] =  1.f; // x3
        verts[i++] =  1.f; // y2
        verts[i++] =  1.f; // u3
        verts[i++] =  1.f; // v3
        verts[i++] = -1.f; // x4
        verts[i++] =  1.f; // y4
        verts[i++] =  0.f; // u4
        verts[i++] =  1.f; // v4
        Mesh tmpMesh = new Mesh(true, 4, 0
                , new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position")
                , new VertexAttribute(VertexAttributes.Usage.TextureCoordinates
                , 2, "a_texCoord0"));
        tmpMesh.setVertices(verts);
        return tmpMesh;
    }

    public void captureDepth(){
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
        spriteBatch.draw(depthTextureRegion, 0, 0);


        spriteBatch.end();
    }

}
