package com.mygdx.polyplot;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.SnapshotArray;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled;

public class PolyPlot extends ApplicationAdapter {

	private Sprite map;
	private SpriteBatch batch;
	private Stage stage;
	public static Skin skin;
	private TextureAtlas atlas;
	private AssetManager assets;
	private BitmapFont font;
	private BitmapFont headerFont;
	private ToggleButton addPointButton;
	private ToggleButton deleteButton;
	private ToggleButton selectButton;
	private ToggleButton spawnBeaconButton;
	private TextButton saveButton;
	private Table table;
	private OrthographicCamera camera;
	private InputMultiplexer inputMultiplexer;

	private ArrayList<MapObject> selected;

	private Window window;
	private MultiProperties multiProperties;

	private ShapeRenderer shapeRenderer;
	private Array<PolygonBody> polygons;
	private Array<SpawnBeacon> beacons;

	@Override
	public void create ()
	{
		init();
	}

	@Override
	public void render ()
	{
		Gdx.gl.glClearColor(.15f, .15f, .15f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		this.camera.update();

		this.batch.setProjectionMatrix(camera.combined);
		this.batch.begin();
		this.map.draw(this.batch);
		this.batch.end();

		this.shapeRenderer.setProjectionMatrix(this.camera.combined);
		this.shapeRenderer.begin();
		for(int i = 0; i < this.beacons.size; i ++)
		{
			int width;
			if(this.beacons.get(i).isSelected())
				width = 50;
			else if(this.beacons.get(i).isMouseOvered())
				width = 35;
			else
				width = 25;
			this.shapeRenderer.setColor(this.beacons.get(i).getColor());
			this.shapeRenderer.set(Filled);
			this.shapeRenderer.ellipse(this.beacons.get(i).getPosition().x - width / 2, this.beacons.get(i).getPosition().y - width / 4, width, width / 2);
			for(int k = 0; k < this.beacons.get(i).getProperties().getWindow().getChildren().size; k++)
			{
				if(this.beacons.get(i).getProperties().getWindow().getChildren().get(k).getClass() == TextField.class)
				{
					TextField property = (TextField) this.beacons.get(i).getProperties().getWindow().getChildren().get(k);
					if(property.getText().equals("sprite"))
					{
						TextField value = (TextField) this.beacons.get(i).getProperties().getWindow().getChildren().get(k + 1);
						Sprite sprite = this.beacons.get(i).getSprite();
						if(sprite == null)
						{
							try
							{
								sprite = this.beacons.get(i).createSprite(value.getText(), this.beacons.get(i).getPosition());
								this.shapeRenderer.end();
								this.batch.begin();
								sprite.draw(this.batch);
								this.batch.end();
								this.shapeRenderer.begin();
							}
							catch (GdxRuntimeException e) {}
						}
						else
						{
							this.shapeRenderer.end();
							this.batch.begin();
							sprite.draw(this.batch);
							this.batch.end();
							this.shapeRenderer.begin();
						}
					}
				}
			}
		}
		for(int i = 0; i < this.polygons.size; i++)
		{
			this.shapeRenderer.setColor(this.polygons.get(i).getColor());
			this.shapeRenderer.set(Filled);
			if(this.polygons.get(i).getVertices().size > 1)
			{
				for (int k = 0; k <= this.polygons.get(i).getVertices().size; k++)
				{
					int width;
					if(this.polygons.get(i).isSelected())
						width = 7;
					else if((this.polygons.get(i).isComplete() && this.polygons.get(i).isMouseOvered()))
						width = 4;
					else
						width = 2;
					if (k + 1 >= this.polygons.get(i).getVertices().size)
					{
						this.shapeRenderer.rectLine(this.polygons.get(i).getVertices().get(0), this.polygons.get(i).getVertices().get(this.polygons.get(i).getVertices().size - 1), width);
						this.shapeRenderer.circle(this.polygons.get(i).getVertices().get(this.polygons.get(i).getVertices().size - 1).x, this.polygons.get(i).getVertices().get(this.polygons.get(i).getVertices().size - 1).y, Gdx.graphics.getWidth() / 300);
					}
					else
					{
						this.shapeRenderer.rectLine(this.polygons.get(i).getVertices().get(k), this.polygons.get(i).getVertices().get(k + 1), width);
						this.shapeRenderer.circle(this.polygons.get(i).getVertices().get(k).x, this.polygons.get(i).getVertices().get(k).y, Gdx.graphics.getWidth() / 300);
					}
				}
			}
			else if(this.polygons.get(i).getVertices().size > 0)
				this.shapeRenderer.circle(this.polygons.get(i).getVertices().get(0).x, this.polygons.get(i).getVertices().get(0).y, Gdx.graphics.getWidth() / 300);
		}
		this.shapeRenderer.end();

		this.stage.draw();
	}
	
	@Override
	public void dispose () { }

	private void init()
	{
		this.inputMultiplexer = new InputMultiplexer();
		this.camera = new OrthographicCamera();
		this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.batch = new SpriteBatch();
		this.map = new Sprite(new Texture("map.png"));

		this.shapeRenderer = new ShapeRenderer();
		this.shapeRenderer.setAutoShapeType(true);
		this.polygons = new Array<PolygonBody>();
		this.beacons = new Array<SpawnBeacon>();

		this.selected = new ArrayList<MapObject>();

		initFonts();
		initTextures();
		initStage();

		this.inputMultiplexer.addProcessor(this.stage);
		this.inputMultiplexer.addProcessor(new Input(this));
		Gdx.input.setInputProcessor(this.inputMultiplexer);
	}

	private void initFonts()
	{
		int size = Gdx.graphics.getHeight() / 30;

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/sitka.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();

		params.size = size;
		params.color = Color.WHITE;
		font = generator.generateFont(params);

		params.size = (int) (size * 2.5);
		headerFont = generator.generateFont(params);
	}

	private void initTextures()
	{
		this.assets = new AssetManager();
		this.assets.load("ui/ui.pack", TextureAtlas.class);
		this.assets.finishLoading();

		this.atlas = new TextureAtlas();
		this.atlas = assets.get("ui/ui.pack");

		this.skin = new Skin();
		this.skin.addRegions(this.atlas);
		this.skin.add("default-font", font);
		this.skin.add("header-font", headerFont);
		this.skin.load(Gdx.files.internal("ui/ui.json"));
	}

	private void initStage()
	{
		initButtons();

		this.table = new Table();
		this.table.setSkin(this.skin);
		this.table.add(this.addPointButton).size(this.addPointButton.getWidth(), this.addPointButton.getHeight()).pad(Gdx.graphics.getWidth() / 300);
		this.table.add(this.spawnBeaconButton).size(this.spawnBeaconButton.getWidth(), this.spawnBeaconButton.getHeight()).pad(Gdx.graphics.getWidth() / 300);
		this.table.add(this.selectButton).size(this.selectButton.getWidth(), this.selectButton.getHeight()).pad(Gdx.graphics.getWidth() / 300);
		this.table.add(this.deleteButton).size(this.deleteButton.getWidth(), this.deleteButton.getHeight()).pad(Gdx.graphics.getWidth() / 300);
		this.table.add(this.saveButton).size(this.saveButton.getWidth(), this.saveButton.getHeight()).pad(Gdx.graphics.getWidth() / 300);
		this.table.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getWidth() / 35);

		this.multiProperties = new MultiProperties(this);

		this.stage = new Stage();
		this.stage.addActor(this.table);
	}

