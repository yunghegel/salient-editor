package ui.builders;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisWindow;
import org.apache.commons.lang3.builder.Builder;

public class TextElementBuilder
{

    public class TextFieldBuilder implements Builder<VisTextField>, ElementBuilder<TextFieldBuilder>
    {

        private VisTextField textField;

        public TextFieldBuilder() {
            textField = new VisTextField();
        }

        public TextFieldBuilder text(String text) {
            textField.setText(text);
            return this;
        }

        public TextFieldBuilder addTo(Table table) {
            table.add(textField);
            return this;
        }

        public TextFieldBuilder addTo(VisWindow window) {
            window.add(textField);
            return this;
        }

        public TextFieldBuilder width(float width) {
            textField.setWidth(width);
            return this;
        }

        public TextFieldBuilder height(float height) {
            textField.setHeight(height);
            return this;
        }

        public TextFieldBuilder fillParent(boolean fillParent) {
            textField.setFillParent(fillParent);
            return this;
        }

        public TextFieldBuilder align(int align) {
            textField.setAlignment(align);
            return this;
        }

        @Override
        public VisTextField build() {
            return new VisTextField();
        }

    }

    public class LabelBuilder implements Builder<VisLabel>, ElementBuilder<LabelBuilder>
    {

        private VisLabel label;

        public LabelBuilder() {
            this.label = new VisLabel();
        }

        public LabelBuilder text(String text) {
            this.label.setText(text);
            return this;
        }

        public LabelBuilder x(float x) {
            this.label.setX(x);

            return this;
        }

        public LabelBuilder y(float y) {
            this.label.setY(y);
            return this;
        }

        @Override
        public VisLabel build() {
            return this.label;
        }

        public LabelBuilder addTo(Table table) {
            table.add(this.label);
            return this;
        }

        public LabelBuilder addTo(VisWindow window) {
            window.add(this.label);
            return this;
        }

        public LabelBuilder width(float width) {
            this.label.setWidth(width);
            return this;
        }

        public LabelBuilder height(float height) {
            this.label.setHeight(height);
            return this;
        }

        public LabelBuilder fillParent(boolean fillParent) {
            this.label.setFillParent(fillParent);
            return this;
        }

        public LabelBuilder align(int align) {
            this.label.setAlignment(align);
            return this;
        }

    }

}
