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
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

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
	private ToggleButton deleteShapeButton;
	private ToggleButton selectShapeButton;
	private TextButton saveButton;
	private Table table;
	private OrthographicCamera camera;
	private InputMultiplexer inputMultiplexer;

	private PolygonBody selectedPolygon;

	private Window window;

	private ShapeRenderer shapeRenderer;
	private Array<PolygonBody> polygons;

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
		for(int i = 0; i < this.polygons.size; i++)
		{
			this.shapeRenderer.setColor(this.polygons.get(i).getColor());
			this.shapeRenderer.set(Filled);
			if(this.polygons.get(i).getVertices().size > 1)
			{
				for (int k = 0; k <= this.polygons.get(i).getVertices().size; k++)
				{
					int width;
					if(this.polygons.get(i).isSelected() || (this.polygons.get(i).isComplete() && this.polygons.get(i).isMouseOvered()))
						width = 7;
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
	public void dispose ()
	{
	}

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
		this.table.add(this.selectShapeButton).size(this.selectShapeButton.getWidth(), this.selectShapeButton.getHeight()).pad(Gdx.graphics.getWidth() / 300);
		this.table.add(this.deleteShapeButton).size(this.deleteShapeButton.getWidth(), this.deleteShapeButton.getHeight()).pad(Gdx.graphics.getWidth() / 300);
		this.table.add(this.saveButton).size(this.saveButton.getWidth(), this.saveButton.getHeight()).pad(Gdx.graphics.getWidth() / 300);
		this.table.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getWidth() / 35);

		this.window = new Window("Properties", this.skin);
		this.window.setSize(Gdx.graphics.getWidth() / 2.8f, Gdx.graphics.getHeight() / 1.15f);
		this.window.setPosition(Gdx.graphics.getWidth() / 1.005f - this.window.getWidth(), Gdx.graphics.getHeight() / 9);

		this.stage = new Stage();
		this.stage.addActor(this.table);
		this.stage.addActor(this.window);
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
				selectShapeButton.toggle(false);
				deleteShapeButton.toggle(false);
			}
		});


		this.selectShapeButton = new ToggleButton("Select Shape", this.skin);
		this.selectShapeButton.setSize(Gdx.app.getGraphics().getWidth() / 6, Gdx.app.getGraphics().getHeight() / 12);
		this.selectShapeButton.getLabel().setColor(Color.BLACK);
		this.selectShapeButton.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				selectShapeButton.toggle();
				deleteShapeButton.toggle(false);
				addPointButton.toggle(false);
				if(selectedPolygon != null && !selectShapeButton.isToggled())
					selectedPolygon.setSelected(false);
			}
		});

		this.deleteShapeButton = new ToggleButton("Delete Shape", this.skin);
		this.deleteShapeButton.setSize(Gdx.app.getGraphics().getWidth() / 6, Gdx.app.getGraphics().getHeight() / 12);
		this.deleteShapeButton.getLabel().setColor(Color.BLACK);
		this.deleteShapeButton.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				deleteShapeButton.toggle();
				selectShapeButton.toggle(false);
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
		// TODO
		System.out.println("save");
	}

	public OrthographicCamera getCamera() { return camera; }
	public ToggleButton getAddPointButton() { return this.addPointButton; }
	public ToggleButton getDeleteShapeButton() { return this.deleteShapeButton; }
	public ToggleButton getSelectShapeButton() { return this.selectShapeButton; }
	public Array<PolygonBody> getPolygons() { return this.polygons; }
	public PolygonBody getSelectedPolygon() { return selectedPolygon; }
	public void setSelectedPolygon(PolygonBody polygonBody)
	{
		if(this.selectedPolygon != null && this.selectedPolygon == polygonBody)
			return;
		this.window.remove();
		this.selectedPolygon = polygonBody;
		if(polygonBody != null)
		{
			this.window = this.selectedPolygon.getProperties().getWindow();
			this.stage.addActor(this.window);
		}
	}
}
