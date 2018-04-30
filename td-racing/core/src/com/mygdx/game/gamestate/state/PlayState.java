package com.mygdx.game.gamestate.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.CollisionCallbackInterface;
import com.mygdx.game.CollisionListener;
import com.mygdx.game.FPSCounter;
import com.mygdx.game.MainGame;
import com.mygdx.game.MainMap;
import com.mygdx.game.Node;
import com.mygdx.game.PreferencesManager;
import com.mygdx.game.ScoreBoard;
import com.mygdx.game.ScoreBoardCallbackInterface;
import com.mygdx.game.TowerMenu;
import com.mygdx.game.controller.ControllerCallbackInterface;
import com.mygdx.game.controller.ControllerHelper;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.GameStateMethods;
import com.mygdx.game.level.Level;
import com.mygdx.game.level.LevelHandler;
import com.mygdx.game.objects.Car;
import com.mygdx.game.objects.Checkpoint;
import com.mygdx.game.objects.Enemy;
import com.mygdx.game.objects.EnemyCallbackInterface;
import com.mygdx.game.objects.FinishLine;
import com.mygdx.game.objects.Flame;
import com.mygdx.game.objects.Tower;
import com.mygdx.game.objects.checkpoints.NormalCheckpoint;
import com.mygdx.game.objects.enemies.EnemyBicycle;
import com.mygdx.game.objects.enemies.EnemyFat;
import com.mygdx.game.objects.enemies.EnemyLincoln;
import com.mygdx.game.objects.enemies.EnemySmall;
import com.mygdx.game.objects.enemies.EnemySpider;
import com.mygdx.game.objects.towers.FireTower;
import com.mygdx.game.objects.towers.LaserTower;
import com.mygdx.game.objects.towers.MgTower;
import com.mygdx.game.objects.towers.SniperTower;

