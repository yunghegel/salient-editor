package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import net.mgsx.gltf.scene3d.scene.SceneManager;

import project.SettingsManager;
import ui.windows.SettingsWindow;

public class EditorMenuBar {
    Stage stage;
    MenuBar menuBar;
    Menu fileMenu;
    Menu editMenu;
    Menu viewMenu;
    FileChooser fileChooser;
    FileHandle fileHandle;
    FileChooserAdapter fileChooserAdapter;

    SettingsWindow settingsWindow;



    public EditorMenuBar(Stage stage) {
        this.stage = stage;
        build();
    }

    public void build() {
        menuBar = new MenuBar();
        menuBar.getTable().pad(5);

        menuBar.getTable().align(Align.topLeft).setWidth(Gdx.graphics.getWidth());

        fileMenu = new Menu("File");
        fileMenu.pad(3);
        fileMenu.setColor(0, 0, 0, 1);
        editMenu = new Menu("Edit");
        editMenu.pad(3);
        viewMenu = new Menu("View");
        viewMenu.pad(3);


        fileMenu.left();
        menuBar.addMenu(fileMenu);

        menuBar.addMenu(editMenu);
        menuBar.addMenu(viewMenu);

        MenuItem loadFile = new MenuItem("Load File", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                fileChooser = new FileChooser(FileChooser.Mode.OPEN);
                /*fileChooserAdapter = new FileChooserAdapter() {
                    @Override
                    public void selected(Array<FileHandle> files) {
                        fileHandle = files.first();
                        log.info("File selected: " + fileHandle.name());
                        GLTFComponent gltfComponent = new GLTFComponent(fileHandle.path(), fileHandle.name());
                        SceneSystem.gltfComponents.add(gltfComponent);
                        SceneSystem.addScene(gltfComponent);
                        SceneListView.buildListView();
                        TransformWidget.setComponent(gltfComponent);
                        try {
                            SceneSaveState.writeSceneToJson();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        try {
                            SceneSaveState.writeSceneToJson();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };*/
                fileChooser.setListener(fileChooserAdapter);
                stage.addActor(fileChooser.fadeIn());

            }
        });
        fileMenu.addItem(loadFile);

        MenuItem saveFile = new MenuItem("Save Scene", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
/*
                try {
                    SceneSaveState.writeSceneToJson();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
*/

            }
        });
        fileMenu.addItem(saveFile);
        menuBar.getTable().align(Align.left);

        MenuItem settings = new MenuItem("Settings", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showSettingsWindow();
            }
        });
        viewMenu.addItem(settings);
        MenuItem data = new MenuItem("Data", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        });
        viewMenu.addItem(data);
        MenuItem scene = new MenuItem("Scene", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        });


    }

    public void showSettingsWindow() {
        settingsWindow = new SettingsWindow(new SettingsManager(),new SceneManager());
        stage.addActor(settingsWindow.fadeIn());

        if (Gdx.input.isCursorCatched()) {
            Gdx.input.setCursorCatched(false);
        }


    }

    public void addImageTextButton(ImageTextButton imageTextButton) {
        menuBar.getTable().add(imageTextButton).growX().right();

    }

public MenuBar getMenu() {
    return menuBar;
}

    }


