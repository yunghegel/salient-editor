package ui.elements.profiler;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.profiling.GLErrorListener;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import editor.Context;
import ui.UserInterface;
import editor.tools.MousePickingTool;
import editor.tools.TranslateTool;
import util.StringUtils;

public class ProfilerWindow extends VisWindow {
    private Tab envTab;

    //general
    VisTextField gravityInput;
    public boolean axisToggle;
    public boolean debugDrawToggle;
    public boolean cameraToggle;
    ButtonBar buttonBar;
    VisSlider gravitySlider;
    VisCheckBox axisToggleBox;
    VisCheckBox debugDrawToggleBox;
    VisSelectBox<String> cameraModeSelectBox;
    ProfilerTab glTab;
    CameraProfilerTab cameraTab;

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

    VisTextButton setToDataWindow;
    UserInterface ui;





    public ProfilerWindow(Context context, UserInterface ui){
        super("Profiler");
        this.ui = ui;
        TableUtils.setSpacingDefaults(this);

        setResizable(false);

        final VisTable container = new VisTable();
        container.defaults().pad(2)    ;

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

        glTab = new ProfilerTab("GLProfile", context);
        glTab.getContentTable().defaults().pad(2);

        createGLTab(glTab);
        glTab.getContentTable().align(Align.topLeft);
        tabbedPane.add(glTab);

        cameraTab = new CameraProfilerTab();
        cameraTab.getContentTable().defaults().pad(2);
        tabbedPane.add(cameraTab);

        envTab = new ProfilerTab("Environment", context);
        envTab.getContentTable().defaults().pad(2);
        createEnvTab(envTab);
        tabbedPane.add(envTab);




        //setSize(550, 400);
        create();
        centerWindow();

        initListeners();

    }

    private void create() {
        setToDataWindow = new VisTextButton("Set to Data Browser");
        getTitleTable().add(setToDataWindow).right().padRight(5);

    }

    private void createEnvTab(Tab graphicsTab) {

    }

    public void createGLTab(ProfilerTab tab){

    }

    public void initListeners(){

        setToDataWindow.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event , Actor actor) {
            ui.profilerObjectPreviewerSplitPane.setSecondWidget(ui.getDataWindow());


            }
        });


    }

    @Override
    public void close () {
        setVisible(false);
    }

    private class ProfilerTab extends Tab {
        private String title;
        private Table content;
        private Context ctx;

        public ProfilerTab(String title,Context context) {
            super(false, false);
            this.title = title;
            this.ctx = context;
            content = new GLProfilerTable(context);
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



private class GLProfilerTable extends VisTable
{

    private String title;
    private Table content;
    private Context ctx;
    private GLProfiler profiler;
    VisLabel drawCallsLabel;
    VisLabel shaderSwitchesLabel;
    VisLabel textureBindingsLabel;
    VisLabel vertexCountLabel;
    int ticks = 0;

    VisValidatableTextField ticksInput;
    int ticksUntilReset = 1;
    public GLProfilerTable(Context context) {
        super();
        align(Align.topLeft);
        this.ctx = context;
        pad(10);
        populateProfilerFields();
    }

    private void populateProfilerFields() {
        profiler = ctx.getProfiler();
        profiler.setListener(GLErrorListener.LOGGING_LISTENER);
        VisLabel description = new VisLabel("Ticks per GLProfiler reset:");
        ticksInput = new VisValidatableTextField(new Validators.IntegerValidator());
        ticksInput.setWidth(20);

        ticksInput.setText("1");
        ticksInput.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (ticksInput.isInputValid()) {
                    ticksUntilReset = Integer.parseInt(ticksInput.getText());
                }

            }
        });


        drawCallsLabel = new VisLabel("Draw Calls: " + profiler.getDrawCalls());
        shaderSwitchesLabel = new VisLabel("Shader Switches: " + profiler.getShaderSwitches());
        textureBindingsLabel = new VisLabel("Texture Bindings: " + profiler.getTextureBindings());
        vertexCountLabel = new VisLabel("Vertex Count: " + profiler.getVertexCount().count);

        add(description).left();
        add(ticksInput).left().padLeft(20).padRight(50).maxSize(30,20);
        row();
        add(new Separator()).expandX().fillX().padBottom(10).padTop(10).colspan(2);
        row();
        add(drawCallsLabel).left().row();
        add(shaderSwitchesLabel).left().row();
        add(textureBindingsLabel).left().row();
        add(vertexCountLabel).left().row();
        profiler.enable();





    }

    @Override
    public void act(float delta) {
        ticks++;



        if(ticks>ticksUntilReset){
            drawCallsLabel.setText("Draw Calls: " + profiler.getDrawCalls());
            shaderSwitchesLabel.setText("Shader Switches: " + profiler.getShaderSwitches());
            textureBindingsLabel.setText("Texture Bindings: " + profiler.getTextureBindings());
            vertexCountLabel.setText("Vertex Count: " + profiler.getVertexCount().count);

            profiler.reset();
            ticks = 0;
        }

        super.act(delta);
    }

}

private class CameraProfilerTab extends Tab {

    Table content;
    String title;

    public CameraProfilerTab() {
        super(false, false);
        this.title = "Camera";
        this.content = new CameraProfilerTable();
    }

    @Override
    public String getTabTitle() {
        return title;
    }

    @Override
    public Table getContentTable() {
        return content;
    }

}

private class CameraProfilerTable extends Table{

        VisLabel distanceToOrigin,distanceToCamera,distanceToWorldCoord,distanceX,distanceY,distanceZ;
        VisLabel cameraX,cameraY,cameraZ;
        VisLabel worldX,worldY,worldZ;
        VisLabel worldIntersection;
        VisLabel xPlaneIntersection,yPlaneIntersection,zPlaneIntersection;
        VisLabel currentPos;
        VisLabel camDir;
        VisLabel mousePos;
        VisLabel cameraDirection;
        VerticalGroup group1;
        VerticalGroup group2;
        VisLabel transformState;

