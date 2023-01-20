package tests;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import editor.tools.BasePickerTool;
import util.ModelUtils;

public class GizmoTest extends BaseTest

{
    public static Array<ModelInstance> instances = new Array<ModelInstance>();
    public static Array<GameObject> gameObjects = new Array<GameObject>();

    BasePickerTool pickerTool;
    TranslateTool translateTool;
    static public Model outlineModel;
    static public ModelInstance outlineInstance;

    public GizmoTest(Game game) {

        super(game);
        pickerTool = new BasePickerTool(inputMultiplexer,camera);
        translateTool = new TranslateTool(inputMultiplexer,camera,sceneManager);

        createObjects();

    }

    public void createObjects(){
        int amount = 20;
        for(int i = 0; i < amount; i++){
            Model model = ModelUtils.createSphere(1);
            ModelInstance instance = new ModelInstance(model);
            instance.transform.setToTranslation((float) Math.random() * 10 - 5, (float) Math.random() * 10 - 5, (float) Math.random() * 10 - 5);
            instances.add(instance);
            gameObjects.add(new GameObject(instance));
            sceneManager.getRenderableProviders().add(instance);
        }
    }

    @Override
    public void render(float delta) {
        pickerTool.update();

        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        super.render(delta);
        pickerTool.debugDraw();
        translateTool.renderHandles();
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

        update();



    }

    public void update(){
        for (GameObject gameObject : gameObjects) {
            gameObject.update();
        }
    }

    public static class GameObject{
         public Model outlineModel;
         public ModelInstance outlineInstance;
        public  Model model;
         public ModelInstance instance;
        public BoundingBox boundingBox= new BoundingBox();
        public Vector3 position= new Vector3();
        public Matrix4 transform= new Matrix4();
        public boolean selected = false;
        public boolean hovered;

        public GameObject(ModelInstance instance){
            this.instance = instance;
            this.model = instance.model;
            outlineModel = ModelUtils.createOutlineModelAsCopy(model, Color.WHITE, 0.3f);
            outlineInstance = new ModelInstance(outlineModel,instance.transform);
        }

        public void update(){
            instance.transform.getTranslation(position);
            transform.set(instance.transform);
            instance.calculateBoundingBox(boundingBox);
            boundingBox.mul(instance.transform);
        }
    }



}
