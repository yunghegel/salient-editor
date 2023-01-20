package backend.data;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import sys.Log;

import java.lang.reflect.Field;

public class ComponentRegistry
{

    JsonArray componentArray = new JsonArray();


    public static ComponentRegistry instance = new ComponentRegistry();
    public static ObjectMap<Class<? extends Component>, String> nameMap = new ObjectMap<Class<? extends Component>, String>();
    public static Array<Class<? extends Component>> registry = new Array<Class<? extends Component>>();

    public static ComponentRegistry getInstance() {
        return instance;
    }

    public static Array<Class<? extends Component>> getRegistry() {
        return registry;
    }

    public static void register(Component component) {
        registerComponent(component.getClass());

    }

    public static void registerComponent(Class<? extends Component> component) {
        if (nameMap.containsKey(component)) {
            Log.info("skip type name " + component.getSimpleName() + " already registered for the same type.");
            return;
        }
        String name = component.getSimpleName();
        nameMap.put(component , name);
        registry.add(component);
        Log.info("ComponentRegistry" , "Registered component " + component.getSimpleName() + " as " + name);
    }

    public void register(Class<? extends Component> component , String name) {
        if (nameMap.containsValue(component.getSimpleName() , true) | registry.contains(component , true)) {
            Log.info("skip type name " + name + " already registered for the same type.");
            return;
        }
        registry.add(component);
        nameMap.put(component , name);
        Log.info("ComponentRegistry" , "Registered component " + component.getSimpleName() + " as " + name);
    }

    public void unregister() {
    }

    public JsonArray getComponentData() {

        JsonArray componentArray = new JsonArray();
        for (Class<? extends Component> component : registry) {
            JsonArray componentData = new JsonArray();
            JsonObject componentType = new JsonObject();
            componentType.addProperty("type" , component.getSimpleName());
            componentArray.add(componentType);

            JsonArray componentObject = new JsonArray();
            JsonObject fieldObject = new JsonObject();


            Field[] fields = component.getDeclaredFields();
            for (Field field : fields) {

                fieldObject.addProperty(field.getName() , field.getType().getSimpleName());
            }
            componentObject.add(fieldObject);
            componentData.add(componentObject);
            componentArray.add(componentData);

        }
        return componentArray;
    }


}
