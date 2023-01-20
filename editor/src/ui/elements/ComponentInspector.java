package ui.elements;

import backend.tools.Log;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.layout.FlowGroup;
import com.kotcrab.vis.ui.widget.*;
import core.components.SceneComponent;
import core.systems.SceneSystem;
import editor.Context;
import editor.tools.TranslateTool;

public class ComponentInspector extends VisWindow
{
    VisTable root;

    public CollapsibleWidget materialsWidget;
    public CollapsibleWidget transformWidget;
    public CollapsibleWidget meshWidget;
    public CollapsibleWidget lightWidget;
    VisTextButton materialsButton;
    VisTextButton transformButton;
    VisTextButton meshButton;
    VisTextButton lightButton;
    VisTable materialsTable;
    VisTable transformTable;
    VisTable meshTable;
    VisTable lightTable;
    VisWindow materialsWindow;
    VisWindow transformWindow;
    VisWindow meshWindow;
    VisWindow lightWindow;
    VisScrollPane materialsScrollPane;
    VisScrollPane transformScrollPane;
    VisScrollPane meshScrollPane;
    VisScrollPane lightScrollPane;
    VisScrollPane rootScrollPane;

    VisTextButton meshExpandAllButton;
    VisTextButton meshCollapseAllButton;

    VisTextButton materialsExpandAllButton;
    VisTextButton materialsCollapseAllButton;





    public ComponentInspector()
    {
        super("Inspector");

        setFillParent(true);
        padTop(25);

        root = new VisTable();
        //root.setFillParent(true);
        root.align(Align.top);
        rootScrollPane = new VisScrollPane(root);
        root.padTop(25);
        add(rootScrollPane).expand().fill().grow();
        getTitleLabel().setAlignment(Align.center);
        getTitleTable().pad(15).align(Align.center);
align(Align.center);
        init();
    }

