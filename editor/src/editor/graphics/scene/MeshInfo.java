package editor.graphics.scene;

import backend.tools.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.math.collision.Sphere;
import com.badlogic.gdx.utils.Array;
import editor.Context;
import tests.GeometryTest;
import ui.widgets.RenderWidget;

public class MeshInfo
{

    public Mesh mesh;

    VertexAttributes attributes;
    VertexAttribute positionAttribute;
    VertexAttribute normalAttribute;
    VertexAttribute colorAttribute;

    ModelInstance model;
    NodePart nodePart;
    Node node;

    int offset;
    int posOffset;
    int norOffset;
    int colOffset;

    public int vertexSize;
    int vertexCount;
    int indexCount;
    int triangleCount;

    public float[] vertices;
    public short[] indices;
    float[] normals;

    public static class Triangle
    {

        public Vector3 v1;
        public Vector3 v2;
        public Vector3 v3;

        int index1;
        int index2;
        int index3;

        public float x1, y1, z1;
        public float x2, y2, z2;
        public float x3, y3, z3;

        Vector3[] vertices;

        Vector3 edge1;
        Vector3 edge2;
        Vector3 edge3;

        Vector3 normal;

        public Triangle(int index1, int index2, int index3, Vector3 v1, Vector3 v2, Vector3 v3) {

            this.index1 = index1;
            this.index2 = index2;
            this.index3 = index3;

            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;

            edge1 = v2.sub(v1);
            edge2 = v3.sub(v1);
            edge3 = v3.sub(v2);

            normal = edge1.crs(edge2).nor();
            vertices = new Vector3[]{v1 , v2 , v3};

            Log.info("Triangle" , "Triangle created with vertices: " + v1 + " " + v2 + " " + v3);
        }

        Triangle(float x1 , float y1 , float z1 , float x2 , float y2 , float z2 , float x3 , float y3 , float z3) {
            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
            this.x3 = x3;
            this.y3 = y3;
            this.z3 = z3;

            v1 = new Vector3(x1 , y1 , z1);
            v2 = new Vector3(x2 , y2 , z2);
            v3 = new Vector3(x3 , y3 , z3);

            edge1 = new Vector3(x2 - x1 , y2 - y1 , z2 - z1);
            edge2 = new Vector3(x3 - x1 , y3 - y1 , z3 - z1);
            edge3 = new Vector3(x3 - x2 , y3 - y2 , z3 - z2);

            normal = edge1.crs(edge2).nor();
            vertices = new Vector3[]{new Vector3(x1 , y1 , z1) , new Vector3(x2 , y2 , z2) , new Vector3(x3 , y3 , z3)};

          //  Log.info("Triangle" , "Triangle created with vertices: " + x1 + " " + y1 + " " + z1 + " " + x2 + " " + y2 + " " + z2 + " " + x3 + " " + y3 + " " + z3);
        }

        public Vector3 getV1() {
            return v1;
        }

        public Vector3 getV2() {
            return v2;
        }

        public Vector3 getV3() {
            return v3;
        }

        public Vector3 getEdge1() {
            return edge1;
        }

        public Vector3 getEdge2() {
            return edge2;
        }

        public Vector3 getEdge3() {
            return edge3;
        }

        public Vector3 getNormal() {
            return normal;
        }

    }

    public class Vertex
    {

        public float x;
        public float y;
        public float z;

        public Vertex(float x , float y , float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vertex(Vector3 vec) {
            this.x = vec.x;
            this.y = vec.y;
            this.z = vec.z;
        }

    }

    public Array<MeshInfo.Vertex> verticesArray = new Array<MeshInfo.Vertex>();

    public Array<MeshInfo.Triangle> triangles = new Array<MeshInfo.Triangle>();

    public MeshInfo(Mesh mesh) {
        this.mesh = mesh;
        getAttributes();
        getOffsets();
        getCounts();
        allocateArrays();
        traverse();
    }

    public MeshInfo(ModelInstance model , Node node , NodePart nodePart) {
        this.model = model;
        this.node = node;
        this.nodePart = nodePart;
        this.mesh = model.model.meshes.get(0);

        getAttributes();
        getOffsets();
        getCounts();
        allocateArrays();
        traverse();
    }

    private void getAttributes() {
        attributes = mesh.getVertexAttributes();
        positionAttribute = attributes.findByUsage(VertexAttributes.Usage.Position);
        normalAttribute = attributes.findByUsage(VertexAttributes.Usage.Normal);
        colorAttribute = attributes.findByUsage(VertexAttributes.Usage.ColorPacked);
    }

