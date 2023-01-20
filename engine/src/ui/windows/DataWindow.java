package ui.windows;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.kiwi.util.tuple.immutable.Triple;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kotcrab.vis.ui.util.highlight.Highlighter;
import com.kotcrab.vis.ui.widget.HighlightTextArea;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import sys.io.ResourceRegistry;


public class DataWindow extends VisWindow {

static HighlightTextArea textArea;
static ScrollPane scrollPane;

    VisTextButton updateButton;
   public DataWindow(Engine engine, Table table){
        super("Data");
        setResizable(true);
        addCloseButton();
        closeOnEscape();
        setSize(800, 400);
        setPosition(300,300);
        updateButton = new VisTextButton("Update");
        updateButton.setSize(30,20);




        //filter the text area so that arrays and triples are formatted with line breaks


        textArea = new HighlightTextArea(" ");

       scrollPane = textArea.createCompatibleScrollPane();
         scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        scrollPane.setScrollbarsVisible(true);

       add(scrollPane).expand().fill().grow();
       textArea.setFillParent(true);
       textArea.setCursorAtTextEnd();





   //    table.add(this).expand().fill().grow();
        //add(textArea).expand().fill();

        add(updateButton);
        initListeners(engine,table);


    }

    @Override
    public void close () {
        setVisible(false);
    }

    public static String prettyPrintData(Object data){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        textArea.appendText( gson.toJson(data));



    return gson.toJson(data);
    }

    public void setHighighter(Highlighter highlighter){
        textArea.setHighlighter(highlighter);
    }



    public void initListeners(Engine engine, Table table){
        Array<Triple<Class,String,String>> triple = ResourceRegistry.getResources();
       textArea.appendText("\n");

       updateButton.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
                textArea.setText("");



               textArea.appendText(ResourceRegistry.getSceneComponents().toString());
               textArea.appendText("\n");
              textArea.appendText(ResourceRegistry.getImages().toString());
               textArea.appendText("\n");
               textArea.appendText(ResourceRegistry.getResources().toString());
               textArea.appendText("\n");
               for (EntitySystem system : engine.getSystems()) {
                    {
                        textArea.appendText("Priority and process state for " + system.getClass().getSimpleName() + " is: ");
                        textArea.appendText("\n");
                       textArea.appendText(Integer.toString(system.priority));
                        textArea.appendText("\n");
                       textArea.appendText(Boolean.toString(system.checkProcessing()));
                        textArea.appendText("\n");

                       }

                   }

                float i;
                for (i = 0; i < triple.size; i++) {
                    textArea.appendText(" ");
                    textArea.appendText("\n");
                    String s = triple.get((int) i).getFirst().getSimpleName();
                    textArea.appendText("\n");
                    textArea.appendText(s);
                    textArea.appendText(" ");
                    textArea.appendText("\n");
                    textArea.appendText(triple.get((int) i).getSecond());
                    textArea.appendText(" ");
                    textArea.appendText("\n");
                    textArea.appendText(triple.get((int) i).getThird());
                    textArea.appendText(" ");

                }

                scrollPane.setForceScroll(false, true);
                //autoscroll


               }


       });
        textArea.addListener(new EventListener() {
            @Override
            public boolean handle (Event event) {
                textArea.layout();

                return false;
            }
        });
   }


}