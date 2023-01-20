package editor.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import tests.GizmoTest;

public class BasePickerTool extends MultiplexedTool
{
    private static BasePickerTool instance;

    public static BasePickerTool getInstance() {

        return instance;
    }

    Camera camera;
    public Ray ray;
    boolean enabled = true;
    boolean debug = true;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    Vector3 intersection = new Vector3();
    ModelInstance selected;
    public Vector3 selectionPos = new Vector3();
    BoundingBox boundingBox = new BoundingBox();
    BoundingBox selectedBoundingBox = new BoundingBox();
    ModelBatch batch;
    public static GizmoTest.GameObject selectedGameObject;
    public int selectedCount = 0;
    public BasePickerTool(InputMultiplexer multiplexer, Camera camera)
    {

        super(multiplexer);
        instance = this;
        this.camera=camera;
        multiplexer.addProcessor(this);
        batch = new ModelBatch();
    }



    @Override
    public void update() {
    createRay();
    checkDistance();
    }

    public void debugDraw(){
//        if (debug){
//            Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
//            shapeRenderer.setProjectionMatrix(camera.combined);
//            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//            shapeRenderer.setColor(Color.MAGENTA);
//            shapeRenderer.line(ray.origin, ray.direction);
//            shapeRenderer.end();
//            if(selected!=null)
//            {
//                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//                shapeRenderer.setColor(Color.RED);
//
//                shapeRenderer.end();
//
//                shapeRenderer.begin(ShapeRenderer.ShapeType.Point);
//                shapeRenderer.setColor(Color.YELLOW);
//                shapeRenderer.point(selectedBoundingBox.getCenterX() , selectedBoundingBox.getCenterY() , selectedBoundingBox.getCenterZ());
//                shapeRenderer.end();
//
//                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//                shapeRenderer.setColor(Color.GREEN);
//                shapeRenderer.box(selectedBoundingBox.min.x , selectedBoundingBox.min.y , selectedBoundingBox.min.z + selectedBoundingBox.getDepth() , selectedBoundingBox.getWidth() , selectedBoundingBox.getHeight() , selectedBoundingBox.getDepth());
//                shapeRenderer.end();
//            }
//        }
//        for (GizmoTest.GameObject gameObject : GizmoTest.gameObjects) {
//
//            drawBoundingBox(gameObject);
//            outlineDraw(gameObject);
//            Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
//        }

        if (selectedGameObject != null) {
            drawBoundingBox(selectedGameObject);
            outlineDraw(selectedGameObject);
        }

    }

    public void outlineDraw(GizmoTest.GameObject gameObject){
        if (gameObject!=null&&gameObject.hovered){
            batch.begin(camera);
            batch.render(gameObject.outlineInstance);
            batch.end();
        }

    }

    public void drawBoundingBox(GizmoTest.GameObject gameObject){
        if(gameObject!=null&&gameObject.selected) {
            Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.line(intersection,selectedGameObject.position);
            shapeRenderer.box(gameObject.boundingBox.min.x , gameObject.boundingBox.min.y , gameObject.boundingBox.min.z + gameObject.boundingBox.getDepth() , gameObject.boundingBox.getWidth() , gameObject.boundingBox.getHeight() , gameObject.boundingBox.getDepth());
            shapeRenderer.end();
            //        } else if (gameObject!=null&&gameObject.hovered){
            //           shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            //              shapeRenderer.setColor(Color.GREEN);
            //                shapeRenderer.box(gameObject.boundingBox.min.x , gameObject.boundingBox.min.y , gameObject.boundingBox.min.z + gameObject.boundingBox.getDepth() , gameObject.boundingBox.getWidth() , gameObject.boundingBox.getHeight() , gameObject.boundingBox.getDepth());
            //                shapeRenderer.end();
            //        }
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (Gdx.input.isKeyJustPressed(44)) {
            enabled = !enabled;
            System.out.println("Tool enabled: " + enabled);


        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX , int screenY , int pointer , int button) {
        //if not left click, return
        if (button != 0) return false;

        for (GizmoTest.GameObject gameObject: GizmoTest.gameObjects)
        {
            if (gameObject.selected){
                gameObject.selected = false;
            }
            if (gameObject!=null&&gameObject.hovered){
                selectedGameObject = gameObject;
                selected = gameObject.instance;
                selectedBoundingBox = gameObject.boundingBox;
                selectionPos.set(selectedBoundingBox.getCenterX() , selectedBoundingBox.getCenterY() , selectedBoundingBox.getCenterZ());
                gameObject.selected = true;
                System.out.println("Selected: " + selectedGameObject);
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX , int screenY , int pointer , int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX , int screenY , int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX , int screenY) {
        GizmoTest.outlineInstance = null;
        GizmoTest.outlineModel = null;

        boundingBox = new BoundingBox();
        for (com.badlogic.gdx.graphics.g3d.ModelInstance instance : GizmoTest.instances) {
//


        }

        for (GizmoTest.GameObject gameObject: GizmoTest.gameObjects) {

            if (Intersector.intersectRayBounds(ray, gameObject.boundingBox, intersection)) {
//                System.out.println("Intersection: " + intersection);
                selectedGameObject = gameObject;
                gameObject.transform.getTranslation(selectionPos);
                //gameObject.boundingBox.mul(gameObject.transform);
                gameObject.hovered = true;

//                GizmoTest.outlineModel = GizmoTest.outlineModel=ModelUtils.createOutlineModelAsCopy(gameObject.model,Color.WHITE,0.1f);
//                GizmoTest.outlineInstance = new ModelInstance(GizmoTest.outlineModel,gameObject.transform);

            }
            else{
                gameObject.hovered = false;

            }
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX , float amountY) {
        return false;
    }

    public interface PickerToolSelectionCallback<T>
    {
        void onSelection(T selection);
    }

    public void createRay(){
        ray = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
    }

    //check for the distance between the camera and the object. if multiple objects are selected, the closest one will be selected
    public void checkDistance(){
        for (GizmoTest.GameObject gameObject: GizmoTest.gameObjects) {
            if (gameObject.selected){
                if (gameObject.transform.getTranslation(new Vector3()).dst(camera.position) < selectedGameObject.transform.getTranslation(new Vector3()).dst(camera.position)){
                    selectedGameObject = gameObject;
                }
                else if (gameObject.transform.getTranslation(new Vector3()).dst(camera.position) > selectedGameObject.transform.getTranslation(new Vector3()).dst(camera.position)){
                    gameObject.selected = false;
                }
            }
        }
    }

}

