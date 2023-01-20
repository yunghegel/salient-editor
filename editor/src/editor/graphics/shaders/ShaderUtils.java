package editor.graphics.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisWindow;
import games.spooky.gdx.gfx.shader.ShaderEffect;

public class ShaderUtils
{

    public static final String BASE_PATH = "shaders/";
    public static final String EXT = ".glsl";

    public static String getShaderPath(String name) {
        return Gdx.files.internal(BASE_PATH + name + EXT).readString();
    }

    public static String getShaderPath(String name , String ext) {
        return Gdx.files.internal(BASE_PATH + name + ext).readString();
    }

    public static String getShaderPath(String name , String ext , String basePath) {
        return Gdx.files.internal(basePath + name + ext).readString();
    }

    public static class ShaderUniformEditor extends VisWindow
    {

        ShaderEffect shaderEffect;

        VisTextField field1;
        VisTextField field2;
        VisTextField field3;
        float value1;
        float value2;
        float value3;

        public ShaderUniformEditor(ShaderEffect shaderEffect) {
            super("Uniform editor.Editor");
            this.shaderEffect = shaderEffect;
            buildInputFields();
            setSize(300 , 300);
            setVisible(true);

        }

        public void buildInputFields() {
            field1 = new VisTextField(".1");
            this.add(field1).row();
            field2 = new VisTextField(".1");
            this.add(field2).row();
            field3 = new VisTextField(".1");
            this.add(field3).row();

            VisTextButton button = new VisTextButton("Apply");
            this.add(button).row();

            button.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event , float x , float y) {
                    value1 = Float.parseFloat(field1.getText());
                    value2 = Float.parseFloat(field2.getText());
                    value3 = Float.parseFloat(field3.getText());

                }
            });

        }

        public VisWindow associateValues(float value1 , float value2 , float value3) {
            this.value1 = value1;
            this.value2 = value2;
            this.value3 = value3;

            return this;
        }

    }

}

