package ui.widgets;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;

public abstract class RenderWidgetInputProcessor implements InputProcessor
    {

        RenderWidget renderWidget;


        public abstract boolean touchDown(int screenX, int screenY, int pointer, int button);

        public abstract boolean touchUp(int screenX, int screenY, int pointer, int button);

        public abstract boolean touchDragged(int screenX, int screenY, int pointer);

        public abstract boolean mouseMoved(int screenX, int screenY);

        public abstract boolean scrolled(int amount);

    }

