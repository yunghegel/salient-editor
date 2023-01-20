package tests;

import com.badlogic.gdx.Game;

public class Test extends Game
{

    @Override
    public void create() {
        setScreen(new TestSelectionScreen(this));
    }

}

