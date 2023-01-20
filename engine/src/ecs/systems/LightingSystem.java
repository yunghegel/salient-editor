package ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

import ecs.World;
import ecs.components.PickableComponent;
import ecs.components.SceneComponent;
import ecs.components.light.*;
import ecs.entities.LightEntityFactory;
import net.mgsx.gltf.scene3d.scene.Scene;

import sys.Log;
import utils.ModelUtils;

import java.util.ArrayList;

public class LightingSystem extends IteratingSystem {

    Decal decal;
    DecalBatch decalBatch;
    Texture texture;

    World world;

    ArrayList<LightComponent> lights = new ArrayList<>();
    public ImmutableArray<Entity> entities;
    public static Array<PointLight> pointLights= new Array<PointLight>();
    public static Array<DirectionalLight> directionalLights= new Array<DirectionalLight>();
    public static Array<DirectionalShadowLight> directionalShadowLights= new Array<DirectionalShadowLight>();
    public static Array<SpotLight> spotLights= new Array<SpotLight>();


    public LightingSystem() {
        super(Family.one(PointLightComponent.class, SpotLightComponent.class, DirectionalLightComponent.class, ShadowLightComponent.class, SceneComponent.class, PickableComponent.class).get());
        Log.info("LightingSystem", "LightingSystem added to engine");

        texture = new Texture("icons/light_decal.png");
        TextureRegion textureRegion = new TextureRegion(texture);
        decal = Decal.newDecal(textureRegion, true);


    }

    public void setWorld(World world) {
        this.world = world;
    }



    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities=engine.getEntitiesFor(Family.one(PointLightComponent.class, SpotLightComponent.class, DirectionalLightComponent.class, ShadowLightComponent.class, SceneComponent.class).get());
        decalBatch=new DecalBatch(new CameraGroupStrategy(world.getCamera()));
        Log.info("LightingSystem", "Light entity added to engine");
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Gdx.gl.glClear(Gdx.gl.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(Gdx.gl.GL_DEPTH_TEST);

        world.getCamera().update();

        //for components that are selected, associate a batch with their position
        for (Entity entity : entities) {
            if (entity.getComponent(LightComponent.class) != null) {
                LightComponent lightComponent = entity.getComponent(LightComponent.class);
                if (lightComponent instanceof PointLightComponent) {
                    PointLight light  = (PointLight)lightComponent.light;
                    if (lightComponent.selected) {
                        light.color.set(Color.RED);
                        Log.info("LightingSystem", "Point light selected");
                    }
                    Vector3 pos = light.position;
                    decal.setPosition(pos);
                    decal.lookAt(world.getCamera().position, world.getCamera().up);
                    decalBatch.add(decal);
                }
                if (lightComponent instanceof SpotLightComponent) {
                    SpotLight light  = (SpotLight)lightComponent.light;
                    if (lightComponent.selected) {
                        light.color.set(Color.RED);
                        Log.info("LightingSystem", "Spot light selected");
                    }
                    Vector3 pos = light.position;

                    decalBatch.add(decal);
                    decal.setPosition(pos);

                }
            }
        }

        decalBatch.flush();


    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        for (Entity e : entities) {
            LightComponent lightComponent = e.getComponent(LightComponent.class);
            SceneComponent sceneComponent = e.getComponent(SceneComponent.class);
            if (e.getComponent(SceneComponent.class)!=null){
                if (!sceneComponent.parsed){
                    LightEntityFactory.parseSceneAndCreateLightEntities(sceneComponent,this);
                    sceneComponent.parsed=true;
                }

            }

            if(e.getComponent(PointLightComponent.class)!=null){
                PointLightComponent pointLightComponent=e.getComponent(PointLightComponent.class);
                if (pointLightComponent.processed) {
                        return;                }
                lights.add(pointLightComponent);
                pointLights.add(pointLightComponent.light);
                pointLightComponent.processed=true;
                Log.info("LightingSystem", "PointLight found and stored in array... total: "+pointLights.size);


            }
            if(e.getComponent(SpotLightComponent.class)!=null){
                SpotLightComponent spotLightComponent=e.getComponent(SpotLightComponent.class);
                if (spotLightComponent.processed){
                        return;}
                lights.add(spotLightComponent);
                spotLights.add(spotLightComponent.spotLight);
                spotLightComponent.processed=true;
                Log.info("LightingSystem", "PointLight found and stored in array... total: "+spotLights.size);


            }
            if(e.getComponent(DirectionalLightComponent.class)!=null){
                DirectionalLightComponent directionalLightComponent=e.getComponent(DirectionalLightComponent.class);
                if (directionalLightComponent.processed) {
                    return; }
                lights.add(directionalLightComponent);
                directionalLights.add(directionalLightComponent.directionalLight);
                directionalLightComponent.processed=true;
                Log.info("LightingSystem", "DirectionalLight found and stored in array... total: "+directionalLights.size);

            }
            if(e.getComponent(ShadowLightComponent.class)!=null){
                ShadowLightComponent shadowLightComponent=e.getComponent(ShadowLightComponent.class);
                if (shadowLightComponent.processed) {
                    return; }
                lights.add(shadowLightComponent);
                directionalShadowLights.add(shadowLightComponent.light);
                shadowLightComponent.processed=true;
                Log.info("LightingSystem", "ShadowLight found and stored in array... total: "+directionalShadowLights.size);

            }
        }

        //draw a bounding box around the light when its selected

        LightComponent lightComponent = entity.getComponent(LightComponent.class);
        PickableComponent pickableComponent = entity.getComponent(PickableComponent.class);

        for (PointLight pointLight : pointLights) {
            Vector3 pos = pointLight.position;
            BoundingBox boundingBox = new BoundingBox(new Vector3(pos.x - 0.5f, pos.y - 0.5f, pos.z - 0.5f), new Vector3(pos.x + 0.5f, pos.y + 0.5f, pos.z + 0.5f));
            ModelInstance boxInstance = ModelUtils.createBoundingBoxRenderable(boundingBox);
            Scene boxScene = new Scene(boxInstance);
            world.getSceneManager().addScene(boxScene);
            decal.setPosition(pos);
            decal.lookAt(world.getCamera().position, world.getCamera().up);
            decalBatch.add(decal);


        }
        decalBatch.flush();
    }

    public static Array<PointLight> getPointLights() {
        return pointLights;
    }

    public Array<DirectionalLight> getDirectionalLights() {
        return directionalLights;
    }

    public Array<DirectionalShadowLight> getDirectionalShadowLights() {
        return directionalShadowLights;
    }

    public Array<SpotLight> getSpotLights() {
        return spotLights;
    }
}