    private void getOffsets() {
        if (positionAttribute != null) posOffset = positionAttribute.offset / 4;
        if (normalAttribute != null) norOffset = normalAttribute.offset / 4;
        if (colorAttribute != null) colOffset = colorAttribute.offset / 4;
        int offset = attributes.getOffset(VertexAttributes.Usage.Position);

    }

    private void getCounts() {
        vertexCount = mesh.getNumVertices() * mesh.getVertexSize() / 4;
        vertexSize = mesh.getVertexSize() / 4;
        indexCount = mesh.getNumIndices();

        triangleCount = indexCount / 3;

        Log.info("MeshInfo" , "Vertex Count: " + vertexCount + " Vertex Size: " + vertexSize + " Index Count: " + indexCount + " Triangle Count: " + triangleCount);
    }

    private void allocateArrays() {
        vertices = new float[vertexCount];
        indices = new short[indexCount];
        normals = new float[vertexCount];
        mesh.getVertices(norOffset , norOffset + 3 , normals);
        mesh.getVertices(vertices);
        mesh.getIndices(indices);
        Log.info("MeshInfo" , "Arrays allocated...");
    }

    private void traverse() {
        for (int i = 0; i < vertices.length; i += vertexSize) {
            float x = vertices[i + posOffset];
            float y = vertices[i + 1 + posOffset];
            float z = vertices[i + 2 + posOffset];

            float nx = normals[i + norOffset];
            float ny = normals[i + 1 + norOffset];
            float nz = normals[i + 2 + norOffset];
            Vector3 normal = new Vector3(normals[i] , normals[i + 1] , normals[i + 2]);
            Vector3 vertex = new Vector3(x , y , z);
            verticesArray.add(new MeshInfo.Vertex(vertex));
            Vector3 newVertex = vertex.add(normal);

            vertices[i + offset] = newVertex.x;
            vertices[i + 1 + offset] = newVertex.y;
            vertices[i + 2 + offset] = newVertex.z;

        }

        for (int i = 0; i < indices.length; i += 3) {
            int index1 = indices[i];
            int index2 = indices[i + 1];
            int index3 = indices[i + 2];
            float x1 = vertices[index1 * vertexSize + offset];
            float y1 = vertices[index1 * vertexSize + 1 + offset];
            float z1 = vertices[index1 * vertexSize + 2 + offset];
            float x2 = vertices[index2 * vertexSize + offset];
            float y2 = vertices[index2 * vertexSize + 1 + offset];
            float z2 = vertices[index2 * vertexSize + 2 + offset];
            float x3 = vertices[index3 * vertexSize + offset];
            float y3 = vertices[index3 * vertexSize + 1 + offset];
            float z3 = vertices[index3 * vertexSize + 2 + offset];
            Vector3 v1 = new Vector3(x1 , y1 , z1);
            Vector3 v2 = new Vector3(x2 , y2 , z2);
            Vector3 v3 = new Vector3(x3 , y3 , z3);
            MeshInfo.Triangle triangle = new Triangle(x1, y1, z1, x2, y2, z2, x3, y3, z3);
            // Triangle triangle = new Triangle(index1, index2, index3, v1, v2, v3);
            triangles.add(triangle);
        }


    }
    private void printData(){
        Log.info("MeshInfo" , "Vertices: " + verticesArray.size+ " Triangles: " + triangles.size+ " Indices: " + indices.length);
        Log.info("MeshInfo","Attribues: " + colorAttribute + " " + normalAttribute + " " + positionAttribute);
        Log.info("MeshInfo","Offsets: " + posOffset + " " + norOffset + " " + colOffset);
        Log.info("ModelInfo","Model Nodes:"+model.nodes.size);
        for (Node node : model.nodes) {
            Log.info("ModelInfo","Node: " + node.id);
            for (NodePart nodePart : node.parts) {
                Log.info("ModelInfo","NodePart: " + nodePart.meshPart.id);
            }
        }}
    //methods to apply transformations to the mesh
    public void translate(float x , float y , float z) {
        for (int i = 0; i < vertices.length; i += vertexSize) {
            vertices[i + offset] += x;
            vertices[i + 1 + offset] += y;
            vertices[i + 2 + offset] += z;
        }
    }