	private void initButtons()
	{
		this.addPointButton = new ToggleButton("Add Point", this.skin);
		this.addPointButton.setSize(Gdx.app.getGraphics().getWidth() / 6, Gdx.app.getGraphics().getHeight() / 12);
		this.addPointButton.getLabel().setColor(Color.BLACK);
		this.addPointButton.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				addPointButton.toggle();
				selectButton.toggle(false);
				deleteButton.toggle(false);
				spawnBeaconButton.toggle(false);
			}
		});


		this.selectButton = new ToggleButton("Select Shape", this.skin);
		this.selectButton.setSize(Gdx.app.getGraphics().getWidth() / 6, Gdx.app.getGraphics().getHeight() / 12);
		this.selectButton.getLabel().setColor(Color.BLACK);
		this.selectButton.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				selectButton.toggle();
				deleteButton.toggle(false);
				addPointButton.toggle(false);
				spawnBeaconButton.toggle(false);
				if(selected.size() > 0 && !selectButton.isToggled())
					selected.get(0).setSelected(false);
			}
		});

		this.deleteButton = new ToggleButton("Delete Shape", this.skin);
		this.deleteButton.setSize(Gdx.app.getGraphics().getWidth() / 6, Gdx.app.getGraphics().getHeight() / 12);
		this.deleteButton.getLabel().setColor(Color.BLACK);
		this.deleteButton.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				deleteButton.toggle();
				selectButton.toggle(false);
				addPointButton.toggle(false);
				spawnBeaconButton.toggle(false);
			}
		});

		this.spawnBeaconButton = new ToggleButton("Spawn Beacon", this.skin);
		this.spawnBeaconButton.setSize(Gdx.app.getGraphics().getWidth() / 6, Gdx.app.getGraphics().getHeight() / 12);
		this.spawnBeaconButton.getLabel().setColor(Color.BLACK);
		this.spawnBeaconButton.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				spawnBeaconButton.toggle();
				deleteButton.toggle(false);
				selectButton.toggle(false);
				addPointButton.toggle(false);
			}
		});

		this.saveButton = new TextButton("Save", this.skin);
		this.saveButton.setSize(Gdx.app.getGraphics().getWidth() / 6, Gdx.app.getGraphics().getHeight() / 12);
		this.saveButton.getLabel().setColor(Color.BLACK);
		this.saveButton.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				save();
			}
		});
	}





	public void save()
	{
		PrintWriter writer = null;

		try {
			writer = new PrintWriter("map.pp", "UTF-8");
			writer.println("map.png\n");
			for(int i = 0; i < this.polygons.size; i ++)
			{
				writer.println("<polygon>");
				writer.print("coordinates ");
				Array<Vector2> vertices = this.polygons.get(i).getVertices();
				for(int k = 0; k < vertices.size; k++)
				{
					writer.print(vertices.get(k).x + " " + vertices.get(k).y);
					if(k < vertices.size - 1)
						writer.print(" ");
					else
						writer.println();
				}
				SnapshotArray<Actor> properties = this.polygons.get(i).getProperties().getWindow().getChildren();
				saveProperties(writer, properties);
				writer.println("</polygon>\n");
			}

			for(int i = 0; i < this.beacons.size; i ++)
			{
				writer.println("<beacon>");
				writer.print("coordinates ");
				Vector2 vertice = this.beacons.get(i).getPosition();
				writer.println(vertice.x + " " + vertice.y);
				SnapshotArray<Actor> properties = this.beacons.get(i).getProperties().getWindow().getChildren();
				saveProperties(writer, properties);
				writer.println("</beacon>\n");
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void saveProperties(PrintWriter writer, SnapshotArray<Actor> properties)
	{
		int skipIndex = 0;
		for(int k = 2; k < properties.size; k++)
		{
			skipIndex ++;
			if(skipIndex == 3)
			{
				skipIndex = 0;
				continue;
			}
			else if(skipIndex == 1)
			{
				TextField property = (TextField) properties.get(k);
				if(property.getText().isEmpty())
					continue;
				writer.print("\"" + property.getText() + "\" ");
			}
			else if(skipIndex == 2)
			{
				TextField property = (TextField) properties.get(k - 1);
				TextField value = (TextField) properties.get(k);
				if(property.getText().isEmpty())
					continue;
				else if(value.getText().isEmpty())
				{
					writer.println();
					continue;
				}
				writer.println("\"" + value.getText() + "\"");
			}
		}
	}







	public OrthographicCamera getCamera() { return camera; }
	public ToggleButton getAddPointButton() { return this.addPointButton; }
	public ToggleButton getDeleteButton() { return this.deleteButton; }
	public ToggleButton getSelectButton() { return this.selectButton; }
	public ToggleButton getSpawnBeaconButton() { return this.spawnBeaconButton; }
	public Array<PolygonBody> getPolygons() { return this.polygons; }
	public Array<SpawnBeacon> getBeacons() { return this.beacons; }
	public ArrayList<MapObject> getSelected() { return selected; }
	public void addSelected(MapObject mapObject)
	{
		if(this.selected != null && this.selected.contains(mapObject))
			return;
		if(this.window != null)
			this.window.remove();
		if(!Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.CONTROL_LEFT))
			this.selected.clear();
		this.selected.add(mapObject);
		if(mapObject != null)
		{
			if(this.selected.size() == 1)
				this.window = this.selected.get(0).getProperties().getWindow();
			else
				this.window = multiProperties.getWindow();
			this.stage.addActor(this.window);
		}
	}
	public void updateSelected()
	{
		if(this.window != null)
			this.window.remove();
		if(this.selected.size() == 1)
			this.window = this.selected.get(0).getProperties().getWindow();
		else
		{
			this.multiProperties.refresh();
			this.window = multiProperties.getWindow();
		}
		this.stage.addActor(this.window);
	}
}
