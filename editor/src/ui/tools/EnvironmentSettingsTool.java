package ui.tools;

import backend.tools.Log;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;
import editor.Context;
import net.mgsx.gltf.scene3d.attributes.FogAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import ui.UserInterface;
import ui.widgets.RenderWidget;
import util.SceneUtils;

public class EnvironmentSettingsTool extends AbstractTool
{

    VisImageButton environmentControlsButton;
    EnvironmentSettingsWindow environmentSettingsWindow;
    Texture environmentControlsButtonIcon;
    boolean visible;
    Table targetTable;
    Stage stage;
    VisWindow targetWindow;
    Context context;

    public EnvironmentSettingsTool(VisWindow targetWindow , Stage stage , SceneManager sceneManager , Context context) {
        this.targetWindow = targetWindow;
        this.stage = stage;
        this.context = context;
        environmentSettingsWindow = new EnvironmentSettingsWindow(sceneManager , stage , context);
        createButton();

    }

    private void createButton() {
        environmentControlsButtonIcon = new Texture("button_icons/env_settings_icon.png");
        TextureRegionDrawable envControlsIconDrawable = new TextureRegionDrawable(environmentControlsButtonIcon);
        environmentControlsButton = new VisImageButton(envControlsIconDrawable);
        environmentControlsButton.addListener(new ChangeListener()
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

        targetWindow.add(environmentControlsButton).top().pad(5).row();

    }

    @Override
    public void enable() {
        stage.addActor(environmentSettingsWindow);
        environmentSettingsWindow.setPosition(RenderWidget.getInstance().getRight()-33, RenderWidget.getInstance().getTop()+55f, Align.center);
        if (UserInterface.getInstance().cameraSettingsTool.visible)
            UserInterface.getInstance().cameraSettingsTool.disable();
            UserInterface.getInstance().cameraSettingsTool.visible = false;
    }

    @Override
    public void disable() {
        environmentSettingsWindow.remove();
    }

    public enum DefaultSkyboxes
    {
        EDITOR, DEFAULT, APOCALYPSE, APOCALYPSE_LAND, APOCALYPSE_OCEAN, CLASSIC, CLASSIC_LAND, CLEAR, CLEAR_OCEAN, DAWN, DUSK, DUSK_LAND, DUSK_OCEAN, EMPTY_SPACE, GRAY, MOODY, NETHERWORLD, SINISTER, SINISTER_LAND, SINISTER_OCEAN, SUNSHINE
    }

    static class EnvironmentSettingsWindow extends VisWindow
    {

        public boolean colorPickerOpen = false;
        VisCheckBox skyboxCheckbox;
        VisCheckBox axesFlag;
        VisCheckBox directionalLightFlag;
        VisCheckBox ambientLightFlag;
        VisCheckBox shadowLightFlag;
        VisCheckBox fogFlag;
        VisSlider ambientLightSlider;
        VisSlider envRotationSlider;
        VisSlider shadowLightIntensitySlider;
        VisSlider fogIntensitySlider;
        VisSlider fogNearSlider;
        VisSlider fogFarSlider;
        VisLabel ambientLightIntensityLabel;
        VisLabel envRotationLabel;
        VisLabel shadowLightIntensityLabel;
        VisLabel fogDensityLabel;
        VisLabel fogNearLabel;
        VisLabel fogFarLabel;
        VisLabel skyboxLabel;
        SceneManager sceneManager;
        VisTable windowTable;
        SceneSkybox skybox;
        DirectionalLightEx light;
        DirectionalShadowLight shadowLight;
        ColorAttribute fog;
        ColorPicker picker;
        VisTextButton fogColorButton;
        FogAttribute fogAttribute;
        Color currentColor;
        Context context;
        HorizontalGroup skyboxGroup;
        Cubemap cubemap;
        Color fogColor = new Color(Color.valueOf("7f7f7f"));
        VisSelectBox<EnvironmentSettingsTool.DefaultSkyboxes> skyboxSelectionBox;
        private Stage stage;

        public EnvironmentSettingsWindow(SceneManager sceneManager , Stage stage , Context context) {
            super("Environment Settings");
            picker = new ColorPicker();
            currentColor = new Color(Color.WHITE);

            this.context = context;
            this.sceneManager = sceneManager;
            this.stage = stage;
            skybox = sceneManager.getSkyBox();
            light = Context.light;
            shadowLight = Context.shadowLight;
            create();
            windowTable = new VisTable();

            windowTable.add(ambientLightFlag).left().pad(5);
            windowTable.add(ambientLightIntensityLabel).left().pad(5);
            windowTable.add(ambientLightSlider).left().pad(5);
            windowTable.row();
            windowTable.add(directionalLightFlag).left().pad(5);
            windowTable.add(envRotationLabel).left().pad(5);
            windowTable.add(envRotationSlider).left().pad(5);
            windowTable.row();
            windowTable.add(shadowLightFlag).left().pad(5);
            windowTable.add(shadowLightIntensityLabel).left().pad(5);
            windowTable.add(shadowLightIntensitySlider).left().pad(5);
            windowTable.row();
            windowTable.add(fogFlag).left().pad(5);
            windowTable.add(fogDensityLabel).left().pad(5);
            windowTable.add(fogIntensitySlider).left().pad(5);
            windowTable.row();
            windowTable.add(fogColorButton).left().pad(5);

            windowTable.add(fogNearLabel).left().pad(5);
            windowTable.add(fogNearSlider).left().pad(5);
            windowTable.row();
            windowTable.add(axesFlag).left().pad(5);
            windowTable.add(fogFarLabel).left().pad(5);
            windowTable.add(fogFarSlider).left().pad(5).row();
            Separator separator = new Separator();
            separator.setColor(Color.WHITE);
            windowTable.add(separator).colspan(3).fillX().expandX().pad(5).row();
            windowTable.add(skyboxLabel).left().pad(5).row();
            windowTable.add(skyboxGroup).fillX().expandX().pad(5).colspan(3).row();

            add(windowTable);
            pack();

            fog = (ColorAttribute) sceneManager.environment.get(ColorAttribute.Fog);
        }

        private void create() {

            skyboxCheckbox = new VisCheckBox("Skybox Enabled");
            skyboxCheckbox.setChecked(false);
            skyboxCheckbox.padRight(10);

            skyboxSelectionBox = new VisSelectBox<EnvironmentSettingsTool.DefaultSkyboxes>();
            skyboxSelectionBox.setItems(EnvironmentSettingsTool.DefaultSkyboxes.values());
            skyboxSelectionBox.setAlignment(Align.center);
            skyboxLabel = new VisLabel("Skybox Settings");

            skyboxGroup = new HorizontalGroup();
            skyboxGroup.addActor(skyboxCheckbox);
            skyboxGroup.addActor(skyboxSelectionBox);

            axesFlag = new VisCheckBox("Draw Axes");
            axesFlag.setChecked(true);
            directionalLightFlag = new VisCheckBox("Directional Light");
            directionalLightFlag.setChecked(true);
            ambientLightFlag = new VisCheckBox("Ambient Lighting");
            ambientLightFlag.setChecked(true);
            shadowLightFlag = new VisCheckBox("Draw Shadows");
            shadowLightFlag.setChecked(true);
            fogFlag = new VisCheckBox("Fog Enabled");
            fogFlag.setChecked(true);

            ambientLightSlider = new VisSlider(.01f , .5f , 0.01f , false);
            ambientLightSlider.setValue(0.1f);
            envRotationSlider = new VisSlider(0f , 360f , 0.01f , false);
            envRotationSlider.setValue(.5f);
            shadowLightIntensitySlider = new VisSlider(0f , 20f , 0.01f , false);
            fogIntensitySlider = new VisSlider(0.1f , 100f , 0.1f , false);
            fogIntensitySlider.setValue(1f);
            fogNearSlider = new VisSlider(.1f , 1f , 0.01f , false);
            fogNearSlider.setValue(.5f);
            fogFarSlider = new VisSlider(1f , 1000f , 1f , false);
            fogFarSlider.setValue(100f);

            ambientLightIntensityLabel = new VisLabel("Ambient Lighting Intensity: ");
            envRotationLabel = new VisLabel("Environment Rotation: ");
            shadowLightIntensityLabel = new VisLabel("Shadow Light Intensity: ");
            fogDensityLabel = new VisLabel("Fog Density: ");
            fogNearLabel = new VisLabel("Fog Near: ");
            fogFarLabel = new VisLabel("Fog Far: ");
            fogColorButton = new VisTextButton("Fog Color");

            fogColorButton.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {
                    stage.addActor(picker.fadeIn());
                    colorPickerOpen = true;

                    picker.setListener(new ColorPickerListener()
                    {
                        @Override
                        public void canceled(Color oldColor) {
                            colorPickerOpen = false;
                            picker.remove();

                        }

                        @Override
                        public void changed(Color newColor) {
                            currentColor = newColor;
                            sceneManager.environment.set(new ColorAttribute(ColorAttribute.AmbientLight , newColor));

                        }

                        @Override
                        public void reset(Color previousColor , Color newColor) {

                        }

                        @Override
                        public void finished(Color newColor) {
                            currentColor = newColor;
                            sceneManager.environment.set(new ColorAttribute(ColorAttribute.AmbientLight , newColor));
                            picker.remove();
                        }
                    });

                }
            });

            axesFlag.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {

                    context.sceneRenderer.toggleAxes();
                }
            });

            skyboxCheckbox.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {
                    if (skyboxCheckbox.isChecked()) {
                        sceneManager.setSkyBox(skybox);
                    }
                    else {
                        sceneManager.setSkyBox(null);
                    }

                }
            });

            directionalLightFlag.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {
                    if (directionalLightFlag.isChecked()) {
                        sceneManager.environment.add(light);
                    }
                    else {
                        sceneManager.environment.remove(light);
                    }

                }
            });

            ambientLightFlag.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {
                    if (ambientLightFlag.isChecked()) {
                        sceneManager.environment.set(new ColorAttribute(ColorAttribute.AmbientLight , 0f , 0f , 0f , 0.01f));

                    }
                    else {
                        sceneManager.environment.set(new ColorAttribute(ColorAttribute.AmbientLight , 0f , 0f , 0f , 0.01f));
                    }

                }
            });

            shadowLightFlag.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {
                    if (shadowLightFlag.isChecked()) {
                        sceneManager.environment.add(shadowLight);
                        sceneManager.setCamera(context.getCamera());
                    }
                    else {
                        sceneManager.environment.remove(shadowLight);

                    }



                }
            });

            fogFlag.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {
                    if (fogFlag.isChecked()) {
                        sceneManager.environment.set(new ColorAttribute(ColorAttribute.Fog , fogColor));

                        sceneManager.environment.set(( FogAttribute.createFog((float) fogNearSlider.getValue() , fogFarSlider.getValue() , fogIntensitySlider.getValue()) ));
                    }
                    else {

                        sceneManager.environment.remove(ColorAttribute.Fog);
                    }
                }
            });
            ambientLightSlider.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {
                    float lum = ambientLightSlider.getValue();
                    sceneManager.environment.set(new ColorAttribute(ColorAttribute.AmbientLight , lum, lum, lum, 0.01f));

                    //sceneManager.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, currentColor.r, currentColor.g, currentColor.b, slider1.getValue()));

                }
            });
            envRotationSlider.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {
                    sceneManager.setEnvironmentRotation(envRotationSlider.getValue());
                }
            });
            shadowLightIntensitySlider.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {
                    shadowLight.intensity = shadowLightIntensitySlider.getValue();
                }
            });
            fogIntensitySlider.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {
                    sceneManager.environment.set(new ColorAttribute(ColorAttribute.Fog , fogColor));
                    sceneManager.environment.set(FogAttribute.createFog(fogNearSlider.getValue() , fogIntensitySlider.getValue() , fogIntensitySlider.getValue()));

                }
            });

            fogNearSlider.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {
                    sceneManager.environment.set(new ColorAttribute(ColorAttribute.Fog , fogColor));
                    sceneManager.environment.set(FogAttribute.createFog(fogNearSlider.getValue() , fogFarSlider.getValue() , fogIntensitySlider.getValue()));

                }
            });

            fogFarSlider.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {
                    sceneManager.environment.set(new ColorAttribute(ColorAttribute.Fog , fogColor));
                    sceneManager.environment.set(FogAttribute.createFog(fogNearSlider.getValue() , fogFarSlider.getValue() , fogIntensitySlider.getValue()));

                }
            });

            skyboxSelectionBox.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event , Actor actor) {
                    switch (skyboxSelectionBox.getSelected()) {
                        case DEFAULT:
                            cubemap = SceneUtils.createCubemapXYZFormat("default");
                            skybox = new SceneSkybox(cubemap);
                            sceneManager.setSkyBox(skybox);

                            break;
                        case APOCALYPSE:
                            cubemap = SceneUtils.createCubemapDirectionFormat("apocalypse");
                            skybox = new SceneSkybox(cubemap);
                            sceneManager.setSkyBox(skybox);
                            break;
                        case APOCALYPSE_LAND:
                            cubemap = SceneUtils.createCubemapDirectionFormat("apocalypse_land");
                            skybox = new SceneSkybox(cubemap);
                            sceneManager.setSkyBox(skybox);
                            break;
                        case APOCALYPSE_OCEAN:
                            cubemap = SceneUtils.createCubemapDirectionFormat("apocalypse_ocean");
                            skybox = new SceneSkybox(cubemap);
                            sceneManager.setSkyBox(skybox);
                            break;
                        case CLASSIC:
                            cubemap = SceneUtils.createCubemapDirectionFormat("classic");
                            skybox = new SceneSkybox(cubemap);
                            sceneManager.setSkyBox(skybox);
                            break;

                        case CLASSIC_LAND:
                            cubemap = SceneUtils.createCubemapDirectionFormat("classic_land");
                            skybox = new SceneSkybox(cubemap);
                            sceneManager.setSkyBox(skybox);
                            break;

                        case CLEAR:
                            cubemap = SceneUtils.createCubemapDirectionFormat("clear");
                            skybox = new SceneSkybox(cubemap);
                            sceneManager.setSkyBox(skybox);
                            break;
                        case CLEAR_OCEAN:
                            cubemap = SceneUtils.createCubemapDirectionFormat("clear_ocean");
                            skybox = new SceneSkybox(cubemap);
                            sceneManager.setSkyBox(skybox);
                            break;
                        case DAWN:
                            cubemap = SceneUtils.createCubemapDirectionFormat("dawn");
                            skybox = new SceneSkybox(cubemap);
                            sceneManager.setSkyBox(skybox);
                            break;
                        case DUSK:
                            cubemap = SceneUtils.createCubemapDirectionFormat("dusk");
                            skybox = new SceneSkybox(cubemap);
                            sceneManager.setSkyBox(skybox);
                            break;
                        case DUSK_LAND:
                            cubemap = SceneUtils.createCubemapDirectionFormat("dusk_land");
                            skybox = new SceneSkybox(cubemap);
                            sceneManager.setSkyBox(skybox);
                            break;
                        case DUSK_OCEAN:
                            cubemap = SceneUtils.createCubemapDirectionFormat("dusk_ocean");
                            skybox = new SceneSkybox(cubemap);
                            sceneManager.setSkyBox(skybox);
                            break;
                        case EMPTY_SPACE:
                            cubemap = SceneUtils.createCubemapDirectionFormat("empty_space");
                            skybox = new SceneSkybox(cubemap);
                            sceneManager.setSkyBox(skybox);
                            break;
                        case MOODY:
                            cubemap = SceneUtils.createCubemapDirectionFormat("moody");
                            skybox = new SceneSkybox(cubemap);
                            sceneManager.setSkyBox(skybox);
                            break;
                        case NETHERWORLD:
                            cubemap = SceneUtils.createCubemapDirectionFormat("netherworld");
                            skybox = new SceneSkybox(cubemap);
                            sceneManager.setSkyBox(skybox);
                            break;
                        case SINISTER:
                            cubemap = SceneUtils.createCubemapDirectionFormat("sinister");
                            skybox = new SceneSkybox(cubemap);
                            sceneManager.setSkyBox(skybox);
                            break;
                        case SINISTER_LAND:
                            cubemap = SceneUtils.createCubemapDirectionFormat("sinister_land");
                            skybox = new SceneSkybox(cubemap);
                            sceneManager.setSkyBox(skybox);
                            break;
                        case SINISTER_OCEAN:
                            cubemap = SceneUtils.createCubemapDirectionFormat("sinister_ocean");
                            skybox = new SceneSkybox(cubemap);
                            sceneManager.setSkyBox(skybox);
                            break;
                        case SUNSHINE:
                            cubemap = SceneUtils.createCubemapDirectionFormat("sunshine");
                            skybox = new SceneSkybox(cubemap);
                            sceneManager.setSkyBox(skybox);
                            break;
                        case EDITOR:
                            cubemap = SceneUtils.createCubemapDirectionFormat("editor");
                            skybox = new SceneSkybox(cubemap);
                            sceneManager.setSkyBox(skybox);

                    }
                }
            });
        }

    }

}
