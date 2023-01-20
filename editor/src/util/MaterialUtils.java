package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.MathUtils;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import backend.tools.Log;

public class MaterialUtils
{

    public static Material loadTextureIntoMaterial(String filepath , float scale) {
        Texture texture = new Texture(Gdx.files.internal(filepath));
        //Set uv scaling
        texture.setWrap(Texture.TextureWrap.Repeat , Texture.TextureWrap.Repeat);
        PBRTextureAttribute pbrTextureAttribute = new PBRTextureAttribute(PBRTextureAttribute.BaseColorTexture , texture);
        pbrTextureAttribute.scaleU = scale;
        pbrTextureAttribute.scaleV = scale;

        return new Material(pbrTextureAttribute);
    }

    public static Material loadTextureAtlasIntoMaterial(TextureAtlas atlas , String name) {
        Texture texture = atlas.findRegion(name).getTexture();
        //Set uv scaling
        texture.setWrap(Texture.TextureWrap.Repeat , Texture.TextureWrap.Repeat);
        PBRTextureAttribute pbrTextureAttribute = new PBRTextureAttribute(PBRTextureAttribute.BaseColorTexture , texture);
        pbrTextureAttribute.scaleU = 10f;
        pbrTextureAttribute.scaleV = 10f;

        return new Material(pbrTextureAttribute);
    }

    public static void replaceTexture(ModelInstance modelInstance , String filepath) {
        Texture texture = new Texture(Gdx.files.internal(filepath));
        //Set uv scaling
        texture.setWrap(Texture.TextureWrap.Repeat , Texture.TextureWrap.Repeat);
        PBRTextureAttribute pbrTextureAttribute = new PBRTextureAttribute(PBRTextureAttribute.BaseColorTexture , texture);
        pbrTextureAttribute.scaleU = 40f;
        pbrTextureAttribute.scaleV = 40f;
        Material material = new Material(pbrTextureAttribute);
        modelInstance.materials.set(0 , material);
    }

    public static void replaceTexture(Color color , ModelInstance modelInstance) {
        Material material = new Material(ColorAttribute.createDiffuse(color));
        modelInstance.materials.set(0 , material);
    }

    public static Material createMaterialFromFile(String path) {
        Texture texture = new Texture(Gdx.files.internal(path) , true);
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear , Texture.TextureFilter.MipMapLinearLinear);
        texture.setWrap(Texture.TextureWrap.Repeat , Texture.TextureWrap.Repeat);

        PBRTextureAttribute textureAttribute = PBRTextureAttribute.createBaseColorTexture(texture);
        textureAttribute.scaleU = 80f;
        textureAttribute.scaleV = 80f;

        Material material = new Material();
        material.set(textureAttribute);
        return material;
    }

    public static Material getMaterialWithRandomColor() {
        int r = MathUtils.random(0 , 255);
        int g = MathUtils.random(0 , 255);
        int b = MathUtils.random(0 , 255);
        //return a random color
        return new Material(ColorAttribute.createDiffuse(new Color(r , g , b , 1f)));
    }

    public static Material getRandomTextureFromAtlas(TextureAtlas textureAtlas , float scale) {
        TextureRegion textureRegion = textureAtlas.getRegions().random();
        Texture texture = textureRegion.getTexture();
        Log.info("texture region: " + textureRegion.getTexture().toString());
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear , Texture.TextureFilter.MipMapLinearLinear);
        texture.setWrap(Texture.TextureWrap.Repeat , Texture.TextureWrap.Repeat);

        PBRTextureAttribute textureAttribute = PBRTextureAttribute.createBaseColorTexture(texture);
        textureAttribute.scaleU = scale;
        textureAttribute.scaleV = scale;
        return new Material(textureAttribute);
    }

    public static Material addAlphaAttribute(Material material , float alpha) {
        material.set(new BlendingAttribute(GL20.GL_SRC_ALPHA , GL20.GL_ONE_MINUS_SRC_ALPHA));
        material.set(new BlendingAttribute(alpha));
        return material;

    }

    public static Material addBaseColorAttribute(Material material , Color color) {
        material.set(PBRColorAttribute.createBaseColorFactor(color));
        return material;
    }

}
