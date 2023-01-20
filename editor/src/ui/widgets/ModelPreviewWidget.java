package ui.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import editor.graphics.rendering.Renderer;

public class ModelPreviewWidget extends AbstractWidget
{
    public  ScreenViewport viewport;
    public  int width;
    public  int height;
    public Camera cam;
    public Renderer renderer;
    public Vector2 vec = new Vector2();
    public Stage stage;
    public ModelPreviewWidget(Stage stage, PerspectiveCamera cam)
    {
       this.stage = stage;
         this.cam = cam;
        viewport = new ScreenViewport(cam);
        pack();
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
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
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        //renderCompass();

            renderer.render(cam);


        stage.getViewport().apply();

        batch.begin();
    }

    @Override
    void enable() {

    }

    @Override
    void disable() {

    }

}
