package ui.widgets;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisImageButton;
import sys.io.EditorIO;
import ui.windows.CameraControlsWindow;

public class CameraButtonTool {
    boolean cameraControlsOpen = false;
    CameraControlsWindow cameraControlsWindow;
    Table previewTable;
    public VisImageButton cameraButton;
    PerspectiveCamera camera;
    FirstPersonCameraController cameraController;
    Texture texture;
    TextureRegionDrawable cameraButtonDrawable;
Stage stage;
    public CameraButtonTool(Table table, Stage stage, PerspectiveCamera camera, FirstPersonCameraController cameraController){
        this.previewTable = table;
        this.stage = stage;
    /*    camera = InputProcessingSystem.camera;
        cameraController = cameraController;*/
        texture = EditorIO.io().loadTexture("icons/camera_edit.png","Camera Button Texture");
        cameraButtonDrawable = new TextureRegionDrawable();
        cameraButtonDrawable.setRegion(new TextureRegion(texture));
        cameraControlsWindow = new CameraControlsWindow("Camera Controls",camera, cameraController);
        cameraButton = new VisImageButton(cameraButtonDrawable);

        build();
        initListeners();

    }

    private void initListeners() {
        cameraButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!cameraControlsOpen) {
                    cameraControlsWindow = new CameraControlsWindow("Camera Controls,",camera,cameraController);
                    stage.addActor(cameraControlsWindow);
                    cameraControlsOpen = true;
                    sys.Log.info("CameraButtonTool","Camera Controls set to visible");
                } else {
                    cameraControlsWindow.remove();
                    cameraControlsOpen = false;
                    sys.Log.info("CameraButtonTool","Camera Controls set to hidden");
                }
            }
        });
    }

    private void build() {

    }

    public void setPreviewTable(Table table){
        this.previewTable = table;
        table.add(cameraButton);

    }

}
