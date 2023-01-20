package sys.io;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import sys.Log;

public class EditorRegistry {
    public final ObjectMap<Class, Json.Serializer> serializers = new ObjectMap<Class, Json.Serializer>();
    public Array<Class<? extends Component>> components = new Array<Class<? extends Component>>();
    private final ObjectMap<Class, Object> models = new ObjectMap<Class, Object>();
    private static ObjectMap<String, Class<? extends Component>> typeMap = new ObjectMap<String, Class<? extends Component>>();
    private static ObjectMap<Class<? extends Component>, String> nameMap = new ObjectMap<Class<? extends Component>, String>();

    private static EditorRegistry instance = new EditorRegistry();

    public static EditorRegistry getInstance() {
        return instance;
    }

    public EditorRegistry() {

    }

    /**
     * Register a component type to be stored and persisted.
     * @param storable the component type to register
     *
     * @param name the name of the component type
     */


    private static void register(Class<? extends Component> storable, String name)
    {
        if(typeMap.containsKey(name)){
            if(typeMap.get(name) == storable){
                // use syout instead of GDX log because GdxLogger not enabled yet.
                Log.info("Registry","skip type name " + name + " already registered for the same type.");
            }else{
                throw new Error("type name " + name + " already registered for class " + typeMap.get(name).getName());
            }
        }
        typeMap.put(name, storable);
        nameMap.put(storable, name);
    }

}
