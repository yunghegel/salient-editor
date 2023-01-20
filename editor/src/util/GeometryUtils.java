package util;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntArray;

public class GeometryUtils
{

    private static final Vector3 TMP_VEC_1 = new Vector3();
    private static final Vector3 TMP_VEC_2 = new Vector3();
    private static final Vector3 TMP_VEC_3 = new Vector3();
    private static final Array<Renderable> renderables = new Array<>();
    private final static Vector3 tmpVec0 = new Vector3();
    private static final Vector3 tmp1 = new Vector3();
    private static final Vector3 tmp2 = new Vector3();
    private static final Vector3 tmp3 = new Vector3();
    private static final Vector3 tmp4 = new Vector3();
    private static final Vector3 tmp5 = new Vector3();

    private static final Vector3 v0 = new Vector3();
    private static final Vector3 v1 = new Vector3();
    private static final Vector3 v2 = new Vector3();

    public static float[] getVerticesFromMesh(Mesh mesh) {

        VertexAttributes vertexAttributes = mesh.getVertexAttributes();
        int offset = vertexAttributes.getOffset(VertexAttributes.Usage.Position);
        int vertexSize = mesh.getVertexSize() / 4;
        int vertCount = mesh.getNumVertices() * mesh.getVertexSize() / 4;
        float[] vertices = new float[vertCount];
        short[] indices = new short[mesh.getNumIndices()];

        mesh.getVertices(vertices);
        mesh.getIndices(indices);

        return vertices;
    }

    public static float nearestSegmentPointSquareDistance
            (Vector3 nearest, Vector3 start, Vector3 end, Vector3 point) {
        nearest.set(start);
        float abX = end.x - start.x;
        float abY = end.y - start.y;
        float abZ = end.z - start.z;
        float abLen2 = abX * abX + abY * abY + abZ * abZ;
        if (abLen2 > 0) { // Avoid NaN due to the indeterminate form 0/0
            float t = ((point.x - start.x) * abX + (point.y - start.y) * abY + (point.z - start.z) * abZ) / abLen2;
            float s = MathUtils.clamp(t, 0, 1);
            nearest.x += abX * s;
            nearest.y += abY * s;
            nearest.z += abZ * s;
        }
        return nearest.dst2(point);
    }

    public static float getClosestPointOnTriangle(Vector3 a, Vector3 b, Vector3 c, Vector3 p, Vector3 out) {
        // Check if P in vertex region outside A
        Vector3 ab = TMP_VEC_1.set(b).sub(a);
        Vector3 ac = TMP_VEC_2.set(c).sub(a);
        Vector3 ap = TMP_VEC_3.set(p).sub(a);
        float d1 = ab.dot(ap);
        float d2 = ac.dot(ap);
        if (d1 <= 0.0f && d2 <= 0.0f)  {
            if (out != null)
                out.set(a); // barycentric coordinates (1,0,0)
            return p.dst2(a);
        }

        // Check if P in vertex region outside B
        Vector3 bp = TMP_VEC_3.set(p).sub(b);
        float d3 = ab.dot(bp);
        float d4 = ac.dot(bp);
        if (d3 >= 0.0f && d4 <= d3) {
            if (out != null)
                out.set(b); // barycentric coordinates (0,1,0)
            return p.dst2(b);
        }

        // Check if P in edge region of AB, if so return projection of P onto AB
        float vc = d1 * d4 - d3 * d2;
        if (vc <= 0.0f && d1 >= 0.0f && d3 <= 0.0f) {
            Vector3 ret = out != null ? out : TMP_VEC_3;
            float v = d1 / (d1 - d3);
            ret.set(a).mulAdd(ab, v); // barycentric coordinates (1-v,v,0)
            return p.dst2(ret);
        }

        // Check if P in vertex region outside C
        Vector3 cp = TMP_VEC_3.set(p).sub(c);
        float d5 = ab.dot(cp);
        float d6 = ac.dot(cp);
        if (d6 >= 0.0f && d5 <= d6) {
            if (out != null)
                out.set(c); // barycentric coordinates (0,0,1)
            return p.dst2(c);
        }

        // Check if P in edge region of AC, if so return projection of P onto AC
        float vb = d5 * d2 - d1 * d6;
        if (vb <= 0.0f && d2 >= 0.0f && d6 <= 0.0f) {
            Vector3 ret = out != null ? out : TMP_VEC_3;
            float w = d2 / (d2 - d6);
            ret.set(a).mulAdd(ac, w); // barycentric coordinates (1-w,0,w)
            return ret.dst2(p);
        }

        // Check if P in edge region of BC, if so return projection of P onto BC
        float va = d3 * d6 - d5 * d4;
        if (va <= 0.0f && (d4 - d3) >= 0.0f && (d5 - d6) >= 0.0f) {
            Vector3 ret = out != null ? out : TMP_VEC_3;
            float w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
            ret.set(b).mulAdd(TMP_VEC_1.set(c).sub(b), w); // barycentric coordinates (0,1-w,w)
            return ret.dst2(p);
        }

        // P inside face region. Compute Q through its barycentric coordinates (u,v,w)
        float denom = 1.0f / (va + vb + vc);
        float v = vb * denom;
        float w = vc * denom;
        Vector3 ret = out != null ? out : TMP_VEC_3;
        ret.set(a).mulAdd(ab, v).mulAdd(ac, w);
        return ret.dst2(p);
    }

