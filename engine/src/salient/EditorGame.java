package salient;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.kotcrab.vis.ui.VisUI;
import ecs.systems.ObjectPickingSystem;
import ecs.systems.PhysicsSystem;
import ecs.systems.RenderSystem;
import project.Settings;


public class EditorGame extends Game {

  PhysicsSystem bulletPhysicsSystem;
  ObjectPickingSystem objectPickingSystem;
  RenderSystem renderSystem;
  Engine engine;

  public EditorGame() {
    super();


  }

  private void initEngine() {
    engine.addSystem(bulletPhysicsSystem);
    engine.addSystem(objectPickingSystem);
    engine.addSystem(renderSystem);
  }

  @Override
  public void create() {
    VisUI.load(Gdx.files.internal(Settings.FilePaths.SKIN));
    Gdx.gl.glEnable(GL20.GL_BLEND);



    this.setScreen(new EditorScreen(this));

  }

  @Override
  public void render() {

    super.render();
  }

  @Override
  public void resize(int width, int height) {
    super.resize(width, height);
  }

  @Override
  public void dispose() {

    super.dispose();

  }

}