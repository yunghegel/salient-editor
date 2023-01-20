package backend;

import backend.tools.Perf;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.async.ThreadUtils;
import core.entities.TwoDimensionalEntityFactory;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import static backend.EditorIO.io;

public class DefaultAssets
{
    public static DefaultAssets i = new DefaultAssets();

    public SceneAsset playerSceneAsset;
    public SceneAsset staticMapScene;
    public SceneAsset riggedCharacterSceneAsset;
    public SceneAsset crateSceneAsset;

    public Skin skin;
    public Texture redTexture;
    public Texture greenTexture;
    public Texture blueTexture;
    public boolean loaded = false;

    public DefaultAssets(){
        i = this;

        int assets =Perf.start("default_assets");

        int uiSKin = Perf.start("skin");
        skin = new Skin(Gdx.files.internal("skin/tixel.json"));
        Perf.end(uiSKin);

        int textures = Perf.start("textures_all");
        redTexture = io().loadTexture("dev_mat/RED.png", "red_swatch");
        greenTexture = io().loadTexture("dev_mat/GREEN.png", "green_swatch");
        blueTexture = io().loadTexture("dev_mat/BLUE.png", "blue_swatch");
        Perf.end(textures);

        int gltf = Perf.start("gltf_all");
        staticMapScene = io().loadGLTF("models/transmission_test/TransmissionTest.gltf", "map_scene");

        playerSceneAsset = io().loadGLTF("models/Player.gltf", "player_scene");
        riggedCharacterSceneAsset = io().loadGLTF("models/RiggedFigure.gltf", "rigged_model_scene");
        crateSceneAsset = io().loadGLTF("models/crate.gltf", "crate_scene");
        Perf.end(gltf);

        Perf.end(assets);
        io().manager.finishLoading();
        if (!io().manager.isFinished())
            ThreadUtils.yield();
        doneLoading();
    }



    static {

    }

    public void doneLoading(){


        TwoDimensionalEntityFactory.createTextureComponent(redTexture , "RED" , "dev_mat/RED.png");
        TwoDimensionalEntityFactory.createTextureComponent(greenTexture , "GREEN" , "dev_mat/GREEN.png");
        TwoDimensionalEntityFactory.createTextureComponent(blueTexture , "BLUE" , "dev_mat/BLUE.png");
        loaded = true;

    }

}
