package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import com.kotcrab.vis.ui.widget.VisWindow;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import ui.widgets.RenderWidget;

public class SalientUI {
    static VisWindow leftMiddleWindow;
    static VisWindow rightMiddleWindow;
    VisWindow leftBottomWindow;
    VisWindow rightBottomWindow;
    VisSplitPane middlePane;
    VisSplitPane bottomPane;
    Table leftTable;
    Table rightTable;
    Table leftBottomTable;
    Table rightBottomTable;
    VisSplitPane fullPane;
    VisImage viewport;
    static public Stage stage;
    RenderWidget renderWidget;
    Skin skin;
    ObjectTree objectTree;
    EditorMenuBar menuBar;
    Table fullScreenTable;


    //Singleton
    private static SalientUI instance = null;



    public SalientUI(Stage stage, SceneManager sceneManager, DirectionalLightEx directionalLightEx, PerspectiveCamera camera, DirectionalShadowLight directionalShadowLight){
        leftBottomWindow = new VisWindow("Left Bottom");
        rightBottomWindow = new VisWindow("Right Bottom");
        leftMiddleWindow = new VisWindow("Left Middle");
        rightMiddleWindow = new VisWindow("Right Middle");
        this.stage = stage;

        leftBottomTable = new Table();
        rightBottomTable = new Table();

        leftBottomWindow.add(leftBottomTable);
        rightBottomWindow.add(rightBottomTable);

        leftTable = new Table();
        rightTable = new Table();
        fullScreenTable = new Table();

        leftTable.add(leftBottomWindow).expand().fill();
        rightTable.add(rightBottomWindow).expand().fill();
        createMenuBar();
        bottomPane = new VisSplitPane(leftBottomWindow, rightBottomWindow, false);
        middlePane = new VisSplitPane(leftMiddleWindow, rightMiddleWindow, false);
        fullPane = new VisSplitPane(middlePane, bottomPane, true);
        middlePane.setSplitAmount(0.2f);
        fullPane.setSplitAmount(.8f);
        fullPane.setFillParent(true);
        fullScreenTable.row();
        fullScreenTable.add(fullPane).expand().fill().row();
        fullScreenTable.setFillParent(true);
        fullScreenTable.layout();
        fullScreenTable.pack();
        stage.addActor(fullScreenTable);


        createRightMiddleWindow();
        createObjectTree();
        createConsole();


        createWidgetBar();

    }

    private void createWidgetBar() {
    }

    private void createMenuBar() {
        menuBar = new EditorMenuBar(stage);
        skin = new Skin(Gdx.files.internal("skin/tixel.json"));
        menuBar.getMenu().getTable().align(Align.left).setWidth(Gdx.graphics.getWidth());
        menuBar.getMenu().getTable().setHeight(50);
        menuBar.getMenu().getTable().validate();





        fullScreenTable.add(menuBar.getMenu().getTable()).growX().colspan(3);

    }

    private void createConsole() {

    }

    private void createObjectTree() {

    }

    public void setStage(Stage stage){


    }

    public Viewport getViewport(){
        return stage.getViewport();
    }

    public void createRightMiddleWindow(){
        viewport = new VisImage();
        rightMiddleWindow.add(viewport);

    }



    public void setRenderWidget(RenderWidget renderWidget){
        this.renderWidget = renderWidget;
        rightMiddleWindow.add(renderWidget);
        middlePane.setSecondWidget(renderWidget);
        renderWidget.setFillParent(true);
    }


    public void setViewportWindowTexture(TextureRegion texture){
        viewport.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
    }


}