    private void init() {
        materialsTable = new VisTable();
        transformTable = new VisTable();
        meshTable = new VisTable();
        lightTable = new VisTable();

        meshTable.align(Align.topLeft);
        materialsTable.align(Align.topLeft);

        materialsButton = new VisTextButton("Materials");
        //materialsButton.padTop(50);
        transformButton = new VisTextButton("Transform");
        meshButton = new VisTextButton("Mesh");
        lightButton = new VisTextButton("Light");

        materialsWindow = new VisWindow("Materials");
        transformWindow = new VisWindow("Transform");
        meshWindow = new VisWindow("Mesh View");
        lightWindow = new VisWindow("Light");


        meshScrollPane = new VisScrollPane(meshWindow);
        transformScrollPane = new VisScrollPane(transformTable);
        materialsScrollPane = new VisScrollPane(materialsTable);
        lightScrollPane = new VisScrollPane(lightTable);

        materialsTable.add(materialsWindow);
        transformTable.add(transformWindow);
        meshTable.add(meshScrollPane);
        lightTable.add(lightWindow);

        materialsWidget = new CollapsibleWidget(materialsTable, true);
        transformWidget = new CollapsibleWidget(transformTable,true);
        meshWidget = new CollapsibleWidget(meshTable,true);
        lightWidget = new CollapsibleWidget(lightTable,true);
        root.add(materialsButton).expandX().fillX().pad(5).row();
        root.add(materialsWidget).expandX().fillX().row();
        root.add(transformButton).expandX().fillX().pad(5).row();
        root.add(transformWidget).expandX().fillX().row();
        root.add(meshButton).expandX().fillX().pad(5).row();
        root.add(meshWidget).expandX().fillX().row();
        root.add(lightButton).expandX().fillX().pad(5).row();
        root.add(lightWidget).expandX().fillX().row();

        materialsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                materialsWidget.setCollapsed(!materialsWidget.isCollapsed());
            }
        });

        transformButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                transformWidget.setCollapsed(!transformWidget.isCollapsed());
            }
        });

        meshButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                meshWidget.setCollapsed(!meshWidget.isCollapsed());
            }
        });

        lightButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                lightWidget.setCollapsed(!lightWidget.isCollapsed());
            }
        });

        meshExpandAllButton = new VisTextButton("Expand");
        meshCollapseAllButton = new VisTextButton("Collapse");

        materialsExpandAllButton = new VisTextButton("Expand");
        materialsCollapseAllButton = new VisTextButton("Collapse");

        createTransformTable();

    }

    public void populateTables(SceneComponent component)
    {
        populateMeshTable(component);
        populateMaterialsTable(component);
        populateTransformTable(component);
        setTransformTableListener(component);
    }

    public void populateMeshTable(SceneComponent component) {
        int count = 0;
        int totalVertices = 0;
        int totalIndices = 0;
        meshWindow.clear();
//        meshWindow.add(meshExpandAllButton).expandX().fillX().pad(5);
//        meshWindow.add(meshCollapseAllButton).expandX().fillX().pad(5).row();
        materialsWindow.add(new Separator()).expandX().fillX().padTop(5).padBottom(20).colspan(2).row();
        Array<CollapsibleWidget> collapsibleWidgets = new Array<CollapsibleWidget>();
        collapsibleWidgets.clear();
        VisLabel totalVerticesLabel = new VisLabel("Total Vertices: " + totalVertices);
        VisLabel totalIndicesLabel = new VisLabel("Total Indices: " + totalIndices);
        VisLabel totalTrianglesLabel = new VisLabel("Total Triangles: ");


        meshWindow.add(totalVerticesLabel).left().pad(2).row();
        meshWindow.add(totalIndicesLabel).left().pad(2).row();
        meshWindow.add(totalTrianglesLabel).left().pad(2).row();
        meshWindow.add(new Separator()).expandX().fillX().padTop(5).padBottom(20).colspan(2).row();

        for (Mesh mesh : component.meshes) {
            count++;
            VisTable table = new VisTable();
            table.align(Align.topLeft);
            VisTextButton button = new VisTextButton("Mesh " + count);
            CollapsibleWidget widget = new CollapsibleWidget(table, true);
            collapsibleWidgets.add(widget);
            meshWindow.add(button).pad(5).left().row();
            VisLabel label = new VisLabel("Mesh " + count);

            int vertices = mesh.getNumVertices();
            int indices = mesh.getNumIndices();
            totalVertices += vertices;
            totalIndices += indices;

            //meshWindow.add(label).expandX().fillX().growX().row();
            table.add(new VisLabel(mesh.getNumVertices() + " vertices")).left().pad(2).row();
            table.add(new VisLabel(mesh.getNumIndices() + " indices")).left().pad(2).row();
            table.add(new VisLabel(mesh.getNumIndices() / 3 + " triangles")).left().pad(2).row();
            table.add(new Separator()).expandX().fillX().growX().pad(10).row();
            meshWindow.add(widget).expandX().fillX().growX().row();

            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    widget.setCollapsed(!widget.isCollapsed());
                }
            });
        }
        totalVerticesLabel.setText("Total Vertices: " + totalVertices);
        totalIndicesLabel.setText("Total Indices: " + totalIndices);
        totalTrianglesLabel.setText("Total Triangles: " + totalIndices / 3);

        meshExpandAllButton.clearListeners();
        meshExpandAllButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                for (CollapsibleWidget widget : collapsibleWidgets) {
                    widget.setCollapsed(false);
                }
            }
        });
        meshCollapseAllButton.clearListeners();
        meshCollapseAllButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                for (CollapsibleWidget widget : collapsibleWidgets) {
                    widget.setCollapsed(true);
                }
            }
        });
    }

    public void populateMaterialsTable(SceneComponent component) {
        int count = 0;
        materialsWindow.clear();
//        materialsWindow.add(materialsExpandAllButton).pad(5).left();
//        materialsWindow.add(materialsCollapseAllButton).pad(5).left().row();
        materialsWindow.add(new Separator()).expandX().fillX().padTop(5).padBottom(20).colspan(2).row();
        Array<CollapsibleWidget> collapsibleWidgets = new Array<CollapsibleWidget>();
        collapsibleWidgets.clear();
        for (Material material : component.materials) {
            count++;
            VisTable table = new VisTable();
            VisTextButton button = new VisTextButton(material.id);
            CollapsibleWidget widget = new CollapsibleWidget(table, true);
            collapsibleWidgets.add(widget);
            materialsWindow.add(button).pad(5).left().row();



            Array<Attribute> colorAttributes = new Array<>();
            Array<Attribute> textureAttributes = new Array<>();

            material.get(colorAttributes, ColorAttribute.Emissive|ColorAttribute.Diffuse|ColorAttribute.Ambient|ColorAttribute.Specular);
            material.get(textureAttributes, TextureAttribute.Diffuse|TextureAttribute.Normal| TextureAttribute.Specular);

            for (Attribute attribute : colorAttributes) {
                Log.info("Color Attribute: " + attribute.toString());
                if (attribute instanceof ColorAttribute) {
                    ColorAttribute colorAttribute = (ColorAttribute) attribute;
                    VisLabel label = new VisLabel(colorAttribute.toString());
                    VisLabel value = new VisLabel(colorAttribute.color.toString());
                    table.add(label).left().pad(2);
                    table.add(value).left().pad(2).row();
                }
            }
            for (Attribute attribute : textureAttributes) {
                Log.info("Texture Attribute: " + attribute.toString());
                Texture texture = ((TextureAttribute)attribute).textureDescription.texture;
                TextureAttribute textureAttribute = (TextureAttribute) attribute;
                VisImage image = new VisImage(texture);
                Container container = new Container(image);
                container.center();
                container.maxSize(75);
                String path = textureAttribute.textureDescription.texture.toString();
                VisLabel value = new VisLabel(path.substring(path.lastIndexOf("/") + 1));
                VisLabel label = new VisLabel(textureAttribute.toString());
                table.add(label).left().pad(2).row();
                table.add(value).left().pad(2).row();

                table.add(container).pad(5).row();
                table.pack();



            }
            table.add(new Separator()).expandX().fillX().growX().pad(10).row();
            materialsWindow.add(widget).expandX().fillX().growX().row();

            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    widget.setCollapsed(!widget.isCollapsed());
                }
            });
        }
        materialsExpandAllButton.clearListeners();
        materialsExpandAllButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                for (CollapsibleWidget widget : collapsibleWidgets) {
                    widget.setCollapsed(false);
                }
            }
        });
        materialsCollapseAllButton.clearListeners();
        materialsCollapseAllButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                for (CollapsibleWidget widget : collapsibleWidgets) {
                    widget.setCollapsed(true);
                }
            }
        });
    }
    VisTextButton applyButton;
    VisTextButton resetButton;
    Matrix4 transform;

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
    public void createTransformTable()
    {

        SceneComponent sceneComponent;

        transformWindow.add(new Separator()).expandX().fillX().padTop(5).padBottom(20).colspan(2).row();


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

        applyButton = new VisTextButton("Apply");
        resetButton = new VisTextButton("Reset");

        //flowGroup.addActor(applyButton);

        flowGroup.setWidth(400);
        flowGroup.setFillParent(false);
        //flowGroup.setPosition(0 , 5);


        Table table = new Table() {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (SceneSystem.selectedSceneComponent != null) {
                    SceneComponent sceneComponent = SceneSystem.selectedSceneComponent;
                    if (Context.getInstance().gizmoSystem.translateTool.state!= TranslateTool.TransformState.NONE&&transformWidget.isCollapsed())
                        transformWidget.setCollapsed(false);

                    getTitleLabel().setText("Selection: "+sceneComponent.id);

                    transform = sceneComponent.transform;
                    Vector3 translation = new Vector3();
                    Quaternion rotation = new Quaternion();
                    Vector3 scale = new Vector3();
                    transform.getTranslation(translation);
                    transform.getRotation(rotation);
                    transform.getScale(scale);
                    xField.setText(String.valueOf(translation.x));
                    yField.setText(String.valueOf(translation.y));
                    zField.setText(String.valueOf(translation.z));
                    xRotField.setText(String.valueOf(rotation.x));
                    yRotField.setText(String.valueOf(rotation.y));
                    zRotField.setText(String.valueOf(rotation.z));
                    xScaleField.setText(String.valueOf(scale.x));
                    yScaleField.setText(String.valueOf(scale.y));
                    zScaleField.setText(String.valueOf(scale.z));
                } else {
                    xField.setText("0");
                    yField.setText("0");
                    zField.setText("0");
                    xRotField.setText("0");
                    yRotField.setText("0");
                    zRotField.setText("0");
                    xScaleField.setText("1");
                    yScaleField.setText("1");
                    zScaleField.setText("1");
                    getTitleLabel().setText("Inspector");
                }

            }
        };
        table.align(Align.center);
        table.add(flowGroup).colspan(2).center().row();
        table.setPosition(0 , 0);
        table.sizeBy(300 , 300);
        table.setSize(200 , 300);
        flowGroup.setSpacing(3);
        table.add(applyButton).padTop(10);
        table.add(resetButton).padTop(10).row();

        splitPane = new VisSplitPane(table , infoTable , true);
        splitPane.setSplitAmount(0.5f);
        splitPane.setFillParent(true);
        transformWindow.add(table).expand().fill().center().padLeft(50).padRight(50).row();
        transformWindow.align(Align.center);

        transformWindow.pack();
        pack();


    }

    public void populateTransformTable(SceneComponent component){
        xField.setText(String.valueOf(component.transform.getTranslation(new Vector3()).x));
        yField.setText(String.valueOf(component.transform.getTranslation(new Vector3()).y));
        zField.setText(String.valueOf(component.transform.getTranslation(new Vector3()).z));
        xRotField.setText(String.valueOf(component.transform.getRotation(new Quaternion()).x));
        yRotField.setText(String.valueOf(component.transform.getRotation(new Quaternion()).y));
        zRotField.setText(String.valueOf(component.transform.getRotation(new Quaternion()).z));
        xScaleField.setText(String.valueOf(component.transform.getScale(new Vector3()).x));
        yScaleField.setText(String.valueOf(component.transform.getScale(new Vector3()).y));
        zScaleField.setText(String.valueOf(component.transform.getScale(new Vector3()).z));
    }

    public void setTransformTableListener(SceneComponent component){
        applyButton.clearListeners();
        resetButton.clearListeners();

        applyButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event , com.badlogic.gdx.scenes.scene2d.Actor actor) {
                Vector3 translation = new Vector3(Float.parseFloat(xField.getText()) , Float.parseFloat(yField.getText()) , Float.parseFloat(zField.getText()));
                Quaternion rotation = new Quaternion(Float.parseFloat(xRotField.getText()) , Float.parseFloat(yRotField.getText()) , Float.parseFloat(zRotField.getText()) , 1);
                Vector3 scale = new Vector3(Float.parseFloat(xScaleField.getText()) , Float.parseFloat(yScaleField.getText()) , Float.parseFloat(zScaleField.getText()));
                component.transform.setTranslation(translation);
                component.transform.rotate(rotation);
                component.transform.scale(scale.x, scale.y, scale.z);
            }
        });

        resetButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event , com.badlogic.gdx.scenes.scene2d.Actor actor) {
                component.transform.idt();
            }
        });
    }
}


