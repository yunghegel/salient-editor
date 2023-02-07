package ui.scene;

import backend.data.ObjectRegistry;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.layout.FlowGroup;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;
import core.components.SceneComponent;
import core.components.TransformComponent;
import core.components.light.SpotLightComponent;
import core.systems.GizmoSystem;
import core.systems.SceneSystem;
import editor.Context;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import backend.tools.Log;
import ui.UserInterface;
import ui.elements.TransformWindow;

public class ObjectTree
{

    public static Array<SceneComponent> sceneComponents = new Array<SceneComponent>();
    public static VisList<String> sceneComponentList = new VisList<String>();
    public static VisTree tree;
    public static VisTextButton applyButton = new VisTextButton("Apply");
    public static VisTextButton resetButton = new VisTextButton("Reset");
    public static VisTextButton openButton = new VisTextButton("Open");
    static TransformWindow transformWindow;
    static Table sceneAssetComponentView;
    static TitleNode lightTreeRoot = new TitleNode("Lights");
    static TitleNode componentTreeRoot = new TitleNode("Components");
    static Array<core.components.light.LightComponent> lightComponents = new Array<core.components.light.LightComponent>();
    static VisWindow sceneComponentInspectorWindow;
    static VisWindow lightComponentInspectorWindow;
    static VisLabel windowLabel;
    static VisTree.Node root;
    static VisLabel translationLabel = new VisLabel("Translation");
    static VisTextField xField = new VisTextField();
    static VisTextField yField = new VisTextField();
    static VisTextField zField = new VisTextField();
    static VisLabel rotationLabel = new VisLabel("Rotation");
    static VisTextField xRotField = new VisTextField();
    static VisTextField yRotField = new VisTextField();
    static VisTextField zRotField = new VisTextField();
    static VisLabel scaleLabel = new VisLabel("Scale");
    static VisTextField xScaleField = new VisTextField();
    static VisTextField yScaleField = new VisTextField();
    static VisTextField zScaleField = new VisTextField();
    static Array<SceneAsset> sceneAssets = new Array<SceneAsset>();
    static ObjectMap<Class, Object> renderables;
    static ObjectMap<Class, Object> models;
    static Array<SceneComponent> componentArray = new Array<SceneComponent>();
    static Array<core.components.light.LightComponent> lightArray = new Array<core.components.light.LightComponent>();
    static ColorPicker colorPicker;
    Table treeTable;
    Table lightAssetComponentView;
    Table componentInspectorTable;

    public ObjectTree(Table owner , Table treeNodeContentView) {

        this.treeTable = owner;
        sceneAssetComponentView = treeNodeContentView;

    }

    public static void addSceneComponentNode(SceneComponent sceneComponent) {
        SceneAssetTreeNode node = new SceneAssetTreeNode(sceneComponent);
        componentArray.add(sceneComponent);
        String properties = sceneComponent.getProperties();

        componentTreeRoot.add(node);

        for (SceneComponent component : componentArray) {

        }

        node.getActor().addListener(new ClickListener()
        {
            public void clicked(InputEvent event , float x , float y) {
                clearTransformValues();
                sceneComponent.selected = true;
                SceneSystem.selectedSceneComponent = sceneComponent;
                Context.getInstance().objectPickingSystem.setSelectedObject(sceneComponent);

                Log.info(sceneComponent.id + " " + sceneComponent.selected + componentArray.size);

                sceneComponentList.setItems(properties);
                Matrix4 componentTransform = sceneComponent.scene.modelInstance.transform;
                Quaternion quaternion = new Quaternion();
                componentTransform.getRotation(quaternion);

                transformWindow.setComponent(sceneComponent);

                transformWindow.populateFields();
                setWindowTransformValues(sceneComponent);
                //

                //if we press ESC, deselect the component
                if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    sceneComponent.selected = false;
                }

                Context.getInstance().objectPickingSystem.setSelectedObject(sceneComponent);

                //   System.out.println("selected: " + node.getValue().selected);
                //selectedComponent.gltfScene.modelInstance.transform
            }

        });

