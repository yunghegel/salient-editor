package ui.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTextArea;
import com.kotcrab.vis.ui.widget.VisTextButton;
import backend.tools.Log;
import editor.graphics.rendering.Renderer;

public class RenderWidget extends AbstractWidget
{

    private static RenderWidget instance;

    public static RenderWidget getInstance() {
        if(instance == null) {
            instance = new RenderWidget();
        }

        return instance;
    }

    public static Vector2 vec = new Vector2();

    public static ScreenViewport viewport;
    public static int width;
    public static int height;
    public Camera cam;
    public boolean isOrtho = false;
    public boolean isPerspective = true;
    Stage stage;
    private Model compass;
    private ModelInstance compassInstance;
    private OrthographicCamera orthoCam;
    private Renderer renderer;
    private VisTextButton orthoButton;
    private VisTextButton perspectiveButton;
    private VisImageButton translateButton;
    ModelBatch batch;
    private Array<Renderer> renderers = new Array<>();
    FrameBuffer frameBuffer;
    SpriteBatch spriteBatch;
    Texture texture;
    TextureRegionDrawable drawable;
    BitmapFont font;
    VisTextArea label;
    Stage otherStage;
    public FrameBuffer mainBuffer;
    public TextureRegion region;



    public RenderWidget(PerspectiveCamera camera , Stage stage) {
        super();
        instance = this;
        this.cam = camera;
        this.stage = stage;


        mainBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        viewport = new ScreenViewport(cam);


        //        font = generator.generateFont(parameter); // font size 12 pixels
        Log.info("RenderWidget" , "RenderWidget created");

    }

    public RenderWidget() {
        super();
        viewport = new ScreenViewport();
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void setCam(PerspectiveCamera cam) {
        this.cam = cam;
        viewport.setCamera(cam);
    }

    public void setCam(OrthographicCamera cam) {
        this.cam = cam;
        viewport.setCamera(cam);
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
    }

    public void addRenderer(Renderer renderer) {
        renderers.add(renderer);
    }

    @Override
    public void draw(Batch batch , float parentAlpha) {

        batch.end();

        vec.set(getOriginX() , getOriginY());
        vec = localToStageCoordinates(vec);
        width = (int) getWidth();
        height = (int) getHeight();

        viewport.setScreenBounds((int) vec.x , (int) vec.y , width , height);
        viewport.setWorldSize(width * viewport.getUnitsPerPixel() , height * viewport.getUnitsPerPixel());
        viewport.apply();
        Gdx.gl.glEnable(Gdx.gl20.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl20.GL_SRC_ALPHA , Gdx.gl20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glEnable(Gdx.gl20.GL_DEPTH_TEST);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT|GL20.GL_COLOR_BUFFER_BIT);
        //renderCompass();


        for (Renderer renderer : renderers) {
            renderer.render(cam);
        }







//        OutlineRenderer.getInstance().render(cam);



        stage.getViewport().apply();

        batch.begin();


    }

    @Override
    void enable() {
        setVisible(true);
    }

    @Override
    void disable() {
        setVisible(false);
    }

    public void createElements() {

    }

    public void renderCompass(){
        frameBuffer.begin();
        batch.begin(cam);
        compassInstance.transform.setToLookAt(cam.direction , Vector3.Y);
        batch.render(compassInstance);
        batch.end();
        frameBuffer.end();
        texture = frameBuffer.getColorBufferTexture();
        spriteBatch = new SpriteBatch();
        spriteBatch.begin();


        spriteBatch.draw(texture , 100 , 100 , 100 , 100);
        spriteBatch.end();


    }



}