package tests.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class EdgeDetectionShader implements Shader
{
    ShaderProgram program;
    Camera camera;
    RenderContext context;

    @Override
    public void init() {
        String vert = Gdx.files.internal("shaders/unpacked_depth.vs.vert").readString();
        String frag = Gdx.files.internal("shaders/unpacked_depth.fs.frag").readString();
        program = new ShaderProgram(vert, frag);
        if (!program.isCompiled())
            throw new GdxRuntimeException(program.getLog());
    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        return false;
    }

    @Override
    public void begin(Camera camera , RenderContext context) {
        this.camera = camera;
        this.context = context;
        program.bind();
        program.setUniformMatrix("u_projTrans", camera.combined);
        program.setUniform2fv("u_size", new float[]{Gdx.graphics.getWidth(), Gdx.graphics.getHeight()}, 0, 2);
    }

    @Override
    public void render(Renderable renderable) {

    }

    @Override
    public void end() {

    }

    @Override
    public void dispose() {

    }

}
