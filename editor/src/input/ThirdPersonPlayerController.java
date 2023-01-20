package input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.gltf.scene3d.scene.Scene;
import editor.project.settings.Settings;
import ui.UserInterface;

public class ThirdPersonPlayerController implements InputProcessor, AnimationController.AnimationListener
{

    private final Vector3 moveTranslation = new Vector3();
    private final Vector3 currentPosition = new Vector3();
    private final PerspectiveCamera camera;
    float speed = 2.5f;
    float rotationSpeed = 80f;
    float height = 0;
    private Matrix4 playerTransform = new Matrix4();
    private Scene playerScene;

    private CameraMode cameraMode = CameraMode.FREE_LOOK;
    private float camPitch = Settings.CAMERA_START_PITCH;
    private float distanceFromPlayer = 5f;
    private float angleAroundPlayer = 0f;
    private float angleBehindPlayer = 0f;
    private AnimState animState = AnimState.STAND;
    private AnimState lastAnimState = AnimState.STAND;

    public ThirdPersonPlayerController(PerspectiveCamera camera) {
        this.camera = camera;
    }

    public AnimState getAnimState() {
        return animState;
    }

    public void setAnimState(AnimState animState) {
        this.animState = animState;
    }

    public String getAnimUri() {
        return animState.uri;
    }

