package editor;

import backend.DefaultAssets;
import backend.Natives;
import backend.data.RegistryWriter;
import backend.tools.Perf;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.utils.async.ThreadUtils;
import editor.graphics.screens.MainScreen;


import java.io.IOException;

public class EditorGame extends Game
{



    Context context;
    public Natives natives;
    public static EditorGame i(){
        return (EditorGame) Gdx.app.getApplicationListener();
    }
    public boolean assetsLoaded = false;
    long ms = 0;



    public EditorGame(Natives natives) {

        this.natives = natives;
    }

    public static void setScreenTo(Screen screen) {

    }

    @Override
    public void create() {
        DefaultAssets.i = new DefaultAssets();
        try {
            RegistryWriter.instance.writeAllToResourceRegistry();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!DefaultAssets.i.loaded) {
            ThreadUtils.yield();


        }
        context = new Context();

        setScreen(new MainScreen(this,context));
    }

    @Override
    public void dispose() {
        super.dispose();

        context.dispose();
        Perf.flush();
    }

}
