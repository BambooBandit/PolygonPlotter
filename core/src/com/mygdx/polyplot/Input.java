package com.mygdx.polyplot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Input implements InputProcessor
{
    private OrthographicCamera camera;
    private Vector3 grabStart;
    private Vector3 grabCurrent;
    private ToggleButton addPointButton;
    private ToggleButton deleteShapeButton;
    private ToggleButton selectShapeButton;
    private Array<PolygonBody> polygons;

    private boolean makeNewPolygon;

    private PolyPlot polyPlot;

    public Input(PolyPlot polyPlot)
    {
        this.polyPlot = polyPlot;
        this.camera = polyPlot.getCamera();
        this.grabStart = new Vector3();
        this.grabCurrent = new Vector3();
        this.addPointButton = polyPlot.getAddPointButton();
        this.deleteShapeButton = polyPlot.getDeleteShapeButton();
        this.selectShapeButton = polyPlot.getSelectShapeButton();
        this.polygons = polyPlot.getPolygons();
        this.makeNewPolygon = true;
    }

    @Override
    public boolean keyDown(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyTyped(char character)
    {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        this.grabStart.set(screenX, screenY, 0);
        this.camera.unproject(this.grabStart);

        if(button == com.badlogic.gdx.Input.Buttons.RIGHT)
        {
            this.makeNewPolygon = true;
            if(this.polygons.size > 0)
                this.polygons.get(this.polygons.size - 1).complete();
            return false;
        }

        if(addPointButton.isToggled())
        {
            Vector2 vertice = new Vector2(this.grabStart.x, this.grabStart.y);
            if(makeNewPolygon)
                this.polygons.add(new PolygonBody(polyPlot, vertice));
            else
                this.polygons.get(this.polygons.size - 1).addPoint(vertice);
            this.makeNewPolygon = false;
        }
        else
            this.makeNewPolygon = true;

        if(selectShapeButton.isToggled())
        {
            for(int i = this.polygons.size - 1; i >= 0; i --)
            {
                if(this.polygons.get(i).isMouseOvered())
                {
                    this.polygons.get(i).setSelected(true);
                    break;
                }
            }
        }
        else if(deleteShapeButton.isToggled())
        {
            for(int i = this.polygons.size - 1; i >= 0; i --)
            {
                if(this.polygons.get(i).isMouseOvered())
                {
                    if(this.polyPlot.getSelectedPolygon() != null && this.polyPlot.getSelectedPolygon().equals(this.polygons.get(i)))
                    {
                        this.polyPlot.getSelectedPolygon().setSelected(false);
                        this.polyPlot.setSelectedPolygon(null);
                    }
                    this.polygons.removeValue(polygons.get(i), false);
                    break;
                }
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        this.grabCurrent.set(screenX, screenY, 0);
        this.camera.unproject(this.grabCurrent);
        this.camera.position.set(this.camera.position.x + this.grabStart.x - this.grabCurrent.x, this.camera.position.y + this.grabStart.y - this.grabCurrent.y, 0);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        return false;
    }

    @Override
    public boolean scrolled(int amount)
    {
        if(Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.SHIFT_RIGHT))
            this.camera.zoom += amount / 2f;
        else
            this.camera.zoom += amount / 10f;
        if(this.camera.zoom < 0.1f)
            this.camera.zoom = 0.1f;
        return false;
    }
}
