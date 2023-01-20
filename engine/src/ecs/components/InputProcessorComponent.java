package ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.github.czyzby.kiwi.util.tuple.immutable.Pair;
import sys.io.ComponentRegistry;

public class InputProcessorComponent implements Component {
    public final static ComponentMapper<InputProcessorComponent> mapper = ComponentMapper.getFor(InputProcessorComponent.class);


    public static PerspectiveCamera camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    public static FirstPersonCameraController cameraController = new FirstPersonCameraController(camera);
    public static InputMultiplexer inputMultiplexer = new InputMultiplexer();
    public Pair<InputProcessor, Camera> inputAdapterWithCamera;

    public InputProcessorComponent(PerspectiveCamera cam, InputProcessor inputProcessor) {
        ComponentRegistry.register(this);



    }






    public void setInputProcessor(InputProcessor inputProcessor){

    }

    public void setCamera(Camera camera){
        this.camera = (PerspectiveCamera) camera;
    }





}

