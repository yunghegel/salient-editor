package tests.shaders;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;

import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;

public class CustomDepthShaderProvider extends BaseShaderProvider
{
    public final CustomDepthShader.Config config;

    public CustomDepthShaderProvider(final CustomDepthShader.Config config) {
        this.config = (config == null) ? new CustomDepthShader.Config() : config;
    }

    public CustomDepthShaderProvider(final String vertexShader, final String fragmentShader) {
        this(new CustomDepthShader.Config(vertexShader, fragmentShader));
    }

    public CustomDepthShaderProvider(final FileHandle vertexShader, final FileHandle fragmentShader) {
        this(vertexShader.readString(), fragmentShader.readString());
    }

    public CustomDepthShaderProvider() {
        this(null);
    }

    @Override
    protected Shader createShader (final Renderable renderable) {
        return new CustomDepthShader(renderable, config);
    }
}
