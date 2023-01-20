package ui.tools;

import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.*;
import editor.Context;

public class BulletPhysicsTool extends AbstractTool
{
    Context ctx;
    BulletPhysicsToolWindow window;
    public boolean visible;

    public BulletPhysicsTool(Context ctx)
    {
        this.ctx = ctx;
        window = new BulletPhysicsToolWindow(ctx);

    }

    @Override
    public void enable() {
        ctx.getStage().addActor(window);
        visible = true;

    }

    @Override
    public void disable() {
        window.remove();
        visible = false;
    }

    public static class BulletPhysicsToolWindow extends VisWindow{
            Context context;
            BulletPhysicsToolTable table;
            public BulletPhysicsToolWindow(Context context){
                super("Bullet Physics Tool");
                table = new BulletPhysicsToolTable(context);
                setSize(350,200);
                align(Align.topLeft);
                pad(15);
                add(table).expand().fill().grow();
            }

    }

    public static class BulletPhysicsToolTable extends VisTable
    {
        Context context;
        VisLabel gravityLabel;
        VisValidatableTextField gravityInputField;
        VisCheckBox gravityCheckBox;
        VisCheckBox debugCheckBox;

        public BulletPhysicsToolTable(Context context)
        {
            super();
            setFillParent(true);
            create();
        }

        private void create() {
            gravityLabel = new VisLabel("Gravity");
            gravityInputField = new VisValidatableTextField();
            gravityInputField.setWidth(20);
            gravityCheckBox = new VisCheckBox("Enable Gravity");
            debugCheckBox = new VisCheckBox("Enable Debug");

            add(gravityLabel).left().pad(5);
            row();
            add(gravityInputField).left().pad(5);
            add(gravityCheckBox).left().pad(5);
            row();
            add(debugCheckBox).left().pad(5);
        }

    }

}
