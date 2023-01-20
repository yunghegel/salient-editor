package ui;

import com.badlogic.gdx.Gdx;
import com.kotcrab.vis.ui.widget.VisLabel;


public class GLProfileLabel extends VisLabel {
    public GLProfileLabel() {
        super("GLProfileLabel");

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setText("GLProfile:"
        + "Mouse: " + Gdx.input.getX() + ", " + Gdx.input.getY()
                + " | " + EditorStage.leftTable.getWidth() + ", " + EditorStage.bottomPane.getHeight()
                + " | " + Gdx.graphics.getWidth() + ", " + Gdx.graphics.getHeight()
                + " | " + Gdx.graphics.getBackBufferWidth() + ", " + Gdx.graphics.getBackBufferHeight()

        );
    }
}


