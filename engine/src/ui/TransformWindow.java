package ui;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.building.GridTableBuilder;
import com.kotcrab.vis.ui.building.utilities.Padding;
import com.kotcrab.vis.ui.layout.FlowGroup;
import com.kotcrab.vis.ui.widget.*;
import ecs.components.SceneComponent;

public class TransformWindow {
    static Matrix4 transform;
    static SceneComponent component;
    Table transformTable;
    Table infoTable;
    VisSplitPane splitPane;
    FlowGroup flowGroup;
    VisWindow window;
    private VisTextField xField;

    public TransformWindow() {
        transformTable = new Table();
        infoTable = new Table();
        build();
    }

    public static void setComponent(SceneComponent sceneComponent){
        component = sceneComponent;
        transform = sceneComponent.transform;


    }

    public void update(){
        if(component != null){
            component.transform = transform;
        }
    }

    public void build(){
        //translation
        VisLabel translationLabel = new VisLabel("Translation");
        VisTextField xField = new VisTextField();
        VisTextField yField = new VisTextField();
        VisTextField zField = new VisTextField();


        xField.setSize(30,10);
        yField.setWidth(30);
        zField.setWidth(30);

        //rotation
        VisLabel rotationLabel = new VisLabel("Rotation");
        VisTextField xRotField = new VisTextField();
        VisTextField yRotField = new VisTextField();
        VisTextField zRotField = new VisTextField();

        xRotField.setWidth(25);
        yRotField.setWidth(25);
        zRotField.setWidth(25);

        //scale

        VisLabel scaleLabel = new VisLabel("Scale");
        VisTextField xScaleField = new VisTextField();
        VisTextField yScaleField = new VisTextField();
        VisTextField zScaleField = new VisTextField();

        xScaleField.setWidth(25);
        yScaleField.setWidth(25);
        zScaleField.setWidth(25);

        GridTableBuilder builder = new GridTableBuilder(4,12,3);
        builder.setTablePadding(new Padding(15,15,15,15));
        builder.append(translationLabel);
        builder.append(xField).append(yField).append(zField).row();
        builder.append(rotationLabel);
        builder.append(xRotField).append(yRotField).append(zRotField).row();
        builder.append(scaleLabel);
        builder.append(xScaleField).append(yScaleField).append(zScaleField).row();

        /*xField.setText(component.transform.getTranslation(new Vector3()).x + "");
        yField.setText(component.transform.getTranslation(new Vector3()).y + "");
        zField.setText(component.transform.getTranslation(new Vector3()).z + "");*/

        xField.setText("0");
        yField.setText("0");
        zField.setText("0");

        xRotField.setText("0");
        yRotField.setText("0");
        zRotField.setText("0");

        xScaleField.setText("1");
        yScaleField.setText("1");
        zScaleField.setText("1");



        //each row is a new multi split pane
        MultiSplitPane translationSplitPane = new MultiSplitPane(false);
        translationSplitPane.setWidgets( xField, yField, zField);
        translationSplitPane.setWidth(200);


        MultiSplitPane rotationSplitPane = new MultiSplitPane(false);
        rotationSplitPane.setWidgets( xRotField, yRotField, zRotField);




        MultiSplitPane scaleSplitPane = new MultiSplitPane(false);
        scaleSplitPane.setWidgets( xScaleField, yScaleField, zScaleField);


        flowGroup = new FlowGroup(true);
        flowGroup.addActor(translationLabel);
        flowGroup.addActor(translationSplitPane);
        flowGroup.addActor(rotationLabel);
        flowGroup.addActor(rotationSplitPane);
        flowGroup.addActor(scaleLabel);
        flowGroup.addActor(scaleSplitPane);
        VisTextButton applyButton = new VisTextButton("Apply");
        applyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {

                component.transform.translate(new Vector3(Float.parseFloat(xField.getText()), Float.parseFloat(yField.getText()), Float.parseFloat(zField.getText())));
                component.transform.rotate(new Vector3(Float.parseFloat(xRotField.getText()), Float.parseFloat(yRotField.getText()), Float.parseFloat(zRotField.getText())), Float.parseFloat(zRotField.getText()));
                component.transform.scl(new Vector3(Float.parseFloat(xScaleField.getText()), Float.parseFloat(yScaleField.getText()), Float.parseFloat(zScaleField.getText())));
                update();
            }
        });
        flowGroup.addActor(applyButton);

        flowGroup.setWidth(300);
        flowGroup.setFillParent(false);
        flowGroup.setPosition(0,5);

        Table table = new Table();
        table.add(flowGroup).row();
        table.setPosition(0,0);
        table.sizeBy(300,300);
        table.setSize(200,300);
        window = new VisWindow("Transform");
        flowGroup.setSpacing(3);


        splitPane = new VisSplitPane(flowGroup, infoTable, false);
        splitPane.setSplitAmount(0.5f);
        splitPane.setFillParent(true);
        window.add(flowGroup);
        window.setMovable(false);
        window.setResizable(false);


        window.setFillParent(true);








    }
    public void addtoStage(Table container){
        container.addActor(window);


    }



}
