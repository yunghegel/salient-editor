package ui;

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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.layout.FlowGroup;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;
import ecs.components.SceneComponent;
import ecs.components.TransformComponent;
import ecs.components.light.*;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import sys.Log;
import sys.io.ResourceRegistry;

public class ObjectTree {

     Table treeTable;
    static Table sceneAssetComponentView;
     Table lightAssetComponentView;
     static TitleNode lightTreeRoot= new TitleNode("Lights");

    static TitleNode componentTreeRoot = new TitleNode("Components");
    static Array<LightComponent> lightComponents = new Array<LightComponent>();
     public static Array<SceneComponent> sceneComponents = new Array<SceneComponent>();

        public static VisTree tree;
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

    public static VisTextButton applyButton = new VisTextButton("Apply");
    public static VisTextButton openButton = new VisTextButton("Open");


    static Array<SceneAsset> sceneAssets = new Array<SceneAsset>();



    static ObjectMap<Class, Object> renderables;
    static ObjectMap<Class, Object> models;
    static Array<SceneComponent> componentArray = new Array<SceneComponent>();
    static Array<LightComponent> lightArray = new Array<LightComponent>();
    static ColorPicker colorPicker;

    public ObjectTree(Table owner, Table treeNodeContentView) {

        this.treeTable = owner;
        this.sceneAssetComponentView = treeNodeContentView;

    }

