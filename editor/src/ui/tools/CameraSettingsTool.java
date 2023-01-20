package ui.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.*;
import editor.Context;
import ui.UserInterface;
import backend.tools.Log;
import ui.widgets.RenderWidget;

public class CameraSettingsTool extends AbstractTool
{

    public CameraSettingsTool.CameraControlsWindow cameraControlsWindow;
    Stage stage;
    VisImageButton cameraControlsButton;
    Texture cameraControlsIcon;
    boolean visible;
    Table targetTable;
    RenderWidget renderWidget;
    VisWindow targetWindow;

    public CameraSettingsTool(VisWindow window , Stage stage , PerspectiveCamera camera , FirstPersonCameraController cameraController , OrthographicCamera orthoCam , RenderWidget renderWidget) {
        cameraControlsWindow = new CameraSettingsTool.CameraControlsWindow(stage , camera , cameraController , orthoCam , renderWidget);

        this.stage = stage;
        this.targetWindow =window;
        createButton();
    }

    private void createButton() {
        cameraControlsIcon = new Texture(Gdx.files.internal("button_icons/camera_controls_icon.png"));
        TextureRegionDrawable cameraControlsIconDrawable = new TextureRegionDrawable(cameraControlsIcon);
        cameraControlsButton = new VisImageButton(cameraControlsIconDrawable);
        cameraControlsButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event , Actor actor) {
                if (visible) {
                    disable();
                    visible = false;
                }
                else {

                    enable();
                    visible = true;
                }

            }
        });

        targetWindow.add(cameraControlsButton).top().pad(5).row();
       // cameraControlsButton.setX(targetTable.getWidth() - 100);

    }

    @Override
    public void enable() {
        stage.addActor(cameraControlsWindow);
       // cameraControlsWindow.setPosition(UserInterface.middleSplitPane.getFirstWidgetBounds().width + cameraControlsWindow.getWidth() + 10 , Gdx.graphics.getHeight() - 40 , Align.topRight);
        cameraControlsWindow.setPosition(RenderWidget.getInstance().getRight()+45f, RenderWidget.getInstance().getTop()+95f, Align.center);
        if (UserInterface.getInstance().environmentSettingsTool.visible)
            UserInterface.getInstance().environmentSettingsTool.disable();
            UserInterface.getInstance().environmentSettingsTool.visible = false;
    }

    @Override
    public void disable() {
        cameraControlsWindow.remove();
    }

    public static class CameraControlsWindow extends VisWindow
    {

        Texture icon;
        PerspectiveCamera cam;
        OrthographicCamera orthoCam;
        RenderWidget renderWidget;
        InputProcessor inputProcessor;
        Stage stage;
        FirstPersonCameraController cameraController;
        float cameraSpeed = 30f;
        HorizontalGroup projectionSettings;
        VisTextButton setToOrthoButton;
        VisTextButton setToPerspectiveButton;
        VisTextButton lookAtXYPlaneButton;
        VisTextButton lookAtXZPlaneButton;
        VisTextButton lookAtYZPlaneButton;

        public CameraControlsWindow(Stage stage , PerspectiveCamera camera , FirstPersonCameraController cameraController , OrthographicCamera orthographicCamera , RenderWidget renderWidget) {
            super("Camera Controls");
            this.cam = camera;
            this.orthoCam = orthographicCamera;
            this.stage = stage;
            this.cameraController = cameraController;
            this.renderWidget = renderWidget;
            create();

            cameraController.setVelocity(cameraSpeed);
        }

        private void create() {
            VisTextButton cameraDecreaseSpeed = new VisTextButton("     -     ");
            cameraDecreaseSpeed.setSize(40 , 20);
            cameraDecreaseSpeed.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {
                    cameraSpeed = cameraSpeed - 5f;
                    cameraController.setVelocity(cameraSpeed - 5f);
                    Log.info("CameraControlsWindow" , "Camera speed changed: " + ( cameraSpeed + 5 ) + " -> " + cameraSpeed);

                }
            });
            VisTextButton cameraIncreaseSpeed = new VisTextButton("     +     ");
            cameraIncreaseSpeed.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {
                    cameraSpeed = cameraSpeed + 5f;
                    cameraController.setVelocity(cameraSpeed);
                    Log.info("CameraControlsWindow" , "Camera speed changed: " + ( cameraSpeed - 5 ) + " -> " + cameraSpeed);

                }
            });
            VisLabel cameraSpeedLabel = new VisLabel("Speed");

            VisSlider cameraFOVSlider = new VisSlider(0 , 180 , 1 , false);
            cameraFOVSlider.setValue(20);

            cameraFOVSlider.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {
                    cam.fieldOfView = cameraFOVSlider.getValue();
                    Log.info("CameraControlsWindow" , "Camera FOV changed: " + cam.fieldOfView);
                }
            });
            VisLabel cameraFOVLabel = new VisLabel("FOV");

            VisCheckBox enableInputProcessing = new VisCheckBox("Enable input processing");
            enableInputProcessing.setChecked(true);
            enableInputProcessing.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {
                    if (enableInputProcessing.isChecked()) {
                        Context.getInstance().inputMultiplexer.addProcessor(cameraController);
                        Gdx.input.setInputProcessor(Context.getInstance().inputMultiplexer);

                    }
                    else {
                        Context.getInstance().inputMultiplexer.removeProcessor(cameraController);
                        Gdx.input.setInputProcessor(stage);

                    }
                }
            });

            setToOrthoButton = new VisTextButton("Orthographic");
            setToOrthoButton.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {
                    renderWidget.setCam(orthoCam);

                    Log.info("CameraControlsWindow" , "Camera projection changed: " + cam.projection);
                }
            });

            setToPerspectiveButton = new VisTextButton("Perspective");
            setToPerspectiveButton.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {

                    Log.info("CameraControlsWindow" , "Camera projection changed: " + cam.projection);
                }
            });

            VisTextField directionX = new VisTextField();
            VisTextField directionY = new VisTextField();
            VisTextField directionZ = new VisTextField();

            VisLabel zoomLabel = new VisLabel("Zoom");
            VisSlider zoom = new VisSlider(0.01f , 0.1f , 0.01f , false);
            zoom.setValue(0.05f);

            directionX.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {
                    //only if the text is a number and not empty
                    if (directionX.getText().length() > 0 && directionX.getText().matches("-?\\d+(\\.\\d+)?")) {
                        cam.direction.x = Float.parseFloat(directionX.getText());
                        orthoCam.direction.x = Float.parseFloat(directionX.getText());
                    }
                }
            });

            directionY.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {
                    if (directionY.getText().length() > 0 && directionY.getText().matches("-?\\d+(\\.\\d+)?")) {
                        cam.direction.y = Float.parseFloat(directionY.getText());
                        orthoCam.direction.y = Float.parseFloat(directionY.getText());
                    }
                }
            });

            directionZ.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {
                    if (directionZ.getText().length() > 0 && directionZ.getText().matches("-?\\d+(\\.\\d+)?")) {
                        cam.direction.z = Float.parseFloat(directionZ.getText());
                        orthoCam.direction.z = Float.parseFloat(directionZ.getText());
                    }

                }
            });

            zoom.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {

                    orthoCam.zoom = zoom.getValue();
                    orthoCam.update();

                }
            });

            HorizontalGroup directionGroup = new HorizontalGroup();
            directionGroup.addActor(directionX);
            directionGroup.addActor(directionY);
            directionGroup.addActor(directionZ);

            directionGroup.wrap(true);

            projectionSettings = new HorizontalGroup();
            projectionSettings.addActor(setToOrthoButton);
            projectionSettings.space(30);
            projectionSettings.pad(10);
            projectionSettings.center();
            projectionSettings.align(Align.center);
            projectionSettings.addActor(setToPerspectiveButton);

            //add(projectionSettings).row();

            add(cameraSpeedLabel).pad(5f);
            add(cameraDecreaseSpeed).pad(5f);
            add(cameraIncreaseSpeed).pad(5f);
            row();
            add(cameraFOVLabel).pad(5f);
            add(cameraFOVSlider).pad(5f).colspan(2).growX();
            row();
            add(zoomLabel).pad(5f);
            add(zoom).pad(10f).colspan(2).growX();
            row();
            add(enableInputProcessing).pad(5f).colspan(2).growX();

            setSize(250 , 225);
            layout();
            //top().left();
           // setX(Gdx.graphics.getWidth() - 250);
            setY(Gdx.graphics.getHeight() - 400);
            setX(renderWidget.getWidth() - 250);
            Log.info("CameraControlsWindow" , "Camera controls window created");
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            orthoCam.view.set(cam.view);
            UserInterface.debugLabel1.setText("Camera Position: " + orthoCam.position + " Camera Direction: " + orthoCam.direction + " Zoom: " + orthoCam.zoom);
        }

    }

}