        MousePickingTool i;

        public CameraProfilerTable(){
            super();
            i = MousePickingTool.getMousePicker();
            pad(2);
            align(Align.topLeft);
            group1 = new VerticalGroup();
            group2 = new VerticalGroup();
            createLabels();
            addLabels();
        }

    private void createLabels() {
            distanceToOrigin = new VisLabel("Distance to Origin: ");
            distanceToCamera = new VisLabel("Distance to Camera: ");
            distanceToWorldCoord = new VisLabel("Distance to World Coord: ");
            distanceX = new VisLabel("Distance X: ");
            distanceY = new VisLabel("Distance Y: ");
            distanceZ = new VisLabel("Distance Z: ");

            cameraX = new VisLabel("Camera X: ");
            cameraY = new VisLabel("Camera Y: ");
            cameraZ = new VisLabel("Camera Z: ");

            worldX = new VisLabel("World X: ");
            worldY = new VisLabel("World Y: ");
            worldZ = new VisLabel("World Z: ");

            xPlaneIntersection = new VisLabel("X Plane Intersection: ");
            yPlaneIntersection = new VisLabel("Y Plane Intersection: ");
            zPlaneIntersection = new VisLabel("Z Plane Intersection: ");
            transformState = new VisLabel("Transform State: ");

            worldIntersection = new VisLabel("World Intersection: ");
            currentPos = new VisLabel("Current Position: ");
            camDir = new VisLabel("Projected Mouse Position: ");
            mousePos = new VisLabel("Mouse Position: ");
    }

    private void addLabels(){
            group1.addActor(distanceToOrigin);
            group1.addActor(distanceToCamera);
            group1.addActor(distanceToWorldCoord);
            group1.addActor(distanceX);
            group1.addActor(distanceY);
            group1.addActor(distanceZ);

            group2.addActor(cameraX);
            group2.addActor(cameraY);
            group2.addActor(cameraZ);
            group2.addActor(currentPos);
            group2.addActor(camDir);
            group2.addActor(mousePos);
            group2.addActor(xPlaneIntersection);
            group2.addActor(yPlaneIntersection);
            group2.addActor(zPlaneIntersection);
            group2.addActor(transformState);

            group1.align(Align.left);
            group2.align(Align.left);
            group1.columnLeft();
            group2.columnLeft();
            group1.pad(2);
            add(group1).left().pad(10);
            add(group2).left();

//            add(distanceToOrigin).left().pad(2);
//            row();
//            add(distanceToCamera).left().pad(2);
//            row();
//            add(distanceToWorldCoord).left().pad(2);
//
//            row();
//            add(distanceX).left().pad(2);
//            row();
//            add(distanceY).left().pad(2);
//            row();
//            add(distanceZ).left().pad(2);
//            row();
//            add(cameraX).left().pad(2);
//            row();
//            add(cameraY).left().pad(2);
//            row();
//            add(cameraZ).left().pad(2);
//            row();
//
//             add(currentPos).left().pad(2);
//
//
//            row();
//            add(projectedMousePos).left().pad(2);
//            row();
//            add(xPlaneIntersection).left().pad(2);
//            row();
//            add(yPlaneIntersection).left().pad(2);
//            row();
//            add(zPlaneIntersection).left().pad(2);


    }

    private void updateLabels(){
            distanceToOrigin.setText("world dst to origin:  "+ StringUtils.trimFloat(i.distanceToOrigin));
            distanceToCamera.setText("world dst to camera:  "+ StringUtils.trimFloat(i.distanceToCamera));
            distanceToWorldCoord.setText("ray dst to world coord:  "+ StringUtils.trimFloat(i.distanceToWorldIntersection));

            distanceX.setText("dst x:  "+ StringUtils.trimFloat(i.distanceX));
            distanceY.setText("dst y:  "+ StringUtils.trimFloat(i.distanceY));
            distanceZ.setText("dst z:  "+ StringUtils.trimFloat(i.distanceZ));

            cameraX.setText("cam x:  "+ StringUtils.trimFloat(i.cameraPosition.x));
            cameraY.setText("cam y:  "+ StringUtils.trimFloat(i.cameraPosition.y));
            cameraZ.setText("cam z:  "+ StringUtils.trimFloat(i.cameraPosition.z));

            zPlaneIntersection.setText("degree:  "+ StringUtils.trimFloat(Context.getInstance().gizmoSystem.rotateTool.degree));
            yPlaneIntersection.setText("dst:  "+ StringUtils.trimFloat((float)Context.getInstance().gizmoSystem.rotateTool.dst));
            xPlaneIntersection.setText("intersection point (rot):  "+ StringUtils.trimVector3(Context.getInstance().gizmoSystem.rotateTool.intersectionPoint));
            transformState.setText("transform state:  "+ Context.getInstance().gizmoSystem.rotateTool.rotationState);
            mousePos.setText("mouse pos:  "+ (i.mousePos));
            currentPos.setText("camera pos:  "+ StringUtils.trimVector3(TranslateTool.currentPos));
            camDir.setText("camera dir:  "+ StringUtils.trimVector3(Context.getInstance().camera.direction));
            worldIntersection.setText("worldX:  "+ StringUtils.trimFloat(i.worldX)+", worldY:  "+ StringUtils.trimFloat(i.worldY)+", worldZ:  "+ StringUtils.trimFloat(i.worldZ));
    }

    @Override
    public void act(float delta){
            updateLabels();
            i.update();
            i.render(Context.getInstance().camera);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        super.draw(batch, parentAlpha);
    }

}

}