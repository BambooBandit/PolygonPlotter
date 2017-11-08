package com.mygdx.polyplot;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class ToggleButton extends TextButton
{
    private boolean toggle;

    public ToggleButton(String label, Skin skin)
    {
        super(label, skin);
        this.toggle = false;
    }

    public void toggle()
    {
        this.toggle = !this.toggle;
        if(this.toggle)
            this.getLabel().setColor(Color.RED);
        else
            this.getLabel().setColor(Color.BLACK);
    }

    public void toggle(boolean toggle)
    {
        this.toggle = toggle;
        if(this.toggle)
            this.getLabel().setColor(Color.RED);
        else
            this.getLabel().setColor(Color.BLACK);
    }

    public boolean isToggled()
    {
        return this.toggle;
    }
}
