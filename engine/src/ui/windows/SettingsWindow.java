package ui.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;

import ecs.systems.PhysicsSystem;
import net.mgsx.gltf.scene3d.scene.SceneManager;

import project.SettingsManager;
import sys.io.DefaultAssets;


import static sys.Log.info;
public class SettingsWindow extends VisWindow {
    private Tab graphicsTab;

    //general
    VisTextField gravityInput;
    public boolean axisToggle;
    public boolean debugDrawToggle;
    public boolean cameraToggle;
    ButtonBar buttonBar;
    VisSlider gravitySlider;;
    VisCheckBox axisToggleBox;
    VisCheckBox debugDrawToggleBox;
    VisSelectBox<String> cameraModeSelectBox;
    SettingsTab generalTab;;

    //graphics
    VisSelectBox<Integer> AASelectBox;
    VisCheckBox vsyncToggleBox;
    VisCheckBox fullscreenToggleBox;
    VisCheckBox shadowsToggleBox;
    VisCheckBox fogToggleBox;
    VisSlider fogDensitySlider;
    VisSlider fogExponentSlider;
    VisTextField fogNear;
    VisTextField fogFar;
    VisSlider ambientSlider;
    VisSlider diffuseSlider;
    VisSlider specularSlider;
    VisSlider shadowIntensitySlider;
    VisSlider shadowSoftnessSlider;


    public boolean vsyncToggle;
    public boolean fullscreenToggle;
    public boolean shadowsToggle;
    public boolean fogToggle;





    public SettingsWindow (SettingsManager settingsManager, SceneManager sceneManager){
        super("Settings");
        TableUtils.setSpacingDefaults(this);

        setResizable(true);
        addCloseButton();
        closeOnEscape();

        final VisTable container = new VisTable();
        container.defaults().pad(15)    ;
        buttonBar = new ButtonBar();




        TabbedPane tabbedPane = new TabbedPane();
        tabbedPane.addListener(new TabbedPaneAdapter() {
            @Override
            public void switchedTab (Tab tab) {
                container.clearChildren();
                container.add(tab.getContentTable()).expand().fill();



            }
        });


        add(tabbedPane.getTable()).expandX().fillX();
        row();
        add(container).expand().fill();

        generalTab = new SettingsTab("General",null);
        generalTab.getContentTable().defaults().pad(15);

        createGeneralTab(generalTab);
        generalTab.getContentTable().align(Align.topLeft);
        tabbedPane.add(generalTab);

        graphicsTab = new SettingsTab("Graphics",settingsManager);

        graphicsTab.getContentTable().defaults().pad(15);
        createGraphicsTab(graphicsTab);
       tabbedPane.add(graphicsTab);


        tabbedPane.add(new SettingsTab("Audio", settingsManager));
        tabbedPane.add(new SettingsTab("Controls", settingsManager));
        tabbedPane.add(new SettingsTab("Network", settingsManager));
        tabbedPane.add(new SettingsTab("Misc", settingsManager));

        setSize(550, 400);

        centerWindow();

        initListeners();

    }

