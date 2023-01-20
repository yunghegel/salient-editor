package core.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import core.components.light.PointLightComponent;
import core.components.light.SpotLightComponent;
import editor.Context;
import core.components.PickableComponent;
import core.components.SceneComponent;
import core.components.light.LightComponent;
import core.entities.LightEntityFactory;
import editor.graphics.rendering.Renderer;
import backend.tools.Log;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import ui.widgets.RenderWidget;

import java.util.ArrayList;

public class LightsSystem extends IteratingSystem implements Renderer
{
    ShapeRenderer shapeRenderer = new ShapeRenderer();
    public static Array<PointLight> pointLights = new Array<PointLight>();
    public static Array<DirectionalLight> directionalLights = new Array<DirectionalLight>();
    public static Array<DirectionalShadowLight> directionalShadowLights = new Array<DirectionalShadowLight>();
    public static Array<SpotLight> spotLights = new Array<SpotLight>();
    public Array<BoundingBox> pointLightBoundingBoxes = new Array<BoundingBox>();
    public Array<BoundingBox> directionalLightBoundingBoxes = new Array<BoundingBox>();
    public Array<BoundingBox> directionalShadowLightBoundingBoxes = new Array<BoundingBox>();
    public Array<BoundingBox> spotLightBoundingBoxes = new Array<BoundingBox>();
    public ImmutableArray<Entity> entities;
    Decal decal;
    DecalBatch decalBatch;
    Texture texture;
    ArrayList<core.components.light.LightComponent> lights = new ArrayList<>();
    private Context context;
    private PerspectiveCamera cam;
    private SceneManager sceneManager;
    Ray ray;
    Vector3 intersection = new Vector3();
    public LightComponent selection;
    TextureRegion textureRegion;
    public LightsSystem() {
        super(Family.one(core.components.light.PointLightComponent.class , core.components.light.SpotLightComponent.class , core.components.light.DirectionalLightComponent.class , core.components.light.ShadowLightComponent.class , core.components.SceneComponent.class , core.components.PickableComponent.class).get());
        Log.info("LightingSystem" , "LightingSystem added to engine");
        //shapeRenderer.rotate(1,0,0,90);
        texture = new Texture("icons/light_decal.png");

        textureRegion = new TextureRegion(texture);
        decal = Decal.newDecal(textureRegion , true);
        decalBatch = new DecalBatch(new CameraGroupStrategy(Context.getInstance().getCamera()));

        RenderWidget.getInstance().addRenderer(this);

    }

    public static Array<PointLight> getPointLights() {
        return pointLights;
    }

