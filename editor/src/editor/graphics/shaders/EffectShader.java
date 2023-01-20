package editor.graphics.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class EffectShader implements Shader
{

    public int u_outer_color;
    public int u_inner_color;
    public int u_size;
    public int u_depthRange;
    ShaderProgram program;
    Camera camera;
    RenderContext context;
    int u_projViewTrans;
    int u_worldTrans;
    int u_depth_min;
    int u_depth_max;

    @Override
    public void init() {
        String basePath = "shaders/";
        String ext = ".glsl";
        String vert = Gdx.files.internal("shaders/outline.vs.glsl").readString();
        String frag = Gdx.files.internal("shaders/outline.fs.glsl").readString();
        program = new ShaderProgram(vert , frag);
        if (!program.isCompiled()) throw new GdxRuntimeException(program.getLog());
        u_outer_color = program.getUniformLocation("u_outer_color");
        u_inner_color = program.getUniformLocation("u_inner_color");
        u_size = program.getUniformLocation("u_size");
        u_depthRange = program.getUniformLocation("u_depthRange");
        u_projViewTrans = program.getUniformLocation("u_projViewTrans");

        u_depth_min = program.getUniformLocation("u_depth_min");
        u_depth_max = program.getUniformLocation("u_depth_max");

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

        program.setUniformMatrix("u_projTrans" , camera.combined);

        context.setDepthTest(GL20.GL_LEQUAL);
        program.setUniformf("u_depth_min" , 0);
        program.setUniformf("u_depth_max" , 1);
        context.setCullFace(GL20.GL_BACK);

        program.bind();
    }

    @Override
    public void render(Renderable renderable) {

        program.setUniformf(u_outer_color , .1f , 0.1f , 0.1f , 1);
        program.setUniformf(u_inner_color , 1.0f , 1.0f , 1.0f , 1f);

        program.setUniformf(u_size , 1f);
        program.setUniformf(u_depthRange , 1f);
        renderable.meshPart.render(program);

    }

    @Override
    public void end() {

    }

    @Override
    public void dispose() {

    }

}
