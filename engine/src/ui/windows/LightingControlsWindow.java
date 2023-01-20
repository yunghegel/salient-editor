package ui.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.*;
import net.mgsx.gltf.scene3d.attributes.FogAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;

public class LightingControlsWindow extends VisWindow {
    VisCheckBox flag1;
    VisCheckBox flag2;
    VisCheckBox flag3;
    VisCheckBox flag4;
    VisCheckBox flag5;
    VisSlider slider1;
    VisSlider slider2;
    VisSlider slider3;
    VisSlider slider4;
    VisLabel slider1label;
    VisLabel slider2label;
    VisLabel slider3label;
    VisLabel slider4label;
    SceneManager sceneManager;
    VisTable windowTable;
    SceneSkybox skybox;
    DirectionalLightEx light;
    DirectionalShadowLight shadowLight;


    public LightingControlsWindow(String title, SceneManager sceneManager, DirectionalLightEx light, DirectionalShadowLight shadowLight) {
        super(title);
        sys.Log.info("LightingControlsWindow","Creating lighting controls window...");
        this.sceneManager = sceneManager;
        this.skybox = sceneManager.getSkyBox();
        this.light = light;
        this.shadowLight = shadowLight;

        windowTable = new VisTable();
        windowTable.setFillParent(true);
        windowTable.pad(15f);

        windowTable.left();



        createComponents();
        registerListeners();
        addComponentsToWindow();
        setSize(220, 300);
        //move it to the middle right of the screen
        setPosition(Gdx.graphics.getWidth()-50, Gdx.graphics.getHeight()-150, Align.topRight);
        addCloseButton();
        //justifty the window to the left
        //add exit button

        sys.Log.info("LightingControlsWindow","Cighting controls window created");
    }

    private void createComponents() {
        flag1 = new VisCheckBox("Skybox");
        flag1.setChecked(true);
        flag2 = new VisCheckBox("Directional Light");
        flag2.setChecked(true);
        flag3 = new VisCheckBox("Ambient Lighting");
        flag3.setChecked(true);
        flag4 = new VisCheckBox("Draw Shadows");
        flag4.setChecked(true);
        flag5 = new VisCheckBox("Fog Enabled");
        slider1 = new VisSlider(0f, 5f, 0.1f, false);
        slider1.setValue(light.intensity);
        slider2 = new VisSlider(0f, 360f, 0.01f, false);
        slider2.setValue(.5f);
        slider3 = new VisSlider(0f, 20f, 0.01f, false);
        slider4 = new VisSlider(0f, 1f, 0.01f, false);
        slider4.setValue(0.5f);


        slider1label = new VisLabel("Ambient Lighting Intensity: ");

        slider2label = new VisLabel("Environment Rotation: ");

        slider3label = new VisLabel("Shadow Light Intensity: ");

        slider4label = new VisLabel("Fog Density: ");


    }

    private void registerListeners(){
        flag1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (flag1.isChecked()) {
                    sceneManager.setSkyBox(skybox);
                } else {
                    sceneManager.setSkyBox(null);
                }

            }
        });
        flag2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (flag2.isChecked()) {
                    sceneManager.environment.add(light);
                } else {
                   sceneManager.environment.remove(light);
                }

            }
        });
        flag3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (flag3.isChecked()) {
                    sceneManager.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1f));
                } else {
                    sceneManager.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0f, 0f, 0f, 1f));
                }

            }
        });
        flag4.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (flag4.isChecked()) {
                    sceneManager.environment.add(shadowLight);
                } else {
                    sceneManager.environment.remove(shadowLight);

                }




            }
        });
        flag5.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (flag5.isChecked()) {
                    sceneManager.environment.set(new ColorAttribute(ColorAttribute.Fog, Color.WHITE));
                } else {
                    sceneManager.environment.remove(ColorAttribute.Fog);
                }
            }
        });
        slider1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sceneManager.setAmbientLight(slider1.getValue());
            }
        });
        slider2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sceneManager.setEnvironmentRotation(slider2.getValue());
            }
        });
        slider3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                shadowLight.intensity = slider3.getValue();
            }
        });
        slider4.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sceneManager.environment.set(FogAttribute.createFog(1f,100f,slider4.getValue()));

            }
        });
    }
    public void addComponentsToWindow(){
        int pad = 3;
        windowTable.add(flag1).left().pad(pad).row();
        windowTable.add(flag2).left().pad(pad).row();
        windowTable.add(flag3).left().pad(pad).row();
        windowTable.add(flag4).left().pad(pad).row();
        windowTable.add(slider1label).left().row();
        windowTable.add(slider1).left().pad(pad).row();
        windowTable.add(slider2label).left().row();
        windowTable.add(slider2).left().pad(pad).row();
        windowTable.add(slider3label).left().row();
        windowTable.add(slider3).left().pad(pad).row();
        windowTable.add(flag5).left().pad(pad).row();
windowTable.add(slider4label).left().row();
        windowTable.add(slider4).left().pad(pad).row();

        add(windowTable);
    }

}
