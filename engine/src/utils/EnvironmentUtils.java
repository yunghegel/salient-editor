package utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Texture;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

public class EnvironmentUtils {

    public static void setupCamera(Camera camera, SceneManager sceneManager) {
        float d = .02f;
        camera.near = .01f;
        camera.far = 10000;
        sceneManager.setCamera(camera);
        camera.position.set(0,5f, 0);
    }

    public static void setupLight(DirectionalLightEx light,SceneManager sceneManager) {

        light.direction.set(.5f, -.45f, 0);
        light.intensity = 1f;
        light.color.set(1, 1, 1, 1);
        //sceneManager.environment.add(light);
        sceneManager.setAmbientLight(2f);
    }

    public static void setupIBL(SceneManager sceneManager, DirectionalLightEx light, SceneSkybox skybox) {
        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
        Texture brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));
        Cubemap environmentCubemap = iblBuilder.buildEnvMap(1024);
        Cubemap diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        Cubemap specularCubemap = iblBuilder.buildRadianceMap(10);

        sceneManager.setAmbientLight(2f);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

        skybox = new SceneSkybox(environmentCubemap);
        sceneManager.setSkyBox(skybox);
    }


}
