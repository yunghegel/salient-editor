package ui.widgets;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ui.SalientUI;

public class RenderWidget extends Widget {

    private static Vector2 vec = new Vector2();

    public static ScreenViewport viewport;
    private PerspectiveCamera cam;
    private Renderer renderer;

    public RenderWidget(PerspectiveCamera cam) {
        super();
        this.cam = cam;
        viewport = new ScreenViewport(this.cam);
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

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (renderer == null || cam == null) return;

        // render part of the ui & pause rest
        batch.end();

        vec.set(getOriginX(), getOriginY());
        vec = localToStageCoordinates(vec);
        final int width = (int) getWidth();
        final int height = (int) getHeight();

        // apply widget viewport
        viewport.setScreenBounds((int) vec.x, (int) vec.y, width, height);
        viewport.setWorldSize(width * viewport.getUnitsPerPixel(), height * viewport.getUnitsPerPixel());
        viewport.apply();

        // render 3d scene
        renderer.render(cam);

        // re-apply stage viewport
        SalientUI.stage.getViewport().apply();

        // proceed ui rendering
        batch.begin();
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
    }

    /**
     * Used to render the 3d scene within this widget.
     */
    public interface Renderer {
        void render(Camera cam);
    }

}