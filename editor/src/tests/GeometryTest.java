package tests;

import backend.tools.Log;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.kotcrab.vis.ui.widget.VisLabel;
import util.GeometryUtils;

public class GeometryTest extends BaseTest

{
    Array<Vector3> triangles = new Array<Vector3>();
    Array<Vector3> points = new Array<Vector3>();
    ShapeRenderer shapeRenderer = new ShapeRenderer();
    FloatArray floatArray = new FloatArray();
    float[] vertices;

    VisLabel normalCount;
    VisLabel triangleCount;
    VisLabel vertexCount;
    VisLabel currentNormal;
    VisLabel intersectPoint;
    VisLabel distance;
    VisLabel iteration;
    Vector3 intersection = new Vector3();
    Ray ray = new Ray();

    Array<MeshInfo> meshInfos = new Array<MeshInfo>();

    public GeometryTest(Game game) {
        super(game);
        init();
        traverseModel();

    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        sceneManager.update(delta);
        //sceneManager.render();

        //shapeRenderer.setTransformMatrix(scene.modelInstance.transform);


//        if (Gdx.input.isButtonPressed(0)) {
        ray = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
//        }
        //shapeRenderer.line(ray.origin, ray.direction.scl(1000));
        //shapeRenderer.line(ray.origin, intersection);
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(1, 0, 0, 1);

        for (MeshInfo meshInfo:meshInfos){
            for (MeshInfo.Vertex vertex:meshInfo.verticesArray){
                shapeRenderer.set(ShapeRenderer.ShapeType.Point);

                shapeRenderer.point(vertex.x, vertex.y, vertex.z);
            }
            if(Intersector.intersectRayTriangles(ray,meshInfo.vertices,meshInfo.indices,meshInfo.vertexSize,intersection)) {
                float distance = ray.origin.dst(intersection);
                intersectPoint.setText("Intersect Point: " + intersection.toString());
                this.distance.setText("Distance: " + distance);

                for (MeshInfo.Triangle tri:meshInfo.triangles){

                    if (Intersector.isPointInTriangle(intersection,tri.v1,tri.v2,tri.v3)){
                        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
                        currentNormal.setText("Current Normal: " + tri.normal.toString());
                        shapeRenderer.setColor(0, 1, 0, 1);

                        shapeRenderer.line(tri.v1, tri.v2);
                        shapeRenderer.setColor(1, 0, 0, 1);
                        shapeRenderer.line(tri.v2, tri.v3);
                        shapeRenderer.setColor(0, 0, 1, 1);
                        shapeRenderer.line(tri.v3, tri.v1);

                    }


            }





//                shapeRenderer.line(tri.x1, tri.y1, tri.z1, tri.x2, tri.y2, tri.z2);
//                shapeRenderer.line(tri.x2, tri.y2, tri.z2, tri.x3, tri.y3, tri.z3);
//                shapeRenderer.line(tri.x3, tri.y3, tri.z3, tri.x1, tri.y1, tri.z1);
                //shapeRenderer.line(tri.v1, tri.v2);
//                shapeRenderer.line(tri.v2, tri.v3);
//                shapeRenderer.line(tri.v3, tri.v1);


            }
//            for (MeshInfo.Vertex vertex:meshInfo.verticesArray){
//                shapeRenderer.point(vertex.x, vertex.y, vertex.z);
//            }

        }


        //modifyVertices(modelInstance);

        shapeRenderer.end();

        stage.act(delta);
        stage.draw();
    }

    public void init(){
        triangles = GeometryUtils.getTriangles(modelInstance.model);
        System.out.println("triangles: " + triangles.size);
        for (Vector3 triangle : triangles) {
            System.out.println(triangle+ " ");
        }
        //shapeRenderer.scale(.01f, .01f, .01f);
        shapeRenderer.setAutoShapeType(true);
        modelInstance.transform.scale(100,100,100);
        Gdx.gl.glLineWidth(2);

        normalCount = new VisLabel("Normal Count: " + modelInstance.model.meshParts.get(0).mesh.getNumIndices());
        triangleCount = new VisLabel("Triangle Count: " + modelInstance.model.meshParts.get(0).mesh.getNumIndices());
        vertexCount = new VisLabel("Vertex Count: " + modelInstance.model.meshParts.get(0).mesh.getNumVertices());
        normalCount.setPosition(10, 10);
        triangleCount.setPosition(10, 30);
        vertexCount.setPosition(10, 50);

        currentNormal = new VisLabel("Current Normal: ");
        intersectPoint = new VisLabel("Intersection: ");
        distance = new VisLabel("Distance: ");
        currentNormal.setPosition(200, 10);
        intersectPoint.setPosition(200, 30);
        distance.setPosition(200, 50);

        iteration = new VisLabel("Iteration: ");
        iteration.setPosition(10, 70);


        stage.addActor(normalCount);
        stage.addActor(triangleCount);
        stage.addActor(vertexCount);
        stage.addActor(currentNormal);
        stage.addActor(intersectPoint);
        stage.addActor(distance);
        stage.addActor(iteration);



    }

