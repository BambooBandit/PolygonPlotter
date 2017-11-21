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
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

public class Properties
{
    private static int duplicateIndex = 0;

    private TextButton newProperty;

    private ArrayList<PropertyField> properties;

    private Window window;

    private MapObject mapObject;

    public Properties(MapObject mapObject)
    {
        this.properties = new ArrayList<PropertyField>();
        this.mapObject = mapObject;
        this.window = new Window("Properties", PolyPlot.skin);
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
        TextField propertyField = new TextField(property, PolyPlot.skin);
        TextField valueField = new TextField(value, PolyPlot.skin);
        valueField.addListener(new EventListener()
        {
            @Override
            public boolean handle(Event event)
            {
                if(mapObject.getClass() == SpawnBeacon.class)
                {
                    SpawnBeacon beacon = (SpawnBeacon) mapObject;
                    beacon.setSprite(null);
                }
                return false;
            }
        });
        TextButton button = createRemoveButton(propertyField, valueField);
        this.properties.add(new PropertyField(window, propertyField, valueField, button));
        this.window.add(propertyField).pad(2).padBottom(Gdx.graphics.getWidth() / 100);
        this.window.add(valueField).pad(2).padBottom(Gdx.graphics.getWidth() / 100);
        this.window.add(button).row();
    }

    public TextButton createRemoveButton(final Actor actor0, final Actor actor1)
    {
        final TextButton button = new TextButton("x", PolyPlot.skin);
        button.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                window.getCell(actor0).pad(0);
                window.getCell(actor1).pad(0);
                window.getCell(button).pad(0);
                for(int i = 0; i < properties.size(); i ++)
                {
                    if(properties.get(i).getProperty().equals(actor0))
                        properties.remove(i);
                }
                actor0.remove();
                actor1.remove();
                button.remove();
            }
        });
        return button;
    }

    public ArrayList<PropertyField> getPropertiesAndValues() { return this.properties; }

    public Window getWindow() { return this.window; }
}