    public void setContext(Context context) {
        this.context = context;
        this.cam = context.getCamera();
        this.sceneManager = context.getSceneManager();
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(Family.one(core.components.light.PointLightComponent.class , core.components.light.SpotLightComponent.class , core.components.light.DirectionalLightComponent.class , core.components.light.ShadowLightComponent.class , core.components.SceneComponent.class).get());
        decalBatch = new DecalBatch(new CameraGroupStrategy(cam));
        Log.info("LightingSystem" , "Light entity added to engine");
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Gdx.gl.glClear(Gdx.gl.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(Gdx.gl.GL_DEPTH_TEST);

        cam.update();



        //for components that are selected, associate a batch with their position
        for (Entity entity : entities) {
            if (entity.getComponent(core.components.light.LightComponent.class) != null) {
                core.components.light.LightComponent lightComponent = entity.getComponent(core.components.light.LightComponent.class);
                if (lightComponent instanceof core.components.light.PointLightComponent) {
                    PointLight light = (PointLight) lightComponent.light;
                    if (lightComponent.selected) {
                        light.color.set(Color.RED);
                        Log.info("LightingSystem" , "Point light selected");
                    }
                    Vector3 pos = light.position;
                    decal.setPosition(pos);
                    decal.lookAt(cam.position , cam.up);
                    decalBatch.add(decal);
                }
                if (lightComponent instanceof core.components.light.SpotLightComponent) {
                    SpotLight light = (SpotLight) lightComponent.light;
                    if (lightComponent.selected) {
                        light.color.set(Color.RED);
                        Log.info("LightingSystem" , "Spot light selected");
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
    protected void processEntity(Entity entity , float deltaTime) {
        Entity e = entity;

            core.components.light.LightComponent lightComponent = e.getComponent(core.components.light.LightComponent.class);
            core.components.SceneComponent sceneComponent = e.getComponent(core.components.SceneComponent.class);
            if (e.getComponent(SceneComponent.class) != null) {
                if (!sceneComponent.parsed) {
                    LightEntityFactory.parseSceneAndCreateLightEntities(sceneComponent , this);
                    sceneComponent.parsed = true;
                }

            }

            if (e.getComponent(core.components.light.PointLightComponent.class) != null) {
                core.components.light.PointLightComponent pointLightComponent = e.getComponent(core.components.light.PointLightComponent.class);
                if (!pointLightComponent.processed) {
                    lights.add(pointLightComponent);
                    pointLights.add(pointLightComponent.light);
                    pointLightComponent.processed = true;
                    createBoundingBox(pointLightComponent);
                    Log.info("LightingSystem" , "PointLight found and stored in array... total: " + pointLights.size);
                }


            }
            if (e.getComponent(core.components.light.SpotLightComponent.class) != null) {
                core.components.light.SpotLightComponent spotLightComponent = e.getComponent(core.components.light.SpotLightComponent.class);
                if (!spotLightComponent.processed) {
                    lights.add(spotLightComponent);
                    spotLights.add(spotLightComponent.light);
                    spotLightComponent.processed = true;
                    createBoundingBox(spotLightComponent);
                    Log.info("LightingSystem" , "SpotLight found and stored in array... total: " + spotLights.size);
                }


            }
            if (e.getComponent(core.components.light.DirectionalLightComponent.class) != null) {
                core.components.light.DirectionalLightComponent directionalLightComponent = e.getComponent(core.components.light.DirectionalLightComponent.class);
                if (directionalLightComponent.processed) {
                    lights.add(directionalLightComponent);
                    directionalLights.add(directionalLightComponent.directionalLight);
                    directionalLightComponent.processed = true;
                    Log.info("LightingSystem" , "DirectionalLight found and stored in array... total: " + directionalLights.size);
                }


            }
            if (e.getComponent(core.components.light.ShadowLightComponent.class) != null) {
                core.components.light.ShadowLightComponent shadowLightComponent = e.getComponent(core.components.light.ShadowLightComponent.class);
                if (shadowLightComponent.processed) {
                    lights.add(shadowLightComponent);
                    directionalShadowLights.add(shadowLightComponent.light);
                    shadowLightComponent.processed = true;
                    Log.info("LightingSystem" , "ShadowLight found and stored in array... total: " + directionalShadowLights.size);
                }
            }
        updateBoundingBoxesToPosition(lightComponent);
        drawBoxesAroundLights(lightComponent);
        ray = RenderWidget.viewport.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        if (lightComponent instanceof PointLightComponent)
        {
            if (Intersector.intersectRayBounds(ray, ( (PointLightComponent) lightComponent ).boundingBox, intersection))
            {

                selection = lightComponent;
                Log.info("ObjectPickingSystem", "PointLightComponent selected: " + selection);


            }
        }

        if (lightComponent instanceof SpotLightComponent)
        {
            if (Intersector.intersectRayBounds(ray, ( (SpotLightComponent) lightComponent ).boundingBox, intersection))
            {
                if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT))
                {
                    selection = lightComponent;
                    Log.info("ObjectPickingSystem","SpotLightComponent selected: "+selection);
                }
            }

        }
        //draw a bounding box around the light when its selected

        core.components.PickableComponent pickableComponent = entity.getComponent(PickableComponent.class);

//        for (PointLight pointLight : pointLights) {
//            Vector3 pos = pointLight.position;
//            BoundingBox boundingBox = new BoundingBox(new Vector3(pos.x - 0.5f , pos.y - 0.5f , pos.z - 0.5f) , new Vector3(pos.x + 0.5f , pos.y + 0.5f , pos.z + 0.5f));
//            ModelInstance boxInstance = ModelUtils.createBoundingBoxRenderable(boundingBox);
//            Scene boxScene = new Scene(boxInstance);
//            sceneManager.addScene(boxScene);
//            decal.setPosition(pos);
//            decal.lookAt(cam.position , cam.up);
//            decalBatch.add(decal);
//
//        }
        decalBatch.flush();
    }

    public void dispose() {
        texture.dispose();
        decalBatch.dispose();
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
    public void drawBoxesAroundLights(LightComponent lightComponent){
        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        if(lightComponent instanceof PointLightComponent) {
            PointLightComponent pointLightComponent = (PointLightComponent)lightComponent;
            BoundingBox boundingBox = pointLightComponent.boundingBox;
            shapeRenderer.box(boundingBox.min.x , boundingBox.min.y , boundingBox.min.z + boundingBox.getDepth() , boundingBox.getWidth() , boundingBox.getHeight() , boundingBox.getDepth());
            shapeRenderer.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1,0,0,0.5f);
            shapeRenderer.cone(pointLightComponent.light.position.x , pointLightComponent.light.position.y,pointLightComponent.light.position.z, 1, 0,10);

        }
        if(lightComponent instanceof SpotLightComponent) {
            SpotLightComponent spotLightComponent = (SpotLightComponent)lightComponent;
            BoundingBox boundingBox = spotLightComponent.boundingBox;
            shapeRenderer.box(boundingBox.min.x , boundingBox.min.y , boundingBox.min.z + boundingBox.getDepth() , boundingBox.getWidth() , boundingBox.getHeight() , boundingBox.getDepth());
        }
        shapeRenderer.end();
    }

    public void createBoundingBox(LightComponent light){
        BoundingBox boundingBox = new BoundingBox();
        Vector3 pos = new Vector3();

        if(light instanceof PointLightComponent){
            PointLight pointLight = (PointLight)light.light;
            pos = ((PointLightComponent) light).light.position;
            boundingBox = new BoundingBox(new Vector3(pos.x - 0.5f , pos.y - 0.5f , pos.z - 0.5f) , new Vector3(pos.x + 0.5f , pos.y + 0.5f , pos.z + 0.5f));
            ((PointLightComponent) light).boundingBox = boundingBox;
            Log.info("LightingSystem" , "Created bounding box for point light at position: " + pos);
        }

        if(light instanceof SpotLightComponent){
            SpotLight spotLight = (SpotLight) light.light;
            pos.set(spotLight.position);
            boundingBox = new BoundingBox(new Vector3(pos.x - 0.5f , pos.y - 0.5f , pos.z - 0.5f) , new Vector3(pos.x + 0.5f , pos.y + 0.5f , pos.z + 0.5f));
            ( (SpotLightComponent) light ).boundingBox = boundingBox;
            Log.info("LightingSystem" , "Created bounding box for spot light at position: "+pos);

        }



    }

    public void updateBoundingBoxesToPosition(LightComponent light){
        BoundingBox boundingBox = new BoundingBox();
        Vector3 center = new Vector3();

        if(light instanceof PointLightComponent){
            PointLightComponent pointLightComponent = (PointLightComponent)light;
            PointLight pointLight = (PointLight)light.light;
            pointLightComponent.boundingBox.getCenter(center);
            if(pointLightComponent.light.position != center){
                pointLightComponent.boundingBox.set(new Vector3(pointLightComponent.light.position.x - 0.5f , pointLightComponent.light.position.y - 0.5f , pointLightComponent.light.position.z - 0.5f) , new Vector3(pointLightComponent.light.position.x + 0.5f , pointLightComponent.light.position.y + 0.5f , pointLightComponent.light.position.z + 0.5f));
            }

            }
        if (light instanceof SpotLightComponent){
            SpotLightComponent spotLightComponent = (SpotLightComponent)light;
            SpotLight spotLight = (SpotLight)light.light;
            spotLightComponent.boundingBox.getCenter(center);
            if(spotLightComponent.position != spotLight.position){
                spotLightComponent.boundingBox.set(new Vector3(spotLightComponent.light.position.x - 0.5f , spotLightComponent.light.position.y - 0.5f , spotLightComponent.light.position.z - 0.5f) , new Vector3(spotLightComponent.light.position.x + 0.5f , spotLightComponent.light.position.y + 0.5f , spotLightComponent.light.position.z + 0.5f));
            }
        }




    }

    @Override
    public void render(Camera cam) {
        for (LightComponent light : lights) {
            updateBoundingBoxesToPosition(light);
            //drawBoxesAroundLights(light);
            Decal decal = Decal.newDecal(1, 1, textureRegion, true);
            double dst =0;
            Vector3 dir = new Vector3();
            Vector3 pos = new Vector3();



            if (light instanceof PointLightComponent) {
                PointLightComponent pointLightComponent = (PointLightComponent)light;
                PointLight pointLight = (PointLight)light.light;
                decal.setPosition(pointLightComponent.light.position);
                decal.lookAt(cam.position , cam.up);
                decalBatch.add(decal);
                dst = cam.position.dst(pointLightComponent.light.position);
            }
            if (light instanceof SpotLightComponent) {
                SpotLightComponent spotLightComponent = (SpotLightComponent)light;
                SpotLight spotLight = (SpotLight)light.light;
                decal.setPosition(spotLightComponent.light.position);
                //decal.lookAt(cam.position , cam.up);
                pos.set(decal.getPosition());
                dir.set(cam.position).sub(pos).nor();
                dir.y = 0;
                decal.setRotation(dir,cam.up);


                decalBatch.add(decal);
                dst = cam.position.dst(spotLightComponent.light.position);
            }
            dst=Math.sqrt(dst);
            decal.setScale((float)dst/2);



        }
        decalBatch.flush();
    }

}
