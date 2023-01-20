package salient;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import ecs.entities.EntityFactory;

public class SalientAssets {

    public static void createEntities(Engine engine){
        Entity entity = EntityFactory.createSceneComponent("models/test_map/test_map.gltf","scene");
        engine.addEntity(entity);

    }

}
