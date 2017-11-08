package com.mygdx.polyplot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class PolygonBody implements MapObject
{
    private Array<Vector2> vertices;
    private Polygon polygon;
    private Color color;

    private boolean complete = false;
    private boolean selected = false;

    private OrthographicCamera camera;
    private PolyPlot polyPlot;

    private static Vector3 unprojectedPoint = new Vector3();
    private static Vector2 unprojectedPoint0 = new Vector2();
    private static Vector2 unprojectedPoint1 = new Vector2();

    private Properties properties;

    public PolygonBody(PolyPlot polyPlot, Vector2 vertice)
    {
        this.polyPlot = polyPlot;
        this.camera = polyPlot.getCamera();
        this.vertices = new Array<Vector2>();
        this.vertices.add(vertice);
        this.color = new Color(Utils.randomFloat(0, 1), Utils.randomFloat(0, 1), Utils.randomFloat(0, 1), .5f);
        this.properties = new Properties();
    }

    public void addPoint(Vector2 vertice)
    {
        this.vertices.add(vertice);
    }

    public Array<Vector2> getVertices() { return this.vertices; }

    public Vector2 getUnprojectedVertice0(OrthographicCamera camera, int index)
    {
        this.unprojectedPoint.set(this.vertices.get(index).x, this.vertices.get(index).y, 0);
        camera.unproject(this.unprojectedPoint);
        this.unprojectedPoint1.set(this.unprojectedPoint.x, this.unprojectedPoint.y);
        return this.unprojectedPoint0;
    }

    public Vector2 getUnprojectedVertice1(OrthographicCamera camera, int index)
    {
        this.unprojectedPoint.set(this.vertices.get(index).x, this.vertices.get(index).y, 0);
        camera.unproject(this.unprojectedPoint);
        this.unprojectedPoint0.set(this.unprojectedPoint.x, this.unprojectedPoint.y);
        return this.unprojectedPoint1;
    }

    public Color getColor() { return this.color; }

    public void complete()
    {
        if(!complete)
        {
            if(this.getVertices().size < 3)
            {
                this.getVertices().clear();
                return;
            }
            this.complete = true;
            createPolygon();
        }
    }

    public void createPolygon()
    {
        this.polygon = new Polygon();
        float[] vertices = new float[this.getVertices().size * 2];
        for (int i = 0; i < this.getVertices().size * 2; i++)
        {
            if (i % 2 == 0)
                vertices[i] = this.getVertices().get(i / 2).x;
            else
                vertices[i] = this.getVertices().get(i / 2).y;
        }
        this.polygon.setVertices(vertices);
    }

    public boolean isComplete() { return this.complete; }

    public Polygon getPolygon() { return this.polygon; }

    @Override
    public boolean isMouseOvered()
    {
        this.unprojectedPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        this.camera.unproject(unprojectedPoint);

        if(this.polygon != null && this.polygon.contains(unprojectedPoint.x, unprojectedPoint.y))
            return true;
        return false;
    }

    @Override
    public boolean isSelected() { return this.selected; }

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

    public Properties getProperties() { return this.properties; }
}
