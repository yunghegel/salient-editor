package tests;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.CollapsibleWidget;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import ui.elements.ComponentInspector;

public class UITest extends ScreenAdapter
{
    Game game;
    private Stage stage;
    private VisTable root;
    VisWindow window1;
    CollapsibleWidget collapsibleWidget;
    VisTable widget1Table;
    VisTable widget2Table;
    VisTable widget3Table;
    CollapsibleWidget collapsibleWidget2;
    CollapsibleWidget collapsibleWidget3;
    CollapsibleWidget collapsibleWidget1;


    ComponentInspector componentInspector;
    public UITest(Game game) {
        super();
        this.game = game;
        if (!VisUI.isLoaded())
            VisUI.load(new Skin(Gdx.files.internal("skin/tixel.json")));
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        root = new VisTable();
        root.setFillParent(true);
        stage.addActor(root);

        window1 = new VisWindow("Window 1");
        widget1Table = new VisTable();
        widget1Table.setFillParent(true);

        widget2Table = new VisTable();
       // widget2Table.setFillParent(true);
        widget3Table = new VisTable();
       // widget3Table.setFillParent(true);
        widget1Table.add(new VisWindow("Widget 1"));
        widget2Table.add(new VisWindow("Widget 2"));
        widget3Table.add(new VisWindow("Widget 3"));

        collapsibleWidget1 = new CollapsibleWidget(widget1Table, true);

        collapsibleWidget2 = new CollapsibleWidget(widget2Table);

        collapsibleWidget3 = new CollapsibleWidget(widget3Table);

        window1.add(collapsibleWidget1).expandX().fillX().row();
        window1.add(collapsibleWidget2).expandX().fillX().row();
        window1.add(collapsibleWidget3).expandX().fillX().row();

        root.add(window1).expand().fill();
        componentInspector = new ComponentInspector();

        stage.addActor(componentInspector);




    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        ScreenUtils.clear(Color.BLACK);
        stage.act();
        stage.draw();
        if (Gdx.input.isCursorCatched()) {
            Gdx.input.setCursorCatched(false);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            collapsibleWidget1.setCollapsed(!collapsibleWidget1.isCollapsed());
            componentInspector.lightWidget.setCollapsed(!componentInspector.lightWidget.isCollapsed());
            componentInspector.transformWidget.setCollapsed(!componentInspector.transformWidget.isCollapsed());
            componentInspector.meshWidget.setCollapsed(!componentInspector.meshWidget.isCollapsed());
            componentInspector.materialsWidget.setCollapsed(!componentInspector.materialsWidget.isCollapsed());

        }


    }

    @Override
    public void resize(int width , int height) {
        super.resize(width , height);
        stage.getViewport().update(width , height , true);
        stage.getViewport().apply();
    }

}
