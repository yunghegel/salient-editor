package core.entities;

import backend.EditorIO;
import backend.tools.Log;
import backend.data.ObjectRegistry;
import backend.tools.Perf;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import core.components.TextureComponent;

public class TwoDimensionalEntityFactory
{

    public static Entity createTextureEntity(String path , String id) {
        Entity entity = new Entity();
        TextureComponent textureComponent = createTextureComponent(path , id);
        return attachTextureToEntity(entity , textureComponent);

    }

    public static TextureComponent createTextureComponent(String path , String id) {
        int texComp = Perf.start("texture_component_"+id);
        Texture texture = EditorIO.io().loadTexture(path,id);
        TextureComponent textureComponent = new TextureComponent(path , id);
        ObjectRegistry.addTextureComponent(textureComponent);
        Log.info("TextureComponent created from path: " + path + " and stored id: " + id);
        Perf.end(texComp);
        return textureComponent;
    }

    public static Entity attachTextureToEntity(Entity entity , TextureComponent textureComponent) {
        entity.add(textureComponent);
        return new Entity();
    }

    public static Entity createTextureEntity(Texture texture , String id , String path) {
        Entity entity = new Entity();
        TextureComponent textureComponent = createTextureComponent(texture , id , path);
        return attachTextureToEntity(entity , textureComponent);
    }

    public static TextureComponent createTextureComponent(Texture texture , String id , String path) {
        int texComp = Perf.start("texture_component_"+id);
        TextureComponent textureComponent = new TextureComponent(texture,id,path);

        ObjectRegistry.addTextureComponent(textureComponent);
        Log.info("TextureComponent created from Texture object, storing path: " + path + " and id: " + id);
        Perf.end(texComp);
        return textureComponent;
    }

}
