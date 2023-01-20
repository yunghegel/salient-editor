package backend.data;

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

public class ResourceRegistry
{



    private static ResourceRegistry instance = new ResourceRegistry();

     public static ObjectMap<Class, Object> renderables = new ObjectMap<Class, Object>();
     public static ObjectMap<Class, Object> images = new ObjectMap<Class, Object>();
     public static ObjectMap<Class, Object> models = new ObjectMap<Class, Object>();
    public static Triple<Class,String,String> resource = new Triple<Class, String, String>(null,null,null);
    public static Array<Triple<Class,String,String>> resources = new Array<Triple<Class, String, String>>();
    public static JsonArray jsonArray = new JsonArray();
    public static Array<SceneComponent> sceneComponents = new Array<SceneComponent>();

    public static ResourceRegistry getInstance() {
        return instance;
    }

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


    public void load(Class clazz,Object object, String id, String path) {
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




        }


public static JsonArray getResourcesAsJson(){
        JsonArray array = new JsonArray();
        JsonArray images = new JsonArray();
        JsonArray models = new JsonArray();
        images.add("images");
        models.add("models");
        array.add(images);
        array.add(models);


        for (Triple<Class,String,String> resource : resources){
            Class clazz = resource.getFirst();
            String id = resource.getSecond();
            String path = resource.getThird();

            JsonObject object = new JsonObject();
            object.addProperty("class",clazz.getName());
            object.addProperty("id",id);
            object.addProperty("path",path);
            if (clazz == Texture.class || clazz == TextureRegion.class || clazz == Decal.class){
                images.add(object);
            }
            if (clazz == Model.class || clazz == ModelInstance.class || clazz == SceneAsset.class){
                models.add(object);
            }
        }
//    for (Triple<Class,String,String> resource : resources) {
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("class", resource.getFirst().getSimpleName());
//        jsonObject.addProperty("name", resource.getSecond());
//        jsonObject.addProperty("path", resource.getThird());
//        array.add(jsonObject);}
    return array;
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
