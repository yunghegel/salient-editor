package core.components;

import backend.data.ComponentRegistry;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import input.ThirdPersonPlayerController;
import net.mgsx.gltf.scene3d.scene.Scene;

public class PlayerComponent implements Component
{

    public final static ComponentMapper<PlayerComponent> mapper = ComponentMapper.getFor(PlayerComponent.class);

    private Scene playerScene;
    private ThirdPersonPlayerController.AnimState state;

    public PlayerComponent() {
        ComponentRegistry.register(this);
    }

    public void setPlayerScene(Scene playerScene) {
        this.playerScene = playerScene;
    }

    public void setState(ThirdPersonPlayerController.AnimState state) {
        this.state = state;
    }

}
