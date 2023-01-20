package core.components;

import com.badlogic.ashley.core.ComponentMapper;
import core.components.light.DirectionalLightComponent;
import core.components.light.LightComponent;
import core.components.light.PointLightComponent;
import core.components.light.SpotLightComponent;

public class Mappers
{

    public final static ComponentMapper<SceneComponent> scm = ComponentMapper.getFor(SceneComponent.class);

    public final static ComponentMapper<TextureComponent> tcm = ComponentMapper.getFor(TextureComponent.class);

    public final static ComponentMapper<core.components.light.LightComponent> lcm = ComponentMapper.getFor(LightComponent.class);
    public static final ComponentMapper<core.components.light.SpotLightComponent> slcm = ComponentMapper.getFor(SpotLightComponent.class);
    public static final ComponentMapper<core.components.light.PointLightComponent> plcm = ComponentMapper.getFor(PointLightComponent.class);
    public static final ComponentMapper<core.components.light.DirectionalLightComponent> dlcm = ComponentMapper.getFor(DirectionalLightComponent.class);

    public static final ComponentMapper<core.components.BulletComponent> bcm = ComponentMapper.getFor(BulletComponent.class);

}
