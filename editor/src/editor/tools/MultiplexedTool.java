package editor.tools;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

public abstract class MultiplexedTool<T> implements Tool<T>, InputProcessor
{
    /**
     * A tool that operates on a specific type
     */

    protected T target;
    protected InputMultiplexer multiplexer;

    public MultiplexedTool(InputMultiplexer multiplexer)
    {
        this.multiplexer = multiplexer;
    }

    public void setSelection(T selection)
    {
        this.target = selection;
    }

    @Override
    public void enable() {
        multiplexer.addProcessor(this);
    }

    @Override
    public void disable() {
        multiplexer.removeProcessor(this);
    }

}
