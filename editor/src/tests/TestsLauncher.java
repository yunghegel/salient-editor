package tests;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import editor.project.settings.Settings;

public class TestsLauncher
{
    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new Test(), getDefaultConfiguration());
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

    public static void main(String[] args) {

        createApplication();
    }
}
