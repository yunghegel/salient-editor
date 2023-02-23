package core.systems;

import backend.tools.Log;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import core.components.SceneComponent;
import editor.Context;
import editor.graphics.scene.MeshInfo;
import editor.tools.MousePickingTool;

public class MeshManipulationSystem extends IteratingSystem {

    ComponentMapper<SceneComponent> sc = ComponentMapper.getFor(core.components.SceneComponent.class);
    SceneComponent selectedSceneComponent;
    Context context;

    Mesh mesh;

    MeshInfo meshInfo;


    public MeshManipulationSystem() {
        super(Family.all(SceneComponent.class).get());
    }

    public void setContext(Context context){
        this.context = context;
    }

    Vector3 vertex = new Vector3();
    Vector3 selectedVertex = new Vector3();
    ShapeRenderer shapeRenderer = new ShapeRenderer();
    Vector3 transformedVertex= new Vector3();
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SceneComponent sceneComponent = sc.get(entity);
        if (sceneComponent.selected||sceneComponent==SceneSystem.selectedSceneComponent){
            selectedSceneComponent = sceneComponent;
        } else {
            return;
        }
        MousePickingTool picker = MousePickingTool.getMousePicker();
        Ray ray = picker.ray;
        for (MeshInfo meshInfo : selectedSceneComponent.meshInfos) {
            mesh = meshInfo.mesh;
            this.meshInfo = meshInfo;

            vertex = meshInfo.getClosestVertex(ray);
            //project vertex pos in 3d space to 2d screen coords

            //Log.info("MeshManipulationSystem" , "vertex: " + vertex);
            //check if mouse is over vertex
            if (vertex != Vector3.Zero){
                if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){
                    //move vertex
                    selectedVertex = vertex;
                    Log.info("MeshManipulationSystem" , "selectedVertex: " + selectedVertex);
                    int index = meshInfo.getVertexIndexInIndices(selectedVertex);
                    transformedVertex = vertex.mul(sceneComponent.model.transform);
                    Log.info("MeshManipulationSystem" , "index: " + index);

                }
            }


        }








    }

    public void render(){
        Vector3 tmp3 = new Vector3();
        tmp3.set(transformedVertex);
        Vector3 screenPos = context.getCamera().project(tmp3);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0,1,0,1);
        shapeRenderer.circle(screenPos.x,screenPos.y,3);
        shapeRenderer.end();
    }

    public Vector3 getSelectedVertex(){
        return selectedVertex;
    }

}
