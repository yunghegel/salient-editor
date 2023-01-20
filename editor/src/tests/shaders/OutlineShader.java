package tests.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class OutlineShader implements Shader
{
    ShaderProgram program;
    Camera camera;
    RenderContext context;


    @Override
    public void init() {
        String vert = Gdx.files.internal("shaders/outline.vs.glsl").readString();
        String frag = Gdx.files.internal("shaders/outline.fs.glsl").readString();
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
        return true;
    }

    @Override
    public void begin(Camera camera , RenderContext context) {

        this.camera = camera;
        this.context = context;
        program.begin();
        program.setUniformMatrix("u_projTrans", camera.combined);
        program.setUniform4fv("u_outer_color", new float[]{0f,0f, 0, 1f}, 0, 4);
        program.setUniform4fv("u_inner_color", new float[]{.5f, .5f, 1, 1f}, 0, 4);
        program.setUniformf("u_size", 1);
       // program.setUniformf("u_depthRange", 1);
        program.setUniformf("u_depth_min", 0);
        program.setUniformf("u_depth_max", .5f);
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setCullFace(GL20.GL_BACK);

    }

    @Override
    public void render(Renderable renderable) {
        //program.setUniformMatrix("u_worldTrans", renderable.worldTransform);
        program.setUniformMatrix("u_projTrans", camera.combined);
        renderable.meshPart.render(program);

    }

    @Override
    public void end() {
        program.end();
    }

    @Override
    public void dispose() {
        program.dispose();

    }

}
