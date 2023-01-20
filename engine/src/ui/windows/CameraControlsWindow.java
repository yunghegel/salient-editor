package ui.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import sys.Log;

public class CameraControlsWindow extends VisWindow {

    private FirstPersonCameraController cameraController;
    private float cameraSpeed=20f;
    private PerspectiveCamera camera;

    public CameraControlsWindow(String title, PerspectiveCamera camera, InputAdapter cameraController) {
        super(title);
        Log.info("creating camera controls window...");


        VisTextButton cameraDecreaseSpeed = new VisTextButton("-");
        cameraDecreaseSpeed.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                cameraSpeed = cameraSpeed - 5f;
                //cameraController.setVelocity(cameraSpeed - 5f);
                Log.info("CameraControlsWindow","Camera speed changed: " + cameraSpeed);

            }
        });
        VisTextButton cameraIncreaseSpeed = new VisTextButton("+");
        cameraIncreaseSpeed.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                cameraSpeed = cameraSpeed + 5f;
                //cameraController.setVelocity(cameraSpeed);
                Log.info("CameraControlsWindow","Camera speed changed: " + cameraSpeed);

            }
        });
        VisLabel cameraSpeedLabel = new VisLabel("Speed");

        //three columns



       /* root.add(cameraSpeedPane).growX().colspan(3).bottom().left().pad(10).row();
        root.row().grow();*/
        VisSlider cameraFOVSlider = new VisSlider(0, 180, 1, false);
        cameraFOVSlider.setValue(20);

        cameraFOVSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                camera.fieldOfView = cameraFOVSlider.getValue();
                Log.info("CameraControlsWindow","Camera FOV changed: " + camera.fieldOfView);
            }
        });
        VisLabel cameraFOVLabel = new VisLabel("FOV");



        add(cameraSpeedLabel).pad(10f);
        add(cameraDecreaseSpeed).pad(10f);
        add(cameraIncreaseSpeed).pad(10f);
        row();
        add(cameraFOVLabel).pad(10f);
        add(cameraFOVSlider).pad(10f).colspan(2).growX();
        addCloseButton();
        setSize(220,200);
        layout();
        top().left();
        setX(Gdx.graphics.getWidth()-250);
        setY(Gdx.graphics.getHeight()-400);
        Log.info("CameraControlsWindow","Camera controls window created");
    }

    public void injectDependencies(FirstPersonCameraController cameraController, PerspectiveCamera camera) {
        this.cameraController = cameraController;
        this.camera = camera;
        this.cameraSpeed = camera.fieldOfView;
    }
}
