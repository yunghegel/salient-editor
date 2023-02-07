package editor.tools;

import backend.tools.Log;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;

import java.nio.FloatBuffer;

public class MeshManipulationTool implements Tool<Mesh>
{

    private Mesh mesh;
    private Mesh targetMesh;
    private MeshManipulationTool.MeshManipulator meshManipulator;

    public MeshManipulationTool(Mesh mesh) {
        this.mesh = mesh;
        setSelection(mesh);
        meshManipulator = new MeshManipulator(mesh);
    }

    public MeshManipulationTool() {

    }

    @Override
    public void update() {

    }

    @Override
    public void setSelection(Mesh selection) {
        this.targetMesh = selection;
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    static class MeshManipulator
    {

        Mesh targetMesh;
        int lastFloat;
        int vertextFloats;
        VertexAttribute posAttr;
        VertexAttribute norAttr;
        FloatBuffer buf;

        public MeshManipulator(Mesh mesh) {
            this.targetMesh = mesh;
            Log.info("MeshManipulator" , "MeshManipulator created for a target mesh");
            load();
        }

        public void load() {
            buf = targetMesh.getVerticesBuffer();
            lastFloat = targetMesh.getNumVertices() * targetMesh.getVertexSize() / 4;
            vertextFloats = ( targetMesh.getVertexSize() / 4 );
            posAttr = targetMesh.getVertexAttributes().findByUsage(VertexAttributes.Usage.Position);
            norAttr = targetMesh.getVertexAttributes().findByUsage(VertexAttributes.Usage.Normal);
            if (posAttr == null || norAttr == null) {
                throw new IllegalArgumentException("Position/normal vertex attribute not found");
            }
        }

    }

}


