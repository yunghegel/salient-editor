package ui;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import com.strongjoshua.console.GUIConsole;
import ui.windows.LogWindow;

public class BottomTabbedPane extends VisWindow {

    TabbedPane tabbedPane;
    Table container;
    VisWindow window;

    public BottomTabbedPane(){
    super(" ");

        TableUtils.setSpacingDefaults(this);

    setResizable(true);

    closeOnEscape();
    getTitleLabel().setHeight(0);
    getTitleLabel().setLayoutEnabled(false);
    removeActor(getTitleTable());
    layout();
    padTop(2);
    padBottom(2);


    final VisTable container = new VisTable();
    ButtonBar buttonBar = new ButtonBar();

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

    tabbedPane.add(new ContentTab("Log"));
    tabbedPane.add(new ContentTab("Console"));



    setSize(500, 400);
    centerWindow();

}
    @Override
    public void close () {
        setVisible(false);
    }

    @Override
    public float getPadTop() {
        return 0;
    }

    @Override
    public Table getTitleTable() {
        return null;
    }



private class ContentTab extends Tab {
    private String title;
    private Table content;
    LogWindow logWindow;
    GUIConsole console;
    HighlightTextArea textArea;
    VisTextField textField;

    public ContentTab(String title) {
        super(false, false);
        this.title = title;
        logWindow = new LogWindow("Log",getX(),getY(),getWidth(),getHeight());
        console = new GUIConsole();

        textArea = new HighlightTextArea(" ");
        textArea.createCompatibleScrollPane().setScrollingDisabled(true, false);
        textArea.setCursorAtTextEnd();
        textField = new VisTextField();
        textField.setText(" ");

        ScrollableTextArea scrollableTextArea = new ScrollableTextArea(" ");
        ScrollPane pane = scrollableTextArea.createCompatibleScrollPane();
        pane.setScrollbarsVisible(false);
        pane.setScrollingDisabled(true, false);
        pane.setFlickScroll(false);
        pane.setFadeScrollBars(false);
        pane.setScrollbarsOnTop(true);
        pane.setOverscroll(true, false);



        pane.setActor(textArea);
        content = new VisTable();
        content.add(pane).expand().fill().row();
        content.add(textField).expandX().fillX().row();
        content.padBottom(5);
        content.padTop(5);
        console.getWindow().pack();

    }

    @Override
    public String getTabTitle () {
        return title;
    }

    @Override
    public Table getContentTable () {
        return content;
    }


}
}