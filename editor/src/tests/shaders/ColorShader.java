package tests.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ColorShader implements Shader
{
    ShaderProgram program;
    Camera camera;
    RenderContext context;
    float[] color = new float[4];
    @Override
    public void init() {
        String vert = Gdx.files.internal("shaders/color.vs.vert").readString();
        String frag = Gdx.files.internal("shaders/color.fs.frag").readString();
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
      //  program.setUniformMatrix("u_projTrans", camera.combined);
       // program.setUniform4fv("u_color", new float[]{.5f, 1, 1, 1f}, 0, 4);
    }

    @Override
    public void render(Renderable renderable) {
        //program.setUniformMatrix("u_worldTrans", renderable.worldTransform);
      //  program.setUniform4fv("u_color", color, 0, 4);

        program.setUniformMatrix("u_projTrans", camera.combined);
        program.setUniformMatrix("u_worldTrans", renderable.worldTransform);
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

    public static class ColorShaderProvider extends DefaultShaderProvider
    {
        @Override
        public Shader getShader(Renderable renderable) {
            return new ColorShader();
        }

        @Override
        public void dispose() {

        }

        @Override
        protected Shader createShader(Renderable renderable) {
            return new ColorShader();
        }

    }

    public void setColor(float r, float g, float b, float a)
    {
        color[0] = r;
        color[1] = g;
        color[2] = b;
        color[3] = a;
    }

}
