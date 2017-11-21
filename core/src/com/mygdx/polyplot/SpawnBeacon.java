package com.mygdx.polyplot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class SpawnBeacon implements MapObject
{
    private Color color;
    private Properties properties;
    private OrthographicCamera camera;
    private PolyPlot polyPlot;
    private Vector2 position;
    private boolean selected = false;
    private static Vector3 unprojectedPoint = new Vector3();
    private Sprite sprite;

    public SpawnBeacon(PolyPlot polyPlot, Vector2 position)
    {
        this.position = position;
        this.polyPlot = polyPlot;
        this.camera = polyPlot.getCamera();
        this.color = new Color(Utils.randomFloat(0, 1), Utils.randomFloat(0, 1), Utils.randomFloat(0, 1), .5f);
        this.properties = new Properties(this);
    }

    public Color getColor() { return this.color; }

    @Override
    public boolean isMouseOvered()
    {
        unprojectedPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        this.camera.unproject(unprojectedPoint);
        if(this.position.dst(unprojectedPoint.x, unprojectedPoint.y) < 20)
            return true;
        return false;
    }

    @Override
    public void setSelected(boolean selected)
    {
        if(selected)
        {
            if(this.polyPlot.getSelected().size() > 0 && !Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
            {
                for(int i = 0; i < this.polyPlot.getSelected().size(); i ++)
                {
                    this.polyPlot.getSelected().get(i).setSelected(false);
                    this.polyPlot.getSelected().clear();
                }
            }
            this.polyPlot.addSelected(this);
        }
        else
            this.polyPlot.getSelected().remove(this);
        this.selected = selected;
        this.polyPlot.updateSelected();
    }

    @Override
    public Properties getProperties() { return this.properties; }

    @Override
    public boolean isSelected() { return this.selected; }

    public Vector2 getPosition() { return this.position; }

    public Sprite createSprite(String path, Vector2 position) throws GdxRuntimeException
    {
        this.sprite = new Sprite(new Texture(path));
        this.sprite.setPosition(position.x - this.sprite.getWidth() / 2, position.y);
        return this.sprite;
    }

    public Sprite getSprite() { return this.sprite; }

    public void setSprite(Sprite sprite) { this.sprite = sprite; }

}
