package ui.widgets;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.*;

import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import sys.io.EditorIO;
import ui.windows.LightingControlsWindow;

public class LightingButtonTool implements ButtonTool {
    Texture texture;
    public VisImageButton button;
    String path;
    VisCheckBox flag1;
    VisCheckBox flag2;
    VisCheckBox flag3;
    VisCheckBox flag4;
    VisSlider slider1;
    VisSlider slider2;
    VisSlider slider3;
    VisLabel slider1label;
    VisLabel slider2label;
    VisLabel slider3label;
    SceneManager sceneManager;
    VisTable windowTable;
    SceneSkybox skybox;
    DirectionalLightEx light;
    DirectionalShadowLight shadowLight;
    LightingControlsWindow window;
    boolean lightingControlsOpen = false;
    Table parentTable;
Stage stage;



    public LightingButtonTool(Table parentTable, Stage stage,SceneManager sceneManager, DirectionalLightEx light, DirectionalShadowLight shadowLight) {
        this.stage = stage;
        this.parentTable = parentTable;
        texture = EditorIO.io().loadTexture("icons/lightbulb.png","Lighting Button Texture");
        this.sceneManager = sceneManager;
        this.skybox = sceneManager.getSkyBox();
        this.light = light;
        this.shadowLight = shadowLight;


        this.shadowLight = new DirectionalShadowLight();
        TextureRegionDrawable lightButtonDrawable = new TextureRegionDrawable();
        lightButtonDrawable.setRegion(new TextureRegion(texture));

        window = new LightingControlsWindow("Lighting Controls", sceneManager, light, shadowLight);
        button = new VisImageButton(lightButtonDrawable);
        button.setFocusBorderEnabled(true);

        initListeners();
    }

    public void initListeners(){
        button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!lightingControlsOpen) {
                    window = new LightingControlsWindow("Light Controls,",sceneManager,light,shadowLight);

                    stage.addActor(window);
                    lightingControlsOpen = true;
                    sys.Log.info("LightingButtonTool","Lighting Controls set to visible");
                } else {
                    window.remove();
                    lightingControlsOpen = false;
                    sys.Log.info("LightingButtonTool","Lighting Controls set to hidden");
                }

            }
        });
    }


    @Override
    public void setIcon(String icon) {

    }

    @Override
    public void setParentTable(Table parentTable) {
        parentTable.add(button);

    }

    public void setScreen(Screen screen){

    }
}


