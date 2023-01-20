package ui.windows;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.highlight.Highlighter;
import com.kotcrab.vis.ui.widget.HighlightTextArea;
import com.kotcrab.vis.ui.widget.VisWindow;

public class CollapsibleTextWindow extends VisWindow {
    private boolean collapsed;
    private float collapseHeight = 20f;
    private float expandHeight;
    private HighlightTextArea textArea;
    private Highlighter highlighter;

    public CollapsibleTextWindow(String title, float x, float y, float width, float height) {
        super(title);
        TableUtils.setSpacingDefaults(this);
        addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                if (getTapCount() == 2 && getHeight() - y <= getPadTop() && y < getHeight() && x > 0 && x < getWidth())
                    toggleCollapsed();
            }
        });
        setResizable(true);
        setSize(width, height);
        setPosition(x, y);
        setColor(1.0f, 1.0f, 1.0f, 0.5f);
        //hide the title and scrollbars
        getTitleTable().setVisible(false);


        //shrink the text size

        textArea = new HighlightTextArea("");
        textArea.getStyle().background = null;
        textArea.setProgrammaticChangeEvents(true);

        highlighter = new Highlighter();
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
        textArea.setHighlighter(highlighter);
        ScrollPane pane = textArea.createCompatibleScrollPane();
        pane.setScrollingDisabled(true, false);
        pane.setScrollbarsVisible(false);
        add(pane).grow().fill().expand();
        //add(textArea.createCompatibleScrollPane()).grow().fill().expand();
        textArea.setReadOnly(true);
        textArea.setBlinkTime(0);
        //autoscroll to bottom
        textArea.addListener(new EventListener() {
            @Override
            public boolean handle (Event event) {
                textArea.layout();

                return false;
            }
        });
        //hide the scrollbars



    }

    public void addTextAreaListener(EventListener listener) {
        textArea.addListener(listener);
    }

    public String getText() {
        return textArea.getText();
    }

    public void setText(String value) {
        textArea.setText(value);
    }

    public void addText(String value) {
        setText(getText() + value);
        textArea.setCursorPosition(getText().length());
    }

    public void expand () {
        if (!collapsed) return;
        setHeight(expandHeight);
        setY(getY() - expandHeight + collapseHeight);
        collapsed = false;
    }

    public void collapse () {
        if (collapsed) return;
        expandHeight = getHeight();
        setHeight(collapseHeight);
        setY(getY() + expandHeight - collapseHeight);
        collapsed = true;
        if (getStage() != null) getStage().setScrollFocus(null);
    }

    public void toggleCollapsed () {
        if (collapsed)
            expand();
        else
            collapse();
    }

    public boolean isCollapsed () {
        return collapsed;
    }
}