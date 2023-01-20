package backend;

public interface Natives
{
    void setPosition(int x, int y);

    void setFullscreen();

    void setWindowedMode(int width, int height);

    void resizeWindow(int width, int height);

    void restoreWindow();
}