public class PlayState extends GameState implements CollisionCallbackInterface, ControllerCallbackInterface,
		ScoreBoardCallbackInterface, EnemyCallbackInterface {

	public static boolean soundon = false;
	private ScoreBoard scoreBoard;

	private CollisionListener collis;
	private Sprite smaincar;
	private Sprite sfinishline;
	private World world;
	private Car car;
	private FinishLine finishline;

	private final Array<Enemy> enemies;
	private final Array<Tower> towers;
	private final PreferencesManager preferencesManager;
	private final Sprite pitStop;
	private final int moneyPerLap;

	private float tutorialtimer = 0;
	private boolean debugBox2D;
	private boolean debugCollision;
	private boolean debugEntfernung;
	private boolean carsoundPlaying = false;

	private boolean debugWay;
	private final ControllerHelper controllerHelper;
	private TowerMenu towerMenu;

	private MainMap map;

	private Tower buildingtower;

	private final Music backgroundMusic;
	private final Sound splatt, money, carsound, victorysound;

	private Sprite victory;
	private FPSCounter fpscounter;
	private int currentwave = 0;
	private boolean wongame = false;
	private boolean infiniteenemies = false;
	private boolean deploy = false;
	private boolean unlockAllTowers = false;
	private int tutorialstate = 0;

	private float physicsaccumulator = 0f;
	private Box2DDebugRenderer debugRender;

	private float timeforwavetext;
	private String wavetext;
	private boolean threadActive = false;

	private final ShapeRenderer shapeRenderer;

	/**
	 * Time for physic Steps
	 */
	public final static float TIME_STEP = 1 / 60f;
	public final static float PIXEL_TO_METER = 0.05f;
	public final static float METER_TO_PIXEL = 20f;
	public final static float RESOLUTION_WIDTH = 1280f;
	public final static float RESOLUTION_HEIGHT = 720f;

	private static final String STATE_NAME = "Play";

	private Checkpoint[] checkpoints;

	// Zur identifizierung von Collisions Entitys
	public final static short PLAYER_BOX = 0x1; // 0001
	public final static short ENEMY_BOX = 0x1 << 1; // 0010 or 0x2 in hex

	private boolean pause = false;
	private int speedFactor;
	private final Level[] level;
	private Vector3 mousePos;
	private Vector2 trailerpos;

	public PlayState(GameStateManager gameStateManager, int level) {
		super(gameStateManager, STATE_NAME);

		this.level = LevelHandler.loadLevels();

		fpscounter = new FPSCounter();
		MainGame.font70.getData().setScale(0.10f);
		scoreBoard = new ScoreBoard(this, !deploy);
		scoreBoard.reset(0);

		preferencesManager = new PreferencesManager();
		preferencesManager.checkHighscore();

		trailerpos = new Vector2(0, 0);

		Enemy.callbackInterface = this;

		// import textures
		smaincar = createScaledSprite("cars/car_standard.png");
		sfinishline = createScaledSprite("maps/finishline.png");
		victory = createScaledSprite("fullscreens/victorycard.png");

		// set STATIC textures
		TowerMenu.cannonButton = new Texture(Gdx.files.internal("buttons/cannonbutton.png"));
		TowerMenu.laserButton = new Texture(Gdx.files.internal("buttons/laserbutton.png"));
		TowerMenu.flameButton = new Texture(Gdx.files.internal("buttons/flamebutton.png"));
		TowerMenu.sniperButton = new Texture(Gdx.files.internal("buttons/sniperbutton.png"));

		MgTower.groundTower = new Texture(Gdx.files.internal("tower/tower_empty.png"));
		MgTower.upperTower = new Texture(Gdx.files.internal("tower/tower_empty_upper.png"));
		MgTower.towerFiring = new Texture(Gdx.files.internal("tower/tower_mg_firing.png"));
		MgTower.soundShoot = Gdx.audio.newSound(Gdx.files.internal("sounds/mgturret.wav"));

		SniperTower.groundTower = new Texture(Gdx.files.internal("tower/tower_sniper_bottom.png"));
		SniperTower.upperTower = new Texture(Gdx.files.internal("tower/tower_sniper_upper.png"));
		SniperTower.towerFiring = new Texture(Gdx.files.internal("tower/tower_sniper_firing.png"));
		SniperTower.soundShoot = Gdx.audio.newSound(Gdx.files.internal("sounds/mgturret.wav"));

		LaserTower.groundTower = new Texture(Gdx.files.internal("tower/tower_laser_bottom.png"));
		LaserTower.upperTower = new Texture(Gdx.files.internal("tower/tower_laser_upper.png"));
		LaserTower.towerFiring = new Texture(Gdx.files.internal("tower/tower_laser_firing.png"));
		LaserTower.soundShoot = Gdx.audio.newSound(Gdx.files.internal("sounds/laser.wav"));

		FireTower.groundTower = new Texture(Gdx.files.internal("tower/tower_fire_bottom.png"));
		FireTower.upperTower = new Texture(Gdx.files.internal("tower/tower_fire_upper.png"));
		FireTower.towerFiring = new Texture(Gdx.files.internal("tower/tower_fire_firing.png"));
		FireTower.tflame = new Texture(Gdx.files.internal("tower/flame.png"));
		FireTower.soundShoot = Gdx.audio.newSound(Gdx.files.internal("sounds/mgturret.wav"));

		EnemySmall.normalTexture = new Texture(Gdx.files.internal("zombies/zombie_standard.png"));
		EnemySmall.deadTexture = new Texture(Gdx.files.internal("zombies/zombie_standard_dead.png"));
		EnemySmall.damageTexture = new Texture(Gdx.files.internal("zombies/zombie_blood.png"));

		EnemyFat.normalTexture = new Texture(Gdx.files.internal("zombies/zombie_fat.png"));
		EnemyFat.deadTexture = new Texture(Gdx.files.internal("zombies/zombie_fat_dead.png"));
		EnemyFat.damageTexture = new Texture(Gdx.files.internal("zombies/zombie_blood.png"));

		EnemySpider.normalTexture = new Texture(Gdx.files.internal("zombies/zombie_spider.png"));
		EnemySpider.deadTexture = new Texture(Gdx.files.internal("zombies/zombie_spider_dead.png"));
		EnemySpider.damageTexture = new Texture(Gdx.files.internal("zombies/zombie_blood_green.png"));

		EnemyBicycle.normalTexture = new Texture(Gdx.files.internal("zombies/zombie_bicycle.png"));
		EnemyBicycle.deadTexture = new Texture(Gdx.files.internal("zombies/zombie_bicycle_dead.png"));
		EnemyBicycle.damageTexture = new Texture(Gdx.files.internal("zombies/zombie_blood.png"));

		EnemyLincoln.normalTexture = new Texture(Gdx.files.internal("zombies/zombie_lincoln.png"));
		EnemyLincoln.deadTexture = new Texture(Gdx.files.internal("zombies/zombie_lincoln_dead.png"));
		EnemyLincoln.damageTexture = new Texture(Gdx.files.internal("zombies/zombie_blood.png"));

		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/theme.mp3"));
		splatt = Gdx.audio.newSound(Gdx.files.internal("sounds/splatt.wav"));
		money = Gdx.audio.newSound(Gdx.files.internal("sounds/cash.wav"));
		carsound = Gdx.audio.newSound(Gdx.files.internal("sounds/car_sound2.wav"));
		victorysound = Gdx.audio.newSound(Gdx.files.internal("sounds/LevelUp3.wav"));

		// Sets this camera to an orthographic projection, centered at (viewportWidth/2,
		// viewportHeight/2), with the y-axis pointing up or down.
		camera.setToOrtho(false, MainGame.GAME_WIDTH * PIXEL_TO_METER, MainGame.GAME_HEIGHT * PIXEL_TO_METER);

		enemies = new Array<Enemy>();
		towers = new Array<Tower>();
		collis = new CollisionListener(this);
		checkpoints = new Checkpoint[4];

		moneyPerLap = 50;

		debugBox2D = false;
		debugCollision = false;
		debugWay = false;
		debugEntfernung = false;

		pitStop = createScaledSprite("pit_stop/pit_stop_01.png");

		timeforwavetext = 0;
		wavetext = "Loading Level";

		// pitStop.setPosition(100, 100);

		// Sicherstellen dass bei deploy alle test sachen aus sind
		if (deploy)
			soundon = true;

		loadLevel(MainGame.level);

		backgroundMusic.setLooping(true);
		backgroundMusic.setVolume(0.6f);

		if (soundon)
			backgroundMusic.play();

		shapeRenderer = new ShapeRenderer();

		speedFactor = 1;

		if (Controllers.getControllers().size == 0) {
			controllerHelper = null;
			System.out.println("No controller found!");
		} else {
			controllerHelper = new ControllerHelper(this);
			Controllers.addListener(controllerHelper);
		}
	}

	private void loadLevel(int levelNumber) {
		System.out.println("Load Level #" + levelNumber);
		// set/save level number
		scoreBoard.setLevel(levelNumber);

		if (levelNumber > this.level.length) {
			GameVictory();
			return;
		}

		// decrement level number because everything needs to be inconsistent
		levelNumber = levelNumber - 1;
		// clear all enemies and tower
		this.enemies.clear();
		this.towers.clear();
		// create a new world and add contact listener to the new world
		this.world = new World(new Vector2(), true);
		this.world.setContactListener(this.collis);
		// create a new debug renderer
		this.debugRender = new Box2DDebugRenderer(); // needed?
		// setup new car
		this.car = new Car(this.world, this.smaincar, this.level[levelNumber].getCarPos().x,
				this.level[levelNumber].getCarPos().y);
		// create a new TowerMenu
		this.towerMenu = new TowerMenu(this.world, scoreBoard);
		// unlock/lock the right tower
		for (int i = 0; i < this.level[levelNumber].getTowersUnlocked().length; i++) {
			if (this.level[levelNumber].getTowersUnlocked()[i])
				this.towerMenu.unlockTower(i);
			else
				this.towerMenu.unlockTower(i, false);
		}
		this.finishline = new FinishLine(this.world, sfinishline, this.level[levelNumber].getFinishLinePosition().x,
				this.level[levelNumber].getFinishLinePosition().y);
		this.map = new MainMap(this.level[levelNumber].getMapName(), this.world, this.finishline.getBody());
		this.map.setSpawn(this.level[levelNumber].getSpawnPoint());
		trailerpos.set(map.getZielPos().x, map.getZielPos().y);
		this.pitStop.setPosition(this.level[levelNumber].getPitStopPosition().x * PIXEL_TO_METER,
				this.level[levelNumber].getPitStopPosition().y * PIXEL_TO_METER);
		for (int j = 0; j < this.level[levelNumber].getCheckPoints().length; j++)
			this.checkpoints[j] = new NormalCheckpoint(this.world,
					this.level[levelNumber].getCheckPoints()[j].x * PIXEL_TO_METER,
					this.level[levelNumber].getCheckPoints()[j].y * PIXEL_TO_METER);
		scoreBoard.reset(0);
	}

	private static Sprite createScaledSprite(String location) {
		final Sprite s = new Sprite(new Texture(Gdx.files.internal(location)));
		s.setSize(s.getWidth() * PIXEL_TO_METER, s.getHeight() * PIXEL_TO_METER);
		s.setOriginCenter();
		return s;
	}

	private void startBuilding(Tower t) {
		System.out.println("Start building");
		buildingtower = t;
		buildingtower.setBuildingMode(true);
		for (final Tower tower : towers)
			tower.activateRange(true);
	}

	private boolean buildingPositionIsAllowed(final Tower tower) {
		final float[][] cornerPoints = tower.getCornerPoints();
		boolean isAllowed = true;
		for (int i = 0; i < cornerPoints.length; i++) {
			// if tower is placed onto the track do not allow building it
			if (!this.map.isInBody(cornerPoints[i][0], cornerPoints[i][1]))
				isAllowed = false;
			// if tower is placed onto the tower menu do not allow building it
			if (this.towerMenu.contains(cornerPoints[i][0], cornerPoints[i][1]))
				isAllowed = false;
			// if tower is placed onto a tower do not allow building it
			for (final Tower tower1 : towers) {
				if (tower1.contains(cornerPoints[i][0], cornerPoints[i][1]))
					isAllowed = false;
			}
		}
		return isAllowed;
	}

	private boolean buildingMoneyIsEnough(final Tower tower) {
		return tower.getCost() <= scoreBoard.getMoney();
	}

	private void stopBuilding() {
		System.out.println("Stop building");
		towerMenu.unselectAll();
		for (final Tower tower : towers)
			tower.activateRange(false);
	}
	
	private void goBack() {
		gameStateManager.setGameState(new MenuState(gameStateManager));
	}

	@Override
	protected void handleInput() {
		GameStateMethods.toggleFullScreen(Keys.F11);

		if (wongame && (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.justTouched()))
			playerIsDeadCallback();
		if (Gdx.input.isKeyJustPressed(Keys.F3)) {
			for (final Enemy e : enemies)
				e.activateEnemy();
		}
		if (Gdx.input.isCatchBackKey() || Gdx.input.isKeyJustPressed(Keys.ESCAPE))
			goBack();
		if (Gdx.input.isKeyPressed(Keys.W))
			car.accelarate();
		if (Gdx.input.isKeyPressed(Keys.S))
			car.brake();
		if (Gdx.input.isKeyPressed(Keys.A))
			car.steerLeft();
		if (Gdx.input.isKeyPressed(Keys.D))
			car.steerRight();
		if (Gdx.input.isKeyJustPressed(Keys.U)) {
			soundon = !soundon;
			if (soundon == false && backgroundMusic.isPlaying())
				backgroundMusic.pause();
			if (soundon == true)
				backgroundMusic.play();
		}
		if (Gdx.input.isKeyJustPressed(Keys.NUM_1))
			towerMenu.selectTower(0, mousePos, enemies);
		if (Gdx.input.isKeyJustPressed(Keys.NUM_2))
			towerMenu.selectTower(1, mousePos, enemies);
		if (Gdx.input.isKeyJustPressed(Keys.NUM_3))
			towerMenu.selectTower(2, mousePos, enemies);
		if (Gdx.input.isKeyJustPressed(Keys.NUM_4))
			towerMenu.selectTower(3, mousePos, enemies);
		if (Gdx.input.justTouched() && this.buildingtower != null)
			buildTowerIfAllowed();

		// pause the game if P is pressed
		if (Gdx.input.isKeyJustPressed(Keys.P)) {
			this.pause = !this.pause;
			if (pause) {
				wavetext = "PAUSE";
				timeforwavetext = 2f;
			} else {
				timeforwavetext = 0f;
			}
		}

		// mark a tower red if the build position is not correct
		if (buildingtower != null)
			buildTowerIfAllowed(false);

		if (deploy == false)
			debugInputs();

	}

	private void debugInputs() {
		if (Gdx.input.isKeyJustPressed(Keys.F))
			enemies.add(new EnemySmall(new Vector2(220, 20), world, map, 0));
		if (Gdx.input.isKeyJustPressed(Keys.G))
			enemies.add(new EnemyFat(new Vector2(220, 20), world, map, 0));
		if (Gdx.input.isKeyJustPressed(Keys.H))
			enemies.add(new EnemyBicycle(new Vector2(220, 20), world, map, 0));
		if (Gdx.input.isKeyJustPressed(Keys.J))
			enemies.add(new EnemyLincoln(new Vector2(220, 20), world, map, 0));
		if (Gdx.input.isKeyJustPressed(Keys.K))
			enemies.add(new EnemySpider(new Vector2(220, 20), world, map, 0));
		if (Gdx.input.isKeyJustPressed(Keys.X))
			debugBox2D = !debugBox2D;
		if (Gdx.input.isKeyJustPressed(Keys.C))
			debugCollision = !debugCollision;
		if (Gdx.input.isKeyJustPressed(Keys.V))
			debugWay = !debugWay;
		if (Gdx.input.isKeyJustPressed(Keys.B))
			debugEntfernung = !debugEntfernung;
		if (Gdx.input.isKeyJustPressed(Keys.NUM_9)) {
			for (final Enemy e : enemies) {
				if (!e.isActivated())
					e.activateEnemy();
				e.takeDamage(e.getHealth());
			}
		}
		if (Gdx.input.isKeyJustPressed(Keys.NUM_8))
			scoreBoard.reduceLife(scoreBoard.getHelath());
		if (Gdx.input.isKeyJustPressed(Keys.NUM_7))
			scoreBoard.addMoney(1000);
		if (Gdx.input.isKeyJustPressed(Keys.NUM_5)) {
			scoreBoard.setLevel(scoreBoard.getLevel() + 1);
			loadLevel(scoreBoard.getLevel());
		}
		if (Gdx.input.isKeyJustPressed(Keys.NUM_0)) {
			tutorialstate++;
			System.out.println("Cheated tutorial to " + tutorialstate);
		}

		if (Gdx.input.isKeyJustPressed(Keys.T)) {
			// TODO toggle unlock all towers
			this.unlockAllTowers = !this.unlockAllTowers;
			if (this.unlockAllTowers) {
				for (int i = 0; i < 4; i++)
					towerMenu.unlockTower(i);
			} else {
				for (int i = 0; i < this.level[scoreBoard.getLevel() - 1].getTowersUnlocked().length; i++) {
					if (this.level[scoreBoard.getLevel() - 1].getTowersUnlocked()[i])
						this.towerMenu.unlockTower(i);
					else
						this.towerMenu.unlockTower(i, false);
				}
			}
		}
		if (Gdx.input.isKeyJustPressed(Keys.R)) {
			scoreBoard.addScore(1000);
		}

		if (Gdx.input.isKeyJustPressed(Keys.F8)) {
			scoreBoard.toggleDebugDraw();
		}

		if (Gdx.input.isKeyJustPressed(Keys.LEFT)) {
			this.speedFactor = 1;
		}
		if (Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
			this.speedFactor += 1;
		}

	}

	private void buildTowerIfAllowed() {
		buildTowerIfAllowed(true);
	}

	private void buildTowerIfAllowed(final boolean userClicked) {
		// if position and money is ok build it
		if (buildingMoneyIsEnough(this.buildingtower) && buildingPositionIsAllowed(this.buildingtower)) {
			if (userClicked) {
				// Add tower to the tower list
				towerMenu.unselectAll();
				scoreBoard.addMoney(-this.buildingtower.getCost());
				final Tower newTower = this.buildingtower;
				buildingtower = null;
				newTower.activate();
				newTower.setBuildingMode(false);
				towers.add(newTower);
				stopBuilding();
			} else {
				blockBuildingTower(false);
			}
		} else {
			blockBuildingTower(true);
		}
	}

	private void blockBuildingTower(final boolean b) {
		if (this.buildingtower != null)
			this.buildingtower.setBlockBuildingMode(b);
	}

	@Override
	protected void update(float deltaTime) {
		if (pause)
			return;

		scoreBoard.update(deltaTime);

		mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(mousePos);

		updateWaves();

		car.update(deltaTime);

		if (soundon) {
			if (car.getForward().x != 0 && !carsoundPlaying) {
				carsound.loop();
				carsoundPlaying = true;
			} else {
				if (car.getForward().x == 0) {
					carsound.stop();
					carsoundPlaying = false;
				}
			}
		}

		if (buildingtower == null) {
			buildingtower = towerMenu.getCurrentTower();
			if (buildingtower != null)
				startBuilding(buildingtower);
		} else {
			buildingtower.update(deltaTime, mousePos);
			buildingtower = towerMenu.getCurrentTower();
			if (buildingtower == null)
				stopBuilding();
		}

		for (final Tower t : towers)
			t.update(deltaTime, mousePos);

		for (int i = 0; i < enemies.size; i++) {
			enemies.get(i).update(deltaTime);
			if (enemies.size > 0 && (!enemies.get(i).isActivated() && enemies.get(i).getTime() < scoreBoard.getTime()))
				enemies.get(i).activateEnemy();
		}

		if (infiniteenemies) {
			if (MathUtils.random(1000) > 950) {
				final Enemy e = new EnemySmall(new Vector2(220, 20), world, map, 0);
				enemies.add(e);
			}
			if (MathUtils.random(1000) > 990) {
				final Enemy e = new EnemyBicycle(new Vector2(220, 20), world, map, 0);
				enemies.add(e);
			}
			if (MathUtils.random(1000) > 995) {
				final Enemy e = new EnemyFat(new Vector2(220, 20), world, map, 0);
				enemies.add(e);
			}
		}

		timeforwavetext -= deltaTime;
		towerMenu.update();
		camera.update();

		fpscounter.update(deltaTime);

		if (controllerHelper != null)
			controllerHelper.update();

		updatePhysics(deltaTime);
	}

	@Override
	public void render(final SpriteBatch spriteBatch) {

		// set projection matrix
		spriteBatch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(spriteBatch.getProjectionMatrix());
		// draw won game screen
		spriteBatch.begin();
		if (wongame) {
			victory.draw(spriteBatch);
			spriteBatch.end();
			return;
		}
		// draw track bg
		this.map.draw(spriteBatch);
		// draw finish line
		this.finishline.draw(spriteBatch);

		spriteBatch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.begin(ShapeType.Filled);

		// drawTowerRange
		for (final Tower tower : towers)
			tower.drawRange(shapeRenderer);
		if (buildingtower != null)
			buildingtower.drawRange(shapeRenderer, new Color(1, 0, 1, 0.3f));
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		spriteBatch.begin();
		// draw enemies
		for (final Enemy e : enemies)
			e.draw(spriteBatch);
		// draw car
		this.car.draw(spriteBatch);
		// draw tower
		for (final Tower tower : towers)
			tower.draw(spriteBatch);
		spriteBatch.end();
		shapeRenderer.begin(ShapeType.Filled);
		for (final Tower tower : towers)
			tower.drawLine(shapeRenderer);
		shapeRenderer.end();
		spriteBatch.begin();
		for (final Tower tower : towers)
			tower.drawUpperBuddy(spriteBatch);
		// draw tower menu tower
		if (buildingtower != null) {
			buildingtower.draw(spriteBatch);
			buildingtower.drawUpperBuddy(spriteBatch);
		}
		// draw pitstop
		pitStop.draw(spriteBatch);

		renderDebugCollision(spriteBatch);
		renderDebugEntfernung(spriteBatch);
		renderDebugWay(spriteBatch);

		towerMenu.draw(spriteBatch);
		scoreBoard.draw(spriteBatch);

		if (deploy == false)
			MainGame.font.draw(spriteBatch, "FPS: " + fpscounter.getFrames(), 30, 35.5f);

		if (timeforwavetext > 0)
			MainGame.font70.draw(spriteBatch, wavetext, 20, 25);

		renderTutorial(spriteBatch);
		spriteBatch.end();

		if (debugBox2D)
			debugRender.render(world, camera.combined);

	}

	private void renderDebugWay(SpriteBatch spriteBatch) {
		if (debugWay) {
			MainGame.font.getData().setScale(0.06f);
			for (final Enemy e : enemies) {
				if (e.isActivated() && !e.isTot()) {
					MainGame.font.setColor(e.getColor());
					for (Node node : e.getWeg())
						MainGame.font.draw(spriteBatch, "x", node.getPosition().x * PlayState.PIXEL_TO_METER,
								node.getPosition().y * PlayState.PIXEL_TO_METER);
				}
			}
		}
	}

	private void renderDebugEntfernung(SpriteBatch spriteBatch) {
		if (debugEntfernung) {
			final Node[][] test = this.map.getNodesList();
			MainGame.font.getData().setScale(0.02f);
			for (int i = 0; i < MainGame.GAME_WIDTH; i = i + 10) {
				for (int j = 0; j < MainGame.GAME_HEIGHT; j = j + 10) {
					if (test[i][j].getH() <= 0) // black
						MainGame.font.setColor(0, 0, 0, 0.75f);
					else if (test[i][j].getH() <= 10) // blue
						MainGame.font.setColor(0, 0, 1f, 0.75f);
					else if (test[i][j].getH() <= 20) // teal
						MainGame.font.setColor(0, 1, 0.5f, 0.75f);
					else if (test[i][j].getH() <= 30) // green
						MainGame.font.setColor(0.25f, 1, 0, 0.75f);
					else if (test[i][j].getH() <= 40) // yellow
						MainGame.font.setColor(1, 0.8f, 0, 0.75f);
					else if (test[i][j].getH() <= 50) // orange
						MainGame.font.setColor(1, 0.5f, 0, 0.75f);
					else if (test[i][j].getH() <= 70) // dark red
						MainGame.font.setColor(1, 0.25f, 0.1f, 0.75f);
					else if (test[i][j].getH() <= 100) // pink
						MainGame.font.setColor(1, 0, 0.5f, 0.57f);
					else if (test[i][j].getH() <= 200) // purple
						MainGame.font.setColor(0.6f, 0.1f, 1, 0.75f);
					else
						MainGame.font.setColor(1, 0, 0, 0.75f);

					MainGame.font.draw(spriteBatch, test[i][j].getH() + "", i * PlayState.PIXEL_TO_METER,
							j * PlayState.PIXEL_TO_METER);
				}
			}
		}
	}

	private void renderDebugCollision(SpriteBatch spriteBatch) {
		if (debugCollision) {
			final Node[][] test = this.map.getNodesList();
			MainGame.font.getData().setScale(0.05f);
			for (int i = 0; i < MainGame.GAME_WIDTH; i = i + 10) {
				for (int j = 0; j < MainGame.GAME_HEIGHT; j = j + 10) {
					if (test[i][j].getNoUse()) {
						MainGame.font.setColor(0, 0, 1, 0.5f);
						MainGame.font.draw(spriteBatch, "O", i * PlayState.PIXEL_TO_METER,
								j * PlayState.PIXEL_TO_METER);
					} else {
						MainGame.font.setColor(1, 0, 0, 0.5f);
						MainGame.font.draw(spriteBatch, "I", i * PlayState.PIXEL_TO_METER,
								j * PlayState.PIXEL_TO_METER);
					}
				}
			}
		}
	}

	private void renderTutorial(SpriteBatch spriteBatch) {
		// Draws the Tutorial
		// -1: Tutorial disabled/finished
		// 0: Learn how to drive the car
		// 1: Finish laps to earn money
		// 2: Keep finishing laps until enough money for towers
		// 3: Select a tower
		// 4: Build a tower
		// 5: Learn what to protect

		// First check appropriate stage
		switch (tutorialstate) {
		case -1:

			break;
		case 0:
			if (checkPointsCleared() > 1) {
				tutorialstate++;
				System.out.println("Tutorial advanced to Stage " + tutorialstate);
			}
			break;
		case 1:
			// Checked in lineFinished();
			break;
		case 2:
			if (scoreBoard.getMoney() > 100) {
				tutorialstate++;
				System.out.println("Tutorial advanced to Stage " + tutorialstate);
			}

			break;
		case 3:
			if (buildingtower != null) {
				tutorialstate++;
				System.out.println("Tutorial advanced to Stage " + tutorialstate);
			}
			break;
		case 4:
			if (towers.size > 0) {
				tutorialstate++;
				tutorialtimer = 60f;
				System.out.println("Tutorial advanced to Stage " + tutorialstate);
			}
			break;
		case 5:
			if (tutorialtimer > 0) {
				tutorialtimer = tutorialtimer - Gdx.graphics.getDeltaTime();
			} else {
				tutorialstate++;
				System.out.println("Tutorial advanced to Stage " + tutorialstate);
			}
			break;
		case 6:
			tutorialstate = -1;
			break;

		default:
			break;
		}

		// Then write the text
		switch (tutorialstate) {
		case -1:
			break;
		case 0:
			MainGame.fontOutline.draw(spriteBatch, "USE -WASD- TO DRIVE YOUR CAR", car.getX() - 6, car.getY() - 1);
			break;
		case 1:
			MainGame.fontOutline.draw(spriteBatch, "FINISH LAPS TO EARN MONEY!", finishline.getX() - 6,
					finishline.getY() + 4.7f);
			break;
		case 2:
			MainGame.fontOutline.draw(spriteBatch, "GO FAST TO EARN BONUS CASH!", finishline.getX() - 6,
					finishline.getY() + 4.7f);
			break;
		case 3:
			MainGame.fontOutline.draw(spriteBatch, "PRESS 1 OR 2 TO SELECT TOWER", towerMenu.getStart().x - 6.5f,
					towerMenu.getStart().y + 5);
			break;
		case 4:
			MainGame.fontOutline.draw(spriteBatch, "LEFT CLICK TO BUILD", mousePos.x - 5.5f, mousePos.y - 1);
			break;
		case 5:
			MainGame.fontOutline.draw(spriteBatch, "PROTECT YOUR TRAILER FROM THE ZOMBIES!", trailerpos.x - 10.5f,
					trailerpos.y - 2);
			break;
		default:
			break;
		}

	}

	private int checkPointsCleared() {
		int i = 0;
		for (final Checkpoint c : checkpoints) {
			if (c.isActivated())
				i++;
		}
		return i;
	}

	private void updatePhysics(final float deltaTime) {
		if (pause)
			return;

		physicsaccumulator += Math.min(deltaTime, 0.25f);
		while (physicsaccumulator >= TIME_STEP) {
			world.step(TIME_STEP * this.speedFactor, 6, 2);
			physicsaccumulator -= TIME_STEP;
		}
		for (final Enemy enemy : enemies) {
			if (enemy.isJustDied()) {
				enemy.setJustDied(false);
				world.destroyBody(enemy.getBody());
				scoreBoard.killedEnemy(enemy.getScore(), enemy.getMoney());
			}
			if (enemy.isDelete()) {
				world.destroyBody(enemy.getBody());
				enemies.removeValue(enemy, true);
			}
		}

		for (final Tower t : towers) {
			Array<Body> ab = new Array<Body>();
			Array<Body> rb = new Array<Body>();
			rb = t.removeProjectiles();
			if (rb != null)
				ab.addAll(rb);
			for (Body body : ab) {
				if (body.getWorld() == world)
					world.destroyBody(body);
			}
		}

	}

	@Override
	protected void dispose() {
		Controllers.removeListener(controllerHelper);
		// dispose loaded enemies, towers and other objects
		for (final Enemy enemy : enemies)
			enemy.dispose();
		enemies.clear();
		for (final Tower tower : towers)
			tower.dispose();
		towers.clear();
		car.dispose();
		towerMenu.dispose();
		// dispose STATIC textures
		MgTower.groundTower.dispose();
		MgTower.upperTower.dispose();
		MgTower.towerFiring.dispose();
		MgTower.soundShoot.dispose();
		LaserTower.groundTower.dispose();
		LaserTower.upperTower.dispose();
		LaserTower.towerFiring.dispose();
		LaserTower.soundShoot.dispose();
		EnemySmall.normalTexture.dispose();
		EnemySmall.deadTexture.dispose();
		EnemyFat.normalTexture.dispose();
		EnemyFat.deadTexture.dispose();
		EnemyBicycle.normalTexture.dispose();
		EnemyBicycle.deadTexture.dispose();
		TowerMenu.cannonButton.dispose();
		TowerMenu.laserButton.dispose();
		TowerMenu.flameButton.dispose();
		backgroundMusic.dispose();
		splatt.dispose();
		money.dispose();
		carsound.dispose();
		victorysound.dispose();
		shapeRenderer.dispose();
	}

	@Override
	public void collisionCarEnemy(final Car car, final Enemy enemy) {
		// if the new health after the hit is smaller than 0 play kill sound
		if (car.hitEnemy(enemy) < 0 && soundon)
			splatt.play(1, MathUtils.random(0.5f, 2f), 0);
	}

	@Override
	public void collisionCarCheckpoint(final Car car, final Checkpoint checkpoint) {
		// activate checkpoints when the car collides with them
		checkpoint.setActivated(true);
	}

	/**
	 * Returns true when all checkpoints were checked
	 */
	private boolean allCheckPointsChecked() {
		for (final Checkpoint checkpoint : this.checkpoints) {
			if (checkpoint.isActivated() == false)
				return false;
		}
		return true;
	}

	private void lapFinished() {
		// get if all checkpoints are checked
		final boolean allCheckpointsChecked = allCheckPointsChecked();
		// disable all checkpoints
		for (final Checkpoint checkpoint : this.checkpoints)
			checkpoint.setActivated(false);
		// when all checkpoints were checked
		if (allCheckpointsChecked) {
			// add fast bonus and money per lap to the purse
			final int fastBonus = (moneyPerLap - (int) scoreBoard.getCurrentTime() * 2);
			scoreBoard.newLap((fastBonus > 0) ? moneyPerLap + fastBonus : moneyPerLap);
			if (soundon)
				money.play();
			if (tutorialstate == 1) {
				tutorialstate++;
				System.out.println("Tutorial advanced to Stage " + tutorialstate);
			}
		}
		// play cash sound if sound activated

	}

	@Override
	public void collisionCarFinishLine(final Car car, final FinishLine finishLine) {
		lapFinished();
	}

	@Override
	public void collisionFlameEnemy(final Enemy enemy, final Flame flame) {
		enemy.takeDamage(flame.getDamage());
	}

	private void updateWaves() {

		// if all enemies are active (this means no enemy is invisible) and dead
		if (!threadActive && allEnemiesAreActive() && allEnemiesDead()) {
			// and the current wave is the maximum wave
			if (currentwave >= this.level[scoreBoard.getLevel() - 1].getWaves().size) {
				// and all enemies are dead
				LevelVictory();
			} else {
				// else load the next wave
				currentwave++;
				scoreBoard.setWaveNumber(currentwave);

				if (currentwave < this.level[scoreBoard.getLevel() - 1].getWaves().size)
					wavetext = "WAVE " + currentwave;
				else
					wavetext = "FINAL WAVE";
				timeforwavetext = 2f;
				System.out.println("Scoreboard time: " + scoreBoard.getTime());

				long time = TimeUtils.millis();
				enemies.addAll(level[scoreBoard.getLevel() - 1].getWaves().get(currentwave - 1)
						.createEnemies(map.getSpawn(), world, map, scoreBoard.getTime()));
				time = TimeUtils.timeSinceMillis(time);

				System.out.println("Starte Wave " + currentwave + " of "
						+ this.level[scoreBoard.getLevel() - 1].getWaves().size + " Time to load: " + time + " ms");
			}
		}
	}

	private boolean allEnemiesAreActive() {
		for (final Enemy enemy : enemies) {
			if (enemy.isActivated() == false)
				return false;
		}
		return true;
	}

	private void LevelVictory() {
		System.out.println("Level finished " + MainGame.level);
		wavetext = "LEVEL CLEAR!";
		timeforwavetext = 2f;
		currentwave = 0;
		MainGame.level++;
		loadLevel(MainGame.level);
		if (soundon)
			victorysound.play();

	}

	private void GameVictory() {
		wongame = true;
		if (soundon)
			victorysound.play();
	}

	private boolean allEnemiesDead() {
		for (final Enemy e : enemies) {
			if (!e.isTot())
				return false;
		}
		return true;
	}

	@Override
	public void playerIsDeadCallback() {
		pause = true;
		// if score can make it in the top 10 go to the name input else game over
		if (preferencesManager.scoreIsInTop5(scoreBoard.getScore()))
			gameStateManager.setGameState(new HighscoreNameState(gameStateManager, scoreBoard.getScore()));
		else
			gameStateManager.setGameState(new GameOverState(gameStateManager));

	}

	@Override
	public void enemyHitsHomeCallback(final Enemy enemy) {
		scoreBoard.reduceLife(enemy.getDamadge());
	}

	@Override
	public void steerCar(boolean left) {
		if (left)
			car.steerLeft();
		else
			car.steerRight();
	}

	@Override
	public void startBuildingMode(boolean start) {
		if (buildingtower == null) {
			buildingtower = towerMenu.getCurrentTower();
			if (buildingtower != null)
				startBuilding(buildingtower);
		} else {
			buildingtower = towerMenu.getCurrentTower();
			if (buildingtower == null)
				stopBuilding();
		}
	}

	@Override
	public void accelerateCar(boolean forwards) {
		if (forwards)
			car.accelarate();
		else
			car.brake();
	}
	
	@Override
	public void backCallback() {
		goBack();
	}
	
	@Override
	public void fullScreenCallback() {
		GameStateMethods.toggleFullScreen();
	}
}
