package utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.math.Vector3;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;
import sys.Log;

import java.util.logging.Logger;

public class SetupUtils
{

	public static class EnvironmentFactory
	{

		/**
		 * Returns a scene manager which supports 128 bones via custom config.
		 *
		 * @return SceneManager
		 */

		public static SceneManager quickSceneManagerSetup(boolean printLog)
		{
			PBRShaderConfig config = new PBRShaderConfig();
			config.numBones             = 128;
			config.numDirectionalLights = 8;
			config.numPointLights	   = 8;
			config.numSpotLights	   = 8;
			DepthShaderProvider depthShaderProvider = new DepthShaderProvider();
			depthShaderProvider.config.numBones             = 128;
			depthShaderProvider.config.numDirectionalLights = 8;
			depthShaderProvider.config.numPointLights	   = 8;
			depthShaderProvider.config.numSpotLights	   = 8;

			SceneManager sceneManager = new SceneManager(new PBRShaderProvider(config), depthShaderProvider);
			if (printLog)
			{
				Log.info("PBRShader & DepthShader intitialized with the following config parameters: +\n" +
						         "# bones supported: " + config.numBones + ",\n" +
						         "# directional lights supported: " + config.numDirectionalLights + ",\n" +
						         "# point lights supported: " + config.numPointLights + ",\n" +
						         "# spot lights supported: " + config.numSpotLights);
			}
			return sceneManager;
		}

		/**
		 * Creates environmnent, camera, camera controller and adds a directional light. Objects should be created in
		 * screen constructor or create() method first.
		 */
		public static void createEnv(PerspectiveCamera camera, SceneManager sceneManager, DirectionalShadowLight light, Logger log, boolean printLog) {

			Vector3 lightDirection    = new Vector3(- 1f, - 0.8f, - 0.2f);
			float   d                 = .02f;
			camera.near = .1f;
			camera.far  = 5000;
			sceneManager.setCamera(camera);
			camera.position.set(0, 10f, 0f);
			light.direction.set(1, - 3, 1).nor();

			light.intensity=10f;
			sceneManager.environment.add(light);
			//sceneManager.environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, lightDirection));
			sceneManager.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, Color.WHITE));
			sceneManager.setAmbientLight(.1f);
			if (printLog) {
				log.info("Camera created with the following attributes: " + "\n" +

						         "near: " + camera.near + ",\n" +
						         "far: " + camera.far + ",\n" +
						         "position: " + camera.position + ",\n" +
						         "direction: " + camera.direction + ",\n" +
						         "up: " + camera.up + ",\n" +
						         "field of view: " + camera.fieldOfView + ",\n" +
						         "viewport width: " + camera.viewportWidth + ",\n" +
						         "viewport height: " + camera.viewportHeight);

				log.info("Environment created with the following attributes: " + "\n" +


						         "directional light: " + lightDirection + ",\n");
			}
		}

		/**
		 * Initializes image based lighting with cubemaps, adds skybox and sets environment.
		 */
		public static void applyLighting(SceneManager sceneManager, Cubemap diffuseCubemap, Cubemap environmentCubemap, Cubemap specularCubemap, DirectionalShadowLight shadowLight, Logger log, boolean printLog) {
			Texture     brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));
			SceneSkybox skybox  = new SceneSkybox(environmentCubemap);


			sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
			sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
			sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));
			sceneManager.setSkyBox(skybox);
			if (printLog) {
				log.info("Added shadowlight and applied lighting with PBRTextureAttribute and PBRCubemapAttributes specular and diffuse; skybox created and associated with SceneManager" + "\n");
			}
		}
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

		public CubemapFactory(DirectionalLightEx light, Logger log, boolean printLog) {
			IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
			environmentCubemap = iblBuilder.buildEnvMap(2048);
			diffuseCubemap     = iblBuilder.buildIrradianceMap(512);
			specularCubemap    = iblBuilder.buildRadianceMap(10);
			iblBuilder.dispose();
			if (printLog) {
				log.info("Cubemaps created for quick Image-based lighting effects; default parameters used:" + "\n" +
						         "Irradiance map of size 256, radiance map with 10 MipMap levels");
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
