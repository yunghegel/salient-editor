package ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.layout.FlowGroup;
import com.kotcrab.vis.ui.util.highlight.Highlighter;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.strongjoshua.console.GUIConsole;


import dev.lyze.flexbox.FlexBox;
import io.github.orioncraftmc.meditate.YogaNode;
import io.github.orioncraftmc.meditate.YogaNodeFactory;
import io.github.orioncraftmc.meditate.enums.YogaAlign;
import io.github.orioncraftmc.meditate.enums.YogaEdge;
import io.github.orioncraftmc.meditate.enums.YogaFlexDirection;
import io.github.orioncraftmc.meditate.enums.YogaJustify;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.SceneManager;

import project.Settings;
import project.SettingsManager;
import ui.widgets.CameraButtonTool;
import ui.widgets.LightingButtonTool;
import ui.widgets.RenderWidget;
import ui.windows.DataWindow;
import ui.windows.LogWindow;
import ui.windows.SettingsWindow;

public class EditorStage {
    public Stage stage;
    boolean consoleCollapsed = false;
    public TabbedPane tabbedPane;
    Skin skin;
    public static VisSplitPane bottomPane;
    public static Table leftTable;
    public static Table rightTable;
    private Table bottomTable;

    private Table bottomContainer;
    private VisSplitPane horizontalPane;
    private VisSplitPane verticalPane;
    private Table mainLayout;
    private Table customLayout;

    public ListView sceneAssetView;
    public ListView componentView;
    public Table componentTable;
    public Table sceneAssetTable;

    VisTree tree;
    DataWindow window;
    FlowGroup flowGroup;
    public GUIConsole console;
    LightingButtonTool lightingButtonTool;
    CameraButtonTool cameraButtonTool;
    public static LogWindow logWindow;
    static public Table libraryContainer;
    Table fullScreenTable;
    public EditorMenuBar menuBar;
    public BottomTabbedPane bottomTabbedPane;

    SceneManager sceneManager;
    DirectionalShadowLight directionalLight;
    DirectionalLightEx directionalLightEx;
    PerspectiveCamera camera;
    FirstPersonCameraController cameraController;
    GLProfileLabel mousePositionLabel;

    public VisWindow bufferWindow;
    public VisImage bufferImage;
    public RenderWidget     renderWidget;
    Ray ray;
    Engine engine;


    private static EditorStage instance;

    public static EditorStage getInstance() {
        return instance;
    }

    public EditorStage(Stage stage, SceneManager sceneManager, DirectionalLightEx directionalLightEx, PerspectiveCamera camera, DirectionalShadowLight directionalShadowLight) {
        this.stage = stage;
        this.sceneManager = sceneManager;
        this.directionalLightEx = directionalLightEx;
        this.camera = camera;
        this.directionalLight = directionalShadowLight;
        this.cameraController = new FirstPersonCameraController(camera);

        instance = this;
        init();
    }

