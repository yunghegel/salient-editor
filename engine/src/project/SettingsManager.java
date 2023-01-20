package project;

public class SettingsManager {


    private static SettingsManager instance = null;
    public static SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }


}
