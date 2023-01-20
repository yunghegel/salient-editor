package ui.widgets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

abstract class AbstractWidget extends Widget
{

    public Texture icon = null;

    public String name = null;

    public String description = null;

    public void setIcon(Texture icon) {
        this.icon = icon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    abstract void enable();

    abstract void disable();

}
