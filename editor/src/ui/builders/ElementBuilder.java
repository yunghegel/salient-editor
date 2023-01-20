package ui.builders;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisWindow;

public interface ElementBuilder<T>
{

    T addTo(Table table);

    T addTo(VisWindow window);

    T width(float width);

    T height(float height);

    T fillParent(boolean fillParent);

    default T alignLeft() {
        return align(Align.left);
    }

    T align(int align);

    default T alignRight() {
        return align(Align.right);
    }

    default T alignCenter() {
        return align(Align.center);
    }

    default T alignTop() {
        return align(Align.top);
    }

    default T alignBottom() {
        return align(Align.bottom);
    }

}
