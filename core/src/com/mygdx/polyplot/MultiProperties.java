package com.mygdx.polyplot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;

public class MultiProperties
{
    private static int duplicateIndex = 0;

    private TextButton newProperty;

    private Window window;

    private PolyPlot polyPlot;

    public MultiProperties(PolyPlot polyPlot)
    {
        this.polyPlot = polyPlot;
        this.window = new Window("Multi-Properties", PolyPlot.skin);
        this.window.setSize(Gdx.graphics.getWidth() / 2.8f, Gdx.graphics.getHeight() / 1.15f);
        this.window.setPosition(Gdx.graphics.getWidth() / 1.005f - this.window.getWidth(), Gdx.graphics.getHeight() / 9);

        this.newProperty = new TextButton("Add Property", PolyPlot.skin);
        this.newProperty.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                addProperty("", "");
            }
        });

        this.window.add(this.newProperty).row();

        addProperty("Name", "Map Object " + duplicateIndex);
        duplicateIndex ++;
    }

    public void addProperty(String property, String value)
    {
        for(int i = 0; i < this.polyPlot.getSelected().size(); i ++)
            this.polyPlot.getSelected().get(i).getProperties().addProperty(property, value);

        refresh();
    }

    public void refresh()
    {
        int size = this.polyPlot.getSelected().size();
        ArrayList<PropertyField> sharedProperties = new ArrayList<PropertyField>();
        if(size > 0)
            sharedProperties.addAll(this.polyPlot.getSelected().get(0).getProperties().getPropertiesAndValues());
        for(int i = 1; i < size; i++)
            sharedProperties.retainAll(this.polyPlot.getSelected().get(i).getProperties().getPropertiesAndValues());
        this.window.clear();
        this.window.add(this.newProperty).row();
        for(int i = 0; i < sharedProperties.size(); i++)
        {
            TextField property = new TextField(sharedProperties.get(i).getProperty().getText(), PolyPlot.skin);
            TextField value = new TextField(sharedProperties.get(i).getValue().getText(), PolyPlot.skin);
            TextButton button = new TextButton("x", PolyPlot.skin);
            button.addListener(new RemoveSyncListener(window, property));
            button.addListener(new RemoveSyncListener(window, value));
            for(int k = 0; k < this.polyPlot.getSelected().size(); k++)
            {
                for(int s = 0; s < this.polyPlot.getSelected().get(k).getProperties().getPropertiesAndValues().size(); s++)
                {
                    if(!this.polyPlot.getSelected().get(k).getProperties().getPropertiesAndValues().get(s).equals(sharedProperties.get(i)))
                        continue;
                    property.addListener(new InputSyncListener(property, this.polyPlot.getSelected().get(k).getProperties().getPropertiesAndValues().get(s).getProperty()));
                    property.addListener(new InputSyncListener(value, this.polyPlot.getSelected().get(k).getProperties().getPropertiesAndValues().get(s).getValue()));
                    button.addListener(new RemoveSyncListener(this.polyPlot.getSelected().get(k).getProperties().getWindow(), this.polyPlot.getSelected().get(k).getProperties().getPropertiesAndValues().get(s).getProperty(), this.polyPlot.getSelected().get(k).getProperties().getPropertiesAndValues()));
                    button.addListener(new RemoveSyncListener(this.polyPlot.getSelected().get(k).getProperties().getWindow(), this.polyPlot.getSelected().get(k).getProperties().getPropertiesAndValues().get(s).getValue()));
                    button.addListener(new RemoveSyncListener(this.polyPlot.getSelected().get(k).getProperties().getWindow(), this.polyPlot.getSelected().get(k).getProperties().getPropertiesAndValues().get(s).getRemoveButton()));
                }
            }
            button.addListener(new RemoveSyncListener(window, button));
            this.window.add(property).pad(2).padBottom(Gdx.graphics.getWidth() / 100);
            this.window.add(value).pad(2).padBottom(Gdx.graphics.getWidth() / 100);
            this.window.add(button).row();
        }
    }

    public Window getWindow() { return this.window; }
}
