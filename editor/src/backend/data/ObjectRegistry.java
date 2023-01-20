package backend.data;

import backend.tools.Log;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import core.components.SceneComponent;
import core.components.TextureComponent;
import core.components.light.LightComponent;

public class ObjectRegistry
{

    private static final ObjectRegistry instance = new ObjectRegistry();

    public static Array<SceneComponent> sceneComponents = new Array<SceneComponent>();
    public static Array<LightComponent> lightComponents = new Array<LightComponent>();
    public static Array<TextureComponent> textureComponents = new Array<TextureComponent>();

    private ObjectRegistry() {

    }

    public static ObjectRegistry getInstance() {
        return instance;
    }

    public static Array<SceneComponent> getSceneComponents() {
        return sceneComponents;
    }

    public static void addSceneComponent(SceneComponent sceneComponent) {
        sceneComponents.add(sceneComponent);
        ui.scene.ObjectTree.addSceneComponentNode(sceneComponent);
    }

    public static void addLightComponent(LightComponent lightComponent) {
        lightComponents.add(lightComponent);
        ui.scene.ObjectTree.addLightComponentNode(lightComponent);
    }

    public static void addTextureComponent(TextureComponent textureComponent) {
        textureComponents.add(textureComponent);

    }

    /**
     * Retrieve a scene component from the registry by its id
     *
     * @param id the component to retrieve
     *
     * @returns the SceneComponent with the given id
     */

    public static SceneComponent getSceneComponent(String id) {
        Component component = getComponentById(SceneComponent.class , id);
        if (component != null) {
            return (SceneComponent) component;
        }
        Log.info("ObjectRegistry" , "WARNING: Null pointer, SceneComponent not found");

        return null;
    }

    public static Component getComponentById(Class type , String id) {
        if (type == SceneComponent.class) {
            for (SceneComponent sceneComponent : sceneComponents) {
                if (sceneComponent.id.equals(id)) {
                    return sceneComponent;
                }
            }
        }

        else if (type == TextureComponent.class) {
            for (TextureComponent textureComponent : textureComponents) {
                if (textureComponent.id.equals(id)) {
                    return textureComponent;
                }
            }
        }
        Log.info("ObjectRegistry" , "WARNING: Null pointer, component not found");
        return null;

    }

    /**
     * Retrieve a texture component from the registry by its id.
     *
     * @param id the component to retrieve
     *
     * @returns a TextureComponent object matching the id, or null if not found.
     */

    public static TextureComponent getTextureComponent(String id) {
        Component component = getComponentById(TextureComponent.class , id);
        if (component != null) {
            return (TextureComponent) component;
        }
        Log.info("ObjectRegistry" , "WARNING: Null pointer, TextureComponent not found");
        return null;
    }

}
