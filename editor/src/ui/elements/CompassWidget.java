package ui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import editor.Context;
import util.ModelUtils;

public class CompassWidget extends Actor

{
    FrameBuffer frameBuffer;
    SpriteBatch spriteBatch;
    Texture texture;
    TextureRegionDrawable drawable;
    private Model compass;
    private ModelInstance compassInstance;
    ModelBatch batch;
    Context ctx;
    Camera camera;
    public CompassWidget(Context context) {
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 100, 100, false);
        camera = new PerspectiveCamera(67, 100, 100);
        compass = ModelUtils.buildCompassModel();
        compassInstance = new ModelInstance(compass);
        batch = new ModelBatch();
        spriteBatch = new SpriteBatch();
        this.ctx = context;
        //toFront();

    }

    @Override
    public void draw(Batch batch , float parentAlpha) {
        super.draw(batch , parentAlpha);
        Gdx.gl.glClearColor(.3f , .3f , .3f , 1f);
        render(batch);
    }

    @Override
    public void act(float delta) {

        super.act(delta);
    }

    public void render(Batch batch) {
        Gdx.gl.glClearColor(.3f , .3f , .3f , 1f);
        loadBufferIntoTexture();
       // Gdx.gl.glViewport(0 , 0 , 250 , 250);

       Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        spriteBatch.begin();
        spriteBatch.draw(texture, 250, 250);
        spriteBatch.end();

    }

public void dispose() {
        frameBuffer.dispose();
        spriteBatch.dispose();
        texture.dispose();
        compass.dispose();
    }
    public void loadBufferIntoTexture(){
        Gdx.gl.glClearColor(.3f , .3f , .3f , 1f);
        camera.combined.set(ctx.getCamera().combined);
        camera.lookAt(0 , 0 , 0);
        frameBuffer.begin();
        batch.begin(camera);
        compassInstance.transform.setToLookAt(ctx.getCamera().direction , Vector3.Y);
        batch.render(compassInstance);
        batch.end();
        frameBuffer.end();
        texture = frameBuffer.getColorBufferTexture();
    }

    public Texture getBufferTexture(){
        return frameBuffer.getColorBufferTexture();
    }
}
