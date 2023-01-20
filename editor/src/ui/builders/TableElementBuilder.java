package ui.builders;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import org.apache.commons.lang3.builder.Builder;

public class TableElementBuilder implements Builder<VisTable>, ElementBuilder<TableElementBuilder>
{

    private VisTable table;

    public TableElementBuilder() {
        table = new VisTable();
    }

    @Override
    public VisTable build() {
        return table;
    }

    @Override
    public TableElementBuilder addTo(Table table) {
        table.add(this.table);
        return this;
    }

    @Override
    public TableElementBuilder addTo(VisWindow window) {
        window.add(this.table);
        return this;
    }

    @Override
    public TableElementBuilder width(float width) {
        this.table.setWidth(width);
        return this;
    }

    @Override
    public TableElementBuilder height(float height) {
        this.table.setHeight(height);
        return this;
    }

    @Override
    public TableElementBuilder fillParent(boolean fillParent) {
        this.table.setFillParent(fillParent);
        return this;
    }

    @Override
    public TableElementBuilder align(int align) {
        this.table.align(align);
        return this;
    }

}
