package ui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;
import editor.Context;
import editor.graphics.rendering.ModelPreviewRenderer;
import ui.widgets.ModelPreviewWidget;

public class ModelPreviewWindow extends VisWindow
{
    VisCheckBox enableBoundingBoxes;
    VisCheckBox enabled;
    ModelPreviewRenderer renderer;
    ModelPreviewWidget widget;
    Context ctx;
    VisTextButton increaseZoom;
    VisTextButton decreaseZoom;
    VisTable table;

    public ModelPreviewWindow(Context context)
    {
        super("Preview");
        this.ctx = context;
        TableUtils.setSpacingDefaults(this);
        table = new VisTable();
        table.defaults().pad(2);
        add(table).grow().fill().expand();
        renderer = new ModelPreviewRenderer();
        widget = new ModelPreviewWidget(ctx.getStage(),renderer.camera);
        widget.setRenderer(renderer);
        table.add(widget).grow().fill().expand();


        pack();
        setVisible(true);
        setResizable(false);
        setMovable(false);
        increaseZoom = new VisTextButton("  +  ");
        decreaseZoom = new VisTextButton("  -  ");
        enableBoundingBoxes = new VisCheckBox("Draw Bounds");
        enableBoundingBoxes.setChecked(true);
        enableBoundingBoxes.pad(5);
        enableBoundingBoxes.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor)
            {
                renderer.drawBbox = enableBoundingBoxes.isChecked();
            }
        });
        enabled = new VisCheckBox("Enabled");
        enabled.setChecked(true);
        enabled.pad(5);
        enabled.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor)
            {
                renderer.enable = enabled.isChecked();
            }
        });

        getTitleTable().align(Align.left);
        getTitleLabel().setAlignment(Align.left);


        getTitleTable().add(enableBoundingBoxes).pad(5);
        getTitleTable().add(enabled).pad(5);
        increaseZoom.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor)
            {
                if (Gdx.input.isButtonPressed(0))
                {
                    renderer.camera.fieldOfView -= 0.01f;

                }
                renderer.camera.fieldOfView -= 2f;
            }

        });
        decreaseZoom.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor)
            {
                renderer.camera.fieldOfView += 2f;
            }

        });


        widget.addListener(new InputListener()
        {
            @Override
            public boolean scrolled(InputEvent event , float x , float y , float amountX , float amountY) {
                renderer.camera.fieldOfView += amountX;
                return super.scrolled(event , x , y , amountX , amountY);
            }


        });

        VisSlider slider = new VisSlider(0, 100, .5f, true);

        slider.setValue(renderer.camera.fieldOfView);

        slider.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor)
            {
                renderer.camera.fieldOfView = slider.getValue();
            }
        });
        table.add(slider).growY().fillY().expandY().align(Align.right).padTop(15);


        //add(widget).fill().grow();
//        setHeight(200);
    }
}



