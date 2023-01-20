package editor.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import editor.Context;
import editor.graphics.rendering.Renderer;
import ui.widgets.RenderWidget;
import util.MiscUtils;
import util.ModelUtils;

public class MousePickingTool implements Renderer, InputProcessor
{
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    protected static Color COLOR_X = Color.RED;
    protected static Color COLOR_Y = Color.GREEN;
    protected static Color COLOR_Z = Color.BLUE;

    public Vector3 mouseDownPos = new Vector3();
    public Vector3 mouseUpPos = new Vector3();
    public Vector3 mousePos = new Vector3();
    public Vector3 cameraPosition = new Vector3();
    public Vector3 tmp = new Vector3();
    public Matrix4 tmpMatrix = new Matrix4();
    public Vector3 worldIntersectionCoordinates = new Vector3();
    public Vector3 xPlaneIntersection = new Vector3();
    public Vector3 yPlaneIntersection = new Vector3();
    public Vector3 zPlaneIntersection = new Vector3();
    public Vector3 endPointX = new Vector3();
    public Vector3 endPointY = new Vector3();
    public Vector3 endPointZ = new Vector3();

    public Vector3 xPlaneNormal = new Vector3(1, 0, 0);
    public Vector3 yPlaneNormal = new Vector3(0, 1, 0);
    public Vector3 zPlaneNormal = new Vector3(0, 0, 1);

    Vector3 decomposedXVector = new Vector3();
    Vector3 decomposedYVector = new Vector3();
    Vector3 decomposedZVector = new Vector3();


    public float distanceX,distanceY,distanceZ;
    public float worldX,worldY,worldZ;
    public float distanceToOriginX,distanceToOriginY,distanceToOriginZ;
    public float distanceToMouseX,distanceToMouseY,distanceToMouseZ;
    public float distanceToOrigin=0;
    public float distanceToCamera=0;
    public float distanceToWorldIntersection =0;
    public float objectDistanceToOrigin=0;
    public float objectDistanceToCamera=0;
    public Vector3 XYZRayOrigin=new Vector3();
    public Ray PosZRay = new Ray(XYZRayOrigin, new Vector3(0, 0, 1));
    public Ray NegZRay = new Ray(XYZRayOrigin, new Vector3(0, 0, -1));
    public Ray PosYRay = new Ray(XYZRayOrigin, new Vector3(0, 1, 0));
    public Ray NegYRay = new Ray(XYZRayOrigin, new Vector3(0, -1, 0));
    public Ray PosXRay = new Ray(XYZRayOrigin, new Vector3(1, 0, 0));
    public Ray NegXRay = new Ray(XYZRayOrigin, new Vector3(-1, 0, 0));
    public Vector3 origin = new Vector3(0,0,0);

    Plane XYPlane;
    Plane YZPlane;
    Plane ZXPlane;
    Plane LocalXYPlane;
    Plane LocalYZPlane;
    Plane LocalZXPlane;
    public Ray ray;

    Model sphere;
    ModelInstance sphereInstance;
    ModelBatch batch;

    Plane[] planes = new Plane[3];

    public void createPlanes(){
        XYPlane = new Plane(new Vector3(0, 0, 1), 0);
        YZPlane = new Plane(new Vector3(1, 0, 0), 0);
        ZXPlane = new Plane(new Vector3(0, 1, 0), 0);
        planes[0] = XYPlane;
        planes[1] = YZPlane;
        planes[2] = ZXPlane;
    }





    private static MousePickingTool i;

    public static MousePickingTool getMousePicker(){
        if (i == null){
            i = new MousePickingTool();
        }
        return i;
    }

    private MousePickingTool(){
        createPlanes();

        RenderWidget.getInstance().addRenderer(this);
        sphere = ModelUtils.createSphere(1);
        sphereInstance = new ModelInstance(sphere);
        batch = new ModelBatch();
        sphereInstance.transform.setTranslation(10,0,10);
        Context.getInstance().inputMultiplexer.addProcessor(this);

    }