    public static Vector3 barycentric(Vector3 a, Vector3 b, Vector3 c, Vector3 p, Vector3 out) {
        v0.set(b).sub(a);
        v1.set(c).sub(a);
        v2.set(p).sub(a);

        float d00 = v0.dot(v0);
        float d01 = v0.dot(v1);
        float d11 = v1.dot(v1);
        float d20 = v2.dot(v0);
        float d21 = v2.dot(v1);
        float denom = d00 * d11 - d01 * d01;
        if (denom == 0) {
            denom = 1;
        }
        float v = (d11 * d20 - d01 * d21) / denom;
        float w = (d00 * d21 - d01 * d20) / denom;
        float u = 1.0f - v - w;

        return out.set(u, v, w);
    }

    public static Array<Vector3> buildVertices(FloatArray vertices){
        Array<Vector3> points = new Array<Vector3>();
        for(int i=0 ; i<vertices.size ; i+=3){
            points.add(new Vector3(vertices.items[i], vertices.items[i+1], vertices.items[i+2]));
        }
        return points;
    }

    public static FloatArray extractVertices(Node node){
        FloatArray result = new FloatArray();
        Vector3 pos = new Vector3();
        Matrix4 transform = new Matrix4(node.globalTransform);
        for(NodePart nodePart : node.parts){
            Mesh mesh = nodePart.meshPart.mesh;
            VertexAttribute positionAttribute = mesh.getVertexAttribute(VertexAttributes.Usage.Position);
            float [] buffer = new float[3];
            if(mesh.getNumIndices() > 0){
                result.ensureCapacity(result.size + mesh.getNumIndices());
                short[] indices = new short[mesh.getNumIndices()];
                mesh.getIndices(indices);
                for(int i=0 ; i<indices.length ; i++){
                    int index = (int)indices[i] & 0xFFFF;
                    mesh.getVertices(index * mesh.getVertexSize() / 4 + positionAttribute.offset/4, buffer);
                    pos.set(buffer).mul(transform);
                    result.add(pos.x, pos.y, pos.z);
                }
            }else{
                result.ensureCapacity(result.size + mesh.getNumVertices()*3);
                for(int v = 0 ; v<mesh.getNumVertices() ; v++){
                    mesh.getVertices(v * mesh.getVertexSize() / 4 + positionAttribute.offset/4, buffer);
                    pos.set(buffer).mul(transform);
                    result.add(pos.x, pos.y, pos.z);
                }
            }
        }
        return result;
    }

    public static int mergeByEpsilon(Array<Vector3> points, IntArray indices){
        return mergeByEpsilon(points, indices, MathUtils.FLOAT_ROUNDING_ERROR);
    }
    public static int mergeByEpsilon(Array<Vector3> points, IntArray indices, float epsilon){
        int vc=0;
        IntArray mapping = new IntArray(indices.size);
        for(int i=0 ; i<points.size ; i++){
            Vector3 a = points.get(i);
            // boolean found = false;
            for(int j=0 ; j<mapping.size ; j++){
                int index = mapping.items[j];
                Vector3 b = points.get(index);
                if(b.epsilonEquals(a, epsilon)){
                    mapping.add(index);
                    indices.add(indices.get(j));
                    break;
                }
            }
            if(i == mapping.size){
                mapping.add(i);
                indices.add(vc++);
            }
        }

        return vc;
    }
    public static void enable(Iterable<Node> nodes, boolean enabled){
        for(Node node : nodes){
            enable(node, enabled);
        }
    }
    public static void enable(Node node, boolean enabled){
        for(NodePart part : node.parts){
            part.enabled = enabled;
        }
        for(Node child : node.getChildren()){
            enable(child, enabled);
        }
    }

    /**
     * Get Vertices count of a Model
     */
    public static int getVerticesCount(Model model) {
        int vertices = 0;
        for (Mesh mesh : model.meshes) {
            vertices += mesh.getNumVertices();
        }
        return vertices;
    }

    /**
     * Get Indices count of a Model
     */
    public static int getIndicesCount(Model model) {
        int indices = 0;
        for (Mesh mesh : model.meshes) {
            indices += mesh.getNumIndices();
        }
        return indices;
    }

