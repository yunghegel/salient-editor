package backend.data;

import backend.annotations.PluginDef;
import backend.annotations.Storable;
import backend.tools.Plugin;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import sys.io.ComponentRegistry;
import util.ReflectionHelper;

public class EditorRegistry
{

    public final ObjectMap<Class, Json.Serializer> serializers = new ObjectMap<Class, Json.Serializer>();
    public final ObjectMap<String, Class<? extends Component>> typeMap = new ObjectMap<String, Class<? extends Component>>();
    public final ObjectMap<Class<? extends Component>, String> nameMap = new ObjectMap<Class<? extends Component>, String>();
    private final ObjectMap<Class, Object> models = new ObjectMap<Class, Object>();
    public Array<Class<? extends Component>> components = new Array<Class<? extends Component>>();
    ClassRegistry classRegistry = ClassRegistry.getInstance();
    ObjectRegistry objectRegistry = ObjectRegistry.getInstance();
    ComponentRegistry componentRegistry = ComponentRegistry.getInstance();
    private TypeMap<Plugin> plugins = new TypeMap<Plugin>();

    public EditorRegistry() {
    }

    private void registerPlugin(Class plugin) {
        scan(plugin);
    }

    public <T extends Plugin> T getPlugin(Class<T> type) {
        return (T) plugins.get(type);
    }

    public boolean registerPlugin(Plugin plugin) {
        if (plugins.containsKey(plugin.getClass())) return false;

        Class<?> type = plugin.getClass();
        boolean shouldBeLoaded = true;
        PluginDef def = type.getAnnotation(PluginDef.class);
        if (def != null) {
            for (String fqnTypeDep : def.requires()) {
                if (!ReflectionHelper.hasName(fqnTypeDep)) {
                    Gdx.app.log("KIT" , "plugin " + type.getName() + " not loaded : require " + fqnTypeDep);
                    shouldBeLoaded = false;
                }
            }
        }
        if (shouldBeLoaded) {
            scan(plugin.getClass());
            plugins.put(plugin.getClass() , plugin);
            return true;
        }
        plugins.put(plugin.getClass() , null);
        return false;
    }

    private void register(Class<? extends Component> type) {
        if (components.contains(type , true)) return;
        Storable storable = type.getAnnotation(Storable.class);
        if (storable != null) {
            register(type , storable.value());
        }

        components.add(type);

    }

    private void register(Class<? extends Component> storable , String name) {
        if (typeMap.containsKey(name)) {
            if (typeMap.get(name) == storable) {
                // use syout instead of GDX log because GdxLogger not enabled yet.
                System.out.println("skip type name " + name + " already registered for the same type.");
            }
            else {
                throw new Error("type name " + name + " already registered for class " + typeMap.get(name).getName());
            }
        }
        typeMap.put(name , storable);
        nameMap.put(storable , name);
    }

    private boolean scan(Class<?> type) {
        // TODO if debug : Gdx.app.log("registry", type.getName());
        if (type.getSuperclass() != null) {
            scan(type.getSuperclass());
        }
        for (Class<?> iface : type.getInterfaces()) {
            scan(iface);
        }
        PluginDef def = type.getAnnotation(PluginDef.class);
        if (def != null) {
            for (Class<? extends Plugin> dependency : def.dependencies()) {
                registerPlugin(ReflectionHelper.newInstance(dependency));
            }
            for (Class<? extends Component> component : def.components()) {
                register(component);
            }
        }
        return true;
    }

}
