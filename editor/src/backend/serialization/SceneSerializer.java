package backend.serialization;

import backend.data.ComponentRegistry;
import backend.data.ObjectRegistry;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

import java.lang.reflect.Field;

public class SceneSerializer
{

    ComponentRegistry componentRegistry;
    ObjectRegistry objectRegistry;
    SceneSaveState sceneSaveState;

    public SceneSerializer() {
        sceneSaveState = SceneSaveState.getInstance();
        getRegistry();
    }

    public void getRegistry() {
        componentRegistry = ComponentRegistry.getInstance();
        objectRegistry = ObjectRegistry.getInstance();
    }

    public void serializeComponentRegistry() {
        Array<Class<? extends Component>> registry = ComponentRegistry.getRegistry();
        for (Class<? extends Component> component : registry) {
            serializeType(component);
        }
    }

    private void serializeType(Class component) {
        Array<Field> fields = new Array<Field>();
        Field[] declaredFields = component.getFields();
        for (int i = 0; i < declaredFields.length; i++) {
            Field field = declaredFields[i];
            sceneSaveState.writeFieldToJson(field);
        }

    }

}