    public void processInput(Scene playerScene , PerspectiveCamera camera) {
        playerTransform.set(playerScene.modelInstance.transform);
        this.playerScene = playerScene;
        UserInterface.debugLabel1.setText(animState.toString());
        animState = AnimState.STAND;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            moveTranslation.z += speed * Gdx.graphics.getDeltaTime();
            animState = AnimState.WALK;
            lastAnimState = AnimState.WALK;
            playerScene.animationController.setAnimation(AnimState.WALK.uri , -1 , 1f , this);
        }
        else if (!Gdx.input.isKeyPressed(Input.Keys.W) && !Gdx.input.isKeyPressed(Input.Keys.S)) {
            playerScene.animationController.action(AnimState.STAND.uri , 1 , 1f , this , 0.5f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            animState = AnimState.WALK_BACK;
            lastAnimState = AnimState.WALK_BACK;
            moveTranslation.z -= speed * Gdx.graphics.getDeltaTime();
            playerScene.animationController.setAnimation(AnimState.WALK_BACK.uri , -1 , 0.5f , this);
        }
        else if (!Gdx.input.isKeyPressed(Input.Keys.W) && !Gdx.input.isKeyPressed(Input.Keys.S)) {
            playerScene.animationController.action(AnimState.STAND.uri , 1 , 1f , this , 0.5f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            animState = AnimState.WALK_STRAFE_LEFT;
            lastAnimState = AnimState.WALK_STRAFE_LEFT;
            playerTransform.rotate(Vector3.Y , rotationSpeed * Gdx.graphics.getDeltaTime());
            angleBehindPlayer += rotationSpeed * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            animState = AnimState.WALK_STRAFE_RIGHT;
            lastAnimState = AnimState.WALK_STRAFE_RIGHT;
            playerTransform.rotate(Vector3.Y , -rotationSpeed * Gdx.graphics.getDeltaTime());
            angleBehindPlayer -= rotationSpeed * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            animState = AnimState.JUMP;
            lastAnimState = AnimState.JUMP;
            playerScene.animationController.setAnimation(AnimState.JUMP_START.uri , -1 , 1f , this);
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            switch (cameraMode) {
                case FREE_LOOK:
                    cameraMode = CameraMode.BEHIND_PLAYER;
                    angleAroundPlayer = angleBehindPlayer;
                    break;
                case BEHIND_PLAYER:
                    cameraMode = CameraMode.FREE_LOOK;
                    break;
            }
        }
        playerTransform.translate(moveTranslation);

        camera.update();
        playerScene.modelInstance.transform.set(playerTransform);
        playerScene.modelInstance.transform.getTranslation(currentPosition);

        currentPosition.y = height;
        playerScene.modelInstance.transform.setTranslation(currentPosition);

        moveTranslation.setZero();

        UserInterface.debugLabel1.setText("CURRENT_ANIM_STATE:    " + animState.toString());
        UserInterface.debugLabel2.setText("PREVIOUS_ANIM_STATE:   " + lastAnimState.toString());
        UserInterface.debugLabel3.setText("CAMERA MODE:           " + cameraMode.toString());
        UserInterface.debugLabel4.setText("CURRENT_ANIM_DURATION: " + playerScene.animationController.current.duration);
        UserInterface.debugLabel5.setText("CURRENT_ANIM_TIME:     " + playerScene.animationController.current.time);

    }

    public void updateCamera() {
        float horDistance = this.calculateHorizontalDistance(distanceFromPlayer);
        float vertDistance = this.calculateVerticalDistance(distanceFromPlayer);
        this.calculatePitch();
        this.calculateAngleAroundPlayer();
        this.calculateCameraPosition(currentPosition , horDistance , vertDistance);
        camera.up.set(Vector3.Y);
        camera.lookAt(currentPosition);

        camera.update();
    }

    private float calculateHorizontalDistance(float distanceFromPlayer) {
        return (float) ( distanceFromPlayer * Math.cos(Math.toRadians(camPitch)) );
    }

    private float calculateVerticalDistance(float distanceFromPlayer) {
        return (float) ( distanceFromPlayer * Math.sin(Math.toRadians(camPitch)) );
    }

    private void calculatePitch() {
        float pitchChange = Gdx.input.getDeltaY() * Settings.CAMERA_PITCH_FACTOR;
        camPitch -= pitchChange;

        if (camPitch < Settings.CAMERA_MIN_PITCH) camPitch = Settings.CAMERA_MIN_PITCH;
        else if (camPitch > Settings.CAMERA_MAX_PITCH) camPitch = Settings.CAMERA_MAX_PITCH;
    }

    private void calculateAngleAroundPlayer() {
        if (cameraMode == CameraMode.FREE_LOOK) {
            float angleChange = Gdx.input.getDeltaX() * Settings.CAMERA_ANGLE_AROUND_PLAYER_FACTOR;
            angleAroundPlayer -= angleChange;
        }
        else {
            angleAroundPlayer = angleBehindPlayer;
        }
    }

    private void calculateCameraPosition(Vector3 currentPosition , float horDistance , float vertDistance) {
        float offsetX = (float) ( horDistance * Math.sin(Math.toRadians(angleAroundPlayer)) );
        float offsetZ = (float) ( horDistance * Math.cos(Math.toRadians(angleAroundPlayer)) );
        camera.position.x = currentPosition.x - offsetX;
        camera.position.z = currentPosition.z - offsetZ;
        camera.position.y = currentPosition.y + vertDistance;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        animState = AnimState.STAND;
        UserInterface.debugLabel1.setText(animState.toString());
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX , int screenY , int pointer , int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX , int screenY , int pointer , int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX , int screenY , int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX , int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX , float amountY) {
        float zoomLevel = amountY * Settings.CAMERA_ZOOM_LEVEL_FACTOR;
        if (distanceFromPlayer < Settings.CAMERA_MIN_DISTANCE_FROM_PLAYER)
            distanceFromPlayer = Settings.CAMERA_MIN_DISTANCE_FROM_PLAYER;
        else if (distanceFromPlayer > Settings.CAMERA_MAX_DISTANCE_FROM_PLAYER)
            distanceFromPlayer = Settings.CAMERA_MAX_DISTANCE_FROM_PLAYER;
        else distanceFromPlayer -= zoomLevel;
        return false;
    }

    @Override
    public void onEnd(AnimationController.AnimationDesc animation) {
        if (animState == AnimState.WALK) {
            playerScene.animationController.animate(AnimState.WALK.uri , -1 , this , 1f);
        }

    }

    @Override
    public void onLoop(AnimationController.AnimationDesc animation) {

    }

    enum CameraMode
    {
        FREE_LOOK, BEHIND_PLAYER
    }

    public enum AnimState
    {

        TURN_LEFT("TurnLeft"), TURN_RIGHT("TurnRight"), RUN_STRAFE_LEFT("RunStrafeLeft"), RUN_STRAFE_RIGHT("RunStrafeRight"), WALK_STRAFE_LEFT("WalkStrafeLeft"), WALK_STRAFE_RIGHT("WalkStrafeRight"), WALK_BACK("WalkBack"), RUN("Run"), WALK("Walk"), JUMP("Jump"), JUMP_START("JumpStart"), JUMP_MID("JumpMid"), JUMP_END("JumpEnd"), JUMP_FORWARD("JumpForward"), CROUCH("Crouch"), CROUCH_TO_STAND("CrouchToStand"), STAND_TO_CROUCH("StandToCrouch"), STOP("Stop"), STAND("Stand");

        public String uri;

        AnimState(String uri) {
            this.uri = uri;
        }

    }

}
