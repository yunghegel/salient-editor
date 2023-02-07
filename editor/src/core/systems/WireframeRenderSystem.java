package core.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.collision.BoundingBox;
import core.components.SceneComponent;
import editor.Context;
import editor.graphics.rendering.Renderer;
import editor.graphics.scene.MeshInfo;
import tests.GeometryTest;
import ui.elements.ComponentInspector;
import ui.widgets.RenderWidget;

public class WireframeRenderSystem extends IteratingSystem
{

    public ShapeRenderer shapeRenderer = new ShapeRenderer();
    public final ComponentMapper<SceneComponent> mapper = ComponentMapper.getFor(SceneComponent.class);
    public boolean drawWireframe = false;
    public boolean drawPoints = true;
    private Context context;
    public PerspectiveCamera camera;

    public WireframeRenderSystem() {
        super(Family.all(core.components.SceneComponent.class).get());
    }

    public void setContext(Context context) {
        this.context = context;
        this.camera = context.camera;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);





    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        shapeRenderer.setAutoShapeType(true);

        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.setColor(1 , 0 , 1 , 1);
        SceneComponent sceneComponent = mapper.get(entity);
//        if (sceneComponent.model != null) {
//            for (MeshInfo meshInfo : sceneComponent.meshInfos) {
//                if (sceneComponent.drawWireframe) {
//                    meshInfo.drawWireframe(shapeRenderer, Color.RED, camera);
//                }
//                if (sceneComponent.drawPoints) {
//                    meshInfo.drawPoints(shapeRenderer, Color.RED, camera);
//                }
//
//            }
//        }

        if (sceneComponent.drawBounds){
            drawBbox(sceneComponent);
        }




    }


    public void render() {

        if (!drawWireframe && !drawPoints) return;

        shapeRenderer.setAutoShapeType(true);

        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.setColor(1 , 0 , 1 , 1);

//            ComponentInspector.getInstance().getSelectedMeshInfo().drawWireframe(shapeRenderer, Color.RED , camera);
//            if (ComponentInspector.getInstance().selectedMesh != null) {
//                MeshInfo info = ComponentInspector.getInstance().selectedMeshInfo;
//
//
//                for (MeshInfo.Triangle tri : info.triangles) {
//                    shapeRenderer.setColor(1, 0, 0, 1);
//
//                    shapeRenderer.line(tri.v1, tri.v2);
//                    shapeRenderer.line(tri.v2, tri.v3);
//                    shapeRenderer.line(tri.v3, tri.v1);
//                }
//            }

            for (Entity entity : getEntities()) {
                SceneComponent sceneComponent = mapper.get(entity);
                MeshInfo info = ComponentInspector.getInstance().selectedMeshInfo;
//                if (info!=null){
//                    info.drawWireframe(shapeRenderer, Color.RED, camera);
//                }

                for (MeshInfo meshInfo : sceneComponent.meshInfos) {
                    if (sceneComponent.drawWireframe) {
                        meshInfo.drawWireframe(shapeRenderer, Color.RED, camera);}
                    if (sceneComponent.drawPoints) {
                        meshInfo.drawPoints(shapeRenderer , Color.GREEN , camera);
                    }

                    }


                }


        if(drawPoints){
            if (ComponentInspector.getInstance().expandedMeshInfos != null) {


            }
           // ComponentInspector.getInstance().getSelectedMeshInfo().drawPoints(shapeRenderer, Color.RED , camera);

//            for (Entity entity : getEntities()) {
//                SceneComponent sceneComponent = mapper.get(entity);
//                for (MeshInfo meshInfo : sceneComponent.meshInfos) {
//                    meshInfo.drawPoints(shapeRenderer , Color.RED , cam);
//                }
//            }
        }
//        shapeRenderer.end();
    }
    public void drawBbox(SceneComponent sceneComponent) {
        BoundingBox boundingBox = sceneComponent.boundingBox;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.box(boundingBox.min.x , boundingBox.min.y , boundingBox.min.z + boundingBox.getDepth() , boundingBox.getWidth() , boundingBox.getHeight() , boundingBox.getDepth());
        shapeRenderer.end();
    }
}
