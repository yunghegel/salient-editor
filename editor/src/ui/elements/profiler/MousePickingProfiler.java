package ui.elements.profiler;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisWindow;
import ui.elements.profiler.tables.MousePickingProfilerTable;

public class MousePickingProfiler
{
    VisLabel distanceToOrigin,distanceToCamera,distanceToWorldCoord,distanceX,distanceY,distanceZ;
    VisLabel cameraX,cameraY,cameraZ;
    VisLabel worldX,worldY,worldZ;
    VisLabel worldIntersection;
    VisLabel xPlaneIntersection,yPlaneIntersection,zPlaneIntersection;
    VisWindow mouseProfilerWindow;
    public MousePickingProfiler(Camera camera)

    {
        MousePickingProfilerTable table = new MousePickingProfilerTable(camera);
        mouseProfilerWindow = new VisWindow("Mouse Picking Profiler");
        mouseProfilerWindow.add(table);
    }

    public void addToStage(Stage stage)
    {
        stage.addActor(mouseProfilerWindow);

    }

}
