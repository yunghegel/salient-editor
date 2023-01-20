package ui.windows;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;

public class SelectionWindow extends VisWindow {
    public SelectionWindow(String title) {
        super(title);
        TableUtils.setSpacingDefaults(this);
        columnDefaults(0).left();
        add(createLabels()).row();
        add(createButtons()).row();
        add(createCheckboxes()).row();
        add(createTextFields()).row();

        pack();
        centerWindow();
        setFillParent(false);

        MenuBar menuBar = new MenuBar();
        VisTable menuTable = new VisTable();
        menuTable.add(menuBar.getTable()).expandX().fillX().row();

        addActor(menuTable);

    }
    private VisTable createLabels () {
        VisLabel label = new VisLabel("label (hover for tooltip)");
        new Tooltip.Builder("this label has a tooltip").target(label).build();

        VisTable labelTable = new VisTable(true);
        labelTable.add(label);
        return labelTable;
    }

    private VisTable createButtons () {
        VisTextButton normalButton = new VisTextButton("button");
        VisTextButton normalBlueButton = new VisTextButton("button blue", "blue");
        VisTextButton toggleButton = new VisTextButton("toggle", "toggle");
        VisTextButton disabledButton = new VisTextButton("disabled");
        disabledButton.setDisabled(true);

        VisTable buttonTable = new VisTable(true);
        buttonTable.add(normalButton);
        buttonTable.add(normalBlueButton);
        buttonTable.add(toggleButton);
        buttonTable.add(disabledButton);
        return buttonTable;
    }

    private VisTable createCheckboxes () {
        VisCheckBox normalCheckbox = new VisCheckBox("checkbox");
        VisCheckBox disabledCheckbox = new VisCheckBox("disabled");
        disabledCheckbox.setDisabled(true);

        VisTable checkboxTable = new VisTable(true);
        checkboxTable.add(normalCheckbox);
        checkboxTable.add(disabledCheckbox);
        return checkboxTable;
    }

    private Actor createTextFields () {
        VisTextField normalTextField = new VisTextField("textbox");
        VisTextField disabledTextField = new VisTextField("disabled");
        VisTextField passwordTextField = new VisTextField("password");
        disabledTextField.setDisabled(true);
        passwordTextField.setPasswordMode(true);

        VisTable textFieldTable = new VisTable(true);
        textFieldTable.defaults().width(120);
        textFieldTable.add(normalTextField);
        textFieldTable.add(disabledTextField);
        textFieldTable.add(passwordTextField);
        return textFieldTable;
    }
}