    public void rotate(float angle , float x , float y , float z) {
        for (int i = 0; i < vertices.length; i += vertexSize) {
            float x1 = vertices[i + offset];
            float y1 = vertices[i + 1 + offset];
            float z1 = vertices[i + 2 + offset];
            Vector3 vertex = new Vector3(x1 , y1 , z1);
            vertex.rotate(angle , x , y , z);
            vertices[i + offset] = vertex.x;
            vertices[i + 1 + offset] = vertex.y;
            vertices[i + 2 + offset] = vertex.z;
        }
    }

    public void scale(float x , float y , float z) {
        for (int i = 0; i < vertices.length; i += vertexSize) {
            vertices[i + offset] *= x;
            vertices[i + 1 + offset] *= y;
            vertices[i + 2 + offset] *= z;
        }
    }

    public void updateMesh() {
        mesh.setVertices(vertices);
    }

    //method to set the position of the mesh
    public void setPosition(float x , float y , float z) {
        for (int i = 0; i < vertices.length; i += vertexSize) {
            vertices[i + offset] = vertices[i + offset] - x;
            vertices[i + 1 + offset] = vertices[i + 1 + offset] - y;
            vertices[i + 2 + offset] = vertices[i + 2 + offset] - z;
        }
    }

    //multiply the vertices by a matrix and return the result as a new array
    public float[] multiplyMatrix(Matrix4 matrix) {
        float[] result = new float[vertices.length];
        for (int i = 0; i < vertices.length; i += vertexSize) {
            float x = vertices[i + offset];
            float y = vertices[i + 1 + offset];
            float z = vertices[i + 2 + offset];
            Vector3 vertex = new Vector3(x , y , z);
            vertex.mul(matrix);
            result[i + offset] = vertex.x;
            result[i + 1 + offset] = vertex.y;
            result[i + 2 + offset] = vertex.z;
        }
        return result;
    }

    //set vertex to its actual position

    public void setModel(ModelInstance model) {
        this.model = model;
    }

    public void drawWireframe(ShapeRenderer shapeRenderer, Color color, Camera camera) {

        if (model!=null) shapeRenderer.setTransformMatrix(model.transform);
        if (model!=null) multiplyMatrix(model.transform);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setProjectionMatrix(camera.combined);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        for (MeshInfo.Triangle tri : triangles) {
            shapeRenderer.setColor(1, 0, 0, 1);

            shapeRenderer.line(tri.v1, tri.v2);
            shapeRenderer.line(tri.v2, tri.v3);
            shapeRenderer.line(tri.v3, tri.v1);
        }
        shapeRenderer.end();

    }

    public void drawPoints(ShapeRenderer shapeRenderer, Color color, Camera camera) {
        model.model.calculateTransforms();
        if (model!=null) shapeRenderer.setTransformMatrix(model.transform);

        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(color);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Point);
        for (MeshInfo.Vertex vertex : verticesArray) {

            shapeRenderer.point(vertex.x, vertex.y, vertex.z);
        }
        shapeRenderer.end();
    }

    //method for selecting a vertex
    public void selectVertex(float x , float y , float z) {
        for (int i = 0; i < vertices.length; i += vertexSize) {
            float x1 = vertices[i + offset];
            float y1 = vertices[i + 1 + offset];
            float z1 = vertices[i + 2 + offset];
            if (x1 == x && y1 == y && z1 == z) {
                vertices[i + offset] = 0;
                vertices[i + 1 + offset] = 0;
                vertices[i + 2 + offset] = 0;
            }
        }
    }

    //method for selecting a triangle
    public void selectTriangle(float x1 , float y1 , float z1 , float x2 , float y2 , float z2 , float x3 , float y3 , float z3) {
        for (int i = 0; i < vertices.length; i += vertexSize) {
            float x = vertices[i + offset];
            float y = vertices[i + 1 + offset];
            float z = vertices[i + 2 + offset];
            if (x == x1 && y == y1 && z == z1) {
                vertices[i + offset] = 0;
                vertices[i + 1 + offset] = 0;
                vertices[i + 2 + offset] = 0;
            }
            if (x == x2 && y == y2 && z == z2) {
                vertices[i + offset] = 0;
                vertices[i + 1 + offset] = 0;
                vertices[i + 2 + offset] = 0;
            }
            if (x == x3 && y == y3 && z == z3) {
                vertices[i + offset] = 0;
                vertices[i + 1 + offset] = 0;
                vertices[i + 2 + offset] = 0;
            }
        }
    }

