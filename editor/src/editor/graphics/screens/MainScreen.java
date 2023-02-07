package editor.graphics.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.ui.widget.VisTextArea;
import editor.Context;
import ui.UserInterface;
import ui.elements.CompassWidget;

public class MainScreen implements Screen
{

    Engine engine;
    Stage stage;
    Game game;
    UserInterface ui;
    Context context;
    CompassWidget compassWidget;
    BitmapFont font;
    VisTextArea label;
    Stage otherStage;
    SpriteBatch batch;


    public MainScreen(Game game,Context context) {
        this.game = game;
        this.context = context;

        stage = context.getStage();
        engine = context.engine;
        ui = UserInterface.getInstance();
        ui.setContext(context);
        ui.init();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("skin/Minecraftia-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size=14;
        parameter.shadowColor= Color.BLACK;
        parameter.borderColor = Color.BLACK;
        font = generator.generateFont(parameter);
        batch = new SpriteBatch();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glViewport(0 , 0 , Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(.3f , .3f , .3f , 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        engine.update(delta);

        processInput();
        stage.act(delta);
        stage.draw();


        batch.begin();
        font.draw(batch, "" + Gdx.graphics.getFramesPerSecond(), ui.leftTable.getWidth()+10, Gdx.graphics.getHeight()-40);
        batch.end();
        context.getConsole().draw();

    }

    @Override
    public void resize(int width , int height) {
        stage.getViewport().update(width , height , true);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        context.dispose();
        game.dispose();
    }

    public void processInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.input.setCursorCatched(false);
        }
//        if () {
//
//            System.out.println("removed camera controller");
//        }
//        else {
//
//        }
//        if(Gdx.input.getY()<35&&Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
//            int deltaX = Gdx.input.getDeltaX();
//            int deltaY = Gdx.input.getDeltaY();
//
//            int x=Math.round(deltaX);
//            int y=Math.round(deltaY);
//
//            if (deltaX > 0) {
//                deltaX = 1;
//            }
//            else if (deltaX < 0) {
//                deltaX = -1;
//            }
//
//            EditorGame.getInstance().natives.setPosition(x,y);
//        }

    }

}