    /**
     * Checks if visible to camera using sphereInFrustum and radius
     */
    public static boolean isVisible(final Camera cam, final ModelInstance modelInstance, Vector3 center, float radius) {
        modelInstance.transform.getTranslation(tmpVec0);
        tmpVec0.add(center);

        return cam.frustum.sphereInFrustum(tmpVec0, radius);
    }

    /**
     * Checks if visible to camera using boundsInFrustum and dimensions
     */
    public static boolean isVisible(final Camera cam, final ModelInstance modelInstance, Vector3 center, Vector3 dimensions) {
        modelInstance.transform.getTranslation(tmpVec0);
        tmpVec0.add(center);
        return cam.frustum.boundsInFrustum(tmpVec0, dimensions);
    }

    public static int getMeshCount(Model model){
        return model.meshes.size;
    }
    public static int getMaterialsCount(Model model){
        return model.materials.size;
    }

    public static String getGeometryInformation(Model model){
        int vertices;
        float indices;
        int meshes;
        int materials;
        int nodes;
        int nodeParts = 0;
        int nodePartMeshParts = 0;
        int animations;
        vertices = getVerticesCount(model);
        indices = getIndicesCount(model);
        meshes = getMeshCount(model);
        materials = getMaterialsCount(model);
        nodes = model.nodes.size;
        for(Node node : model.nodes){
            nodeParts += node.parts.size;
            for(NodePart part : node.parts){
                nodePartMeshParts += part.meshPart.size;
            }
        }
        animations = model.animations.size;

        return ("Vertices: " + vertices + ", Indices: " + indices + ", Meshes: " + meshes + ", Materials: " + materials + ", Nodes: " + nodes + ", NodeParts: " + nodeParts + ", NodePartMeshParts: " + nodePartMeshParts + ", Animations: " + animations);



    }
    public static void renderIntersection(Vector3 intersection,Camera camera){
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Point);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.point(intersection.x , intersection.y , intersection.z);
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.line(Vector3.Zero , intersection);
        shapeRenderer.end();
    }

    public static void renderLine(Vector3 origin,Vector3 endpoint,Camera camera){
        ShapeRenderer shapeRenderer = new ShapeRenderer();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.line(origin , endpoint);
        shapeRenderer.end();
    }

    public static void renderBoundingBox(BoundingBox boundingBox, Color color,Camera camera){
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(color);
        shapeRenderer.box(boundingBox.min.x , boundingBox.min.y , boundingBox.min.z + boundingBox.getDepth() , boundingBox.getWidth() , boundingBox.getHeight() , boundingBox.getDepth());
        shapeRenderer.end();
    }

    //get triangles from a models meshes
    public static Array<Vector3> getTriangles(Model model){
        Array<Vector3> triangles = new Array<Vector3>();
        for(Mesh mesh : model.meshes){
            int numVertices = mesh.getNumVertices();
            int numIndices = mesh.getNumIndices();
            int numTriangles = numIndices / 3;
            int numVerticesPerTriangle = 3;
            int numFloatsPerVertex = 3;
            int numFloatsPerTriangle = numFloatsPerVertex * numVerticesPerTriangle;
            float[] vertices = new float[numVertices * numFloatsPerVertex];
            short[] indices = new short[numIndices];
            mesh.getVertices(vertices);
            mesh.getIndices(indices);
            for(int i=0 ; i<numTriangles ; i++){
                Vector3 a = new Vector3(vertices[indices[i * 3] * numFloatsPerVertex] , vertices[indices[i * 3] * numFloatsPerVertex + 1] , vertices[indices[i * 3] * numFloatsPerVertex + 2]);
                Vector3 b = new Vector3(vertices[indices[i * 3 + 1] * numFloatsPerVertex] , vertices[indices[i * 3 + 1] * numFloatsPerVertex + 1] , vertices[indices[i * 3 + 1] * numFloatsPerVertex + 2]);
                Vector3 c = new Vector3(vertices[indices[i * 3 + 2] * numFloatsPerVertex] , vertices[indices[i * 3 + 2] * numFloatsPerVertex + 1] , vertices[indices[i * 3 + 2] * numFloatsPerVertex + 2]);
                triangles.add(a);
                triangles.add(b);
                triangles.add(c);
            }
        }
        return triangles;
    }

    public static float barryCentric(Vector3 p1, Vector3 p2, Vector3 p3, Vector2 pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    public static float getAngleBetween(Vector3 from, Vector3 to) {
        float absolute = (float) Math.sqrt(from.len() * to.len());
        if (com.badlogic.gdx.math.MathUtils.isZero(absolute))
            return 0; // It is close enough to just return 0

        float angleDot = from.dot(to);
        float dot = com.badlogic.gdx.math.MathUtils.clamp(angleDot / absolute, -1f, 1f);
        return com.badlogic.gdx.math.MathUtils.acos(dot) * com.badlogic.gdx.math.MathUtils.radiansToDegrees;
    }
}
