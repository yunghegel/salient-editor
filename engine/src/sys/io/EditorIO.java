package sys.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.utils.Array;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import sys.Log;
import utils.reflection.ReflectionHelper;

import java.io.IOException;

public class EditorIO {


    public static interface IOListener{

        void added(Class type,Object object,String id,String path);


    }

    IOListener  listener;

    public static interface IOReloadListener<T>
    {
        public void reload(T asset);
    }

    IOReloadListener reloadListener;

    private Array<IOListener> listeners = new Array<IOListener>();
    private Array<IOReloadListener> reloadListeners = new Array<IOReloadListener>();

    public void addIOListener(IOListener listener){
        listeners.add(listener);
    }
    public void removeIOListener(IOListener listener){
        listeners.removeValue(listener, true);
    }

    public <T> void addReloadIOListener(IOReloadListener<T> listener){
        reloadListeners.add(listener);
    }
    public void removeReloadIOListener(IOReloadListener listener){
        reloadListeners.removeValue(listener, true);
    }



    FileHandle file;
    static AssetManager manager;
    static GLTFLoader loader = new GLTFLoader();
    static ObjLoader objLoader;
    static TextureLoader textureLoader;

    private static EditorIO instance;

    public void initDefaultListeners(){
        listener = new IOListener() {
            @Override
            public void added(Class type, Object object,String id,String path) {
                if(type == Model.class){
                    ResourceRegistry.instance.load(Model.class, object,id,path);

                    Log.info("EditorIO", "Model loaded into resource ledger");
                }
                if (type == SceneAsset.class) {
                    ResourceRegistry.instance.load(SceneAsset.class, object,id,path);
                    Log.info("EditorIO", "Scene loaded into resource ledger");
                }
                if (type == Texture.class) {
                    ResourceRegistry.instance.load(Texture.class, object,id,path);
                    Log.info("EditorIO", "Texture loaded into resource ledger");
                    return;
                }
            }
        };
        addIOListener(listener);
    }


    static {
        manager = new AssetManager();
        loader = new GLTFLoader();
        instance = new EditorIO();
        objLoader = new ObjLoader();
        instance.initDefaultListeners();

    }

    public static EditorIO io() {
        return instance;
    }




    public SceneAsset loadGLTF(String path,String id) {
        SceneAsset sceneAsset= new GLTFLoader().load(Gdx.files.internal(path));
        ResourceRegistry.load(SceneAsset.class, sceneAsset,id,path);
        try {
            RegistryWriter.writeToResourceRegistry(SceneAsset.class, path,id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /*try {
            ReflectionHelper.invoke(listeners.get(0),IOListener.class.getMethod("added", Class.class, Object.class), SceneAsset.class, sceneAsset);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Method not found");
        }*/

        return sceneAsset;
    }

    public Model loadOBJ(FileHandle file) {
        Model model = objLoader.loadModel(file);
        try {
            ReflectionHelper.invoke(listeners.get(0),IOListener.class.getMethod("added", Class.class, Object.class), Model.class, model);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Method not found");


        }
       return model;
    }

    public Model loadOBJ(String path,String id) {
        Model model = objLoader.loadModel(Gdx.files.internal(path));
        ResourceRegistry.load(Model.class, model,id,path);
        try {
            ReflectionHelper.invoke(listeners.get(0),IOListener.class.getMethod("added", Class.class, Object.class), Model.class, model);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Method not found");


        }
        return model;
    }

    public Texture loadTexture(String path, String id) {
        manager.load(path, Texture.class);
        Texture texture = new Texture(Gdx.files.internal(path));
        ResourceRegistry.load(Texture.class, texture,id,path);

            listener.added(Texture.class, texture,id,path);
           // ReflectionHelper.invoke(listeners.get(0),IOListener.class.getMethod("added", Class.class, Object.class), Texture.class, texture);


        return texture;
    }
}
