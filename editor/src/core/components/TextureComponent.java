package core.components;

import backend.data.ObjectRegistry;
import backend.data.ComponentRegistry;
import backend.annotations.Storable;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

@Storable(value = "TextureComponent", auto = true)

public class TextureComponent implements Component
{

    public final static ComponentMapper<TextureComponent> tm = ComponentMapper.getFor(TextureComponent.class);

    public String path;
    public String id;
    public Texture texture;
    public TextureRegion textureRegion;
    public TextureRegionDrawable textureRegionDrawable;
    public boolean componentRegistered;
    public boolean objectRegistered;

    /**
     * Creates a TextureComponent from a path and an ID
     */

    public TextureComponent(String path , String id) {
        ComponentRegistry.register(this);
        componentRegistered = true;
        this.path = path;
        this.id = id;
        loadTexture();
        initTexture();

    }



    private void loadTexture() {
        texture = new Texture(path);
    }

    private void initTexture() {

        textureRegion = new TextureRegion(texture);
        textureRegionDrawable = new TextureRegionDrawable(textureRegion);
    }

    private void addToObjectRegistry() {
        ObjectRegistry.addTextureComponent(this);
    }

    /**
     * Loads an already existing Texture into a TextureComponent
     */

    public TextureComponent(Texture texture , String id , String path) {

        ComponentRegistry.register(this);
        componentRegistered = true;
        this.path = path;
        this.id = id;
        this.texture = texture;
        initTexture();


    }

    /**
     * @return the texture associated with this TextureComponent
     */
    public Texture getTexture() {
        return texture;
    }

}
