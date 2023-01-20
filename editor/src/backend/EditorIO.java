package backend;

import backend.tools.Log;
import backend.tools.Perf;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.utils.Array;
import net.mgsx.gltf.loaders.gltf.GLTFAssetLoader;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;


import backend.data.ResourceRegistry;



import util.ReflectionHelper;

public class EditorIO
{


    public interface IOListener{

        void added(Class type,Object object,String id,String path);


    }

    IOListener  listener;

    public interface IOReloadListener<T>
    {
        void reload(T asset);
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
     AssetManager manager = new AssetManager();
    static GLTFLoader loader = new GLTFLoader();
    static ObjLoader objLoader;
    static TextureLoader textureLoader;

    private static EditorIO instance;

    public void initDefaultListeners(){
        listener = new IOListener() {
            @Override
            public void added(Class type, Object object,String id,String path) {
                if(type == Model.class){
                    ResourceRegistry.getInstance().load(Model.class, object, id, path);

                    Log.info("EditorIO", "Model loaded into resource ledger");
                }
                if (type == SceneAsset.class) {
                    ResourceRegistry.getInstance().load(SceneAsset.class, object,id,path);
                    Log.info("EditorIO", "Scene loaded into resource ledger");
                }
                if (type == Texture.class) {
                    ResourceRegistry.getInstance().load(Texture.class, object,id,path);
                    Log.info("EditorIO", "Texture loaded into resource ledger");
                    return;
                }
            }
        };
        addIOListener(listener);
    }


    static {


        loader = new GLTFLoader();
        instance = new EditorIO();
        objLoader = new ObjLoader();
        instance.initDefaultListeners();

    }

    public static EditorIO io() {
        return instance;
    }




    public SceneAsset loadGLTF(String path,String id) {
        manager.setLoader(SceneAsset.class, ".gltf", new GLTFAssetLoader());
        int gltf = Perf.start("gltf_"+id);
        //SceneAsset sceneAsset= new GLTFLoader().load(Gdx.files.internal(path));
        manager.load(path, SceneAsset.class);
        manager.finishLoading();
        SceneAsset sceneAsset = manager.get(path, SceneAsset.class);
        //SceneAsset sceneAsset = manager.get("path", SceneAsset.class);

        //ResourceRegistry.getInstance().load(SceneAsset.class, sceneAsset,id,path);
        listener.added(SceneAsset.class,sceneAsset,id,path);
//        try {
//            RegistryWriter.instance.writeToResourceRegistry(SceneAsset.class, path, id);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        Perf.end(gltf);
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
        listener.added(Model.class,model,id,path);
        //ResourceRegistry.getInstance().load(Model.class, model,id,path);
        try {
            ReflectionHelper.invoke(listeners.get(0),IOListener.class.getMethod("added", Class.class, Object.class), Model.class, model);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Method not found");


        }
        return model;
    }

    public Texture loadTexture(String path, String id) {
        int tex = Perf.start("texture_"+id);
        manager.load(path, Texture.class);
        manager.finishLoading();
        Texture texture = manager.get(path, Texture.class);
        //Texture texture = new Texture(Gdx.files.internal(path));
        //ResourceRegistry.getInstance().load(Texture.class, texture,id,path);
        listener.added(Texture.class, texture,id,path);

//        try {
//            RegistryWriter.instance.writeToResourceRegistry(Texture.class, path, id);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        Perf.end(tex);
        return texture;
    }
}
