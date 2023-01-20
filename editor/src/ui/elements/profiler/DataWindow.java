package ui.elements.profiler;

import backend.data.ComponentRegistry;
import backend.data.ObjectRegistry;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.kiwi.util.tuple.immutable.Triple;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.kotcrab.vis.ui.util.highlight.Highlighter;
import com.kotcrab.vis.ui.widget.*;
import editor.Context;
import sys.io.ResourceRegistry;
import ui.UserInterface;
import util.MiscUtils;

public class DataWindow extends VisWindow {
    MenuBar menu;
    HighlightTextArea textArea;
    ScrollPane scrollPane;
    Context ctx;
    VisTextButton setToProfilerWindow;
    UserInterface ui;
    VisTextButton updateButton;
    Menu dataBrowser;

    MenuItem engineData;
    MenuItem systemData;
    MenuItem entityData;
    MenuItem componentData;
    MenuItem resourceData;
    MenuItem inputData;

    ComponentRegistry componentRegistry;
    ObjectRegistry objectRegistry;


   public DataWindow(Context ctx, UserInterface ui){
        super("Data");
        this.ctx = ctx;
        this.ui = ui;


        initDataAccessors();
        createDataBrowsingMenu();
        createDataViewer();
        initListeners();
        createMenuListeners();

    }

    private void initDataAccessors() {
       componentRegistry = ComponentRegistry.getInstance();
       objectRegistry = ObjectRegistry.getInstance();
    }

    private void createDataBrowsingMenu() {

        menu = new MenuBar();
        getTitleTable().add(menu.getTable()).expandX().right().padRight(0);
        dataBrowser = new Menu("Browse");
        menu.addMenu(dataBrowser);

        engineData = new MenuItem("Engine");
        systemData = new MenuItem("Systems");
        entityData = new MenuItem("Entities");
        componentData = new MenuItem("Components");
        resourceData = new MenuItem("Resources");
        inputData = new MenuItem("Input");

        dataBrowser.addItem(engineData);
        dataBrowser.addItem(systemData);
        dataBrowser.addItem(entityData);
        dataBrowser.addItem(componentData);
        dataBrowser.addItem(resourceData);
        dataBrowser.addItem(inputData);
    }

    private void createMenuListeners(){
       componentData.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonArray componentJson =componentRegistry.getComponentData();
                String componentData = gson.toJson(componentJson);
                textArea.setText(componentData);
            }
        });
        entityData.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                textArea.setText(objectRegistry.toString());
            }
        });
        resourceData.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        });

        inputData.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                textArea.setText("Input Data");
                for(InputProcessor inputProcessor : ctx.inputMultiplexer.getProcessors()){
                    textArea.appendText(inputProcessor.getClass().getSimpleName()+"\n");
                }
            }
        });
    }

    public void createDataViewer(){
        updateButton = new VisTextButton("Update");
        updateButton.setSize(30,20);
        getTitleTable().add(updateButton).right().pad(5);

       setToProfilerWindow = new VisTextButton("Set to Profiler");
       getTitleTable().add(setToProfilerWindow).right().pad(5);

        textArea = new HighlightTextArea(" ");
        textArea.setReadOnly(true);
        textArea.setTouchable(Touchable.disabled);


        setHighighter(MiscUtils.createHighlighter());
        scrollPane = textArea.createCompatibleScrollPane();
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);


        scrollPane.setScrollbarsVisible(true);

        add(scrollPane).expand().fill().growY();

        textArea.setCursorAtTextEnd();
    }

    @Override
    public void close () {
        setVisible(false);
    }

    public String prettyPrintData(Object data){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        textArea.appendText( gson.toJson(data));
        return gson.toJson(data);
    }

    public void setHighighter(Highlighter highlighter){
        textArea.setHighlighter(highlighter);
    }

    public void initListeners() {
        Array<Triple<Class, String, String>> triple = ResourceRegistry.getResources();
        textArea.appendText("\n");

        updateButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event , Actor actor) {
                textArea.addListener(new EventListener()
                {
                    @Override
                    public boolean handle(Event event) {
                        textArea.layout();

                        return false;
                    }
                });
            }
        });
        setToProfilerWindow.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event , Actor actor) {
                ui.profilerObjectPreviewerSplitPane.setSecondWidget(ui.getEditorProfilerWindow());
            }
        });
    }
}