    //method for selecting a face
    public void selectFace(float x1 , float y1 , float z1 , float x2 , float y2 , float z2 , float x3 , float y3 , float z3 , float x4 , float y4 , float z4) {
        for (int i = 0; i < vertices.length; i += vertexSize) {
            float x = vertices[i + offset];
            float y = vertices[i + 1 + offset];
            float z = vertices[i + 2 + offset];
            if (x == x1 && y == y1 && z == z1) {
                vertices[i + offset] = 0;
                vertices[i + 1 + offset] = 0;
                vertices[i + 2 + offset] = 0;
            }
            if (x == x2 && y == y2 && z == z2) {
                vertices[i + offset] = 0;
                vertices[i + 1 + offset] = 0;
                vertices[i + 2 + offset] = 0;
            }
            if (x == x3 && y == y3 && z == z3) {
                vertices[i + offset] = 0;
                vertices[i + 1 + offset] = 0;
                vertices[i + 2 + offset] = 0;
            }
            if (x == x4 && y == y4 && z == z4) {
                vertices[i + offset] = 0;
                vertices[i + 1 + offset] = 0;
                vertices[i + 2 + offset] = 0;
            }
        }
    }

    //method for selecting a edge
    public void selectEdge(float x1 , float y1 , float z1 , float x2 , float y2 , float z2) {
        for (int i = 0; i < vertices.length; i += vertexSize) {
            float x = vertices[i + offset];
            float y = vertices[i + 1 + offset];
            float z = vertices[i + 2 + offset];
            if (x == x1 && y == y1 && z == z1) {
                vertices[i + offset] = 0;
                vertices[i + 1 + offset] = 0;
                vertices[i + 2 + offset] = 0;
            }
            if (x == x2 && y == y2 && z == z2) {
                vertices[i + offset] = 0;
                vertices[i + 1 + offset] = 0;
                vertices[i + 2 + offset] = 0;
            }
        }
    }

    //given a ray, this method will return the closest vertex to the ray

    ShapeRenderer shapeRenderer = new ShapeRenderer();
    public Vector3 getClosestVertex(Ray ray) {

        Vector3 closestVertex = new Vector3();
        for (MeshInfo.Vertex vertex : verticesArray) {

            Vector3 vertexVector = new Vector3(vertex.x, vertex.y, vertex.z);
            Vector3 transformedVertex = new Vector3();
            transformedVertex.set(vertex.x, vertex.y, vertex.z);
            Matrix4 transform = new Matrix4();
            if (model != null)
                transform.set(model.transform);

            transformedVertex.mul(transform);
            if (Intersector.intersectRaySphere(ray, transformedVertex, .1f,null)) {
                closestVertex = new Vector3(vertex.x, vertex.y, vertex.z);

                return new Vector3(vertex.x, vertex.y, vertex.z);
            }
        }

//        for (int i = 0; i < vertices.length; i += vertexSize) {
//            float x = vertices[i + offset];
//            float y = vertices[i + 1 + offset];
//            float z = vertices[i + 2 + offset];
//            Vector3 vertex = new Vector3(x, y, z);
//            float distance = ray.origin.dst(vertex);
//            Sphere sphere = new Sphere(vertex, .2f);
//            if (Intersector.intersectRaySphere(ray, vertex, .2f,null)) {
//                closestVertex = vertex;
//                }
//            }

        return closestVertex;
    }

    //given a ray, return the triangle that the ray intersects
    public Triangle getClosestTriangle(Ray ray){

        Vector3 intersection = new Vector3();
        for (Triangle triangle: triangles) {
            if (Intersector.intersectRayTriangle(ray, triangle.getV1().cpy().mul(model.transform), triangle.v2.cpy().mul(model.transform), triangle.v3.cpy().mul(model.transform), intersection)) {
                return triangle;
            }
        }

        return new Triangle(0, 0, 0, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0, 0, 0));
    }

    //given a vertex, return the index of the vertex in the vertices array
    public int getVertexIndex(Vector3 vertex) {
        for (int i = 0; i < vertices.length; i += vertexSize) {
            float x = vertices[i + offset];
            float y = vertices[i + 1 + offset];
            float z = vertices[i + 2 + offset];
            if (x == vertex.x && y == vertex.y && z == vertex.z) {
                return i;
            }
        }
        return -1;
    }

    //given a vertex, return the index in the indices array
    public int getVertexIndexInIndices(Vector3 vertex) {
        for (int i = 0; i < indices.length; i++) {
            float x = vertices[indices[i] * vertexSize + offset];
            float y = vertices[indices[i] * vertexSize + 1 + offset];
            float z = vertices[indices[i] * vertexSize + 2 + offset];
            if (x == vertex.x && y == vertex.y && z == vertex.z) {
                return i;
            }
        }
        return -1;
    }



}

