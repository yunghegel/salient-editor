package util;

import backend.tools.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.math.Vector3;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;
import editor.project.settings.Settings;

import java.util.logging.Logger;

public class SceneUtils
{

    private final GLTFLoader loader = new GLTFLoader();



    public static void addSceneFromPath(String path , SceneManager sceneManager) {
        SceneAsset sceneAsset = new GLTFLoader().load(Gdx.files.internal(path));
        Scene scene = new Scene(sceneAsset.scene);
        sceneManager.addScene(scene);
    }

    /**
     * Basic camera setup with generic settings.
     *
     * @param camera
     * @param sceneManager
     */

    public static void setupCamera(Camera camera , SceneManager sceneManager) {
        float d = .02f;
        camera.near = d;
        camera.far = 10000;
        sceneManager.setCamera(camera);
        camera.position.set(0 , 10f , -10f);
    }

    public static void setupLight(DirectionalLightEx light , SceneManager sceneManager) {
        light.direction.set(0 , -3 , 0);
        light.intensity = 1f;
        light.color.set(1 , 1 , 1 , 1);
        sceneManager.environment.add(light);
        sceneManager.setAmbientLight(1f);
    }

    public static void setupIBL(SceneManager sceneManager , DirectionalLightEx light , SceneSkybox skybox) {
        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
        Texture brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));
        Cubemap environmentCubemap = iblBuilder.buildEnvMap(1024);
        Cubemap diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        Cubemap specularCubemap = iblBuilder.buildRadianceMap(10);

        sceneManager.setAmbientLight(1f);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture , brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

        skybox = new SceneSkybox(environmentCubemap);
        sceneManager.setSkyBox(skybox);
    }

    public static SceneManager quickSceneManagerSetup() {
        PBRShaderConfig config = new PBRShaderConfig();
        config.numBones = 128;
        config.numDirectionalLights = 5;
        DepthShaderProvider depthShaderProvider = new DepthShaderProvider();
        depthShaderProvider.config.numBones = 128;
        depthShaderProvider.config.numDirectionalLights = 4;
        SceneManager sceneManager = new SceneManager(new PBRShaderProvider(config) , depthShaderProvider);

        Log.info("PBRShader & DepthShader intitialized with the following config parameters: +\n" + "# bones supported: " + config.numBones + ",\n" + "# directional lights supported: " + config.numDirectionalLights + ",\n" + "# point lights supported: " + config.numPointLights + ",\n" + "# spot lights supported: " + config.numSpotLights);

        return sceneManager;
    }

    /**
     * Creates environmnent, camera, camera controller and adds a directional light. Objects should be created in
     * screen constructor or create() method first.
     */
    public static void createBasicEnv(PerspectiveCamera camera , SceneManager sceneManager , DirectionalShadowLight light) {
        Color ambientLightColor = Color.WHITE;
        Vector3 lightDirection = new Vector3(-1f , -0.8f , -0.2f);
        float d = .02f;
        camera.near = .1f;
        camera.far = 5000;
        sceneManager.setCamera(camera);
        camera.position.set(0 , 1f , 0f);
        light.direction.set(1 , -3 , 1).nor();
        light.color.set(ambientLightColor);
        light.intensity = 10f;
        sceneManager.environment.add(light);
        //sceneManager.environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, lightDirection));
        sceneManager.environment.set(new ColorAttribute(ColorAttribute.AmbientLight , ambientLightColor));
        sceneManager.setAmbientLight(.1f);

        Log.info("Camera created with the following attributes: " + "\n" +

                         "near: " + camera.near + ",\n" + "far: " + camera.far + ",\n" + "position: " + camera.position + ",\n" + "direction: " + camera.direction + ",\n" + "up: " + camera.up + ",\n" + "field of view: " + camera.fieldOfView + ",\n" + "viewport width: " + camera.viewportWidth + ",\n" + "viewport height: " + camera.viewportHeight);

        Log.info("Environment created with the following attributes: " + "\n" +

                         "ambient light color: " + ambientLightColor + ",\n" + "ambient light: " + ambientLightColor + ",\n" + "directional light: " + lightDirection + ",\n");

    }

    /**
     * Initializes image based lighting with cubemaps, adds skybox and sets environment.
     */
    public static void applyLighting(SceneManager sceneManager , Cubemap diffuseCubemap , Cubemap environmentCubemap , Cubemap specularCubemap , DirectionalShadowLight shadowLight) {
        Texture brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));
        SceneSkybox skybox = new SceneSkybox(environmentCubemap);

        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture , brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));
        sceneManager.setSkyBox(skybox);

        Log.info("Added shadowlight and applied lighting with PBRTextureAttribute and PBRCubemapAttributes specular and diffuse; skybox created and associated with SceneManager" + "\n");
    }

    /**
     * Creates a cubemap from a named based directory. The directory should contain 6 files named as follows:
     * px.png, nx.png, py.png, ny.png, pz.png, nz.png
     *
     * @param prefix the name of the subdirectory containing the images, defined in {@link Settings.FilePaths}
     *
     * @returns the {@link Cubemap} generated from the images
     */

    public static Cubemap createCubemapXYZFormat(String prefix) {

        if (!prefix.endsWith("/")) prefix += "/";

        String dir = Settings.FilePaths.IMAGES_DIR + Settings.FilePaths.CUBEMAP_DIR + prefix;
        return createCubemapFromPaths(dir + "px.png" , dir + "nx.png" , dir + "py.png" , dir + "ny.png" , dir + "pz.png" , dir + "nz.png");
    }

    private static Cubemap createCubemapFromPaths(String path1 , String path2 , String path3 , String path4 , String path5 , String path6) {
        return new Cubemap(new Pixmap(Gdx.files.internal(path1)) , new Pixmap(Gdx.files.internal(path2)) , new Pixmap(Gdx.files.internal(path3)) , new Pixmap(Gdx.files.internal(path4)) , new Pixmap(Gdx.files.internal(path5)) , new Pixmap(Gdx.files.internal(path6)));
    }

    /**
     * Creates a cubemap from a named based directory. The directory should contain 6 files named as follows:
     * back.png, front.png, left.png, right.png, top.png, bottom.png
     *
     * @param prefix the name of the subdirectory containing the images, defined in {@link Settings.FilePaths}
     *
     * @returns the {@link Cubemap} generated from the images
     */

    public static Cubemap createCubemapDirectionFormat(String prefix) {

        if (!prefix.endsWith("/")) prefix += "/";

        String dir = Settings.FilePaths.IMAGES_DIR + Settings.FilePaths.CUBEMAP_DIR + prefix;
        return createCubemapFromPaths(dir + "right.png" , dir + "left.png" , dir + "up.png" , dir + "down.png" , dir + "front.png" , dir + "back.png");
    }

    public SceneAsset getModel(String path) {
        return loader.load(Gdx.files.internal(path));
    }

    /**
     * Data container class which creates and holds three cubemaps with default configuration for static use.
     * Create an instance of this class and access the cubemaps via the getter methods.
     */
    public static class CubemapFactory
    {

        private Cubemap environmentCubemap;
        private Cubemap diffuseCubemap;
        private Cubemap specularCubemap;

        public CubemapFactory(DirectionalLightEx light , Logger log , boolean printLog) {
            IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
            environmentCubemap = iblBuilder.buildEnvMap(2048);
            diffuseCubemap = iblBuilder.buildIrradianceMap(512);
            specularCubemap = iblBuilder.buildRadianceMap(10);
            iblBuilder.dispose();
            if (printLog) {
                log.info("Cubemaps created for quick Image-based lighting effects; default parameters used:" + "\n" + "Irradiance map of size 256, radiance map with 10 MipMap levels");
            }
        }

        public Cubemap getEnvironmentCubemap() {
            return environmentCubemap;
        }

        public Cubemap getDiffuseCubemap() {
            return diffuseCubemap;
        }

        public Cubemap getSpecularCubemap() {
            return specularCubemap;
        }

    }

}
