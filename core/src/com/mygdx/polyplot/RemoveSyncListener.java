package com.mygdx.polyplot;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;

public class RemoveSyncListener extends ClickListener
{
    private Window window;
    private Actor toRemove; // Whatever to remove when button is pressed
    private ArrayList<PropertyField> properties;
    public RemoveSyncListener(Window window, Actor toRemove)
    {
        this.window = window;
        this.toRemove = toRemove;
    }
    public RemoveSyncListener(Actor toRemove)
    {
        this.toRemove = toRemove;
    }

    public RemoveSyncListener(Window window, TextField property, ArrayList<PropertyField> properties)
    {
        this.window = window;
        this.properties = properties;
        this.toRemove = property;
    }

    @Override
    public void clicked(InputEvent event, float x, float y)
    {
        if(window != null)
        {
            try {
                window.getCell(toRemove).pad(0);
            }catch(NullPointerException e){}
        }
        if(toRemove.getClass() == TextField.class && properties != null)
        {
            for(int i = 0; i < properties.size(); i ++)
            {
                if(properties.get(i).getProperty().equals(toRemove))
                    properties.remove(i);
            }
        }
        this.toRemove.remove();
    }
}