    public void initListeners(){
        tree.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        }) ;

    }


    public void createTree(Table treeTable,Table treeNodeView) {
        tree = new VisTree();
        tree.add(componentTreeRoot);
        treeTable.add(tree).expand().fill();

        sceneComponentInspectorWindow = new VisWindow("Inspector");
        treeNodeView.add(sceneComponentInspectorWindow).expand().fill();
        windowLabel = new VisLabel("");
        sceneComponentInspectorWindow.add(windowLabel).expand().fill();
        createTransformTable(treeNodeView);

        tree.add(lightTreeRoot);



        TableUtils.setSpacingDefaults(sceneComponentInspectorWindow);
        sceneComponentInspectorWindow.columnDefaults(0).left();

    }

    public VisTree getTree() {
        createTree(treeTable,sceneAssetComponentView);
        return tree;
    }

    public static void addSceneComponentNode(SceneComponent sceneComponent){
        SceneAssetTreeNode node= new SceneAssetTreeNode(sceneComponent);
        componentArray.add(sceneComponent);
        componentTreeRoot.add(node);


        for (SceneComponent component : componentArray) {

        }

        node.getActor().addListener(new ClickListener() {
            public void clicked (InputEvent event, float x, float y) {
                clearTransformValues();
                sceneComponent.selected = true;

                sys.Log.info(sceneComponent.id + " " + sceneComponent.selected + componentArray.size);


                Matrix4 componentTransform = sceneComponent.gltfScene.modelInstance.transform;
                windowLabel.setText(sceneComponent.id);
                xField.setText(String.valueOf(componentTransform.getTranslation(new Vector3()).x));
                yField.setText(String.valueOf(componentTransform.getTranslation(new Vector3()).y));
                zField.setText(String.valueOf(componentTransform.getTranslation(new Vector3()).z));

                Quaternion quaternion = new Quaternion();
                componentTransform.getRotation(quaternion);

                xRotField.setText(String.valueOf(quaternion.x));
                yRotField.setText(String.valueOf(quaternion.y));
                zRotField.setText(String.valueOf(quaternion.z));

                xScaleField.setText(String.valueOf(componentTransform.getScale(new Vector3()).x));

                yScaleField.setText(String.valueOf(componentTransform.getScale(new Vector3()).y));

                zScaleField.setText(String.valueOf(componentTransform.getScale(new Vector3()).z));

                //if we press ESC, deselect the component
                if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    sceneComponent.selected = false;
                }


             //   System.out.println("selected: " + node.getValue().selected);
                //selectedComponent.gltfScene.modelInstance.transform
            }






        });

        node.getActor().addListener(new ClickListener() {
            public void clicked (InputEvent event, float x, float y) {
                if (getTapCount()==2){
                    sceneComponent.selected = false;
                    clearTransformValues();
                }
                sys.Log.info(sceneComponent.id + " " + sceneComponent.selected + componentArray.size); }}
                );


        applyButton.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                if(sceneComponent.selected){

                sceneComponent.gltfScene.modelInstance.transform.setTranslation(Float.parseFloat(xField.getText()),Float.parseFloat(yField.getText()),Float.parseFloat(zField.getText()));
               // sceneComponent.gltfScene.modelInstance.transform.setRotation(Float.parseFloat(xRotField.getText()),Float.parseFloat(yRotField.getText()),Float.parseFloat(zRotField.getText()),1);
                sceneComponent.gltfScene.modelInstance.transform.scl(Float.parseFloat(xScaleField.getText()),Float.parseFloat(yScaleField.getText()),Float.parseFloat(zScaleField.getText()));
                sys.Log.info("Transform applied: \n"+sceneComponent.gltfScene.modelInstance.transform.toString());

            }}
        });


    }

    public static void addLightComponentNode(LightComponent lightComponent){
        if (lightComponent instanceof PointLightComponent){
            PointLightComponent pointLightComponent = (PointLightComponent) lightComponent;
            PointLightTreeNode node = new PointLightTreeNode(pointLightComponent);
            lightArray.add(pointLightComponent);
            lightTreeRoot.add(node);
            node.getActor().addListener(new ClickListener() {
                public void clicked (InputEvent event, float x, float y) {
                    clearTransformValues();
                    lightComponent.selected = true;

                    sys.Log.info(lightComponent.getClass().getSimpleName() + " " + lightComponent.selected + lightArray.size); }
            });

        }

        if (lightComponent instanceof DirectionalLightComponent){
            DirectionalLightComponent directionalLightComponent = (DirectionalLightComponent) lightComponent;
            DirectionalLightTreeNode node = new DirectionalLightTreeNode(directionalLightComponent);
            lightArray.add(directionalLightComponent);
            lightTreeRoot.add(node);
            node.getActor().addListener(new ClickListener() {
                public void clicked (InputEvent event, float x, float y) {
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


                    colorPicker = new ColorPicker("Directional Light Color", new ColorPickerListener() {
                        @Override
                        public void canceled(Color oldColor) {

                        }

                        @Override
                        public void changed(Color newColor) {
                            directionalLightComponent.color = newColor;

                        }

                        @Override
                        public void reset(Color previousColor, Color newColor) {

                        }

                        @Override
                        public void finished(Color newColor) {
                            directionalLightComponent.color = newColor;
                        }
                    });


                    applyButton.addListener(new ClickListener(){
                        public void clicked (InputEvent event, float x, float y) {
                            if(directionalLightComponent.selected){
                                directionalLightComponent.directionalLight.setDirection(Float.parseFloat(xField.getText()),Float.parseFloat(yField.getText()),Float.parseFloat(zField.getText()));
                                directionalLightComponent.directionalLight.setColor(Float.parseFloat(xRotField.getText()),Float.parseFloat(yRotField.getText()),Float.parseFloat(zRotField.getText()),1);

                                sys.Log.info("Transform applied: \n"+directionalLightComponent.direction.toString());
                            }}
                    });





                    sceneAssetComponentView.add(lightComponentInspectorWindow).expand().fill();
                    sceneComponentInspectorWindow.add(openButton);


                    sys.Log.info(lightComponent.getClass().getSimpleName() + " " + directionalLightComponent.selected + lightArray.size); }
            });
        }

        if (lightComponent instanceof SpotLightComponent){
            SpotLightComponent spotLightComponent = (SpotLightComponent) lightComponent;
            SpotLightTreeNode node = new SpotLightTreeNode(spotLightComponent);
            lightArray.add(spotLightComponent);
            lightTreeRoot.add(node);
            node.getActor().addListener(new ClickListener() {
                public void clicked (InputEvent event, float x, float y) {
                    clearTransformValues();
                    spotLightComponent.selected = true;
                    lightComponent.selected = true;
                    colorPicker = new ColorPicker("Directional Light Color", new ColorPickerListener() {
                        @Override
                        public void canceled(Color oldColor) {

                        }

                        @Override
                        public void changed(Color newColor) {
                            spotLightComponent.color = newColor;

                        }

                        @Override
                        public void reset(Color previousColor, Color newColor) {

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

                    applyButton.addListener(new ClickListener(){
                        public void clicked (InputEvent event, float x, float y) {
                            if(spotLightComponent.selected){
                                spotLightComponent.spotLight.setPosition(Float.parseFloat(xField.getText()),Float.parseFloat(yField.getText()),Float.parseFloat(zField.getText()));
                                spotLightComponent.spotLight.setDirection(Float.parseFloat(xRotField.getText()),Float.parseFloat(yRotField.getText()),Float.parseFloat(zRotField.getText()));
                                spotLightComponent.spotLight.setIntensity(Float.parseFloat(xScaleField.getText()));
                            }}
                    });

                    sceneAssetComponentView.add(lightComponentInspectorWindow).expand().fill();
                    sceneComponentInspectorWindow.add(openButton);





                    Log.info(lightComponent.getClass().getSimpleName() + " " + spotLightComponent.selected + lightArray.size); }
            });
        }

        if (lightComponent instanceof ShadowLightComponent) {
            ShadowLightComponent shadowLightComponent = (ShadowLightComponent) lightComponent;
            ShadowLightTreeNode node = new ShadowLightTreeNode(shadowLightComponent);
            lightArray.add(shadowLightComponent);
            lightTreeRoot.add(node);
            node.getActor().addListener(new ClickListener() {
                public void clicked (InputEvent event, float x, float y) {
                    clearTransformValues();
                    shadowLightComponent.selected = true;
                    lightComponent.selected = true;
                    sys.Log.info(lightComponent.getClass().getSimpleName() + " " + shadowLightComponent.selected + lightArray.size);
                }
            });

        }

       }

    public VisTable matrixTransformationTable(){
        VisTable table = new VisTable();
        VisLabel label = new VisLabel("Transform");

        table.add(label).expand().fill();
        return table;
    }


    public static Array<SceneComponent> retrieveArray(){
        Array<SceneComponent> array = ResourceRegistry.getSceneComponents();

        return array;
    }

    public static void clearTransformValues(){



        applyButton.clearListeners();
        sceneComponentInspectorWindow.clear();


        for (SceneComponent sceneComponents : retrieveArray()) {


                sceneComponents.selected = false;


            Log.info("SceneComponent: " + sceneComponents.id + sceneComponents.selected);
        }

        for (LightComponent lightComponents : lightArray) {
            lightComponents.selected = false;

        }
    }



    public static void createTransformTable(Table table){



        xField.setSize(30,10);
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
        translationSplitPane.setWidgets( xField, yField, zField);
        translationSplitPane.setWidth(200);


        MultiSplitPane rotationSplitPane = new MultiSplitPane(false);
        rotationSplitPane.setWidgets( xRotField, yRotField, zRotField);




        MultiSplitPane scaleSplitPane = new MultiSplitPane(false);
        scaleSplitPane.setWidgets( xScaleField, yScaleField, zScaleField);

        FlowGroup flowGroup = new FlowGroup(true);


        flowGroup.addActor(translationLabel);
        flowGroup.addActor(translationSplitPane);
        flowGroup.addActor(rotationLabel);
        flowGroup.addActor(rotationSplitPane);
        flowGroup.addActor(scaleLabel);
        flowGroup.addActor(scaleSplitPane);
        flowGroup.addActor(applyButton);
        flowGroup.addActor(openButton);

        table.row();
        table.add(flowGroup).expand().fill();
        table.row();

            applyButton = new VisTextButton("Apply");

            applyButton.addListener(new ClickListener(){
                public void clicked (InputEvent event, float x, float y) {
                    SceneComponent sceneComponent = (SceneComponent)tree.getSelectedNode().getValue();
                    sceneComponent.gltfScene.modelInstance.transform.setTranslation(Float.parseFloat(xField.getText()),Float.parseFloat(yField.getText()),Float.parseFloat(zField.getText()));
                    sceneComponent.gltfScene.modelInstance.transform.rotate(Float.parseFloat(xRotField.getText()),Float.parseFloat(yRotField.getText()),Float.parseFloat(zRotField.getText()),1);
                    sceneComponent.gltfScene.modelInstance.transform.setToScaling(Float.parseFloat(xScaleField.getText()),Float.parseFloat(yScaleField.getText()),Float.parseFloat(zScaleField.getText()));
                    Log.info("Transform applied"+sceneComponent.transform.toString());
                    System.out.println("clicked");
                }
            });

            openButton = new VisTextButton("Open");
            openButton.addListener(new ClickListener(){
                public void clicked (InputEvent event, float x, float y) {

                }
            });


        }

    public static void createLightTable(Table table){


    }





    public void setTransformTable(SceneComponent component){
        VisTable table = new VisTable();
        table.add(matrixTransformationTable()).expand().fill();

        sceneComponentInspectorWindow.add(table).expand().fill();
    }

    public static class SceneAssetTreeNode extends Tree.Node<VisTree.Node, SceneComponent,VisLabel> {

        public SceneAssetTreeNode(SceneComponent value) {
            super(new VisLabel(value.id));

        }

    }

    public static class TitleNode extends Tree.Node<VisTree.Node, String, VisLabel> {
        public TitleNode(String value) {
            super(new VisLabel(value));
        }
    }

    public static class LightingTreeNode extends Tree.Node<VisTree.Node, LightComponent, VisLabel> {
        public LightingTreeNode(LightComponent value) {

            super(new VisLabel(value.getClass().getSimpleName()));

            }


        }

    public static class PointLightTreeNode extends Tree.Node<VisTree.Node, PointLightComponent, VisLabel> {
        public PointLightTreeNode(PointLightComponent value) {

            super(new VisLabel(value.getClass().getSimpleName()));

            }



        }

    public static class TransformTreeNode extends Tree.Node<VisTree.Node, TransformComponent, VisLabel> {
        public TransformTreeNode(TransformComponent value) {

            super(new VisLabel(value.getClass().getSimpleName()));

            }



        }


    public static class SpotLightTreeNode extends Tree.Node<VisTree.Node, SpotLightComponent, VisLabel> {
        public SpotLightTreeNode(SpotLightComponent value) {

            super(new VisLabel(value.getClass().getSimpleName()));

            }



        }


    public static class DirectionalLightTreeNode extends Tree.Node<VisTree.Node, DirectionalLightComponent, VisLabel> {
        public DirectionalLightTreeNode(DirectionalLightComponent value) {

            super(new VisLabel(value.getClass().getSimpleName()));

            }
        }


        public static class ShadowLightTreeNode extends Tree.Node<VisTree.Node, ShadowLightComponent, VisLabel> {
        public ShadowLightTreeNode(ShadowLightComponent value) {

            super(new VisLabel(value.getClass().getSimpleName()));

            }
        }
    }



