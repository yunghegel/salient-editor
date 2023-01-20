package input;

import backend.tools.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import core.systems.SceneSystem;

public class EditorPerspectiveCameraController extends FirstPersonCameraController implements InputProcessor
{

    private final static Vector3 tmpV = new Vector3();
    private final Vector3 tmpV1 = new Vector3();
    private final Vector3 tmpV2 = new Vector3();
    public float translateUnits = .1f;
    public PerspectiveCamera camera;
    Vector3 lookAt = new Vector3();
    TransformMode mode = TransformMode.Look;
    ProjectionMode projectionMode = ProjectionMode.Perspective;
    boolean translated = false;
    Vector2 tCurr = new Vector2();
    Vector2 last = new Vector2();
    Vector2 delta = new Vector2();
    Vector2 currWindow = new Vector2();
    Vector2 lastWindow = new Vector2();
    Vector3 curr3 = new Vector3();
    Vector3 delta3 = new Vector3();
    Plane lookAtPlane = new Plane(new Vector3(0 , 1 , 0) , 0);
    Matrix4 rotMatrix = new Matrix4();
    Vector3 xAxis = new Vector3(1 , 0 , 0);
    Vector3 yAxis = new Vector3(0 , 1 , 0);
    Vector3 point = new Vector3();
    float startX, startY;
    private Vector2 lastMousePosition = new Vector2();
    private Vector2 clickDownPosition = new Vector2();
    private Vector2 clickUpPosition = new Vector2();
    public float rotateAngle = 1f;
    private boolean isMouseDown = false;
    private boolean isMouseUp = false;
    Vector3 target = new Vector3();
    public int orthoButton = Input.Keys.O;
    public int perspectiveButton = Input.Keys.P;

    public EditorPerspectiveCameraController(PerspectiveCamera camera) {

        super(camera);
        this.camera = camera;
    }

    public void setCamera(PerspectiveCamera camera) {
        this.camera = camera;
    }

    @Override
    public boolean touchDown(int screenX , int screenY , int pointer , int button) {
        clickDownPosition.set(screenX , screenY);
        isMouseDown = true;
        isMouseUp = false;
        return super.touchDown(screenX , screenY , pointer , button);
    }

    @Override
    public boolean touchUp(int screenX , int screenY , int pointer , int button) {
        clickUpPosition.set(screenX , screenY);
        isMouseUp = true;
        isMouseDown = false;
        Gdx.input.setCursorCatched(false);
        return super.touchUp(screenX , screenY , pointer , button);
    }

    @Override
    public boolean mouseMoved(int screenX , int screenY) {

        return super.mouseMoved(screenX , screenY);
    }

    @Override
    public boolean scrolled(float amountX , float amountY) {
        float intensity = 0.1f * ( camera.position.dst(camera.direction) );
        camera.translate(camera.direction.x * amountY * intensity , camera.direction.y * amountY * intensity , camera.direction.z * amountY * intensity);
        return super.scrolled(amountX , amountY);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode==orthoButton){
            projectionMode = ProjectionMode.Orthographic;
            Log.info("CameraController","Switched to orthographic mode");
            camera.view.setToOrtho(-.5f, .5f, -.5f, .5f, -1.0f, 1.0f);
        }
        if (keycode==perspectiveButton){
            projectionMode = ProjectionMode.Perspective;
            Log.info("CameraController","Switched to perspective mode");
        }
        return super.keyDown(keycode);
    }

    @Override
    public boolean touchDragged(int screenX , int screenY , int pointer) {
        float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
        float deltaY = -Gdx.input.getDeltaY() * degreesPerPixel;
        startX = screenX;
        startY = screenY;
        if (Gdx.input.isButtonPressed(0)) return false;

        if (Gdx.input.isButtonPressed(2)) {
            Gdx.input.setCursorCatched(true);
            if (SceneSystem.selectedSceneComponent!=null)
            {
//                SceneSystem.selectedSceneComponent.getPosition(target);
//                camera.lookAt(target);
//
//                tmpV1.set(camera.direction).crs(camera.up).y = 0f;
//
//                camera.rotateAround(target, tmpV1.nor(), deltaY * rotateAngle);
//                camera.rotateAround(target, Vector3.Y, deltaX * -rotateAngle);

            } else {
            camera.translate(tmpV1.set(camera.direction).crs(camera.up).nor().scl(-deltaX * translateUnits));
            camera.translate(tmpV2.set(camera.up).scl(-deltaY * translateUnits));}

            return true;
        }

        if (Gdx.input.isButtonPressed(1)) {

            camera.direction.rotate(camera.up, deltaX);
            tmp.set(camera.direction).crs(camera.up).nor();
            camera.direction.rotate(tmp, deltaY);
        }

        delta.set(screenX , screenY).sub(last);

//        if (mode == TransformMode.Rotate) {
//            point.set(camera.position).sub(lookAt);
//
//            if (tmpV.set(point).nor().dot(yAxis) < 0.9999f) {
//                xAxis.set(camera.direction).crs(yAxis).nor();
//                rotMatrix.setToRotation(xAxis , delta.y / 5);
//                point.mul(rotMatrix);
//            }
//
//            rotMatrix.setToRotation(yAxis , -delta.x / 5);
//            point.mul(rotMatrix);
//
//            camera.position.set(point.add(lookAt));
//            camera.lookAt(lookAt.x , lookAt.y , lookAt.z);
//        }
//        if (mode == TransformMode.Zoom) {
//            camera.fieldOfView -= -delta.y / 10;
//        }
//        if (mode == TransformMode.Translate) {
//            tCurr.set(screenX , screenY);
//            translated = true;
//        }
//
//        if (mode == TransformMode.Look) {
//
//            camera.direction.rotate(camera.up , deltaX);
//            tmp.set(camera.direction).crs(camera.up).nor();
//            camera.direction.rotate(tmp , deltaY);
//        }

        camera.update();
        last.set(screenX , screenY);
        return true;
    }

    enum TransformMode
    {
        Look, Rotate, Translate, Zoom, None
    }

    enum ProjectionMode
    {
        Perspective, Orthographic
    }

}
