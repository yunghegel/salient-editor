package core.systems;

import backend.tools.Log;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import core.components.PlayerComponent;
import core.components.SceneComponent;
import editor.Context;
import input.ThirdPersonPlayerController;
import net.mgsx.gltf.scene3d.scene.Scene;

public class PlayerSystem extends IteratingSystem
{

    public boolean playerModeEnabled = false;
    ComponentMapper<PlayerComponent> pc = ComponentMapper.getFor(PlayerComponent.class);
    ComponentMapper<SceneComponent> sc = ComponentMapper.getFor(SceneComponent.class);
    SceneComponent sceneComponent;
    PlayerComponent playerComponent;
    Scene playerScene;
    AnimationController animController;
    Context context;
    ThirdPersonPlayerController thirdPersonPlayerController;

    public PlayerSystem() {
        super(Family.all(PlayerComponent.class , SceneComponent.class).get());

    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void addedToEngine(Engine engine) {
        thirdPersonPlayerController = new ThirdPersonPlayerController(context.getCamera());
        Log.info("PlayerSystem" , "PlayerSystem added to engine");
        super.addedToEngine(engine);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (playerModeEnabled) {
            thirdPersonPlayerController.updateCamera();
            thirdPersonPlayerController.processInput(playerScene , context.getCamera());
            animController.update(deltaTime);
            animController.animate("Stand" , -1 , 1f , null , 0.2f);
        }

    }

    @Override
    protected void processEntity(Entity entity , float deltaTime) {
        sceneComponent = sc.get(entity);
        playerComponent = pc.get(entity);
        playerScene = sceneComponent.scene;
        animController = sceneComponent.scene.animationController;

    }

}
