package editor.tools;

public interface Tool<T>
{
    /**
    * A tool that operates on a specific type
    */

    void update();

    void setSelection(T selection);

    void enable();

    void disable();
}
