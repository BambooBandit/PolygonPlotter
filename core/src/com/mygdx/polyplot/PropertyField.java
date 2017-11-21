package com.mygdx.polyplot;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class PropertyField
{
    private TextField property;
    private TextField value;
    private TextButton remove;
    private Window window;

    public PropertyField(Window window, TextField property, TextField value, TextButton remove)
    {
        this.window = window;
        this.property = property;
        this.value = value;
        this.remove = remove;
    }

    public TextField getProperty() { return this.property; }
    public TextField getValue() { return this.value; }
    public TextButton getRemoveButton() { return this.remove; }
    public Window getWindow() { return this.window; }

    @Override
    public boolean equals(Object obj)
    {
        PropertyField other = (PropertyField) obj;
        return (other.getProperty().getText().equals(property.getText()) && other.getValue().getText().equals(value.getText()));
    }
}
