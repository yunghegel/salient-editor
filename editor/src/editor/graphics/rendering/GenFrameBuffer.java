package editor.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GenFrameBuffer implements Disposable
{
public FrameBuffer fbo;
public Viewport viewport;
private Texture fboBaseColorTexture;
private TextureRegion fboBaseColorTextureRegion;
public Pixmap fboPixmap;
GLFrameBuffer.FrameBufferBuilder fbb;
FrameBuffer frameBuffer;



public GenFrameBuffer(boolean hasDepth){

    int width = Gdx.graphics.getWidth();
    int height = Gdx.graphics.getHeight();

    {
        GLFrameBuffer.FrameBufferBuilder frameBufferBuilder = new GLFrameBuffer.FrameBufferBuilder(Gdx.graphics.getWidth(),
                                                                                                   Gdx.graphics.getHeight());
        frameBufferBuilder.addColorTextureAttachment(GL30.GL_RGBA8, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE);
        frameBufferBuilder.addColorTextureAttachment(GL30.GL_RGB8, GL30.GL_RGB, GL30.GL_UNSIGNED_BYTE);
        frameBufferBuilder.addColorTextureAttachment(GL30.GL_RGB8, GL30.GL_RGB, GL30.GL_UNSIGNED_BYTE);
        frameBufferBuilder.addDepthTextureAttachment(GL30.GL_DEPTH_COMPONENT, GL30.GL_UNSIGNED_SHORT);
        frameBufferBuilder.addDepthRenderBuffer(GL30.GL_DEPTH_COMPONENT24);


        fbo = frameBufferBuilder.build();

    }
}

public GenFrameBuffer (int width, int height, boolean hasDepth) {
    fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, hasDepth);
}

public void begin(Viewport viewport){
    this.viewport = viewport;
    fbo.begin();
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    Gdx.gl.glViewport(viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(),
                      viewport.getScreenHeight());
}

public void end(){
    fbo.end();
    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    fboBaseColorTexture = fbo.getColorBufferTexture();
    //apply smoothing
    fboBaseColorTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

    fboBaseColorTextureRegion = new TextureRegion(fboBaseColorTexture);
    fboBaseColorTextureRegion.flip(false, true);
}

public TextureRegion getFboTexture(){
    return fboBaseColorTextureRegion;
}

    @Override
    public void dispose() {
        fbo.dispose();
    }

    public void resize(ScreenViewport viewport) {
        //fbo.dispose();

        //fbo = new FrameBuffer(Pixmap.Format.RGBA8888, viewport.getScreenWidth(), viewport.getScreenHeight(), true);
    }

}
