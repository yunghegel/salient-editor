package ui;

import backend.DefaultAssets;
import backend.tools.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.strongjoshua.console.Console;
import core.systems.BulletPhysicsSystem;
import core.systems.GizmoSystem;
import core.systems.SceneSystem;
import editor.Context;
import editor.EditorGame;
import editor.graphics.rendering.SceneRenderer;
import editor.graphics.screens.MenuScreen;
import ui.elements.CompassWidget;
import ui.elements.ComponentInspector;
import ui.elements.ModelPreviewWindow;
import ui.elements.profiler.DataWindow;
import ui.elements.profiler.ProfilerWindow;
import ui.scene.ObjectTree;
import ui.tools.BulletPhysicsTool;
import ui.tools.CameraSettingsTool;
import ui.tools.EnvironmentSettingsTool;
import ui.widgets.RenderWidget;

public class UserInterface
{
    private static UserInterface instance;
    VisSplitPane fullSplitPane;
    public static VisSplitPane middleSplitPane;
    public static VisWindow sceneGraphWindow;
    public static VisWindow componentInspectorWindow;
    public static Console console;
    public static HighlightTextArea logArea;
    public static VisTextArea profiler;
    public static VisSplitPane multiSplitPane;
    public static VisLabel debugLabel1;
    public static VisLabel debugLabel2;
    public static VisLabel debugLabel3;
    public static VisLabel debugLabel4;
    public static VisLabel debugLabel5;
    public static VisLabel debugLabel6;
    public static VisLabel debugLabel7;
    public static VisLabel debugLabel8;
    public static VisLabel debugLabel9;
    public static VisLabel debugLabel10;
    public Table rootTable;
    public Table rightTable;
    public Table leftTable;
    public Table middleTable;
    public Table bottomTable;
    public Stack middleStack;
    public VisSplitPane bottomPane;
    public VisSplitPane bottomSplitPane;

    public VisTable componentInspectorTable;
    public MenuBar menuBar;
    public SceneRenderer sceneRenderer;
    public boolean isOrtho = false;
    public boolean isPerspective = true;
    Table toolSidebar;
    //SceneGraphWindow
    public VisTextButton addChildNodeButton;
    public VisSelectBox<NodeType> nodeTypeSelectBox;
    public Stage stage;
    public RenderWidget renderWidget;
    Table bottomLeftTable;
    Table bottomRightTable;
    VisWindow logWindow;
    private Context ctx;
    private VisTextButton orthoButton;
    private VisTextButton perspectiveButton;
    private VisTextButton playButton;
    private VisImageButton translateButton;
    private VisImageButton rotateButton;
    private VisImageButton scaleButton;
    private VisImageButton physicsButton;
    public CameraSettingsTool cameraSettingsTool;
    public EnvironmentSettingsTool environmentSettingsTool;
    private ProfilerWindow profilerWindow;
    private ModelPreviewWindow modelPreviewWindow;
    private DataWindow dataWindow;
    private VisScrollPane componentInspectorScrollPane;
    public VisWindow toolSidebarWindow;
    VisSplitPane sceneGraphSplitPane;
    public VisSplitPane profilerObjectPreviewerSplitPane;
    public BulletPhysicsTool bulletPhysicsTool;
    public ComponentInspector componentInspector;
    CollapsibleWidget collapsibleWidget;
    public static UserInterface getInstance() {
        if (instance == null) {
            instance = new UserInterface();
        }
        return instance;
    }

    public UserInterface() {
        instance = this;
    }

    public void log(String msg) {
        Context.getInstance().console.log(msg);
    }

    public void setContext(Context context) {
        this.ctx = context;
    }

