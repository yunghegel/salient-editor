package ui.elements.profiler.tables;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import editor.tools.MousePickingTool;
import util.StringUtils;

public class MousePickingProfilerTable extends Table {
    VisLabel distanceToOrigin,distanceToCamera,distanceToWorldCoord,distanceX,distanceY,distanceZ;
    VisLabel cameraX,cameraY,cameraZ;
    VisLabel worldX,worldY,worldZ;
    VisLabel worldIntersection;
    VisLabel xPlaneIntersection,yPlaneIntersection,zPlaneIntersection;
    MousePickingTool i;
    Camera camera;

    public MousePickingProfilerTable(Camera camera) {
        super();
        this.camera = camera;
        pad(2);
        align(Align.topLeft);
        createLabels();
        addLabels();
    }
    private void createLabels() {
        distanceToOrigin = new VisLabel("Distance to Origin: ");
        distanceToCamera = new VisLabel("Distance to Camera: ");
        distanceToWorldCoord = new VisLabel("Distance to World Coord: ");
        distanceX = new VisLabel("Distance X: ");
        distanceY = new VisLabel("Distance Y: ");
        distanceZ = new VisLabel("Distance Z: ");

        cameraX = new VisLabel("Camera X: ");
        cameraY = new VisLabel("Camera Y: ");
        cameraZ = new VisLabel("Camera Z: ");

        worldX = new VisLabel("World X: ");
        worldY = new VisLabel("World Y: ");
        worldZ = new VisLabel("World Z: ");

        xPlaneIntersection = new VisLabel("X Plane Intersection: ");
        yPlaneIntersection = new VisLabel("Y Plane Intersection: ");
        zPlaneIntersection = new VisLabel("Z Plane Intersection: ");

        worldIntersection = new VisLabel("World Intersection: ");
    }

    private void addLabels(){
        add(distanceToOrigin).left().pad(2);
        row();
        add(distanceToCamera).left().pad(2);
        row();
        add(distanceToWorldCoord).left().pad(2);

        row();
        add(distanceX).left().pad(2);
        row();
        add(distanceY).left().pad(2);
        row();
        add(distanceZ).left().pad(2);
        row();
        add(cameraX).left().pad(2);
        row();
        add(cameraY).left().pad(2);
        row();
        add(cameraZ).left().pad(2);
        row();

        add(worldX).left().pad(2);
        row();
        add(worldY).left().pad(2);
        row();
        add(worldZ).left().pad(2);

        row();
        add(xPlaneIntersection).left().pad(2);
        row();
        add(yPlaneIntersection).left().pad(2);
        row();
        add(zPlaneIntersection).left().pad(2);


    }
    private void updateLabels(){
        distanceToOrigin.setText("world dst to origin:  "+ StringUtils.trimFloat(i.distanceToOrigin));
        distanceToCamera.setText("world dst to camera:  "+ StringUtils.trimFloat(i.distanceToCamera));
        distanceToWorldCoord.setText("ray dst to world coord:  "+ StringUtils.trimFloat(i.distanceToWorldIntersection));

        distanceX.setText("dst x:  "+ StringUtils.trimFloat(i.distanceX));
        distanceY.setText("dst y:  "+ StringUtils.trimFloat(i.distanceY));
        distanceZ.setText("dst z:  "+ StringUtils.trimFloat(i.distanceZ));

        cameraX.setText("cam x:  "+ StringUtils.trimFloat(i.cameraPosition.x));
        cameraY.setText("cam y:  "+ StringUtils.trimFloat(i.cameraPosition.y));
        cameraZ.setText("cam z:  "+ StringUtils.trimFloat(i.cameraPosition.z));

        zPlaneIntersection.setText("z plane intersection:  "+ StringUtils.trimVector3(i.zPlaneIntersection));
        yPlaneIntersection.setText("y plane intersection:  "+ StringUtils.trimVector3(i.yPlaneIntersection));
        xPlaneIntersection.setText("x plane intersection:  "+ StringUtils.trimVector3(i.xPlaneIntersection));

        worldIntersection.setText("worldX:  "+ StringUtils.trimFloat(i.worldX)+", worldY:  "+ StringUtils.trimFloat(i.worldY)+", worldZ:  "+ StringUtils.trimFloat(i.worldZ));
    }
    @Override
    public void act(float delta){
        updateLabels();
        i.update();
        i.render(camera);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        super.draw(batch, parentAlpha);
    }

}