        node.getActor().addListener(new ClickListener()
        {
            public void clicked(InputEvent event , float x , float y) {
                if (getTapCount() == 2) {
                    sceneComponent.selected = false;
                    SceneSystem.selectedSceneComponent = null;
                    GizmoSystem.selectedComponent.setText("Selected: " + "None");
                    clearTransformValues();
                }

            }
        });

        applyButton.addListener(new ClickListener()
        {
            public void clicked(InputEvent event , float x , float y) {
                if (sceneComponent.selected) {

                    SceneSystem.selectedSceneComponent = sceneComponent;
                    Matrix4 transform = transformWindow.getTransformFromFields();
                    sceneComponent.scene.modelInstance.transform.set(transform);
                    Log.info("Transform applied: \n" + sceneComponent.scene.modelInstance.transform.toString());
                    //                SceneSystem.selectedSceneComponent.scene.modelInstance.transform.setTranslation(Float.parseFloat(xField.getText()), Float.parseFloat(yField.getText()), Float.parseFloat(zField.getText()));

                }
            }
        });

        resetButton.addListener(new ClickListener()
        {

            @Override
            public void clicked(InputEvent event , float x , float y) {
                super.clicked(event , x , y);
                SceneSystem.selectedSceneComponent.scene.modelInstance.transform.set(new Matrix4());
                Log.info("Transform applied: \n" + sceneComponent.scene.modelInstance.transform.toString());

            }
        });

    }

    public static void clearTransformValues() {

        //applyButton.clearListeners();
        //sceneComponentInspectorWindow.clear();

        for (SceneComponent sceneComponents : retrieveArray()) {

            sceneComponents.selected = false;

        }

        for (core.components.light.LightComponent lightComponents : lightArray) {
            lightComponents.selected = false;

        }
    }

    public static void setWindowTransformValues(SceneComponent sceneComponent) {
        Matrix4 transform = sceneComponent.scene.modelInstance.transform;
        Vector3 translation = new Vector3();
        transform.getTranslation(translation);
        xField.setText(String.valueOf(translation.x));
        yField.setText(String.valueOf(translation.y));
        zField.setText(String.valueOf(translation.z));

        Quaternion rotation = new Quaternion();
        transform.getRotation(rotation);
        xRotField.setText(String.valueOf(rotation.x));
        yRotField.setText(String.valueOf(rotation.y));
        zRotField.setText(String.valueOf(rotation.z));

        Vector3 scale = new Vector3();
        transform.getScale(scale);
        xScaleField.setText(String.valueOf(scale.x));
        yScaleField.setText(String.valueOf(scale.y));
        zScaleField.setText(String.valueOf(scale.z));



    }

    public static Array<SceneComponent> retrieveArray() {
        Array<SceneComponent> array = ObjectRegistry.getSceneComponents();

        return array;
    }

    public static void addLightComponentNode(core.components.light.LightComponent lightComponent) {
        if (lightComponent instanceof core.components.light.PointLightComponent) {
            core.components.light.PointLightComponent pointLightComponent = (core.components.light.PointLightComponent) lightComponent;
            PointLightTreeNode node = new PointLightTreeNode(pointLightComponent);
            lightArray.add(pointLightComponent);
            lightTreeRoot.add(node);
            node.getActor().addListener(new ClickListener()
            {
                public void clicked(InputEvent event , float x , float y) {
                    clearTransformValues();
                    lightComponent.selected = true;

                    Log.info(lightComponent.getClass().getSimpleName() + " selected...");
                }
            });

        }

        if (lightComponent instanceof core.components.light.DirectionalLightComponent) {
            core.components.light.DirectionalLightComponent directionalLightComponent = (core.components.light.DirectionalLightComponent) lightComponent;
            DirectionalLightTreeNode node = new DirectionalLightTreeNode(directionalLightComponent);
            lightArray.add(directionalLightComponent);
            lightTreeRoot.add(node);
            node.getActor().addListener(new ClickListener()
            {
                public void clicked(InputEvent event , float x , float y) {
                    clearTransformValues();
                    translationLabel.setText("Direction");
                    rotationLabel.setText("Color");
                    scaleLabel.setText("Intensity");

                    lightComponent.selected = true;

                    directionalLightComponent.selected = true;

                    xField.setText(String.valueOf(directionalLightComponent.direction.x));
                    yField.setText(String.valueOf(directionalLightComponent.direction.y));
                    zField.setText(String.valueOf(directionalLightComponent.direction.z));

                    xRotField.setText(String.valueOf(directionalLightComponent.color.r));
                    yRotField.setText(String.valueOf(directionalLightComponent.color.g));
                    zRotField.setText(String.valueOf(directionalLightComponent.color.b));

                    xScaleField.setVisible(false);
                    yScaleField.setVisible(false);
                    zScaleField.setVisible(false);

                    colorPicker = new ColorPicker("Directional Light Color" , new ColorPickerListener()
                    {
                        @Override
                        public void canceled(Color oldColor) {

                        }

                        @Override
                        public void changed(Color newColor) {
                            directionalLightComponent.color = newColor;

                        }

                        @Override
                        public void reset(Color previousColor , Color newColor) {

                        }

                        @Override
                        public void finished(Color newColor) {
                            directionalLightComponent.color = newColor;
                        }
                    });

                    applyButton.addListener(new ClickListener()
                    {
                        public void clicked(InputEvent event , float x , float y) {
                            if (directionalLightComponent.selected) {
                                directionalLightComponent.directionalLight.setDirection(Float.parseFloat(xField.getText()) , Float.parseFloat(yField.getText()) , Float.parseFloat(zField.getText()));
                                directionalLightComponent.directionalLight.setColor(Float.parseFloat(xRotField.getText()) , Float.parseFloat(yRotField.getText()) , Float.parseFloat(zRotField.getText()) , 1);

                                Log.info("Transform applied: \n" + directionalLightComponent.direction.toString());
                            }
                        }
                    });

                    sceneAssetComponentView.add(lightComponentInspectorWindow).expand().fill();
                    sceneComponentInspectorWindow.add(openButton);

                    Log.info(lightComponent.getClass().getSimpleName() + " " + directionalLightComponent.selected + lightArray.size);
                }
            });
        }

        if (lightComponent instanceof core.components.light.SpotLightComponent) {
            core.components.light.SpotLightComponent spotLightComponent = (core.components.light.SpotLightComponent) lightComponent;
            SpotLightTreeNode node = new SpotLightTreeNode(spotLightComponent);
            lightArray.add(spotLightComponent);
            lightTreeRoot.add(node);
            node.getActor().addListener(new ClickListener()
            {
                public void clicked(InputEvent event , float x , float y) {
                    clearTransformValues();
                    spotLightComponent.selected = true;
                    lightComponent.selected = true;
                    colorPicker = new ColorPicker("Directional Light Color" , new ColorPickerListener()
                    {
                        @Override
                        public void canceled(Color oldColor) {

                        }

                        @Override
                        public void changed(Color newColor) {
                            spotLightComponent.color = newColor;

                        }

                        @Override
                        public void reset(Color previousColor , Color newColor) {

                        }

                        @Override
                        public void finished(Color newColor) {
                            spotLightComponent.color = newColor;
                        }
                    });

                    spotLightComponent.selected = true;
                    xField.setText(String.valueOf(spotLightComponent.position.x));
                    yField.setText(String.valueOf(spotLightComponent.position.y));
                    zField.setText(String.valueOf(spotLightComponent.position.z));

                    xRotField.setText(String.valueOf(spotLightComponent.direction.x));
                    yRotField.setText(String.valueOf(spotLightComponent.direction.y));
                    zRotField.setText(String.valueOf(spotLightComponent.direction.z));

                    xScaleField.setText(String.valueOf(spotLightComponent.intensity));

                    translationLabel.setText("Position");
                    rotationLabel.setText("Direction");
                    scaleLabel.setText("Intensity");

                    yScaleField.setVisible(false);
                    zScaleField.setVisible(false);

                    applyButton.addListener(new ClickListener()
                    {
                        public void clicked(InputEvent event , float x , float y) {
                            if (spotLightComponent.selected) {
                                spotLightComponent.light.setPosition(Float.parseFloat(xField.getText()) , Float.parseFloat(yField.getText()) , Float.parseFloat(zField.getText()));
                                spotLightComponent.light.setDirection(Float.parseFloat(xRotField.getText()) , Float.parseFloat(yRotField.getText()) , Float.parseFloat(zRotField.getText()));
                                spotLightComponent.light.setIntensity(Float.parseFloat(xScaleField.getText()));
                            }
                        }
                    });

                    sceneAssetComponentView.add(lightComponentInspectorWindow).expand().fill();
                    sceneComponentInspectorWindow.add(openButton);

                    Log.info(lightComponent.getClass().getSimpleName() + " selected...");
                }
            });
        }

        if (lightComponent instanceof core.components.light.ShadowLightComponent) {
            core.components.light.ShadowLightComponent shadowLightComponent = (core.components.light.ShadowLightComponent) lightComponent;
            ShadowLightTreeNode node = new ShadowLightTreeNode(shadowLightComponent);
            lightArray.add(shadowLightComponent);
            lightTreeRoot.add(node);
            node.getActor().addListener(new ClickListener()
            {
                public void clicked(InputEvent event , float x , float y) {
                    clearTransformValues();
                    shadowLightComponent.selected = true;
                    lightComponent.selected = true;
                    Log.info(lightComponent.getClass().getSimpleName() + " " + shadowLightComponent.selected + lightArray.size);
                }
            });

        }

    }

    public static void createTransformTable(Table table) {

        xField.setSize(30 , 10);
        yField.setWidth(30);
        zField.setWidth(30);

        xRotField.setWidth(25);
        yRotField.setWidth(25);
        zRotField.setWidth(25);

        xScaleField.setWidth(25);
        yScaleField.setWidth(25);
        zScaleField.setWidth(25);

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
        translationSplitPane.setWidgets(xField , yField , zField);
        translationSplitPane.setWidth(200);

        MultiSplitPane rotationSplitPane = new MultiSplitPane(false);
        rotationSplitPane.setWidgets(xRotField , yRotField , zRotField);

        MultiSplitPane scaleSplitPane = new MultiSplitPane(false);
        scaleSplitPane.setWidgets(xScaleField , yScaleField , zScaleField);

        FlowGroup flowGroup = new FlowGroup(true);

        flowGroup.addActor(translationLabel);
        flowGroup.addActor(translationSplitPane);
        flowGroup.addActor(rotationLabel);
        flowGroup.addActor(rotationSplitPane);
        flowGroup.addActor(scaleLabel);
        flowGroup.addActor(scaleSplitPane);
        //        flowGroup.addActor(applyButton);
        //        flowGroup.addActor(resetButton);
        flowGroup.setSpacing(5);
        flowGroup.setVertical(true);

        table.row();
        table.add(flowGroup).expand().fill().row();
        table.add(applyButton).align(Align.center).pad(10).row();
        table.add(resetButton);
        table.row();
        table.pack();

        applyButton = new VisTextButton("Apply");

        applyButton.addListener(new ClickListener()
        {
            public void clicked(InputEvent event , float x , float y) {
                SceneComponent sceneComponent = (SceneComponent) tree.getSelectedNode().getValue();
                sceneComponent.scene.modelInstance.transform.setTranslation(Float.parseFloat(xField.getText()) , Float.parseFloat(yField.getText()) , Float.parseFloat(zField.getText()));
                sceneComponent.scene.modelInstance.transform.rotate(Float.parseFloat(xRotField.getText()) , Float.parseFloat(yRotField.getText()) , Float.parseFloat(zRotField.getText()) , 1);
                sceneComponent.scene.modelInstance.transform.setToScaling(Float.parseFloat(xScaleField.getText()) , Float.parseFloat(yScaleField.getText()) , Float.parseFloat(zScaleField.getText()));
                Log.info("Transform applied" + sceneComponent.model.transform.toString());
                System.out.println("clicked");
            }
        });

        resetButton = new VisTextButton("Reset");
        resetButton.addListener(new ClickListener()
        {
            public void clicked(InputEvent event , float x , float y) {
                SceneComponent sceneComponent = (SceneComponent) tree.getSelectedNode().getValue();
                if (sceneComponent != null) {

                    SceneSystem.selectedSceneComponent = sceneComponent;
                    sceneComponent.scene.modelInstance.transform.setTranslation(0 , 0 , 0);
                    sceneComponent.scene.modelInstance.transform.rotate(0 , 0 , 0 , 0);
                    sceneComponent.scene.modelInstance.transform.setToScaling(1 , 1 , 1);

                    Log.info("Transform reset for component " + sceneComponent.model.transform.toString());
                    System.out.println("clicked");
                }
            }
        });
        openButton = new VisTextButton("Open");
        openButton.addListener(new ClickListener()
        {
            public void clicked(InputEvent event , float x , float y) {

            }
        });

    }

    public void initListeners() {
        tree.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event , Actor actor) {

            }
        });

    }

    public VisTree getTree() {
        createTree(treeTable , sceneAssetComponentView);
        return tree;
    }

    public void createTree(Table treeTable , Table treeNodeView) {
        tree = new VisTree();
        tree.expandAll();
        tree.add(componentTreeRoot);
        tree.add(lightTreeRoot);

        treeTable.add(tree).expand().fill().row();
        transformWindow = new TransformWindow(UserInterface.componentInspectorWindow , new SceneComponent("null"));
        treeNodeView.setFillParent(true);

    }

    public void setExpanded() {
        tree.expandAll();
    }

    public void setSelected() {
    }

    public VisTable matrixTransformationTable() {
        VisTable table = new VisTable();
        VisLabel label = new VisLabel("Transform");

        table.add(label).expand().fill();
        return table;
    }

    public void createFooter() {
        VisTable footer = new VisTable();
        VisTextButton clearSelectionButton = new VisTextButton("Clear Selection");

    }

    public static class SceneAssetTreeNode extends Tree.Node<VisTree.Node, SceneComponent, VisLabel>
    {

        public SceneAssetTreeNode(SceneComponent value) {
            super(new VisLabel(value.id));

        }

    }

    public static class TitleNode extends Tree.Node<VisTree.Node, String, VisLabel>
    {

        public TitleNode(String value) {
            super(new VisLabel(value));
        }

    }

    public static class LightingTreeNode extends Tree.Node<VisTree.Node, core.components.light.LightComponent, VisLabel>
    {

        public LightingTreeNode(core.components.light.LightComponent value) {

            super(new VisLabel(value.getClass().getSimpleName()));

        }

    }

    public static class PointLightTreeNode extends Tree.Node<VisTree.Node, core.components.light.PointLightComponent, VisLabel>
    {

        public PointLightTreeNode(core.components.light.PointLightComponent value) {

            super(new VisLabel(value.getClass().getSimpleName()));

        }

    }

    public static class TransformTreeNode extends Tree.Node<VisTree.Node, TransformComponent, VisLabel>
    {

        public TransformTreeNode(TransformComponent value) {

            super(new VisLabel(value.getClass().getSimpleName()));

        }

    }

    public static class SpotLightTreeNode extends Tree.Node<VisTree.Node, core.components.light.SpotLightComponent, VisLabel>
    {

        public SpotLightTreeNode(SpotLightComponent value) {

            super(new VisLabel(value.getClass().getSimpleName()));

        }

    }

    public static class DirectionalLightTreeNode extends Tree.Node<VisTree.Node, core.components.light.DirectionalLightComponent, VisLabel>
    {

        public DirectionalLightTreeNode(core.components.light.DirectionalLightComponent value) {

            super(new VisLabel(value.getClass().getSimpleName()));

        }

    }

    public static class ShadowLightTreeNode extends Tree.Node<VisTree.Node, core.components.light.ShadowLightComponent, VisLabel>
    {

        public ShadowLightTreeNode(core.components.light.ShadowLightComponent value) {

            super(new VisLabel(value.getClass().getSimpleName()));

        }

    }

}



