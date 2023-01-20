package ui.builders;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.VisWindow;
import org.apache.commons.lang3.builder.Builder;

public class WindowElementBuilder implements Builder<VisWindow>, ElementBuilder<WindowElementBuilder>
{

    private VisWindow window;

    public WindowElementBuilder() {
        window = new VisWindow("");
    }

    @Override
    public VisWindow build() {
        return null;
    }

    @Override
    public WindowElementBuilder addTo(Table table) {
        table.add(this.window);
        return this;
    }

    @Override
    public WindowElementBuilder addTo(VisWindow window) {
        window.add(this.window);
        return this;
    }

    @Override
    public WindowElementBuilder width(float width) {
        this.window.setWidth(width);
        return this;
    }

    @Override
    public WindowElementBuilder height(float height) {
        this.window.setHeight(height);
        return this;
    }

    @Override
    public WindowElementBuilder fillParent(boolean fillParent) {
        this.window.setFillParent(fillParent);
        return this;
    }

    @Override
    public WindowElementBuilder align(int align) {
        this.window.align(align);
        return this;
    }

}