    public void init() {
        if(!VisUI.isLoaded())
            VisUI.load(DefaultAssets.i.skin);
        this.stage = ctx.getStage();

        sceneRenderer = new SceneRenderer(ctx.getSceneManager() , ctx.getCamera());
        sceneRenderer.setContext(ctx);

        rootTable = new Table();
        leftTable = new Table();
        middleTable = new Table();
        rightTable = new Table();
        bottomTable = new Table();
        bottomLeftTable = new Table();
        bottomRightTable = new Table();
        componentInspectorTable = new VisTable();

        componentInspector = new ComponentInspector();


        //profilerWindow = new VisWindow("Component Settings");

        logWindow = new VisWindow("Log");
        sceneGraphWindow = new VisWindow("Scene Graph");

        logArea = new HighlightTextArea("Log");
        profiler = new VisTextArea();

        renderWidget = new RenderWidget(ctx.getCamera() , stage);
        //renderWidget.setRenderer(sceneRenderer);
        renderWidget.addRenderer(sceneRenderer);

        console = ctx.getConsole();


        logArea.setFillParent(true);
        profiler.setFillParent(true);
        bottomLeftTable.setFillParent(true);
        bottomRightTable.setFillParent(true);
        rootTable.setFillParent(true);

//        profilerWindow.getTitleTable().getChild(0).remove();
//        profilerWindow.setMovable(false);
//        profilerWindow.setResizable(false);
        componentInspectorWindow = new VisWindow("editor.Editor");
        componentInspectorWindow.setFillParent(true);
        componentInspectorWindow.add(componentInspectorTable);
        componentInspectorWindow.pack();
        componentInspectorWindow.row();
        componentInspectorWindow.align(Align.top);
        componentInspectorWindow.setMovable(false);

        profiler.setReadOnly(true);
        logArea.setReadOnly(true);

        componentInspectorTable.align(Align.top).setWidth(componentInspectorWindow.getWidth());
        createModelPreviewWindow();
        create();

//        componentInspectorScrollPane = new VisScrollPane(componentInspectorWindow);
//        componentInspectorScrollPane.setFillParent(true);

//        ModelPreviewRenderer modelPreviewRenderer = new ModelPreviewRenderer();
//        ModelPreviewWidget modelPreviewWidget = new ModelPreviewWidget(stage,modelPreviewRenderer.camera);
//        modelPreviewWidget.setRenderer(modelPreviewRenderer);
//        VisWindow modelPreviewWindow = new VisWindow("Model Preview");
//        modelPreviewWindow.setResizable(true);
//        modelPreviewWindow.setMovable(true);
//        modelPreviewWindow.add(modelPreviewWidget).grow().expand().fill();
//
//        modelPreviewWindow.pack();
//        modelPreviewWindow.setPosition(0,0);
//
//        stage.addActor(modelPreviewWindow);







    }

    private void createModelPreviewWindow() {
        modelPreviewWindow = new ModelPreviewWindow(ctx);
        modelPreviewWindow.pad(10);
        //leftTable.add(modelPreviewWindow).grow().expand().fill();
    }

    private void createCompassWidget() {
        CompassWidget compassWidget = new CompassWidget(ctx);

    }

    private void create() {
        createToolElements();
        createMenuBar();
        createWindows();
        createPanes();

        populateTables();
        populateWindows();
        populateStage();
        createSceneGraphElements();

    }

