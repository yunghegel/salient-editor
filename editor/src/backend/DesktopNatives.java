package backend;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import org.lwjgl.glfw.GLFW;

public class DesktopNatives implements Natives
{
    @Override
    public void setPosition(int x, int y) {
        Lwjgl3Window window = ((Lwjgl3Graphics) Gdx.graphics).getWindow();
        int currentX = window.getPositionX();
        int currentY = window.getPositionY();
        //GLFW.glfwSetWindowPos(window.getWindowHandle() , currentX, currentY);
        int newX = currentX + x;
        int newY = currentY + y;

        //window.setPosition(currentX - x, currentY - y);
        System.out.println("Current position: " + currentX+x + " " + currentY+x);



        GLFW.glfwSetWindowPos(window.getWindowHandle(), newX, newY);
    }

    @Override
    public void setFullscreen() {
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
    }

    @Override
    public void setWindowedMode(int width , int height) {
        Gdx.graphics.setWindowedMode(width, height);
    }

    @Override
    public void resizeWindow(int width , int height) {
        Lwjgl3Window window = ((Lwjgl3Graphics) Gdx.graphics).getWindow();
        GLFW.glfwSetWindowSize(window.getWindowHandle(), width, height);
    }

    @Override
    public void restoreWindow() {

    }

}