    @Override
    public void render(Camera cam) {
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        shapeRenderer.setProjectionMatrix(cam.combined);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.YELLOW);
//        shapeRenderer.line(cam.position,Vector3.Zero);
//        shapeRenderer.line(cam.position,worldIntersectionCoordinates);
//        ray = RenderWidget.viewport.getPickRay(Gdx.input.getX(), Gdx.input.getY());
//        shapeRenderer.line(ray.origin,ray.direction.sub(ray.origin));
//        shapeRenderer.line((ray.origin),decomposedXVector);
//        shapeRenderer.line((ray.origin),decomposedYVector);
//        shapeRenderer.line((ray.origin),decomposedZVector);
//
//        shapeRenderer.end();

//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.RED);
//        shapeRenderer.line(origin,decomposedXVector);
//        shapeRenderer.end();
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.GREEN);
//        shapeRenderer.line(origin,decomposedYVector);
//        shapeRenderer.end();
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.BLUE);
//        shapeRenderer.line(origin,decomposedZVector);
//        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        //shapeRenderer.circle(worldIntersectionCoordinates.x,worldIntersectionCoordinates.z,1);
        shapeRenderer.end();


//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.RED);
//        shapeRenderer.line(decomposedZVector.cpy().add(decomposedXVector),decomposedZVector);
//        shapeRenderer.line(decomposedYVector,decomposedXVector.cpy().add(decomposedYVector));
//        shapeRenderer.end();
//
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.GREEN);
//        shapeRenderer.line(decomposedZVector,decomposedYVector.cpy().add(decomposedZVector));
//        shapeRenderer.line(decomposedXVector.cpy().add(decomposedYVector),decomposedXVector);
//        shapeRenderer.end();
//
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.BLUE);
//        shapeRenderer.line(decomposedXVector,decomposedZVector.cpy().add(decomposedXVector));
//        shapeRenderer.line(decomposedYVector.cpy().add(decomposedZVector),decomposedYVector);
//        shapeRenderer.end();

        batch.begin(cam);


        batch.render(sphereInstance,Context.getInstance().getSceneManager().environment);
        batch.end();





    }

    public void update(){
        ray = RenderWidget.viewport.getPickRay(mousePos.x , mousePos.y);
        updateCameraPos();
//        calculateObjectDistToOrigin(sphereInstance,distanceToOrigin);
//        updatePlaneDistanceFromOrigin(distanceToOrigin);
//        setOriginToObjectTranslation(sphereInstance);
        calculateDecomposedXYZVectors();
    }

    @Override
    public boolean keyDown(int keycode) {
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
        mouseDownPos.set(screenX , screenY , 0);
        ray = RenderWidget.viewport.getPickRay(screenX , screenY);

        return false;
    }

    @Override
    public boolean touchUp(int screenX , int screenY , int pointer , int button) {
        mouseUpPos.set(screenX , screenY , 0);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX , int screenY , int pointer) {

        Intersector.intersectRayPlane(ray , YZPlane , xPlaneIntersection);
        Intersector.intersectRayPlane(ray , ZXPlane , yPlaneIntersection);
        Intersector.intersectRayPlane(ray , XYPlane , zPlaneIntersection);
        worldIntersectionCoordinates = new Vector3(-zPlaneIntersection.x , xPlaneIntersection.y , -yPlaneIntersection.z);
        calculateDecomposedXYZVectors();
        updateWorldIntersectionValues();
        calculateDistanceToOrigin(worldIntersectionCoordinates);
        calculateDistanceToCamera(worldIntersectionCoordinates);
        calculateDistanceToWorldIntersection(ray.origin);


        return false;
    }

    @Override
    public boolean mouseMoved(int screenX , int screenY) {
        mousePos.set(screenX , screenY , 0);

        ray = RenderWidget.viewport.getPickRay(mousePos.x , mousePos.y);

        setDistanceXYZ(ray);


        Intersector.intersectRayPlane(ray , YZPlane , xPlaneIntersection);
        Intersector.intersectRayPlane(ray , ZXPlane , yPlaneIntersection);
        Intersector.intersectRayPlane(ray , XYPlane , zPlaneIntersection);
        worldIntersectionCoordinates = new Vector3(-zPlaneIntersection.x , xPlaneIntersection.y , -yPlaneIntersection.z);
        updateWorldIntersectionValues();
        calculateDistanceToOrigin(worldIntersectionCoordinates);
        calculateDistanceToCamera(worldIntersectionCoordinates);
        calculateDistanceToWorldIntersection(ray.origin);


        return false;
    }

    private void setDistanceXYZ(Ray ray){
        distanceX = -ray.origin.x / ray.direction.x;
        distanceY = -ray.origin.y / ray.direction.y;
        distanceZ = -ray.origin.z / ray.direction.z;
    }

    private void calculateDistanceToOrigin(Vector3 in){
        distanceToOrigin = in.dst(Vector3.Zero);
    }

    private void calculateDistanceToCamera(Vector3 in){
        updateCameraPos();
        distanceToCamera = in.dst(Context.getInstance().camera.position);
    }

    private void calculateDistanceToWorldIntersection(Vector3 in){
        distanceToWorldIntersection = in.dst(worldIntersectionCoordinates);
    }

    private void updateCameraPos(){
        cameraPosition.set(Context.getInstance().camera.position);
    }

    public void updateWorldIntersectionValues(){
        worldX = worldIntersectionCoordinates.z;
        worldY = worldIntersectionCoordinates.x;
        worldZ = worldIntersectionCoordinates.y;

    }

    public void calculateDecomposedXYZVectors(){
        decomposedXVector= new Vector3(-worldIntersectionCoordinates.x,origin.y,origin.z);
        decomposedYVector= new Vector3(origin.x,worldIntersectionCoordinates.y,origin.z);
        decomposedZVector = new Vector3(origin.x,origin.y,-worldIntersectionCoordinates.z);
    }

    public void intersectRaysAtPosition(){

    }

    public void updateRayOrigin(){
        PosXRay.origin.set(XYZRayOrigin);
        PosYRay.origin.set(XYZRayOrigin);
        PosZRay.origin.set(XYZRayOrigin);
    }

    public void setXYZRayOrigin(Vector3 newOrigin){
        XYZRayOrigin.set(newOrigin);
    }

    public Vector3 getClosestRayIntersectionPoint(){
        Vector3 closestPoint = new Vector3();
        //Intersector.intersectRayRay(XYZRayOrigin)

        return closestPoint;
    }
    public void calculateObjectDistToOrigin(ModelInstance model,float out){

        Vector3 position =new Vector3();
        MiscUtils.getPosition(model.transform,position);
        distanceToOrigin = position.dst(Vector3.Zero);
        out = distanceToOrigin;


    }

    public void calculateObjectDistToCamera(){
        objectDistanceToCamera = worldIntersectionCoordinates.dst(Context.getInstance().camera.position);
    }

    public void updatePlaneDistanceFromOrigin(){
        XYPlane = new Plane(new Vector3(0, 0, 1), distanceZ);
        YZPlane = new Plane(new Vector3(1, 0, 0), distanceX);
        ZXPlane = new Plane(new Vector3(0, 1, 0), distanceY);
    }
    public void updatePlaneDistanceFromOrigin(float distance){
        XYPlane = new Plane(new Vector3(0, 0, 1), distance);
        YZPlane = new Plane(new Vector3(1, 0, 0), distance);
        ZXPlane = new Plane(new Vector3(0, 1, 0), distance);
    }
    public void updatePlaneDistanceFromOrigin(float x,float y,float z) {
        XYPlane = new Plane(new Vector3(0 , 0 , 1) , x);
        YZPlane = new Plane(new Vector3(1 , 0 , 0) , y);
        ZXPlane = new Plane(new Vector3(0 , 1 , 0) , z);
    }

    public void updatePlaneDistanceFromOrigin(Vector3 in){
        XYPlane = new Plane(new Vector3(0, 0, 1), in.x);
        YZPlane = new Plane(new Vector3(1, 0, 0), in.y);
        ZXPlane = new Plane(new Vector3(0, 1, 0), in.z);
    }

    public void setOriginToObjectTranslation(ModelInstance model){
        Vector3 position =new Vector3();
        MiscUtils.getPosition(model.transform,position);
        updatePlaneDistanceFromOrigin(position);
        origin = position;
    }


    @Override
    public boolean scrolled(float amountX , float amountY) {
        return false;
    }

}
