package input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class EditorOrthoCameraController implements InputProcessor
{

    private final Vector3 tmpV1 = new Vector3();
    private final Vector3 tmpV2 = new Vector3();
    public OrthographicCamera camera;
    public float translateUnits = 10f;
    protected int button = -1;
    private float startX, startY;

    public EditorOrthoCameraController(OrthographicCamera cam) {
        this.camera = cam;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX , int screenY , int pointer , int button) {
        startX = screenX;
        startY = screenY;
        return false;
    }

    @Override
    public boolean touchUp(int screenX , int screenY , int pointer , int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX , int screenY , int pointer) {
        final float deltaX = ( screenX - startX ) / Gdx.graphics.getWidth();
        final float deltaY = ( startY - screenY ) / Gdx.graphics.getHeight();
        startX = screenX;
        startY = screenY;
        camera.translate(tmpV1.set(camera.direction).crs(camera.up).nor().scl(-deltaX * translateUnits));
        camera.translate(tmpV2.set(camera.up).scl(-deltaY * translateUnits));
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX , int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX , float amountY) {
        return false;
    }

    public void update() {

    }

}
