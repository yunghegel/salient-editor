package tests;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

public class TestSelectionScreen extends ScreenAdapter
{
    Game game;
    private Stage stage;
    private VisSelectBox<TestSelectionScreen.Tests> screenSelect;
    private VisWindow window;
    private VisTextButton exitButton;
    private VisTextButton settingsButton;
    private VisSplitPane splitPane;
    ButtonBar buttonBar;

    public enum Tests
    {
        Empty,
        BaseTest,
        ShaderTest,
        GizmoTest,
        UITest,
        GeometryTest,
    }

    public TestSelectionScreen(Game game) {
        this.game = game;
        if (!VisUI.isLoaded())
            VisUI.load(new Skin(Gdx.files.internal("skin/tixel.json")));

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        Gdx.input.setInputProcessor(stage);

        VisTable selectTable = new VisTable();
        VisTable buttonTable = new VisTable();



        selectTable.pad(30);

        exitButton = new VisTextButton("Exit");
        exitButton.setWidth(100f);
        exitButton.align(Align.left);



        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        settingsButton = new VisTextButton("Settings");
        settingsButton.setWidth(100f);
        settingsButton.align(Align.right);

        settingsButton.setWidth(100f);
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        });


        buttonTable.setHeight(100);
        buttonTable.setWidth(250);
        buttonTable.add(exitButton).pad(10).align(Align.left);

        buttonTable.add(settingsButton).pad(10).align(Align.right);
        buttonTable.scaleBy(1.25f);



        screenSelect = new VisSelectBox<>();
        screenSelect.setItems(Tests.values());
        screenSelect.setSelected(Tests.Empty);
        screenSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                switch (screenSelect.getSelected()) {
                    case BaseTest:
                        game.setScreen(new BaseTest(game));

                        break;
                    case ShaderTest:
                        //game.setScreen(new RigidBodyPhysics(game));

                        game.setScreen(new ShaderTest(game));
                        break;
                    case GizmoTest:


                        game.setScreen(new GizmoTest(game));
                        break;
                    case UITest:
                        game.setScreen(new UITest(game));
                        break;
                    case GeometryTest:
                        game.setScreen(new GeometryTest(game));
                        break;
                }

            }
        });

        selectTable.add(new VisLabel("Select test: ")).colspan(2);
        selectTable.row().pad(10, 0, 10, 0);
        selectTable.add(screenSelect).colspan(2).fillX().expandX();
        selectTable.row();





        selectTable.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        //buttonTable.setPosition(stage.getWidth() / 2, stage.getHeight() / 3);
        buttonTable.setWidth(250);

        buttonTable.align(Align.center);
        selectTable.row();
        selectTable.add(exitButton).pad(10);

        selectTable.add(settingsButton).pad(10);
        //selectTable.add(buttonTable);
        window = new VisWindow("Tests");
        window.setFillParent(true);
        window.add(selectTable);

        stage.addActor(window);




        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            Gdx.app.exit();
        }

        ScreenUtils.clear(Color.BLACK);
        stage.act();
        stage.draw();
        if (Gdx.input.isCursorCatched()) {
            Gdx.input.setCursorCatched(false);
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}