    public void init(){
        fullScreenTable = new Table();
        fullScreenTable.setFillParent(true);
        stage.addActor(fullScreenTable);
        menuBar = new EditorMenuBar(stage);
        skin = new Skin(Gdx.files.internal("skin/tixel.json"));
        menuBar.getMenu().getTable().align(Align.left).setWidth(Gdx.graphics.getWidth());





        fullScreenTable.add(menuBar.getMenu().getTable()).growX().colspan(3);





        tree = new VisTree();


        constructSplitPanes();


        initLogAndConsole();

        leftTable.add(tree).grow();

        initListViews();

        initFrameBufferWindow();
    Table buttonTable = new Table();
    buttonTable.setFillParent(false);
    buttonTable.setWidth(100);

    FlexBox flexBox = new FlexBox();
    flexBox.getRoot().setFlexDirection(YogaFlexDirection.ROW);
    flexBox.getRoot().setJustifyContent(YogaJustify.FLEX_END);
    flexBox.getRoot().setAlignContent(YogaAlign.CENTER);
    flexBox.getRoot().setPadding(YogaEdge.HORIZONTAL, 10);



    menuBar.getMenu().getTable().add(flexBox).growX();
    YogaNode yogaNode = YogaNodeFactory.create().setFlexDirection(YogaFlexDirection.ROW).setJustifyContent(YogaJustify.FLEX_END).setFlexGrow(1).setFlexShrink(1);
    yogaNode.setFlexBasisPercent(100);


    VisImageButton button = new VisImageButton(skin.getDrawable("icon-close"));
    VisImageButton minimizeButton = new VisImageButton(skin.getDrawable("icon-minimize"));
    minimizeButton.padLeft(10);
    minimizeButton.padRight(10);;

    VisImageButton maximizeButton = new VisImageButton(skin.getDrawable("icon-maximize") );

    maximizeButton.padLeft(10);
    maximizeButton.padRight(10);

    maximizeButton.addListener(new ClickListener(){
        @Override
        public void clicked(InputEvent event, float x, float y) {
            if(Gdx.graphics.isFullscreen()){
                Gdx.graphics.setWindowedMode(Settings.Window.WIDTH, Settings.Window.HEIGHT);
            }else{
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
        }
    });


    minimizeButton.addListener(new ClickListener(){
        @Override
        public void clicked(InputEvent event, float x, float y) {
            Gdx.graphics.setWindowedMode(Settings.Window.WIDTH, Settings.Window.HEIGHT);
        }
    });

    button.padLeft(10);
    button.padRight(10);

    button.addListener(new ClickListener(){
        @Override
        public void clicked(InputEvent event, float x, float y) {
            Gdx.app.exit();
        }
                       });





        lightingButtonTool = new LightingButtonTool(buttonTable,stage,sceneManager,directionalLightEx,directionalLight);
        cameraButtonTool = new CameraButtonTool(buttonTable,stage,camera,cameraController);
        cameraButtonTool.cameraButton.padRight(10);
        cameraButtonTool.cameraButton.padLeft(10);
        cameraButtonTool.cameraButton.center();
        lightingButtonTool.button.padRight(10);
        lightingButtonTool.button.padLeft(10);
        lightingButtonTool.button.center();

        flexBox.add(lightingButtonTool.button);

        flexBox.add(cameraButtonTool.cameraButton);
        flexBox.add(minimizeButton);
        flexBox.add(buttonTable);
        buttonTable.add(maximizeButton);
        buttonTable.add(button).right().center();
//
        buttonTable.pack();

        stage.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if(keycode == Input.Keys.ENTER){
                    if(!console.isVisible()){
                        console.setVisible( true );
                        console.select(  );
                        console.draw();
                        console.getWindow().getStage().setKeyboardFocus(console.getWindow());


                    }
                }
                return super.keyDown(event, keycode);
            }
        });
        mousePositionLabel = new GLProfileLabel();
        rightTable.add(mousePositionLabel).growX().bottom().left();

    }

    public void setRenderWidget(RenderWidget renderWidget){
        this.renderWidget = renderWidget;
        horizontalPane.setSecondWidget(renderWidget);
    }

    private void initFrameBufferWindow() {
        bufferWindow = new VisWindow("Frame Buffer");

        bufferWindow.setMovable(false);
        bufferWindow.setResizable(false);


        bufferImage = new VisImage();
        bufferImage.setScaling(Scaling.fit);
        bufferWindow.add(bufferImage).grow();


    }

    public void setFrameBuffer(FrameBuffer frameBuffer){
        bufferImage.setDrawable(new TextureRegionDrawable(new TextureRegion(ScreenUtils.getFrameBufferTexture())));

    }

    public void initListViews() {
        ObjectTree objectTree = new ObjectTree(leftTable, bottomContainer);
        tree = objectTree.getTree();


        leftTable.add(sceneAssetTable).grow();
        /*rightTable.add(sceneAssetTable).grow();*/

    }

    public void createListeners(){}

    public GUIConsole getConsole() {
        return console;
    }

    public Camera getCamera(){
        return stage.getCamera();
    }

    public void initLogAndConsole(){
       /* logWindow = new LogWindow("Log", libraryContainer.getX(), libraryContainer.getY(), libraryContainer.getWidth(), libraryContainer.getHeight());

        libraryContainer.add(logWindow).grow().row();*/
        console = new GUIConsole(VisUI.getSkin(), false, 0, VisWindow.class, VisTable.class, "default-pane", TextField.class,
                VisTextButton.class, VisLabel.class, VisScrollPane.class);
        console.getWindow().addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if(keycode == Input.Keys.ENTER){
                    stage.setScrollFocus(console.getWindow());
                    console.draw();
                    console.setVisible(true);
                    console.select();

                    stage.setKeyboardFocus(console.getWindow());
                    stage.addActor(console.getWindow());

                }
                return super.keyDown(event, keycode);
            }
        });
        logWindow = new LogWindow("Log", libraryContainer.getX(), libraryContainer.getY(), libraryContainer.getWidth(), libraryContainer.getHeight());
        TabbedPane tabbedPane = new TabbedPane();
        Tab logTab = new Tab(false, false) {
            Table table;
            HighlightTextArea textArea = new HighlightTextArea("");




            @Override
            public String getTabTitle() {
                return "Log";
            }

            @Override
            public Table getContentTable() {
                logWindow.setFillParent(true);
                table = new Table();
                table.add(logWindow).grow().fill(true);
                return table;
            }
        };

        Tab consoleTab = new Tab(false, false) {
            @Override
            public String getTabTitle() {
                return "Console";
            }

            @Override
            public Table getContentTable() {
                return console.getWindow();
            }
            @Override
            public void onShow() {
                console.getWindow().setTouchable(Touchable.enabled);
                console.draw();

            }
        };


        VisImageButton consoleSettingsButton = new VisImageButton(skin.getDrawable("icon-minimize"));
        VisTextButton dataWindowButton = new VisTextButton("Data Viewer");
        dataWindowButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                libraryContainer.clear();



                ;

                    DataWindow dataWindow = new DataWindow(engine,libraryContainer);

                    dataWindow.pack();

                    Window window = dataWindow;

                    stage.addActor(dataWindow);
                    dataWindow.setSize(800, 500);



                    dataWindow.setHighighter(getHighlighter());



            }
        });
        consoleSettingsButton.getImage().setScale(0.5f);
        consoleSettingsButton.getImageCell().size(16,16);

        PopupMenu consoleSettingsMenu = new PopupMenu();
        MenuItem consoleSettings = new MenuItem("View");
        consoleSettingsMenu.addItem(consoleSettings);



        consoleSettingsButton.addListener(new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y) {
                consoleCollapsed = !consoleCollapsed;
                if (!consoleCollapsed) {
                    verticalPane.setSplitAmount(.7f);
                    console.enableSubmitButton(true);
                    consoleSettingsButton.getImage().setDrawable(skin.getDrawable("icon-restore"));
                } else {
                    verticalPane.setSplitAmount(0.97f);
                    console.enableSubmitButton(false);
                    consoleSettingsButton.getImage().setDrawable(skin.getDrawable("icon-restore"));

                }

            }
        });

        console.getWindow().getTitleTable().add(dataWindowButton).right().padRight(3).padBottom(3).padTop(3);
        console.getWindow().getTitleTable().add(consoleSettingsButton).right().padRight(3).padBottom(3).padTop(3);

        console.getWindow().setMovable(false);


        tabbedPane.add(consoleTab);
        tabbedPane.add(logTab);
        SettingsWindow settingsWindow = new SettingsWindow(new SettingsManager(),sceneManager);
        //settingsWindow.getTitleTable().clearChildren();
        //settingsWindow.getTitleTable().setVisible(false);
        bottomTabbedPane = new BottomTabbedPane();
        tabbedPane.getTable().setSize(libraryContainer.getWidth(), libraryContainer.getHeight());
        //bottomContainer.add(tabbedPane.getTable()).grow().fill(true).expand();
        //settingsWindow.getTitleTable().pad(0);
        //libraryContainer.add(console.getWindow()).expand().fill();
        libraryContainer.add(console.getWindow()).grow().row();
        HighlightTextArea textArea = new HighlightTextArea("");
        textArea.setHighlighter(getHighlighter());
        textArea.processHighlighter();

        bottomContainer.pad(3);
    }

    public Highlighter getHighlighter(){
      Highlighter  highlighter = new Highlighter();
        highlighter.regex(Color.valueOf("EFC090"), "\\b(foo|bar|lowp|mediump|highp)\\b");
        highlighter.regex(Color.valueOf("66CCB3"), "\\b(class|private|protected|public|if|else|void|precision)\\b");
        highlighter.regex(Color.valueOf("BED6FF"), "\\b(int|float|boolean|public|if|else|void|vec2|vec3|vec4|mat2|mat3|mat4|sampler2D)\\b");
        highlighter.regex(Color.valueOf("BFD615"), "\\b(model|instance|ModelInstance|cache|Model|Cache)\\b");
        highlighter.regex(Color.valueOf("75715E"), "/\\*(.|[\\r\\n])*?\\*/"); //block comments (/* comment */)
        highlighter.regex(Color.valueOf("75715E"), "(\\/\\/.*)[\\n]"); //line comments (// comment)
        highlighter.regex(Color.valueOf("75F85E"), "(\\#.*)"); //macro (#macro)
        //regex that matches [CONSOLE]
        highlighter.regex(Color.ORANGE, "\\[CONSOLE\\]");


        highlighter.regex(Color.valueOf("F92672"), "\\b(bullet|Bullet|body|rigid|collision|ray|test|dynamics|static|dynamic)\\b");
        highlighter.regex(Color.valueOf("66CCB3"), "\\b(Create|create|Destroy|destroy|Dispose|dispose|Manage|manage|Loaded|loaded|Save|save|Saved|saved|added|removed|Added|Removed|Creating|creating|Intializing|initializing|adding|Adding)\\b");
        return highlighter;
    }

    private void constructSplitPanes () {

        Table layoutContainer = new Table();
        mainLayout = new Table();
        customLayout = new Table();
        customLayout.setVisible(false);
        Stack stack = new Stack(mainLayout, customLayout);
        layoutContainer.add(stack).grow();





        Table midTable = new Table();
        bottomTable = new Table();



        bottomContainer = new Table();
        libraryContainer = new Table();

        libraryContainer.addListener(new ClickListener(0) { //Quick hack for library container intercepting touch as its an empty table currently
            @Override
            public void clicked (InputEvent event, float x, float y) {
            }
        });
        libraryContainer.addListener(new ClickListener(1) { //Quick hack for library container intercepting touch as its an empty table currently
            @Override
            public void clicked (InputEvent event, float x, float y) {
            }
        });
        libraryContainer.setTouchable(Touchable.enabled);
        bottomPane = new VisSplitPane(bottomContainer, libraryContainer, false);

        bottomPane.setSplitAmount(.2f); // remove this line when the bottom-right panel content will be implemented (which is the library container)
        bottomPane.setMaxSplitAmount(0.2f);
        bottomTable.add(bottomPane).expand().grow();

        verticalPane = new VisSplitPane(midTable, bottomTable, true);
        verticalPane.setMaxSplitAmount(1f);
        verticalPane.setMinSplitAmount(0f);
        verticalPane.setSplitAmount(0.75f);
        VisWindow leftTableWindow = new VisWindow("Scene Graph");
        leftTable = new Table();
        leftTableWindow.add(leftTable).grow();
        leftTable.add().grow();
        rightTable = new Table();
        rightTable.add().grow();
        horizontalPane = new VisSplitPane(leftTableWindow, rightTable, false);
        midTable.add(horizontalPane).expand().grow().fill();
        horizontalPane.setMaxSplitAmount(0.8f);
        horizontalPane.setMinSplitAmount(0.2f);
        horizontalPane.setSplitAmount(0.2f);

        fullScreenTable.row();
        fullScreenTable.add(layoutContainer).grow();

        mainLayout.add(verticalPane).grow();

        stage.addActor(fullScreenTable);
    }
    public void update(){
    }

    public  void resize(int width, int height){
        stage.getViewport().update(width, height, true);

    }

    public void setLogWindowText(String msg) {
        if (logWindow != null) {
            logWindow.addText(msg+"\n");
        }


    }
}

