package ecs.entities;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g3d.environment.*;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import ecs.components.PickableComponent;
import ecs.components.SceneComponent;
import ecs.components.light.*;
import ecs.systems.LightingSystem;
import sys.Log;
import ui.ObjectTree;

public class LightEntityFactory
{

    private static LightEntityFactory instance;

    public static LightEntityFactory getInstance()
    {
        if (instance == null)
        {
            instance = new LightEntityFactory();
        }
        return instance;
    }

    public static Entity createLightEntity(BaseLight light)
    {
        Entity lightEntity = new Entity();
        PickableComponent pickableComponent = new PickableComponent();
        if (light instanceof DirectionalLight)
        {
            DirectionalLightComponent directionalLightComponent = new DirectionalLightComponent();
            directionalLightComponent.setLight(light);
            directionalLightComponent.direction = ((DirectionalLight) light).direction;
            directionalLightComponent.color = light.color;
            Log.info("LightEntityFactory", "DirectionalLight created...");
            ObjectTree.addLightComponentNode(directionalLightComponent);
            Log.info("LightEntityFactory", "DirectionalLight added to ObjectTree...");

        }
        else if (light instanceof PointLight)
        {
            PointLightComponent pointLightComponent = new PointLightComponent();
            pointLightComponent.setLight(light);
            pointLightComponent.position = ((PointLight) light).position;
            pointLightComponent.color = light.color;
            pointLightComponent.intensity = ((PointLight) light).intensity;
            lightEntity.add(pointLightComponent);
            lightEntity.add(pickableComponent);
            pickableComponent.setPosition(pointLightComponent.position);
            Log.info("LightEntityFactory", "PointLight created...");
            ObjectTree.addLightComponentNode(pointLightComponent);
            Log.info("LightEntityFactory", "PointLight added to ObjectTree...");
        }
        else if (light instanceof SpotLight)
        {
            SpotLightComponent spotLightComponent = new SpotLightComponent();
            spotLightComponent.setLight(light);
            spotLightComponent.position = ((SpotLight) light).position;
            spotLightComponent.color = light.color;
            spotLightComponent.direction = ((SpotLight) light).direction;
            spotLightComponent.intensity = ((SpotLight) light).intensity;
            lightEntity.add(spotLightComponent);
            lightEntity.add(pickableComponent);
            pickableComponent.setPosition(spotLightComponent.position);
            Log.info("LightEntityFactory", "SpotLight created...");
            ObjectTree.addLightComponentNode(spotLightComponent);
            Log.info("LightEntityFactory", "SpotLight added to ObjectTree...");

        }
        else if (light instanceof DirectionalShadowLight)
        {
            ShadowLightComponent shadowLightComponent = new ShadowLightComponent();
            shadowLightComponent.setLight((DirectionalShadowLight) light);
            Log.info("LightEntityFactory", "ShadowLight created...");
            ObjectTree.addLightComponentNode(shadowLightComponent);
            Log.info("LightEntityFactory", "ShadowLight added to ObjectTree...");
        }

        return lightEntity;
    }

    public static void parseSceneAndCreateLightEntities(Array<SceneComponent> sceneComponents, LightingSystem lightsSystem) {
        for (SceneComponent sceneComponent : sceneComponents) {
            for(ObjectMap.Entry<Node, BaseLight> light:sceneComponent.gltfScene.lights){
                Entity lightEntity = createLightEntity(light.value);
                lightsSystem.getEngine().addEntity(lightEntity);
                LightComponent lightComponent = lightEntity.getComponent(LightComponent.class);
                if (lightComponent instanceof DirectionalLightComponent) {
                    ObjectTree.addLightComponentNode((DirectionalLightComponent) lightComponent);
                } else if (lightComponent instanceof PointLightComponent) {
                    ObjectTree.addLightComponentNode((PointLightComponent) lightComponent);
                } else if (lightComponent instanceof SpotLightComponent) {
                    ObjectTree.addLightComponentNode((SpotLightComponent) lightComponent);
                } else if (lightComponent instanceof ShadowLightComponent) {
                    ObjectTree.addLightComponentNode((ShadowLightComponent) lightComponent);
                }

                Log.info("LightEntityFactory", "Light entity of type "+ light.value.getClass().getSimpleName()+" parsed from scene; added to lighting engine");


            }
        }

    }

    public static void parseSceneAndCreateLightEntities(SceneComponent sceneComponent, LightingSystem lightsSystem) {

            for(ObjectMap.Entry<Node, BaseLight> light:sceneComponent.gltfScene.lights) {
                Entity lightEntity = createLightEntity(light.value);
                lightsSystem.getEngine().addEntity(lightEntity);

                Log.info("LightEntityFactory", "Light entity of type " + light.value.getClass().getSimpleName() + " parsed from scene; added to object picking engine");
            }}
    public static LightComponent createLightEntity(BaseLight light, Engine engine) {
        Entity lightEntity = createLightEntity(light);
        LightComponent lightC = lightEntity.getComponent(LightComponent.class);
        engine.addEntity(lightEntity);
        Log.info("LightEntityFactory", "Light entity of type "+ light.getClass().getSimpleName()+" created and added to engine");
        return lightC;
    }
}

