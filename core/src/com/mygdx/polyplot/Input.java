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
    private ToggleButton spawnBeaconButton;
    private Array<PolygonBody> polygonBodies;
    private Array<SpawnBeacon> spawnBeacons;

    private boolean makeNewPolygon;

    private PolyPlot polyPlot;

    public Input(PolyPlot polyPlot)
    {
        this.polyPlot = polyPlot;
        this.camera = polyPlot.getCamera();
        this.grabStart = new Vector3();
        this.grabCurrent = new Vector3();
        this.addPointButton = polyPlot.getAddPointButton();
        this.deleteShapeButton = polyPlot.getDeleteButton();
        this.selectShapeButton = polyPlot.getSelectButton();
        this.spawnBeaconButton = polyPlot.getSpawnBeaconButton();
        this.polygonBodies = polyPlot.getPolygons();
        this.spawnBeacons = polyPlot.getBeacons();
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
            if(this.polygonBodies.size > 0)
            {
                int size = this.polygonBodies.size - 1;
                this.polygonBodies.get(size).complete();
                if(this.polygonBodies.get(size).getVertices().size < 3)
                    this.polygonBodies.removeIndex(size);
            }
            return false;
        }

        if(addPointButton.isToggled())
        {
            Vector2 vertice = new Vector2(this.grabStart.x, this.grabStart.y);
            if(makeNewPolygon)
                this.polygonBodies.add(new PolygonBody(polyPlot, vertice));
            else
                this.polygonBodies.get(this.polygonBodies.size - 1).addPoint(vertice);
            this.makeNewPolygon = false;
        }
        else
            this.makeNewPolygon = true;

        if(selectShapeButton.isToggled())
        {
            for(int i = this.polygonBodies.size - 1; i >= 0; i --)
            {
                if(this.polygonBodies.get(i).isMouseOvered())
                {
                    if(!Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.CONTROL_LEFT))
                    {
                        for(int k = 0; k < this.polyPlot.getSelected().size(); k ++)
                        {
                            if(!this.polyPlot.getSelected().get(k).equals(this.polygonBodies.get(i)))
                                this.polyPlot.getSelected().get(k).setSelected(false);
                        }
                        this.polygonBodies.get(i).setSelected(true);
                    }
                    else
                        this.polygonBodies.get(i).setSelected(!this.polygonBodies.get(i).isSelected());
                    break;
                }
            }
            for(int i = this.spawnBeacons.size - 1; i >= 0; i --)
            {
                if(this.spawnBeacons.get(i).isMouseOvered())
                {
                    if(!Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.CONTROL_LEFT))
                    {
                        for(int k = 0; k < this.polyPlot.getSelected().size(); k ++)
                        {
                            if(!this.polyPlot.getSelected().get(k).equals(this.spawnBeacons.get(i)))
                                this.polyPlot.getSelected().get(k).setSelected(false);
                        }
                        this.spawnBeacons.get(i).setSelected(true);
                    }
                    else
                        this.spawnBeacons.get(i).setSelected(!this.spawnBeacons.get(i).isSelected());
                    break;
                }
            }
        }
        else if(deleteShapeButton.isToggled())
        {
            for(int i = this.polygonBodies.size - 1; i >= 0; i --)
            {
                if(this.polygonBodies.get(i).isMouseOvered())
                {
                    if(this.polyPlot.getSelected().size() > 0 && this.polyPlot.getSelected().contains(this.polygonBodies.get(i)))
                    {
                        this.polyPlot.getSelected().get(i).setSelected(false);
                        this.polyPlot.getSelected().remove(this.polygonBodies.get(i));
                    }
                    this.polygonBodies.removeValue(polygonBodies.get(i), false);
                    break;
                }
            }
            for(int i = this.spawnBeacons.size - 1; i >= 0; i --)
            {
                if(this.spawnBeacons.get(i).isMouseOvered())
                {
                    if(this.polyPlot.getSelected().size() > 0 && this.polyPlot.getSelected().equals(this.spawnBeacons.get(i)))
                    {
                        this.polyPlot.getSelected().get(i).setSelected(false);
                        this.polyPlot.getSelected().remove(this.spawnBeacons.get(i));
                    }
                    this.spawnBeacons.removeValue(spawnBeacons.get(i), false);
                    break;
                }
            }
        }
        else if(spawnBeaconButton.isToggled())
        {
            Vector2 position = new Vector2(this.grabStart.x, this.grabStart.y);
            this.spawnBeacons.add(new SpawnBeacon(polyPlot, position));
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