    private void createGraphicsTab(Tab graphicsTab) {
        AASelectBox = new VisSelectBox<Integer>();
        AASelectBox.setItems(0, 2, 4, 8);
        AASelectBox.setSelected(0);
        AASelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                info("AASelectBox changed");
                //Settings permanence for options that require reload
            }
        });

        fullscreenToggleBox = new VisCheckBox("Fullscreen");
        fullscreenToggleBox.setChecked(false);
        fullscreenToggleBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                fullscreenToggle = fullscreenToggleBox.isChecked();

                try {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                } catch (Exception e) {
                    info("Error changing fullscreen mode");
                }

            }
        });

        vsyncToggleBox = new VisCheckBox("Vsync");
        vsyncToggleBox.setChecked(false);
        vsyncToggleBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                vsyncToggle = vsyncToggleBox.isChecked();
                Gdx.graphics.setVSync(vsyncToggle);
            }
        });

        shadowsToggleBox = new VisCheckBox("Shadows");
        shadowsToggleBox.setChecked(false);
        shadowsToggleBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                shadowsToggle = shadowsToggleBox.isChecked();

            }
        });

        fogToggleBox = new VisCheckBox("Fog");
        fogToggleBox.setChecked(false);
        fogToggleBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                fogToggle = fogToggleBox.isChecked();

            }
        });

        fogDensitySlider = new VisSlider(0, 1, 0.01f, false);
        fogDensitySlider.setValue(0.5f);
        fogDensitySlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        });

        Table lightingSection = new Table();
        lightingSection.defaults().pad(15);


        lightingSection.add(shadowsToggleBox).left();
        lightingSection.row();
        lightingSection.add(fogToggleBox).left();
        lightingSection.row();
        lightingSection.add(new VisLabel ("Fog Density")).left();
        lightingSection.add(fogDensitySlider).left();
        lightingSection.pack();





        Table graphicsTabContent = new Table();
        graphicsTabContent.pad(15);
        graphicsTabContent.defaults().pad(15);

        graphicsTabContent.add(new VisLabel("Anti-Aliasing")).left().pad(10);
        graphicsTabContent.add(AASelectBox).left().pad(5);
        graphicsTabContent.row();
        graphicsTabContent.add(fullscreenToggleBox).left().pad(10);
        graphicsTabContent.add(vsyncToggleBox).left().pad(10);
        graphicsTabContent.align(Align.topLeft);

        graphicsTabContent.add(lightingSection).right().pad(10);
        graphicsTab.getContentTable().add(graphicsTabContent).expand().right();
        graphicsTabContent.pack();
        graphicsTab.getContentTable().pack();








    }

    public void createGeneralTab(SettingsTab tab){
           gravitySlider = new VisSlider(-20, 20, 1, false);

           gravityInput = new VisTextField();
              gravityInput.setText("-10");
              gravityInput.setWidth(30);


           debugDrawToggleBox = new VisCheckBox("Debug Draw");
              axisToggleBox = new VisCheckBox("Show Axis");
                cameraModeSelectBox = new VisSelectBox<String>();
                cameraModeSelectBox.setItems("Captured", "Uncaptured");
                axisToggleBox.setChecked(true);

                cameraModeSelectBox.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if(cameraModeSelectBox.getSelected().equals("Captured")){
                            cameraToggle = true;
                        }else{
                            cameraToggle = false;
                        }
                    }
                });

                Table generalTabContent = new Table();
                generalTabContent.pad(30);
                generalTabContent.defaults().pad(15);

                generalTabContent.setSkin(DefaultAssets.skin);

                generalTabContent.add("Gravity").left().row();
                generalTabContent.add(gravityInput).left();
                generalTabContent.add(gravitySlider).left().row();

                generalTabContent.add(debugDrawToggleBox).left();

                generalTabContent.add(axisToggleBox).left().row();
                generalTabContent.add("Camera Mode").left().pad(5);
                generalTabContent.add(cameraModeSelectBox).left().pad(10).row();
                tab.getContentTable().add(generalTabContent).expand().fill();

    }

    public void initListeners(){
        //GENERAL

        gravitySlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gravityInput.setText(String.valueOf(gravitySlider.getValue()));
                //PhysicsSystem.gravity = new Vector3(0,gravitySlider.getValue(),0);
                info("Gravity set to: " + gravitySlider.getValue());
            }
        });
        gravityInput.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gravitySlider.setValue(Float.parseFloat(gravityInput.getText()));
              //  EditorContext.getInstance().physicsSystem.setGravity(gravitySlider.getValue());
                info("Gravity set to: " + gravitySlider.getValue());

            }
        });
        axisToggleBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                axisToggle = axisToggleBox.isChecked();
                if(!axisToggle) {
                   // RenderSystem.removeScene(RenderSystem.axesScene);
                }
                else{
                   // RenderSystem.addScene(RenderSystem.axesScene);
                }
               // RenderSystem.drawAxes = axisToggle;
                info("Axis toggle set to: " + axisToggle);
            }
        });
        debugDrawToggleBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                debugDrawToggle = debugDrawToggleBox.isChecked();
                //PhysicsSystem.debugDraw = debugDrawToggle;
                info("Debug Draw toggle set to: " + debugDrawToggle);
            }
        });

    }

    @Override
    public void close () {
        setVisible(false);
    }

    private class SettingsTab extends Tab {
        private String title;
        private Table content;

        public SettingsTab (String title, SettingsManager settingsManager) {
            super(false, false);
            this.title = title;

            content = new VisTable();

        }

        @Override
        public String getTabTitle () {
            return title;
        }

        @Override
        public Table getContentTable () {

            return content;
        }

        public void setContent (Table content) {
            this.content = content;
        }

    }
}
