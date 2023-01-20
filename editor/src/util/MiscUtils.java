package util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.util.highlight.Highlighter;

public class MiscUtils
{

    private static final Vector3 tmpVec = new Vector3();

    /**
     * Returns a vector which represents the direction a given matrix is facing.
     *
     * @param transform - in
     * @param out       - out
     */

    public static void getDirection(Matrix4 transform , Vector3 out) {
        tmpVec.set(Vector3.Z);
        out.set(tmpVec.rot(transform).nor());
    }

    /**
     * Gets the world position of modelInstance and sets it on the out vector
     *
     * @param transform modelInstance transform
     * @param out       out vector to be populated with position
     */
    public static void getPosition(Matrix4 transform , Vector3 out) {
        transform.getTranslation(out);
    }

    /**
     * Gets the world position of modelInstance and sets it on the out vector
     *
     * @param transform modelInstance transform
     * @param out       out vector to be populated with position
     */
    public static void getWorldCoordinates(Matrix4 transform , Vector3 out) {
        transform.getTranslation(out);
    }

    public static Color getRandomColor() {
        Array<Color> colors = new Array<>();
        colors.add(Color.BLACK);
        colors.add(Color.BLUE);
        colors.add(Color.CYAN);
        colors.add(Color.GOLD);
        colors.add(Color.FIREBRICK);
        int r = MathUtils.random(0 , 255);
        int g = MathUtils.random(0 , 255);
        int b = MathUtils.random(0 , 255);
        //return a random color
        return new Color(r , g , b , 1f);
    }

    public static Vector3 getTranslation (Matrix4 worldTransform, Vector3 center, Vector3 output) {
        if (center.isZero()) worldTransform.getTranslation(output);
        else if (!worldTransform.hasRotationOrScaling()) worldTransform.getTranslation(output).add(center);
        else output.set(center).mul(worldTransform);
        return output;
    }

    public static boolean isInsideViewport(int screenX, int screenY, Stage stage) {
        return screenX >= 0 && screenY >= 0 && screenX < stage.getCamera().viewportWidth && screenY < stage.getCamera().viewportHeight;
    }

    public static boolean isInsideViewport(int screenX, int screenY, Stage stage, int padding) {
        return screenX >= padding && screenY >= padding && screenX < stage.getCamera().viewportWidth - padding && screenY < stage.getCamera().viewportHeight - padding;
    }

    public static boolean isInsideViewport(int screenX, int screenY, Viewport viewport) {
        return screenX >= 0 && screenY >= 0 && screenX < viewport.getCamera().viewportWidth && screenY < viewport.getCamera().viewportHeight;
    }

    public static Highlighter createHighlighter(){

    Highlighter highlighter = new Highlighter();
        highlighter.regex(Color.valueOf("EFC090"), "\\b(foo|bar|lowp|mediump|highp)\\b");
        highlighter.regex(Color.SKY, "\\b(class|private|protected|public|if|else|void|precision)\\b");
        highlighter.regex(Color.valueOf("A6E22E"), "\\b(if|else|void|vec2|vec3|vec4|mat2|mat3|mat4|sampler2D)\\b");
        highlighter.regex(Color.MAGENTA, "\\b(ModelInstance|Model|Cache|Mesh|Scene|Texture|SceneAsset|SceneModel|Pixmap|TextureRegion|Skybox|SceneSkybox|Animation|Material|Meshes|Materials|Node|NodePart|Node|ModelBatch|Renderable|RenderableProvider)\\b");
        highlighter.regex(Color.PURPLE, "\\b(model|instance|cache|mesh|material|materials|textures|texture|pixmap|mdl|node|scene|sceneModel|sceneAsset|nodes|nodePart|meshes|models|scene|modelInstance|instance|vertices|indices|vertex|point|renderable|renderableProvider)\\b");
     //   highlighter.regex(Color.valueOf("75715E"), "/\\*(.|[\\r\\n])*?\\*/"); //block comments (/* comment */)
       // highlighter.regex(Color.valueOf("75715E"), "(\\/\\/.*)[\\n]"); //line comments (// comment)
        highlighter.regex(Color.valueOf("75F85E"), "(\\#.*)"); //macro (#macro)
        highlighter.regex(Color.ORANGE, "\\[CONSOLE\\]");
        highlighter.regex(Color.valueOf("F92672"), "\\b(Bullet|btRigidBody|btMotionState|btCollisionObject|BoundingBox|Quaternion|Physics|Bullet|MotionState|Intertia|Mass|btCollisionShape|Ray|Intersection)\\b");
        highlighter.regex(Color.ROYAL, "\\b(shape|rigidBody|bullet|bullet|body|rigid|collision|ray|test|dynamics|static|dynamic|ray|center|normal|vector|tmp|vec3|intersection|plane|motionState|boundingBox|position|transform|rotation|scale|quaternion|physics|intertia)\\b");

        highlighter.regex(Color.valueOf("66CCB3"), "\\b(Create|create|Destroy|destroy|Dispose|dispose|Manage|manage|Loaded|loaded|Save|save|Saved|saved|added|removed|Added|Removed|Creating|creating|Intializing|initializing|adding|Adding)\\b");
        highlighter.regex(Color.valueOf("66CCB3"), "\\b(Initialize|initialize|Init|init|Load|load|Loaded|loaded|Save|save|Saved|saved|added|removed|Added|Removed|Creating|creating|Intializing|initializing|adding|Adding)\\b");
        //Regex which highlights Json braces
        //Regex which highlights Json keys
        highlighter.regex(Color.CORAL, "\\b(String|int|long|float|double|boolean|short|Array|List|Map)\\b");
        highlighter.regex(Color.GOLD, "\\b(Vector3|Matrix4|Vector2)\\b");
        highlighter.regex(Color.valueOf("F92672"), "[{.*}]");
        //Regex which highlights Json values
        highlighter.regex(Color.valueOf("66CCB3"), "[\\[.*\\]]");
        //Regex which highlights Json numbers
       highlighter.regex(Color.valueOf("A6E22E"), "\\b(\\d+)\\b");
       //highlight quotations
        highlighter.regex(Color.VIOLET, "[\"]");
        //highlight commas only
        highlighter.regex(Color.GREEN, "[,]");
        //highlight colon only
        highlighter.regex(Color.OLIVE, "[:]");
        return highlighter;
    }

public static Vector3 getRandomVector3(float min, float max) {
        Vector3 vector3 = new Vector3();
        vector3.x = MathUtils.random(min, max);
        vector3.y = MathUtils.random(min, max);
        vector3.z = MathUtils.random(min, max);
        return vector3;
}

public static float getRandomFloat(float min, float max) {
        return MathUtils.random(min, max);
}
        //Regex which highlights json brackets




}

