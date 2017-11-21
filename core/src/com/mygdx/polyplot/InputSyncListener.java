package com.mygdx.polyplot;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class InputSyncListener implements EventListener
{
    private TextField writeIn; // Should be writing in this textfield
    private TextField sync; // Should sync to whatever writeIn has
    public InputSyncListener(TextField writeIn, TextField sync)
    {
        this.writeIn = writeIn;
        this.sync = sync;
    }

    @Override
    public boolean handle(Event event)
    {
        this.sync.setText(writeIn.getText());
        return false;
    }
}
