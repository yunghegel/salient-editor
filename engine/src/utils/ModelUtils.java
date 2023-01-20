package utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModelUtils {

    public static final Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.PURPLE, Color.ORANGE, Color.BROWN, Color.PINK, Color.LIME, Color.TEAL, Color.NAVY, Color.MAROON, Color.OLIVE, Color.GRAY, Color.LIGHT_GRAY, Color.DARK_GRAY, Color.WHITE, Color.BLACK};


    public static ModelInstance createAxes() {
        final float GRID_MIN = -1000f;
        final float GRID_MAX = 1000f;
        final float GRID_STEP = 2.5f;
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        Material material = new Material();

        MeshPartBuilder builder = modelBuilder.part("grid", GL20.GL_LINES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, new Material());
        builder.setColor(Color.LIGHT_GRAY);
        for (float t = GRID_MIN; t <= GRID_MAX; t += GRID_STEP) {
            builder.line(t, 0, GRID_MIN, t, 0, GRID_MAX);
            builder.line(GRID_MIN, 0, t, GRID_MAX, 0, t);
        }
        builder = modelBuilder.part("axes", GL20.GL_LINES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, new Material());
        builder.setColor(Color.RED);
        builder.line(0, .1f, 0, 100, 0, 0);
        builder.setColor(Color.GREEN);
        builder.line(0, .1f, 0, 0, 100, 0);
        builder.setColor(Color.BLUE);
        builder.line(0, .1f, 0, 0, 0, 100);


        Model axesModel = modelBuilder.end();
        createAlphaAttribute(axesModel,1.f);
        ModelInstance axesInstance = new ModelInstance(axesModel);

        return axesInstance;
    }
    public static ModelInstance createRayModelInstance(Vector3 origin, Vector3 direction, float length, Color color){
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        Material material = new Material(PBRColorAttribute.createDiffuse(color));
        MeshPartBuilder builder = modelBuilder.part("ray", GL20.GL_LINES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, material);
        builder.setColor(color);

        builder.line(origin, direction.scl(length).add(origin));
        Model model = modelBuilder.end();
        return new ModelInstance(model);
    }
    public static ModelInstance createBoundingBoxRenderable(BoundingBox boundingBox) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("BoundingBox", GL20.GL_LINES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, new Material());
        builder.setColor(Color.RED);
        BoxShapeBuilder.build(builder, boundingBox);
        Model boundingBoxModel = modelBuilder.end();

        return new ModelInstance(boundingBoxModel);
    }
    public static ModelInstance createFloor(int width, int height, int depth) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();


        MeshPartBuilder meshBuilder = modelBuilder.part("floor", GL20.GL_TRIANGLES, VertexAttribute.Position().usage |VertexAttribute.Normal().usage | VertexAttribute.TexCoords(0).usage, new Material());

        BoxShapeBuilder.build(meshBuilder, width, height, depth);
        Model floor = modelBuilder.end();

        ModelInstance floorInstance = new ModelInstance(floor);
        floorInstance.transform.trn(0, -0.5f, 0f);

        return floorInstance;
    }

    public static ModelInstance createPlane(int width, int height, int depth)
        {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        BoundingBox boundingBox = new BoundingBox();
        boundingBox.set(new Vector3(-width/2, -height/2, -depth/2), new Vector3(width/2, height/2, depth/2));
        MeshPartBuilder meshBuilder = modelBuilder.part("plane", GL20.GL_TRIANGLES, VertexAttribute.Position().usage |VertexAttribute.Normal().usage | VertexAttribute.TexCoords(0).usage, new Material());
        BoxShapeBuilder.build(meshBuilder,boundingBox);


        Model plane = modelBuilder.end();
        ModelInstance planeInstance = new ModelInstance(plane);

        return planeInstance;

        }

    public static void animateModelInstancePosition(ModelInstance modelInstance, Vector3 start, Vector3 end, float alpha){
        modelInstance.transform.setTranslation(lerp(start, end, alpha));
    }
    public static Vector3 lerp(Vector3 start, Vector3 end, float alpha){
        return start.cpy().lerp(end, alpha);
    }

    public static Model createArrowStub(Material mat, Vector3 from, Vector3 to) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder meshBuilder;
        // line
        meshBuilder = modelBuilder.part("line", GL20.GL_LINES,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked |VertexAttributes.Usage.Normal, mat);
        meshBuilder.line(from.x, from.y, from.z, to.x, to.y, to.z);
        // stub
        Node node = modelBuilder.node();
        node.translation.set(to.x, to.y, to.z);
        meshBuilder = modelBuilder.part("stub", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position, mat);
        BoxShapeBuilder.build(meshBuilder, 2, 2, 2);
        return modelBuilder.end();
    }
    public static void fatten(Model model, float amount) {
        Vector3 pos = new Vector3();
        Vector3 nor = new Vector3();
        for (Node node : model.nodes) {
            for (NodePart n : node.parts) {
                Mesh mesh = n.meshPart.mesh;
                FloatBuffer buf = mesh.getVerticesBuffer();
                int lastFloat = mesh.getNumVertices() * mesh.getVertexSize() / 4;
                int vertexFloats = (mesh.getVertexSize() / 4);
                VertexAttribute posAttr = mesh.getVertexAttributes().findByUsage(VertexAttributes.Usage.Position);
                VertexAttribute norAttr = mesh.getVertexAttributes().findByUsage(VertexAttributes.Usage.Normal);
                if (posAttr == null || norAttr == null) {
                    throw new IllegalArgumentException("Position/normal vertex attribute not found");
                }
                int pOff = posAttr.offset / 4;
                int nOff = norAttr.offset / 4;

                for (int i = 0; i < lastFloat; i += vertexFloats) {
                    pos.x = buf.get(pOff + i);
                    pos.y = buf.get(pOff + i + 1);
                    pos.z = buf.get(pOff + i + 2);

                    nor.x = buf.get(nOff + i);
                    nor.y = buf.get(nOff + i + 1);
                    nor.z = buf.get(nOff + i + 2);

                    nor.nor().scl(amount);

                    buf.put(pOff + i, pos.x + nor.x);
                    buf.put(pOff + i + 1, pos.y + nor.y);
                    buf.put(pOff + i + 2, pos.z + nor.z);
                }
            }
        }
    }

    public static void createOutlineModel(Model model, Color outlineColor, float fattenAmount) {

        fatten(model, fattenAmount);
        for (Material m : model.materials) {
            m.clear();

            m.set(new IntAttribute(IntAttribute.CullFace, Gdx.gl.GL_FRONT));
            m.set(PBRColorAttribute.createBaseColorFactor(outlineColor));
            m.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
            m.set(new BlendingAttribute(0.5f));
        }
    }



    public static void createAlphaAttribute(Model model,float alpha){
        for (Material m : model.materials) {
            m.set(new IntAttribute(IntAttribute.CullFace, Gdx.gl.GL_FRONT));
            m.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
            m.set(new BlendingAttribute(alpha));
        }

        if (!Gdx.gl.glIsEnabled(GL20.GL_BLEND)) {
            Gdx.gl.glEnable(GL20.GL_BLEND);

        }
    }

    public static List<String> getAnimationNames(ModelInstance gameModel) {
        List<String> animationNames = new ArrayList<>();
        for (Animation animation : gameModel.animations) {
            animationNames.add(animation.id);
        }
        if (animationNames.isEmpty()) {
            animationNames.add("No animations");
        }
        return animationNames;
    }
    public Model createBox(int width, int height, int depth) {
    ModelBuilder modelBuilder = new ModelBuilder();
    modelBuilder.begin();
    MeshPartBuilder meshBuilder = modelBuilder.part("box", GL20.GL_TRIANGLES, VertexAttribute.Position().usage |VertexAttribute.Normal().usage | VertexAttribute.TexCoords(0).usage, new Material());

    BoxShapeBuilder.build(meshBuilder, width, height, depth);

    return  modelBuilder.end();}


}
