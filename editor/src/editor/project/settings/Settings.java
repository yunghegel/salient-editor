package editor.project.settings;

public class Settings
{

    public static final float CAMERA_START_PITCH = 20f;                     // Default Starting pitch
    public static final float CAMERA_MIN_PITCH = Settings.CAMERA_START_PITCH - 20f;  // Min Pitch
    public static final float CAMERA_MAX_PITCH = Settings.CAMERA_START_PITCH + 20f;  // Max Pitch
    public static final float CAMERA_PITCH_FACTOR = 0.3f;
    public static final float CAMERA_ZOOM_LEVEL_FACTOR = 0.5f;              // Our zoom multiplier (speed)
    public static final float CAMERA_ANGLE_AROUND_PLAYER_FACTOR = 0.2f;     // Rotation around player speed
    public static final float CAMERA_MIN_DISTANCE_FROM_PLAYER = 4;          // Min zoom distance
    public static final float CAMERA_MAX_DISTANCE_FROM_PLAYER = 10f;

    public static final String PROJECT_NAME = "Salient";
    public static final String PROJECT_VERSION = "0.0.1";
    public static final String PROJECT_AUTHOR = "Salient Team";
    public static final String PROJECT_DESCRIPTION = "Salient is a 3D game engine written in Java using the LibGDX framework.";

    public enum CursorMode
    {
        CAPTURED, RELEASED
    }



    public enum Resolution
    {
        _720p(1280 , 720), _1080p(1920 , 1080), _1440p(2560 , 1440), _2160p(3840 , 2160);

        private final int width;
        private final int height;

        Resolution(int width , int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    public static class FilePaths
    {

        public static final String ICON = "icons/icon.png";
        public static final String SKIN = "skin/tixel.json";
        public static final String MODELS_DIR = "models";
        public static final String RES_DIR = "resources";
        public static final String SHADERS_DIR = "shaders/";
        public static final String IMAGES_DIR = "images/";
        public static final String CUBEMAP_DIR = "cubemaps/";

    }

    public static class Window
    {

        public static final int WIDTH = 1920;
        public static final int HEIGHT = 1080;
        public static final String TITLE = "editor.Editor";

    }

    public static class Graphics
    {

        public static final int ANTIALIASING = 2;
        public static final int DEPTH = 8;
        public static final int STENCIL = 8;
        public static final boolean VSYNC = false;
        public static final int IDLE_FPS = 144;

    }

    public static class GameConfig
    {
        public static boolean PROFILING_ENABLED = true;
        public static boolean DEBUG = true;
    }

}
