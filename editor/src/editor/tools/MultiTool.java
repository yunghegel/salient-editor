package editor.tools;

public interface MultiTool<TypeGroup>
{
    /**
     * A tool that operates on multiple types
     */

    void update();

    void setTarget(Object target);
}