    public void traverseModel(){
        for (Mesh mesh:modelInstance.model.meshes)
        {
            MeshInfo meshInfo = new MeshInfo(mesh);
            meshInfos.add(meshInfo);
        }
    }

    public void modifyVertices(ModelInstance modelInstance) {
        iteration.setText("Iteration: ");
        for (Mesh mesh : modelInstance.model.meshes) {
            VertexAttributes vertexAttributes = mesh.getVertexAttributes();
            VertexAttribute norAttr = mesh.getVertexAttributes().findByUsage(VertexAttributes.Usage.Normal);
            VertexAttribute posAttr = mesh.getVertexAttributes().findByUsage(VertexAttributes.Usage.Position);
            int pOff = posAttr.offset / 4;
            int nOff = norAttr.offset / 4;

            int offset = vertexAttributes.getOffset(VertexAttributes.Usage.Position);
            int vertexSize = mesh.getVertexSize() / 4;
            int vertCount = mesh.getNumVertices() * mesh.getVertexSize() / 4;

            float[] vertices = new float[vertCount];
            short[] indices = new short[mesh.getNumIndices()];
            float[] normals = new float[vertCount];

            mesh.getVertices(vertices);
            mesh.getIndices(indices);

            //get normals
            int normalOffset = vertexAttributes.getOffset(VertexAttributes.Usage.Normal);

            mesh.getVertices(nOff , nOff + 3 , normals);

            // Get XYZ vertices position data
            for (int i = 0; i < vertices.length; i += vertexSize) {
                Gdx.gl.glLineWidth(4);
                float x = vertices[i + offset];
                float y = vertices[i + 1 + offset];
                float z = vertices[i + 2 + offset];

                float nx = normals[i + normalOffset];
                float ny = normals[i + 1 + normalOffset];
                float nz = normals[i + 2 + normalOffset];


                shapeRenderer.set(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(1 , 0 , 0 , 1);
             //  shapeRenderer.point(x , y , z);
//
//                shapeRenderer.line(x , y , z , x + normals[i]*5 , y + normals[i + 1] * 5 , z + normals[i + 2] * 5);
//                System.out.println("Normal: " + normals[i] + " " + normals[i + 1] + " " + normals[i + 2]);

                Vector3 normal = new Vector3(normals[i] , normals[i + 1] , normals[i + 2]);
                Vector3 vertex = new Vector3(x , y , z);
                Vector3 newVertex = vertex.add(normal);



                vertices[i + offset] = newVertex.x;
                vertices[i + 1 + offset] = newVertex.y;
                vertices[i + 2 + offset] = newVertex.z;

//                shapeRenderer.setColor(1 , 0 , 0 , 1);
//                shapeRenderer.point(newVertex.x , newVertex.y , newVertex.z);
//
//                shapeRenderer.line(newVertex.x , newVertex.y , newVertex.z , newVertex.x + normals[i] , newVertex.y + normals[i + 1] , newVertex.z + normals[i + 2]);

                Vector3 cross = normal.crs(new Vector3(0 , 1 , 0));
                Vector3 newVertex2 = newVertex.add(cross);
//                shapeRenderer.setColor(0 , 0 , 1 , 1);
//                shapeRenderer.point(newVertex2.x , newVertex2.y , newVertex2.z);
//
                shapeRenderer.line(newVertex2.x , newVertex2.y , newVertex2.z , newVertex2.x + normals[i] , newVertex2.y + normals[i + 1] , newVertex2.z + normals[i + 2]);

                normalCount.setText("Normal Count: " + normals.length);
                triangleCount.setText("Triangle Count: " + indices.length);
                vertexCount.setText("Vertex Count: " + vertices.length);
                currentNormal.setText("Current Normal: " + normal);
                intersectPoint.setText("Current Triangle: " + vertex);
                distance.setText("Current Vertex: " + newVertex);
                iteration.setText("Iteration(Vertices): " + i);

                }
                for (int i = 0; i < indices.length; i += 3) {
                    Gdx.gl.glLineWidth(1);
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
                    Vector3 edge1 = v2.sub(v1);
                    Vector3 edge2 = v3.sub(v1);
                    Vector3 edge3 = v3.sub(v2);
                    Vector3 normal = edge1.crs(edge2).nor().scl(2);
                    shapeRenderer.setColor(0 , 1 , 0 , 1);
                    shapeRenderer.line(x1 , y1 , z1 , x1 + normal.x , y1 + normal.y , z1 + normal.z);
//                    shapeRenderer.line(x2 , y2 , z2 , x2 + normal.x , y2 + normal.y , z2 + normal.z);
//                    shapeRenderer.line(x3 , y3 , z3 , x3 + normal.x , y3 + normal.y , z3 + normal.z);



                       Vector3 rayFromWorld = new Vector3();
                       Vector3 rayToWorld   = new Vector3();
                    //   ray = camera.getPickRay(Gdx.graphics.getWidth() / 2f , Gdx.graphics.getHeight() / 2f);

                      // Ray ray = new Ray(camera.position , camera.direction);

                    Vector3 endPoint = new Vector3();
                    ray = camera.getPickRay(Gdx.input.getX() , Gdx.input.getY());
                    if (Intersector.intersectRayTriangle(ray, v1,v2,v3, intersection)) {

                        shapeRenderer.setColor(1 , 1 , 0 , 1);

                        ray.getEndPoint(endPoint,intersection.dst(ray.origin));

                        if(Intersector.isPointInTriangle(endPoint , v1 , v2 , v3)){
                            shapeRenderer.setColor(0 , 1 , 1 , 1);
                            shapeRenderer.line(x1 , y1 , z1 , x2 , y2 , z2);
                                                    shapeRenderer.line(x2 , y2 , z2 , x3 , y3 , z3);
                                               shapeRenderer.line(x3 , y3 , z3 , x1 , y1 , z1);
                                            return;
                        }


                    }





//                    if (Intersector.intersectRayTriangle(ray, v1,v2,v3, intersection)){
//                        shapeRenderer.setColor(1 , 1 , 0 , 1);
//                        shapeRenderer.line(ray.origin, ray.direction.scl(100).add(ray.origin));
//                        shapeRenderer.line(x1 , y1 , z1 , x2 , y2 , z2);
//                        shapeRenderer.line(x2 , y2 , z2 , x3 , y3 , z3);
//                        shapeRenderer.line(x3 , y3 , z3 , x1 , y1 , z1);
//
//                        shapeRenderer.setColor(1 , 0 , 1 , 1);
//                     shapeRenderer.line(x1 , y1 , z1 , x1 + normal.x , y1 + normal.y , z1 + normal.z);
//                         shapeRenderer.line(x2 , y2 , z2 , x2 + normal.x , y2 + normal.y , z2 + normal.z);
//                        shapeRenderer.line(x3 , y3 , z3 , x3 + normal.x , y3 + normal.y , z3 + normal.z);
//                        return;
//                    }
                   // shapeRenderer.line(ray.origin, ray.direction.scl(100).add(ray.origin));
                    //shapeRenderer.line(ray.origin,intersection);

                    iteration.setText("Intersection: " + intersection);
                    //normal.set(v1).sub(v2).crs(v1.sub(v3)).nor();



//                    shapeRenderer.setColor(1 , 0 , 0 , .5f);
//                    shapeRenderer.line(x1 , y1 , z1 , x2 , y2 , z2);
//                    shapeRenderer.line(x2 , y2 , z2 , x3 , y3 , z3);
//                    shapeRenderer.line(x3 , y3 , z3 , x1 , y1 , z1);


                }
                for (int i=0;i<normals.length;i+=vertexSize){
                    float x = normals[i];
                    float y = normals[i+1];
                    float z = normals[i+2];
                    shapeRenderer.setColor(0,0,1,1);
                    shapeRenderer.line(x,y,z,x*2,y*2,z*2);
                    iteration.setText("Iteration (Normals): " + x+" "+y+" "+z);
                }

            }
        }

        static class MeshInfo
        {

        Mesh mesh;



        VertexAttributes attributes;
        VertexAttribute positionAttribute;
        VertexAttribute normalAttribute;
        VertexAttribute colorAttribute;

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

        class Triangle {

            Vector3 v1;
            Vector3 v2;
            Vector3 v3;

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

            Triangle(int index1, int index2, int index3, Vector3 v1, Vector3 v2, Vector3 v3) {

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
                vertices = new Vector3[] {v1, v2, v3};

                Log.info("Triangle", "Triangle created with vertices: " + v1 + " " + v2 + " " + v3);
            }

            Triangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3) {
                this.x1 = x1;
                this.y1 = y1;
                this.z1 = z1;
                this.x2 = x2;
                this.y2 = y2;
                this.z2 = z2;
                this.x3 = x3;
                this.y3 = y3;
                this.z3 = z3;

                v1 = new Vector3(x1, y1, z1);
                v2 = new Vector3(x2, y2, z2);
                v3 = new Vector3(x3, y3, z3);

                edge1 = new Vector3(x2 - x1, y2 - y1, z2 - z1);
                edge2 = new Vector3(x3 - x1, y3 - y1, z3 - z1);
                edge3 = new Vector3(x3 - x2, y3 - y2, z3 - z2);

                normal = edge1.crs(edge2).nor();
                vertices = new Vector3[] {new Vector3(x1, y1, z1), new Vector3(x2, y2, z2), new Vector3(x3, y3, z3)};

                Log.info("Triangle", "Triangle created with vertices: " + x1 + " " + y1 + " " + z1 + " " + x2 + " " + y2 + " " + z2 + " " + x3 + " " + y3 + " " + z3);
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

        class Vertex
        {
            public float x;
            public float y;
            public float z;

            public Vertex(float x, float y, float z) {
                this.x = x;
                this.y = y;
                this.z = z;
            }

            public Vertex(Vector3 vec){
                this.x = vec.x;
                this.y = vec.y;
                this.z = vec.z;
            }
        }

        public Array<Vertex> verticesArray = new Array<Vertex>();

        public Array<Triangle> triangles = new Array<Triangle>();

        public MeshInfo(Mesh mesh){
            this.mesh = mesh;
            getAttributes();
            getOffsets();
            getCounts();
            allocateArrays();
            traverse();
        }

        private void getAttributes(){
            attributes = mesh.getVertexAttributes();
            positionAttribute = attributes.findByUsage(VertexAttributes.Usage.Position);
            normalAttribute = attributes.findByUsage(VertexAttributes.Usage.Normal);
            colorAttribute = attributes.findByUsage(VertexAttributes.Usage.ColorPacked);
        }

        private void getOffsets(){
            if (positionAttribute != null) posOffset = positionAttribute.offset / 4;
            if (normalAttribute != null) norOffset = normalAttribute.offset / 4;
            if (colorAttribute != null) colOffset = colorAttribute.offset / 4;
            int offset = attributes.getOffset(VertexAttributes.Usage.Position);



        }

        private void getCounts() {
            vertexCount = mesh.getNumVertices()*mesh.getVertexSize()/4;
            vertexSize = mesh.getVertexSize()/4;
            indexCount = mesh.getNumIndices();

            triangleCount = indexCount / 3;

            Log.info("MeshInfo", "Vertex Count: " + vertexCount+" Vertex Size: "+vertexSize+" Index Count: "+indexCount+" Triangle Count: "+triangleCount);
        }

        private void allocateArrays(){
            vertices = new float[vertexCount];
            indices = new short[indexCount];
            normals = new float[vertexCount];
            mesh.getVertices(norOffset,norOffset+3,normals);
            mesh.getVertices(vertices);
            mesh.getIndices(indices);
            Log.info("MeshInfo", "Arrays allocated...");
        }

        private void traverse(){
            for (int i = 0; i < vertices.length; i += vertexSize){
                float x = vertices[i + posOffset];
                float y = vertices[i + 1 + posOffset];
                float z = vertices[i + 2 + posOffset];

                float nx = normals[i + norOffset];
                float ny = normals[i + 1 + norOffset];
                float nz = normals[i + 2 + norOffset];
                Vector3 normal = new Vector3(normals[i] , normals[i + 1] , normals[i + 2]);
                Vector3 vertex = new Vector3(x , y , z);
                verticesArray.add(new Vertex(vertex));
                Vector3 newVertex = vertex.add(normal);



                vertices[i + offset] = newVertex.x;
                vertices[i + 1 + offset] = newVertex.y;
                vertices[i + 2 + offset] = newVertex.z;

            }

            for (int i = 0; i < indices.length; i += 3){
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
                Triangle triangle = new Triangle(x1, y1, z1, x2, y2, z2, x3, y3, z3);
               // Triangle triangle = new Triangle(index1, index2, index3, v1, v2, v3);
                triangles.add(triangle);
            }
        }








        }

    }
