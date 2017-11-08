package com.mygdx.polyplot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class SpawnBeacon implements MapObject
{
    private Color color;
    private Properties properties;
    private OrthographicCamera camera;
    private PolyPlot polyPlot;
    private Vector2 position;
    private boolean selected = false;
    private static Vector3 unprojectedPoint = new Vector3();

    public SpawnBeacon(PolyPlot polyPlot, Vector2 position)
    {
        this.position = position;
        this.polyPlot = polyPlot;
        this.camera = polyPlot.getCamera();
        this.color = new Color(Utils.randomFloat(0, 1), Utils.randomFloat(0, 1), Utils.randomFloat(0, 1), .5f);
        this.properties = new Properties();
    }

    public Color getColor() { return this.color; }

    @Override
    public boolean isMouseOvered()
    {
        unprojectedPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        this.camera.unproject(unprojectedPoint);
        if(this.position.dst(unprojectedPoint.x, unprojectedPoint.y) < 30)
            return true;
        return false;
    }

    @Override
    public void setSelected(boolean selected)
    {
        if(selected)
        {
            if(this.polyPlot.getSelected() != null)
                this.polyPlot.getSelected().setSelected(false);
            this.polyPlot.setSelected(this);
        }
        this.selected = selected;
    }

    @Override
    public Properties getProperties() { return this.properties; }

    @Override
    public boolean isSelected() { return this.selected; }

    public Vector2 getPosition() { return this.position; }

}