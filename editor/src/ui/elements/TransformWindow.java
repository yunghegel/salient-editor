package ui.elements;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.layout.FlowGroup;
import com.kotcrab.vis.ui.widget.*;
import core.components.SceneComponent;

public class TransformWindow
{

    public static SceneComponent component;
    static Matrix4 transform;
    Table transformTable;
    Table infoTable;
    VisSplitPane splitPane;
    FlowGroup flowGroup;
    VisWindow window;
    VisTextField xField;
    VisTextField yField;
    VisTextField zField;
    VisTextField xRotField;
    VisTextField yRotField;
    VisTextField zRotField;
    VisTextField xScaleField;
    VisTextField yScaleField;
    VisTextField zScaleField;
    SceneComponent sceneComponent;
    Table parentTable;

    public TransformWindow() {
        transformTable = new Table();
        infoTable = new Table();
        build();
    }

    public void build() {
        //translation
        VisLabel translationLabel = new VisLabel("Translation");

        xField = new VisTextField();

        yField = new VisTextField();

        zField = new VisTextField();

        xField.setSize(30 , 10);
        yField.setWidth(30);
        zField.setWidth(30);

        //rotation
        VisLabel rotationLabel;
        rotationLabel = new VisLabel("Rotation");

        xRotField = new VisTextField();

        yRotField = new VisTextField();

        zRotField = new VisTextField();

        xRotField.setWidth(25);
        yRotField.setWidth(25);
        zRotField.setWidth(25);

        //scale

        VisLabel scaleLabel = new VisLabel("Scale");

        xScaleField = new VisTextField();

        yScaleField = new VisTextField();

        zScaleField = new VisTextField();

        xScaleField.setWidth(25);
        yScaleField.setWidth(25);
        zScaleField.setWidth(25);

        setIdentity();

        //each row is a new multi split pane
        MultiSplitPane translationSplitPane = new MultiSplitPane(false);
        translationSplitPane.setWidgets(xField , yField , zField);
        translationSplitPane.setWidth(200);

        MultiSplitPane rotationSplitPane = new MultiSplitPane(false);
        rotationSplitPane.setWidgets(xRotField , yRotField , zRotField);

        MultiSplitPane scaleSplitPane = new MultiSplitPane(false);
        scaleSplitPane.setWidgets(xScaleField , yScaleField , zScaleField);

        flowGroup = new FlowGroup(true);
        flowGroup.addActor(translationLabel);
        flowGroup.addActor(translationSplitPane);
        flowGroup.addActor(rotationLabel);
        flowGroup.addActor(rotationSplitPane);
        flowGroup.addActor(scaleLabel);
        flowGroup.addActor(scaleSplitPane);

        VisTextButton applyButton = new VisTextButton("Apply");
        applyButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event , com.badlogic.gdx.scenes.scene2d.Actor actor) {
                Matrix4 transform = getTransformFromFields();
                component.scene.modelInstance.transform.set(transform);

                //                component.transform.translate(new Vector3(Float.parseFloat(xField.getText()) , Float.parseFloat(yField.getText()) , Float.parseFloat(zField.getText())));
                //                component.transform.rotate(new Vector3(Float.parseFloat(xRotField.getText()) , Float.parseFloat(yRotField.getText()) , Float.parseFloat(zRotField.getText())) , Float.parseFloat(zRotField.getText()));
                //                component.transform.scl(new Vector3(Float.parseFloat(xScaleField.getText()) , Float.parseFloat(yScaleField.getText()) , Float.parseFloat(zScaleField.getText())));
                update();
            }
        });
        flowGroup.addActor(applyButton);

        flowGroup.setWidth(300);
        flowGroup.setFillParent(false);
        flowGroup.setPosition(0 , 5);

        Table table = new Table();
        table.add(flowGroup).row();
        table.setPosition(0 , 0);
        table.sizeBy(300 , 300);
        table.setSize(200 , 300);
        window = new VisWindow("Transform");
        flowGroup.setSpacing(3);

        splitPane = new VisSplitPane(table , infoTable , true);
        splitPane.setSplitAmount(0.5f);
        splitPane.setFillParent(true);
        //parentTable.add(splitPane).row();
        window.add(table);
        window.setMovable(false);
        window.setResizable(false);

        window.setFillParent(true);

    }

    public void setIdentity() {
        xField.setText("0");
        yField.setText("0");
        zField.setText("0");

        xRotField.setText("0");
        yRotField.setText("0");
        zRotField.setText("0");

        xScaleField.setText("1");
        yScaleField.setText("1");
        zScaleField.setText("1");
    }

    public Matrix4 getTransformFromFields() {
        populateFields();
        Matrix4 transform = new Matrix4();
        Vector3 translation = new Vector3(Float.parseFloat(xField.getText()) , Float.parseFloat(yField.getText()) , Float.parseFloat(zField.getText()));
        Vector3 rotationDegrees = new Vector3(Float.parseFloat(xRotField.getText()) , Float.parseFloat(yRotField.getText()) , Float.parseFloat(zRotField.getText()));
        Vector3 scale = new Vector3(Float.parseFloat(xScaleField.getText()) , Float.parseFloat(yScaleField.getText()) , Float.parseFloat(zScaleField.getText()));
        Vector3 rotationRadians = new Vector3(rotationDegrees.x * MathUtils.degreesToRadians , rotationDegrees.y * MathUtils.degreesToRadians , rotationDegrees.z * MathUtils.degreesToRadians);
        Quaternion quaternion = new Quaternion();
        quaternion.setEulerAngles(rotationDegrees.x , rotationDegrees.y , rotationDegrees.z);
        transform.set(translation , quaternion , scale);

        return transform;
    }

    public void update() {
        if (component != null) {
            Matrix4 transform = getTransformFromFields();
            component.scene.modelInstance.transform.set(transform);

        }
    }

    public void populateFields() {
        Matrix4 transform = component.scene.modelInstance.transform;
        Vector3 translation = new Vector3();
        transform.getTranslation(translation);
        xField.setText(String.valueOf(translation.x));
        yField.setText(String.valueOf(translation.y));
        zField.setText(String.valueOf(translation.z));

        Quaternion rotation = new Quaternion();
        transform.getRotation(rotation);
        float rotX = rotation.getPitchRad() * MathUtils.radiansToDegrees;
        float rotY = rotation.getYawRad() * MathUtils.radiansToDegrees;
        float rotZ = rotation.getRollRad() * MathUtils.radiansToDegrees;
        xRotField.setText(String.valueOf(rotX));
        yRotField.setText(String.valueOf(rotY));
        zRotField.setText(String.valueOf(rotZ));

        Vector3 scale = new Vector3();
        transform.getScale(scale);
        xScaleField.setText(String.valueOf(scale.x));
        yScaleField.setText(String.valueOf(scale.y));
        zScaleField.setText(String.valueOf(scale.z));

    }

    public TransformWindow(VisWindow window , SceneComponent component) {
        this.sceneComponent = component;

        transformTable = new Table();
        infoTable = new Table();

        build();
        addToWindow(window);
    }

    public void addToWindow(VisWindow window) {
        window.add(flowGroup).row();
        flowGroup.setWidth(window.getWidth());
    }

    public void setComponent(SceneComponent sceneComponent) {
        component = sceneComponent;
        transform = sceneComponent.transform;
        update();

    }

    public void addtoStage(Table container) {
        container.clear();
        container.addActor(splitPane);

    }

}
