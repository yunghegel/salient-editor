package core;

import backend.DefaultAssets;
import backend.tools.Perf;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisWindow;

public class SplashScreen
{

    static Skin skin;
    static BitmapFont font;
    static VisLabel progressLabel;
    static CharSequence text = "Salient editor.Editor";
    static ShapeRenderer shapeRenderer;
    static VisProgressBar progressBar;
    public Perf.PerfEndedListener perfEndedListener;
    public static boolean assetsFinishedLoading = false;

    public SplashScreen(){


        perfEndedListener = new Perf.PerfEndedListener()
        {
            @Override
            public boolean perfEnded(Perf.Metric metric,String name) {
                System.out.println("Perf "+name +" ended in duration "+ metric.duration);
                updateProgressLabel(name);
                return true;
            }
        };
        Perf.addListener(perfEndedListener);
    }

    public static void createSplashScreen() {

        Lwjgl3ApplicationConfiguration config = createApplicationConfiguration();

        new Lwjgl3Application(new ApplicationAdapter()
        {
            Stage stage;
            Table table;
            VisLabel label;
            VisImage image;
            private Batch batch;
            private Texture texture;
            private float time;

            @Override
            public void create() {
                skin = new Skin(Gdx.files.internal("skin/tixel.json"));
                texture = new Texture(Gdx.files.internal("images/splash.png"));
                shapeRenderer = new ShapeRenderer();
                VisUI.load(skin);

                progressBar = new VisProgressBar(0 , 3 , 1 , false);
                progressBar.setColor(Color.WHITE);
                progressBar.setHeight(5);
                progressBar.setWidth(300);

                stage = new Stage(new ScreenViewport());
                table = new Table();
                table.setFillParent(true);
                label = new VisLabel("Salient editor.Editor");
                label.setColor(.1f , .1f , .1f , 1);
                label.setAlignment(Align.center);
                label.scaleBy(2);
                image = new VisImage(texture);
                image.setFillParent(true);
                table.add(image);
                table.row();
                //        table.add(image);
                VisWindow window = new VisWindow("Salient editor.Editor");
                window.add(image);
                window.row();
                progressLabel = new VisLabel("Loading settings and creating default assets...");
                progressLabel.setAlignment(Align.center);

                window.add(progressLabel).padTop(5);
                window.row();

                window.add(progressBar).padBottom(10).padLeft(60).padRight(60f).expandX().growX().fillX();
                window.getTitleTable().pad(15f);
                window.setFillParent(true);

                stage.addActor(window);

                font = skin.getFont("default-font");
                // load your PNG

                batch = new SpriteBatch();
            }

            @Override
            public void render() {

                time += Gdx.graphics.getDeltaTime();

                // render your PNG
                Gdx.gl.glClearColor(0 , 0 , 0 , 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                Gdx.gl.glEnable(GL20.GL_BLEND);

                batch.getProjectionMatrix().setToOrtho2D(0 , 0 , 1 , 1);
                batch.begin();
                batch.draw(texture , 0 , 0 , 1 , 1);

                //        font.draw(batch, text, Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f);

                batch.end();
                stage.act();
                stage.draw();
                progressBar.setValue(time);
                progressBar.updateVisualValue();

                if (DefaultAssets.i.loaded) {
                    Gdx.app.exit();
                    Perf.flush();
                }


            }

            @Override
            public void dispose() {
                VisUI.dispose();
                batch.dispose();
            }

        } , config);

    }

    public static Lwjgl3ApplicationConfiguration createApplicationConfiguration() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.disableAudio(true);
        config.setDecorated(false);
        config.setResizable(false);
        config.setTransparentFramebuffer(true);
        config.setWindowedMode(805 , 458);
        return config;
    }

    public static void setProgressValue(float value) {
        progressBar.setValue(value);
        progressBar.updateVisualValue();
    }

    public static void updateProgressLabel(String status){
        progressLabel.setText("Loading "+status);
    }

}