    public void createToolElements() {
        orthoButton = new VisTextButton("Ortho");
        orthoButton.center();

        perspectiveButton = new VisTextButton("Perspective");
        perspectiveButton.setX(sceneGraphWindow.getWidth() + 10 + orthoButton.getWidth() + 10);

        playButton = new VisTextButton("Play");
        playButton.setX(sceneGraphWindow.getWidth() + 10 + orthoButton.getWidth() + 10 + perspectiveButton.getWidth() + 10);
        Texture translateIcon = new Texture("button_icons/translate_icon.png");
        TextureRegionDrawable translateDrawable = new TextureRegionDrawable(translateIcon);

        Texture scaleIcon = new Texture("button_icons/scale_icon.png");
        TextureRegionDrawable scaleDrawable = new TextureRegionDrawable(scaleIcon);

        Texture physicsIcon= new Texture("button_icons/physics_tool_button.png");
        TextureRegionDrawable physicsDrawable = new TextureRegionDrawable(physicsIcon);
        physicsButton = new VisImageButton(physicsDrawable);
        translateButton = new VisImageButton(translateDrawable);
        scaleButton = new VisImageButton(scaleDrawable);

        toolSidebarWindow = new VisWindow("Tools");
        toolSidebar=new Table();





        orthoButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event , Actor actor) {

                if (isOrtho) {
                    isOrtho = false;
                }
                else if (isPerspective) {
                    isPerspective = false;
                    ctx.getSceneManager().setCamera(ctx.getOrthoCam());
                    ctx.inputMultiplexer.addProcessor(ctx.orthoCamController);
                    ctx.inputMultiplexer.removeProcessor(ctx.getCameraController());
                    ctx.getOrthoCam().direction.set(0 , 1 , -.25f);
                    ctx.orthoCam.zoom = 0.01f;
                }

            }
        });

        perspectiveButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event , Actor actor) {
                isOrtho = false;
                isPerspective = true;
                ctx.getSceneManager().setCamera(ctx.getCamera());
                ctx.inputMultiplexer.addProcessor(ctx.getCameraController());
                ctx.inputMultiplexer.removeProcessor(ctx.orthoCamController);

            }
        });

        translateButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event , Actor actor) {
                boolean enabled = GizmoSystem.translateToolEnabled;

                if(SceneSystem.selectedSceneComponent==null){
                    return;
                }

                ctx.gizmoSystem.toggleTranslateTool();

            }
        });

        scaleButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event , Actor actor) {
                boolean enabled = GizmoSystem.translateToolEnabled;

                if(SceneSystem.selectedSceneComponent==null){
                    return;
                }

                ctx.gizmoSystem.toggleScaleTool();}
        });

        playButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event , Actor actor) {
                ctx.playerSystem.playerModeEnabled = !ctx.playerSystem.playerModeEnabled;
                if (ctx.playerSystem.playerModeEnabled) {
                    ctx.inputMultiplexer.removeProcessor(ctx.cameraController);
                    Gdx.input.setCursorCatched(true);
                }
                else if (!ctx.playerSystem.playerModeEnabled) {
                    ctx.inputMultiplexer.addProcessor(ctx.cameraController);
                    Gdx.input.setCursorCatched(false);

                }
            }

        });

        physicsButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event , Actor actor) {
                if (bulletPhysicsTool.visible){
                    bulletPhysicsTool.visible = false;
                    bulletPhysicsTool.disable();
                }
                else if (!bulletPhysicsTool.visible){
                    bulletPhysicsTool.visible = true;
                    bulletPhysicsTool.enable();
                }
            }

        });
    }

    private void createMenuBar() {
        menuBar = new MenuBar();


        menuBar.getTable().addListener(new InputListener()
        {
            float startX;
            float startY;

            @Override
            public boolean mouseMoved(InputEvent event , float x , float y) {
                if (Gdx.input.isButtonPressed(0)){
                    float deltaX = startX+Gdx.input.getDeltaX();
                    float deltaY = startX+Gdx.input.getDeltaY();

                    if (deltaX > 0) {
                        deltaX = 1;
                    }
                    else if (deltaX < 0) {
                        deltaX = -1;
                    }

                    int xMoveBy = (int) deltaX;
                    int yMoveBy = (int) deltaY;


                }
                return super.mouseMoved(event , x , y);
            }

            @Override
            public boolean touchDown(InputEvent event , float x , float y , int pointer , int button) {
                Log.info("UserInterface","MenuBar touchDown");
                float deltaX = Gdx.input.getDeltaX();
                float deltaY = Gdx.input.getDeltaY();
                startX = x;
                startY = y;
                Log.info("UI",startX+" "+startY);

                if (deltaX > 0) {
                    deltaX = 1;
                }
                else if (deltaX < 0) {
                    deltaX = -1;
                }

                int xMoveBy = (int) deltaX;
                int yMoveBy = (int) deltaY;




                return super.touchDown(event , x , y , pointer , button);
            }

            @Override
            public void touchDragged(InputEvent event , float x , float y , int pointer) {
                float deltaX = x - event.getStageX();
                float deltaY = y - event.getStageY();

                if (deltaX > 0) {
                    deltaX = 1;
                }
                else if (deltaX < 0) {
                    deltaX = -1;
                }

                int xMoveBy = (int) deltaX;
                int yMoveBy = (int) deltaY;


                Log.info("UserInterface","MenuBar touchDragged");

                super.touchDragged(event , x , y , pointer);
            }


        });

        Menu menu = new Menu("File");
        Menu view = new Menu("View");
        Menu edit = new Menu("Edit");
        Menu physics = new Menu("Physics");
        menuBar.addMenu(menu);
        menuBar.addMenu(view);
        menuBar.addMenu(edit);
        menuBar.addMenu(physics);

        MenuItem menuScreen = new MenuItem("Main Menu");
        menu.addItem(menuScreen);

        menuScreen.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event , Actor actor) {
                EditorGame.i().setScreen(new MenuScreen(EditorGame.i()));
            }
        });

        MenuItem viewSettings = new MenuItem("Settings");
        view.addItem(viewSettings);

        MenuItem exit = new MenuItem("Exit");
        menu.addItem(exit);

        MenuItem save = new MenuItem("Save");
        menu.addItem(save);

        MenuItem newScene = new MenuItem("New Scene");
        menu.addItem(newScene);

        MenuItem toggleDebugDraw = new MenuItem("Toggle Debug Draw");
        physics.addItem(toggleDebugDraw);

        MenuItem toggleStaticDebugDraw = new MenuItem("Toggle Static Debug Draw");
        physics.addItem(toggleStaticDebugDraw);

        MenuItem collapseBottompane = new MenuItem("Toggle Bottom Pane");
        view.addItem(collapseBottompane);

        MenuItem hideALl = new MenuItem("Hide All");
        view.addItem(hideALl);

        MenuItem showAll = new MenuItem("Show All");
        view.addItem(showAll);

        hideALl.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event , Actor actor) {
                fullSplitPane.setSplitAmount(1);
                multiSplitPane.setMaxSplitAmount(1);
                multiSplitPane.setSplitAmount(1);
                middleSplitPane.setSplitAmount(0);
                toolSidebarWindow.remove();
            }
        });

        showAll.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event , Actor actor) {
                fullSplitPane.setSplitAmount(0.8f);
                multiSplitPane.setMaxSplitAmount(.85f);
                multiSplitPane.setSplitAmount(0.85f);
                middleSplitPane.setSplitAmount(0.1f);
                middleTable.add(toolSidebarWindow).growY();
            }
        });

        collapseBottompane.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event , Actor actor) {
                if (fullSplitPane.getSplit()!=1){
                    fullSplitPane.setSplitAmount(1);
                }
                else if (fullSplitPane.getSplit()==1){
                    fullSplitPane.setSplitAmount(0.75f);
                }

            }
        });

        save.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event , Actor actor) {
                ctx.sceneSerializer.serializeComponentRegistry();
                Log.info("SceneSaveState" , "Scene Saved");

            }
        });

        toggleStaticDebugDraw.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event , Actor actor) {
                BulletPhysicsSystem.toggleStaticBodyDebugDraw();
            }
        });

        toggleDebugDraw.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeListener.ChangeEvent event , Actor actor) {
                BulletPhysicsSystem.debugDraw = !BulletPhysicsSystem.debugDraw;
            }
        });

        menuBar.getTable().add(orthoButton).center().padLeft(200);
        menuBar.getTable().add(perspectiveButton).center().padLeft(5);
        menuBar.getTable().add(playButton).center().padLeft(5);
        menuBar.getTable().add(translateButton).padLeft(50);

        rootTable.add(menuBar.getTable()).expandX().fillX().growX().row();
        Table menuBarButtonTable = new Table();
        menuBarButtonTable.setFillParent(true);
        menuBar.getTable().add(menuBarButtonTable).expandX().fillX();

        cameraSettingsTool = new CameraSettingsTool(toolSidebarWindow , ctx.getStage() , ctx.getCamera() , ctx.getCameraController() , ctx.getOrthoCam() , renderWidget);

        environmentSettingsTool = new EnvironmentSettingsTool(toolSidebarWindow , ctx.getStage() , ctx.getSceneManager() , ctx);

        bulletPhysicsTool = new BulletPhysicsTool(ctx);
    }

    private void createWindows() {
        createProfilerWindow();
        logWindow.setKeepWithinParent(true);

        bottomRightTable.add(logWindow).expand().fill().row();
        //bottomLeftTable.add(profilingWindow).expand().fill();

        console.enableSubmitButton(true);

        console.select();
        console.resetInputProcessing();

    }

    private void createPanes() {
        middleSplitPane = new VisSplitPane(leftTable , middleTable , false);
        multiSplitPane = new VisSplitPane(middleSplitPane , rightTable , false);
        multiSplitPane.setSplitAmount(0.8f);
        multiSplitPane.setMaxSplitAmount(0.85f);
        multiSplitPane.setMinSplitAmount(0.8f);

        middleSplitPane.pack();
        multiSplitPane.pack();

        middleSplitPane.setSplitAmount(0.1f);
        VisWindow consoleWindow = new VisWindow("Console");

        consoleWindow.add(console.getWindow().getChild(1)).expand().fill();
//        Container tabPaneContainer = new Container();
//        tabPaneContainer.setActor(new SettingsWindow(null,ctx.getSceneManager()));
//
//        tabPaneContainer.setFillParent(true);


        profilerObjectPreviewerSplitPane = new VisSplitPane(modelPreviewWindow , profilerWindow , false);
        profilerObjectPreviewerSplitPane.setSplitAmount(0.33f);
        bottomSplitPane = new VisSplitPane(profilerObjectPreviewerSplitPane , consoleWindow , false);
        bottomSplitPane.setSplitAmount(0.5f);
        //bottomSplitPane.setMaxSplitAmount(0.7f);
        bottomRightTable.pad(10);
        Table bottomTable = new Table();
        bottomTable.add(bottomSplitPane).expand().fill().grow();

        collapsibleWidget = new CollapsibleWidget(bottomTable , false);
        bottomPane = new VisSplitPane(multiSplitPane , collapsibleWidget , true);
        bottomPane.setSplitAmount(0.3f);
        bottomPane.setFillParent(true);

    }

    private void populateTables() {

        fullSplitPane = new VisSplitPane(multiSplitPane , bottomSplitPane , true);
        rootTable.add(fullSplitPane).expand().fill().row();
        fullSplitPane.setSplitAmount(0.8f);

      /*  rootTable.add(middleSplitPane).expand().fill().row();
        rootTable.add(bottomPane).expandX().fillX().row();*/
        leftTable.add(sceneGraphWindow).fill().expand().row();
       // sceneGraphWindow.setFillParent(true);
        sceneGraphWindow.getTitleTable().padBottom(10).padTop(10).padLeft(0).row().spaceBottom(15);
        sceneGraphWindow.getTitleTable().align(Align.left);
        sceneGraphWindow.getTitleTable().add(GizmoSystem.selectedComponent).left().expandX().fillX();

        componentInspector.getTitleTable().padBottom(10).padTop(10).padLeft(0).row().spaceBottom(15);
        componentInspector.getTitleTable().align(Align.left);
        componentInspector.getTitleTable().add(GizmoSystem.selectedComponent).left().expandX().fillX();


        toolSidebarWindow.align(Align.top);
        toolSidebarWindow.setWidth(30);
        toolSidebarWindow.add(translateButton).top().row();
        toolSidebarWindow.add(scaleButton).top().pad(5).row();
        toolSidebarWindow.add(physicsButton).top().pad(5).row();

        toolSidebarWindow.row();
        translateButton.align(Align.top);
        //toolSidebarWindow.add(toolSidebar);
        middleTable.add(renderWidget).expand().fill();
        middleTable.add(toolSidebarWindow).growY();

        rightTable.add(componentInspector).expand().fill().row();

        componentInspectorWindow.setFillParent(true);
        componentInspectorWindow.setWidth(300);

        fullSplitPane.pack();

    }

    private void populateWindows() {
        logWindow.add(logArea).expand().fill();

    }

    private void populateStage() {
        stage.addActor(rootTable);

    }

    private void createSceneGraphElements() {
        Table sceneGraphTable = new Table();
        addChildNodeButton = new VisTextButton("Add Child Node");
        nodeTypeSelectBox = new VisSelectBox<NodeType>();
        nodeTypeSelectBox.setItems(NodeType.values());

        HorizontalGroup sceneGraphButtons = new HorizontalGroup();
        sceneGraphButtons.pad(10);
        sceneGraphButtons.addActor(addChildNodeButton);
        sceneGraphButtons.space(10);
        sceneGraphButtons.addActor(nodeTypeSelectBox);

        sceneGraphTable.setFillParent(true);
        sceneGraphWindow.add(sceneGraphTable);
        Table sceneGraphInspectorTable = new Table();
        sceneGraphInspectorTable.setFillParent(true);

        profilerWindow.add(sceneGraphInspectorTable);
        Separator separator = new Separator();
        componentInspectorWindow.add(sceneGraphButtons).align(Align.center).row();
        componentInspectorWindow.add(new Separator()).padBottom(20).expandX().growX().row();
        //componentEditorWindow.add(nodeTypeSelectBox).align(Align.left).row();

        ObjectTree objectTree = new ObjectTree(sceneGraphTable , componentInspectorTable);

        sceneGraphWindow.setMovable(false);
        sceneGraphWindow.padLeft(10);
        sceneGraphWindow.add(objectTree.getTree()).expand().fill();
        objectTree.setExpanded();

    }

    public void createProfilerWindow() {
        debugLabel1 = new VisLabel("Debug 1:");
        debugLabel2 = new VisLabel("Debug 2:");
        debugLabel3 = new VisLabel("Debug 3:");
        debugLabel4 = new VisLabel("Debug 4:");
        debugLabel5 = new VisLabel("Debug 5:");
        debugLabel6 = new VisLabel("Debug 6:");
        debugLabel7 = new VisLabel("Debug 7:");
        debugLabel8 = new VisLabel("Debug 8:");
        debugLabel9 = new VisLabel("Debug 9:");
        debugLabel10 = new VisLabel("Debug 10:");


        profilerWindow = new ProfilerWindow(ctx, this);
        dataWindow = new DataWindow(ctx,this);


    }

    public DataWindow getDataWindow() {
        return dataWindow;
    }

    public ProfilerWindow getEditorProfilerWindow() {
        return profilerWindow;
    }

    enum NodeType
    {
        Spatial, Light, Scene, Primitive
    }

}
