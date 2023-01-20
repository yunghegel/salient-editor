package sys.io;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.tuple.immutable.Triple;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ecs.components.SceneComponent;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import sys.Log;
import ui.ObjectTree;

public class ResourceRegistry {



    public static ResourceRegistry instance = new ResourceRegistry();

     static ObjectMap<Class, Object> renderables = new ObjectMap<Class, Object>();
     static ObjectMap<Class, Object> images = new ObjectMap<Class, Object>();
     static ObjectMap<Class, Object> models = new ObjectMap<Class, Object>();
    static Triple<Class,String,String> resource = new Triple<Class, String, String>(null,null,null);
    static Array<Triple<Class,String,String>> resources = new Array<Triple<Class, String, String>>();
    static JsonArray jsonArray = new JsonArray();
    static Array<SceneComponent> sceneComponents = new Array<SceneComponent>();


    public ResourceRegistry() {

    }

//What is the syntax for or?

    public static void addSceneComponent(SceneComponent sceneComponent) {
        sceneComponents.add(sceneComponent);
        ObjectTree.addSceneComponentNode(sceneComponent);
    }

    public static Array<SceneComponent> getSceneComponents() {
        return sceneComponents;
    }


    public static void load(Class clazz,Object object, String id, String path) {
        if (object instanceof Renderable) {
            renderables.put(clazz, object);

            Log.info("ResourceRegistry", "Added an instance of" + clazz.getSimpleName()+ "to the resource registry");
        }
        if (object instanceof Texture || object instanceof TextureRegion || object instanceof Decal) {
            images.put(clazz, object);
            Log.info("ResourceRegistry", "Added an instance of" + clazz.getSimpleName()+ "to the resource registry");
        }
        if (object instanceof Model || object instanceof ModelInstance || object instanceof SceneAsset) {
            models.put(clazz, object);
            Log.info("ResourceRegistry", "Added an instance of" + clazz.getSimpleName()+ "to the resource registry");
        }
        resource = new Triple<Class, String, String>(clazz,id,path);
        resources.add(resource);
        Log.info("ResourceRegistry","Item "+ clazz.getName() + " successfully added to Resource Registry ... total: "+ resources.size);

        for (Triple<Class,String,String> resource : resources) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("class", resource.getFirst().getSimpleName());
            jsonObject.addProperty("name", resource.getSecond());
            jsonObject.addProperty("path", resource.getThird());
            jsonArray.add(jsonObject);
        }

        }


public static JsonArray getResourcesAsJson(){
    return jsonArray;
}

    public static ObjectMap<Class, Object> getRenderables() {
        return renderables;
    }

    public static ObjectMap<Class, Object> getImages() {
        return images;
    }

    public static ObjectMap<Class, Object> getModels() {
        return models;
    }

    public static Array<Triple<Class, String, String>> getResources() {
        return resources;
    }

    public void unload() {
    }

    public void update() {
    }

    public void dispose() {
        for (Object object : renderables.values()) {
            if (object instanceof Disposable) {
                ((Disposable) object).dispose();
                Log.info("ResourceRegistry", "Disposed " + object.getClass().getName());
            }
        }
        for (Object object : images.values()) {
            if (object instanceof Disposable) {
                ((Disposable) object).dispose();
                Log.info("ResourceRegistry", "Disposed " + object.getClass().getName());
            }
        }
        for (Object object : models.values()) {
            if (object instanceof Disposable) {
                ((Disposable) object).dispose();
                Log.info("ResourceRegistry", "Disposed " + object.getClass().getName());
            }
        }
    }

    public void addResource(String name, String path) {
    }


}
