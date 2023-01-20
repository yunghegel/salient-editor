package core.entities;

import backend.data.ObjectRegistry;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g3d.environment.*;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import core.components.PickableComponent;
import core.components.SceneComponent;
import core.components.light.SpotLightComponent;
import core.systems.LightsSystem;
import backend.tools.Log;

public class LightEntityFactory
{

    private static LightEntityFactory instance;

    public static LightEntityFactory getInstance() {
        if (instance == null) {
            instance = new LightEntityFactory();
        }
        return instance;
    }

    public static void parseSceneAndCreateLightEntities(Array<SceneComponent> sceneComponents , LightsSystem lightsSystem) {
        for (SceneComponent sceneComponent : sceneComponents) {
            for (ObjectMap.Entry<Node, BaseLight> light : sceneComponent.scene.lights) {
                Entity lightEntity = createLightEntity(light.value);
                lightsSystem.getEngine().addEntity(lightEntity);
                core.components.light.LightComponent lightComponent = lightEntity.getComponent(core.components.light.LightComponent.class);
                if (lightComponent instanceof core.components.light.DirectionalLightComponent) {

                    ObjectRegistry.addLightComponent((core.components.light.DirectionalLightComponent) lightComponent);
                }
                else if (lightComponent instanceof core.components.light.PointLightComponent) {

                    ObjectRegistry.addLightComponent((core.components.light.PointLightComponent) lightComponent);
                }
                else if (lightComponent instanceof core.components.light.SpotLightComponent) {

                    ObjectRegistry.addLightComponent((SpotLightComponent) lightComponent);
                }
                else if (lightComponent instanceof core.components.light.ShadowLightComponent) {

                    ObjectRegistry.addLightComponent((core.components.light.ShadowLightComponent) lightComponent);
                }

                Log.info("LightEntityFactory" , "Light entity of type " + light.value.getClass().getSimpleName() + " parsed from scene; added to lighting engine");

            }
        }

    }

    public static Entity createLightEntity(BaseLight light) {
        Entity lightEntity = new Entity();
        PickableComponent pickableComponent = new PickableComponent();
        if (light instanceof DirectionalLight) {
            core.components.light.DirectionalLightComponent directionalLightComponent = new core.components.light.DirectionalLightComponent();
            directionalLightComponent.setLight(light);
            directionalLightComponent.direction = ( (DirectionalLight) light ).direction;
            directionalLightComponent.color = light.color;
            Log.info("LightEntityFactory" , "DirectionalLight created...");

            ObjectRegistry.addLightComponent(directionalLightComponent);
            Log.info("LightEntityFactory" , "DirectionalLight added to ObjectTree...");

        }
        else if (light instanceof PointLight) {
            core.components.light.PointLightComponent pointLightComponent = new core.components.light.PointLightComponent();
            pointLightComponent.setLight(light);
            pointLightComponent.position = ( (PointLight) light ).position;
            pointLightComponent.color = light.color;
            pointLightComponent.intensity = ( (PointLight) light ).intensity;
            lightEntity.add(pointLightComponent);
            lightEntity.add(pickableComponent);
            pickableComponent.setPosition(pointLightComponent.position);
            Log.info("LightEntityFactory" , "PointLight created...");

            ObjectRegistry.addLightComponent(pointLightComponent);
            Log.info("LightEntityFactory" , "PointLight added to ObjectTree...");
        }
        else if (light instanceof SpotLight) {
            core.components.light.SpotLightComponent spotLightComponent = new core.components.light.SpotLightComponent();
            spotLightComponent.setLight(light);
            spotLightComponent.position = ( (SpotLight) light ).position;
            spotLightComponent.color = light.color;
            spotLightComponent.direction = ( (SpotLight) light ).direction;
            spotLightComponent.intensity = ( (SpotLight) light ).intensity;
            lightEntity.add(spotLightComponent);
            lightEntity.add(pickableComponent);
            pickableComponent.setPosition(spotLightComponent.position);
            Log.info("LightEntityFactory" , "SpotLight created...");

            ObjectRegistry.addLightComponent(spotLightComponent);
            Log.info("LightEntityFactory" , "SpotLight added to ObjectTree...");

        }
        else if (light instanceof DirectionalShadowLight) {
            core.components.light.ShadowLightComponent shadowLightComponent = new core.components.light.ShadowLightComponent();
            shadowLightComponent.setLight((DirectionalShadowLight) light);
            Log.info("LightEntityFactory" , "ShadowLight created...");

            ObjectRegistry.addLightComponent(shadowLightComponent);
            Log.info("LightEntityFactory" , "ShadowLight added to ObjectTree...");
        }

        return lightEntity;
    }

    public static void parseSceneAndCreateLightEntities(SceneComponent sceneComponent , LightsSystem lightsSystem) {

        for (ObjectMap.Entry<Node, BaseLight> light : sceneComponent.scene.lights) {
            Entity lightEntity = createLightEntity(light.value);
            lightsSystem.getEngine().addEntity(lightEntity);

            Log.info("LightEntityFactory" , "Light entity of type " + light.value.getClass().getSimpleName() + " parsed from scene; added to object picking engine");
        }
    }

    public static core.components.light.LightComponent createLightEntity(BaseLight light , Engine engine) {
        Entity lightEntity = createLightEntity(light);
        core.components.light.LightComponent lightC = lightEntity.getComponent(core.components.light.LightComponent.class);
        engine.addEntity(lightEntity);
        Log.info("LightEntityFactory" , "Light entity of type " + light.getClass().getSimpleName() + " created and added to engine");
        return lightC;
    }

}

