package editor;

import backend.DesktopNatives;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import core.DesktopWorker;
import editor.project.settings.Settings;

public class Editor implements DesktopWorker
{

    static Skin skin;
    static BitmapFont font;
    static VisLabel progressLabel;
    static CharSequence text = "Salient editor.Editor";
    static ShapeRenderer shapeRenderer;
    static VisProgressBar progressBar;
    boolean doneLoadingCallback = false;


    public static void main(String[] args) {

        //core.SplashScreen splashScreen = new core.SplashScreen();
        //splashScreen.createSplashScreen();
        createApplication();




    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new editor.EditorGame(new DesktopNatives()) , getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();

        configuration.setTitle(Settings.Window.TITLE);
        configuration.setWindowIcon(Settings.FilePaths.ICON);
        configuration.setWindowedMode(Settings.Window.WIDTH , Settings.Window.HEIGHT);
        configuration.setBackBufferConfig(1 , 1 , 1 , 1 , Settings.Graphics.DEPTH , Settings.Graphics.STENCIL , Settings.Graphics.ANTIALIASING);
        configuration.useVsync(Settings.Graphics.VSYNC);
        configuration.setDecorated(true);
        configuration.setForegroundFPS(Settings.Graphics.IDLE_FPS);
        return configuration;
    }

    public static void drawShapeBehindText() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0 , 0 , 0 , 1f);
        shapeRenderer.rect(805 / 2f - 150 , 448 / 2f , 150 , 50);
        shapeRenderer.end();
    }

    @Override
    public void dragWindow(int x , int y) {
        Lwjgl3Window window = ( (Lwjgl3Graphics) Gdx.graphics ).getWindow();
        window.setPosition(x , y);
    }

    @Override
    public int getWindowX() {
        return ( (Lwjgl3Graphics) Gdx.graphics ).getWindow().getPositionX();

    }

    @Override
    public int getWindowY() {
        return ( (Lwjgl3Graphics) Gdx.graphics ).getWindow().getPositionY();

    }

    @Override
    public void closeSplash() {

    }

}
