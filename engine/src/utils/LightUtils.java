package utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.*;

public class LightUtils {
    public static BaseLight createLight(Class lightType, Color color)
    {
        BaseLight light = null;
        if (lightType == DirectionalShadowLight.class)
        {
            light = new DirectionalShadowLight(2048, 2048, 30f, 30f, 1f, 300f);
        }
        else if (lightType == DirectionalLight.class)
        {
            light = new DirectionalLight();
        }
        else if (lightType == PointLight.class)
        {
            light = new PointLight();
        }
        else if (lightType == SpotLight.class)
        {
            light = new SpotLight();
        }
        light.color.set(color);


        return light;
    }

    public static BaseLight createLight(Class lightType)
    {
        BaseLight light = null;
        if (lightType == DirectionalShadowLight.class)
        {

            light = new DirectionalShadowLight(2048, 2048, 30f, 30f, 1f, 300f);
            ((DirectionalShadowLight) light).set(0.5f, 0.5f, 0.5f, -1f, -1f, -1f);
        }
        else if (lightType == DirectionalLight.class)
        {
            light = new DirectionalLight();
            ((DirectionalLight) light).direction.set(1, -1, -1);
        }
        else if (lightType == PointLight.class)
        {
            light = new PointLight();
            ((PointLight) light).position.set(0, 5, 0);
            ((PointLight) light).intensity = 20;
        }
        else if (lightType == SpotLight.class)
        {
            light = new SpotLight();
            ((SpotLight) light).position.set(0, 5, 0);
            ((SpotLight) light).direction.set(0, -1, 0);
            ((SpotLight) light).intensity = 20;
        }
        light.color.set(Color.WHITE);
        return light;
    }
}
