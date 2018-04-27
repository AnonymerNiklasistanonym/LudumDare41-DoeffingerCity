package com.mygdx.game.gamestate.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.Car;
import com.mygdx.game.CollisionCallbackInterface;
import com.mygdx.game.CollisionListener;
import com.mygdx.game.EnemyWaveEntry;
import com.mygdx.game.FPSCounter;
import com.mygdx.game.MainGame;
import com.mygdx.game.MainMap;
import com.mygdx.game.Node;
import com.mygdx.game.PreferencesManager;
import com.mygdx.game.ScoreBoard;
import com.mygdx.game.TurmMenu;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.objects.Checkpoint;
import com.mygdx.game.objects.Enemy;
import com.mygdx.game.objects.FinishLine;
import com.mygdx.game.objects.checkpoints.NormalCheckpoint;
import com.mygdx.game.objects.enemies.EnemyBicycle;
import com.mygdx.game.objects.enemies.EnemyFat;
import com.mygdx.game.objects.enemies.EnemyLincoln;
import com.mygdx.game.objects.enemies.EnemySmall;
import com.mygdx.game.objects.tower.FireTower;
import com.mygdx.game.objects.tower.Flame;
import com.mygdx.game.objects.tower.LaserTower;
import com.mygdx.game.objects.tower.MgTower;
import com.mygdx.game.objects.tower.Tower;

public class PlayState extends GameState implements CollisionCallbackInterface {

	CollisionListener collis;
	private Sprite smaincar;
	private Sprite sfinishline;
	private Sprite strack1;
	private Sprite strack2;
	private Sprite strack3;
	private Sprite scurrenttrack;
	private World world;
	private Car car;
	private FinishLine finishline;
	private Array<Enemy> enemies;
	private Array<Tower> towers;

	private boolean debugBox2D;
	private boolean debugCollision;
	private boolean debugEntfernung;
	private boolean carsoundPlaying = false;

	public static boolean soundon = false;
	private boolean debugWay;

	private final TurmMenu turmmenu;

	public static Thread thread;

	private final PreferencesManager preferencesManager;

	private MainMap map;
	private final Sprite pitStop;

	private static ScoreBoard scoreBoard;
	private Tower buildingtower;

	private final int moneyPerLap = 50;
	private final Music backgroundMusic;
	private final Sound splatt, money, carsound, victorysound;

	/**
	 * Time since last physic Steps
	 */

	Sprite victory;

	FPSCounter fpscounter;
	int currentwave = 0;
	final int totalwaves = 10;
	boolean wongame = false;
	boolean infiniteenemies = false;
	boolean deploy = false;
	boolean unlockAllTowers = false;

	private float physicsaccumulator = 0f;
	private Box2DDebugRenderer debugRender;

	private float timeforwavetext = 3f;
	private String wavetext = "";
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

	private Checkpoint[] checkpoints;

	// Zur identifizierung von Collisions Entitys
	public final static short PLAYER_BOX = 0x1; // 0001
	public final static short ENEMY_BOX = 0x1 << 1; // 0010 or 0x2 in hex

	public Array<EnemyWaveEntry> currentEnemyWaves;

	public PlayState(GameStateManager gameStateManager, int level) {

		super(gameStateManager);
		fpscounter = new FPSCounter();
		System.out.println("Play state entered");
		MainGame.waveFont.getData().setScale(0.10f);
		scoreBoard = new ScoreBoard(this);
		scoreBoard.reset(0);

		preferencesManager = new PreferencesManager();
		preferencesManager.checkHighscore();

		// import textures
		strack1 = createScaledSprite("maps/track1.png");
		strack2 = createScaledSprite("maps/track2.png");
		strack3 = createScaledSprite("maps/track3.png");
		scurrenttrack = strack1;
		smaincar = createScaledSprite("cars/car_standard.png");
		sfinishline = createScaledSprite("maps/finishline.png");

		TurmMenu.cannonButton = new Texture(Gdx.files.internal("buttons/cannonbutton.png"));
		TurmMenu.laserButton = new Texture(Gdx.files.internal("buttons/laserbutton.png"));
		TurmMenu.flameButton = new Texture(Gdx.files.internal("buttons/flamebutton.png"));

		victory = createScaledSprite("fullscreens/victorycard.png");
		// set STATIC textures
		NormalCheckpoint.normalCheckPointActivated = new Texture(
				Gdx.files.internal("checkpoints/checkpoint_normal_activated.png"));
		NormalCheckpoint.normalCheckPointDisabled = new Texture(
				Gdx.files.internal("checkpoints/checkpoint_normal_disabled.png"));

		MgTower.groundTower = new Texture(Gdx.files.internal("tower/tower_empty.png"));
		MgTower.upperTower = new Texture(Gdx.files.internal("tower/tower_empty_upper.png"));
		MgTower.towerFiring = new Texture(Gdx.files.internal("tower/tower_mg_firing.png"));
		MgTower.soundShoot = Gdx.audio.newSound(Gdx.files.internal("sounds/mgturret.wav"));

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

		world = new World(new Vector2(0, 0), true);
		world.setContactListener(collis);
		debugRender = new Box2DDebugRenderer();

		finishline = new FinishLine(world, sfinishline, 380, 220);

		debugBox2D = false;
		debugCollision = false;
		debugWay = false;
		debugEntfernung = false;

		turmmenu = new TurmMenu(world, enemies, scoreBoard);

		checkpoints = new Checkpoint[4];
		float[][] checkPointPosition = { { 300, 230 }, { 320, 600 }, { 850, 600 }, { 850, 230 } };
		for (int i = 0; i < checkpoints.length; i++)
			checkpoints[i] = new NormalCheckpoint(world, checkPointPosition[i][0] * PIXEL_TO_METER,
					checkPointPosition[i][1] * PIXEL_TO_METER);

		pitStop = createScaledSprite("pit_stop/pit_stop_01.png");

		// pitStop.setPosition(100, 100);

		// Sicherstellen dass bei deploy alle test sachen aus sind
		if (deploy)
			soundon = true;
		else
			MainGame.level = 1;
		loadLevel(MainGame.level);

		backgroundMusic.setLooping(true);
		backgroundMusic.setVolume(0.6f);

		if (soundon)
			backgroundMusic.play();

		shapeRenderer = new ShapeRenderer();
	}

	public void loadLevel(int i) {
		System.out.println("Load Level " + i);
		scoreBoard.setLevel(i);
		enemies.clear();
		towers.clear();
		world = new World(new Vector2(), true);
		world.setContactListener(collis);
		car = new Car(world, smaincar, 440, 220);
		debugRender = new Box2DDebugRenderer();
		switch (i) {
		case 1:
			finishline = new FinishLine(world, sfinishline, 380, 220);
			map = new MainMap("track1", world, finishline.getBody());
			map.setSpawn(new Vector2(220, 20));
			scurrenttrack = strack1;
			pitStop.setPosition(165 * PIXEL_TO_METER, -5 * PIXEL_TO_METER);
			float[][] checkPointPosition = { { 300, 230 }, { 320, 600 }, { 850, 600 }, { 850, 230 } };
			for (int j = 0; j < checkpoints.length; j++)
				checkpoints[j] = new NormalCheckpoint(world, checkPointPosition[j][0] * PIXEL_TO_METER,
						checkPointPosition[j][1] * PIXEL_TO_METER);
			turmmenu.unlockTower(0);
			break;
		case 2:
			finishline = new FinishLine(world, sfinishline, 360, 240);
			map = new MainMap("track2", world, finishline.getBody());
			map.setSpawn(new Vector2(230, 50));
			scurrenttrack = strack2;
			pitStop.setPosition(165 * PIXEL_TO_METER, -5 * PIXEL_TO_METER);
			float[][] checkPointPosition1 = { { 300, 230 }, { 320, 600 }, { 850, 600 }, { 850, 230 } };
			for (int j = 0; j < checkpoints.length; j++)
				checkpoints[j] = new NormalCheckpoint(world, checkPointPosition1[j][0] * PIXEL_TO_METER,
						checkPointPosition1[j][1] * PIXEL_TO_METER);
			turmmenu.unlockTower(1);
			break;
		case 3:
			finishline = new FinishLine(world, sfinishline, 350, 150);
			map = new MainMap("track3", world, finishline.getBody());
			map.setSpawn(new Vector2(170, 100));
			scurrenttrack = strack3;
			pitStop.setPosition(100 * PIXEL_TO_METER, -5 * PIXEL_TO_METER);

			float[][] checkPointPosition11 = { { 300, 170 }, { 320, 570 }, { 850, 570 }, { 850, 170 } };
			for (int j = 0; j < checkpoints.length; j++)
				checkpoints[j] = new NormalCheckpoint(world, checkPointPosition11[j][0] * PIXEL_TO_METER,
						checkPointPosition11[j][1] * PIXEL_TO_METER);
			turmmenu.unlockTower(2);
			break;

		default:
			break;

		}
		if (unlockAllTowers) {
			turmmenu.unlockTower(0);
			turmmenu.unlockTower(1);
			turmmenu.unlockTower(2);
		}
		currentEnemyWaves = new Array<EnemyWaveEntry>();
		scoreBoard.reset(0);

		turmmenu.updateMenu(world, enemies);
	}

	public static Sprite createScaledSprite(String location) {
		final Sprite s = new Sprite(new Texture(Gdx.files.internal(location)));
		s.setSize(s.getWidth() * PIXEL_TO_METER, s.getHeight() * PIXEL_TO_METER);
		s.setOriginCenter();
		return s;
	}

	public void startBuilding(Tower t) {
		System.out.println("Start building");
		buildingtower = t;
		buildingtower.setBuildingMode(true);
		for (final Tower tower : towers)
			tower.activateRange(true);
	}

	public boolean buildingPositionIsAllowed(final Tower tower) {
		final float[][] cornerPoints = tower.getCornerPoints();
		boolean isAllowed = true;
		for (int i = 0; i < cornerPoints.length; i++) {
			// if tower is placed onto the track do not allow building it
			if (!this.map.isInBody(cornerPoints[i][0], cornerPoints[i][1]))
				isAllowed = false;
			// if tower is placed onto the tower menu do not allow building it
			if (this.turmmenu.contains(cornerPoints[i][0], cornerPoints[i][1]))
				isAllowed = false;
			// if tower is placed onto a tower do not allow building it
			for (final Tower tower1 : towers) {
				if (tower1.contains(cornerPoints[i][0], cornerPoints[i][1]))
					isAllowed = false;
			}
		}
		System.out.println("buildingPositionIsAllowed: " + isAllowed);
		return isAllowed;
	}

	public boolean buildingMoneyIsEnough(final Tower tower) {
		final boolean moneyIsEnough = tower.getCost() <= scoreBoard.getMoney();
		System.out.println("buildingMoneyIsEnough: " + moneyIsEnough);
		return moneyIsEnough;
	}

	public void stopBuilding() {
		System.out.println("Stop building");
		turmmenu.unselectAll();
		for (final Tower tower : towers)
			tower.activateRange(false);
	}

	@Override
	protected void handleInput() {
		if (Gdx.input.isKeyJustPressed(Keys.F3)) {
			for (final Enemy e : enemies)
				e.activateEnemy();
		}
		if (Gdx.input.isCatchBackKey() || Gdx.input.isKeyJustPressed(Keys.ESCAPE))
			gameStateManager.setGameState(new MenuState(gameStateManager));
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
			turmmenu.selectTower(0);
		if (Gdx.input.isKeyJustPressed(Keys.NUM_2))
			turmmenu.selectTower(1);
		if (Gdx.input.isKeyJustPressed(Keys.NUM_3))
			turmmenu.selectTower(2);
		if (Gdx.input.justTouched() && this.buildingtower != null)
			buildTowerIfAllowed();
		if (Gdx.input.isKeyJustPressed(Keys.F11)) {
			if (Gdx.graphics.isFullscreen())
				Gdx.graphics.setWindowedMode(MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);
			else
				Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		}

		if (deploy == false)
			debugInputs();

	}

	public void debugInputs() {
		if (Gdx.input.isKeyJustPressed(Keys.F))
			enemies.add(new EnemySmall(220, 20, world, map, 0));
		if (Gdx.input.isKeyJustPressed(Keys.G))
			enemies.add(new EnemyFat(220, 20, world, map, 0));
		if (Gdx.input.isKeyJustPressed(Keys.H))
			enemies.add(new EnemyBicycle(220, 20, world, map, 0));
		if (Gdx.input.isKeyJustPressed(Keys.I))
			debugBox2D = !debugBox2D;
		if (Gdx.input.isKeyJustPressed(Keys.K))
			debugCollision = !debugCollision;
		if (Gdx.input.isKeyJustPressed(Keys.L))
			debugWay = !debugWay;
		if (Gdx.input.isKeyJustPressed(Keys.COMMA))
			scoreBoard.addMoney(1000);
		if (Gdx.input.isKeyJustPressed(Keys.J))
			debugEntfernung = !debugEntfernung;
		if (Gdx.input.isKeyJustPressed(Keys.X)) {
			for (final Enemy e : enemies) {
				currentwave = 100;
				e.takeDamage(1000);
				LevelVictory();
			}
		}
	}

	public void buildTowerIfAllowed() {
		// if position and money is ok build it
		if (buildingMoneyIsEnough(this.buildingtower) && buildingPositionIsAllowed(this.buildingtower)) {
			// Add tower to the tower list
			turmmenu.unselectAll();
			scoreBoard.addMoney(-this.buildingtower.getCost());
			final Tower newTower = this.buildingtower;
			buildingtower = null;
			newTower.activate();
			newTower.setBuildingMode(false);
			towers.add(newTower);
			stopBuilding();
		} else {
			blockBuildingTower(true);
			Timer.schedule(new Timer.Task() {
				public void run() {
					blockBuildingTower(false);
				}
			}, 1);
		}
	}

	public void blockBuildingTower(final boolean b) {
		if (this.buildingtower != null)
			this.buildingtower.setBlockBuildingMode(b);
	}

	private Vector3 mousePos;

	@Override
	protected void update(float deltaTime) {

		scoreBoard.update(deltaTime);

		mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(mousePos);

		switch (MainGame.level) {
		case 1:
			updateWaves1();
			break;
		case 2:
			updateWaves2();
			break;
		case 3:
			updateWaves3();
			break;
		default:
			break;
		}

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
			buildingtower = turmmenu.getCurrentTower();
			if (buildingtower != null)
				startBuilding(buildingtower);
		} else {
			buildingtower.update(deltaTime, mousePos);
			buildingtower = turmmenu.getCurrentTower();
			if (buildingtower == null)
				stopBuilding();
		}

		for (final Tower t : towers)
			t.update(deltaTime, mousePos);

		for (final Enemy e : enemies) {
			e.update(deltaTime);
			if (!e.isActivated() && e.getTime() < scoreBoard.getTime())
				e.activateEnemy();
		}

		if (infiniteenemies) {
			if (MathUtils.random(1000) > 950) {
				final Enemy e = new EnemySmall(220, 20, world, map, 0);
				enemies.add(e);
			}
			if (MathUtils.random(1000) > 990) {
				final Enemy e = new EnemyBicycle(220, 20, world, map, 0);
				enemies.add(e);
			}
			if (MathUtils.random(1000) > 995) {
				final Enemy e = new EnemyFat(220, 20, world, map, 0);
				enemies.add(e);
			}

			// for (final EnemyWaveEntry entry : currentEnemyWaves) {
			// if (entry.getTimeInSeconds() < scoreBoard.getTime()) {
			// enemies.addAll(EnemyWaveEntry.createEnemy(entry, world, map));
			// currentEnemyWaves.removeValue(entry, true);
			// }
			// }
		}

		timeforwavetext -= deltaTime;
		turmmenu.updateAlpha();
		camera.update();

		fpscounter.update(deltaTime);

		updatePhysics(deltaTime);
	}

	@Override
	public void render(SpriteBatch spriteBatch) {

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
		scurrenttrack.draw(spriteBatch);
		// draw finish line
		finishline.draw(spriteBatch);
		// draw checkpoints
		if (debugBox2D)
			for (final Checkpoint checkpoint : checkpoints)
				checkpoint.draw(spriteBatch);
		// draw tower range
		spriteBatch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.begin(ShapeType.Filled);
		for (final Tower tower : towers)
			tower.drawRange(shapeRenderer);
		if (buildingtower != null)
			buildingtower.drawRange(shapeRenderer);
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		spriteBatch.begin();
		// draw enemies
		for (final Enemy e : enemies)
			e.draw(spriteBatch);
		// draw car
		car.draw(spriteBatch);
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

		if (debugCollision) {
			final Node[][] test = this.map.getNodesList();
			MainGame.font.getData().setScale(0.05f);
			for (int i = 0; i < MainGame.GAME_WIDTH; i = i + 10) {
				for (int j = 0; j < MainGame.GAME_HEIGHT; j = j + 10) {
					if (test[i][j].getNoUse())
						MainGame.font.draw(spriteBatch, "O", i * PlayState.PIXEL_TO_METER,
								j * PlayState.PIXEL_TO_METER);
					else
						MainGame.font.draw(spriteBatch, "I", i * PlayState.PIXEL_TO_METER,
								j * PlayState.PIXEL_TO_METER);
				}
			}
		}

		if (debugEntfernung) {
			final Node[][] test = this.map.getNodesList();
			MainGame.font.getData().setScale(0.01f);
			for (int i = 0; i < MainGame.GAME_WIDTH; i = i + 10) {
				for (int j = 0; j < MainGame.GAME_HEIGHT; j = j + 10)
					MainGame.font.draw(spriteBatch, test[i][j].getH() + "", i * PlayState.PIXEL_TO_METER,
							j * PlayState.PIXEL_TO_METER);
			}
		}

		if (debugWay) {
			MainGame.font.getData().setScale(0.02f);
			for (final Enemy e : enemies) {
				for (Node node : e.getWeg())
					MainGame.font.draw(spriteBatch, "x", node.getX() * PlayState.PIXEL_TO_METER,
							node.getY() * PlayState.PIXEL_TO_METER);
			}
		}

		turmmenu.draw(spriteBatch);

		scoreBoard.draw(spriteBatch);

		if (deploy == false) {
			String sfps = "FPS: " + fpscounter.getFrames();
			MainGame.font.draw(spriteBatch, sfps, 30, 35.5f);
		}

		if (timeforwavetext > 0)
			MainGame.waveFont.draw(spriteBatch, wavetext, 20, 25);

		spriteBatch.end();

		if (debugBox2D)
			debugRender.render(world, camera.combined);

	}

	public void updatePhysics(float deltaTime) {
		float frameTime = Math.min(deltaTime, 0.25f);
		physicsaccumulator += frameTime;
		while (physicsaccumulator >= TIME_STEP) {
			Enemy.worldIsLocked = true;
			world.step(TIME_STEP, 6, 2);
			Enemy.worldIsLocked = false;
			physicsaccumulator -= TIME_STEP;
		}
		Array<Enemy> toremove = new Array<Enemy>();
		for (final Enemy enemy : enemies) {
			if (enemy.isJustDied()) {
				enemy.getBody().setActive(false);
				enemy.setJustDied(false);
				scoreBoard.killedEnemy(enemy.getScore(), enemy.getMoney());
			}
			if (enemy.isDelete()) {
				toremove.add(enemy);
				world.destroyBody(enemy.getBody());
			}
		}
		for (final Enemy e : toremove) {
			enemies.removeValue(e, true);
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
		// dispose STATIC textures
		NormalCheckpoint.normalCheckPointActivated.dispose();
		NormalCheckpoint.normalCheckPointDisabled.dispose();
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
		TurmMenu.cannonButton.dispose();
		TurmMenu.laserButton.dispose();
		TurmMenu.flameButton.dispose();
		backgroundMusic.dispose();
		splatt.dispose();
		money.dispose();
		carsound.dispose();
		victorysound.dispose();
		shapeRenderer.dispose();
		System.out.println("Play state disposed");
	}

	@Override
	public void collisionCarEnemy(Car car, Enemy enemy) {
		car.hitEnemy(enemy);
		if (enemy.getHealth() < 0 && soundon)
			splatt.play(1, MathUtils.random(0.5f, 2f), 0);
	}

	@Override
	public void collisionCarCheckpoint(Car car, Checkpoint checkpoint) {
		checkpoint.setActivated(true);
	}

	public void lapFinished() {

		boolean allCheckpointsChecked = true;

		for (final Checkpoint checkpoint : this.checkpoints) {
			if (checkpoint.isActivated() == false)
				allCheckpointsChecked = false;
			checkpoint.setActivated(false);
		}
		if (allCheckpointsChecked) {
			final int fastBonus = (moneyPerLap - (int) scoreBoard.getCurrentTime() * 2);

			scoreBoard.newLap((fastBonus > 0) ? moneyPerLap + fastBonus : moneyPerLap);
		}

		if (soundon)
			money.play();

	}

	@Override
	public void collisionCarFinishLine(Car car, FinishLine finishLine) {
		lapFinished();
	}

	public void playIsDeadCallback() {
		preferencesManager.saveHighscore("", scoreBoard.getScore());
		gameStateManager.setGameState(new GameOverState(gameStateManager));
	}

	@Override
	public void collisionFlameEnemy(final Enemy enemy, final Flame flame) {
		enemy.takeDamage(flame.getDamage());
	}

	public void updateWaves1() {

		// if all enemies are active (this means no enemy is invisible) and dead
		if (!threadActive && allEnemiesAreActive() && allEnemiesDead()) {
			// and the current wave is the maximum wave
			if (currentwave >= totalwaves) {
				// and all enemies are dead
				LevelVictory();
			} else {
				// else load the next wave
				currentwave++;
				scoreBoard.setWaveNumber(currentwave);
				System.out.println("Starte Wave " + currentwave);

				if (currentwave < totalwaves)
					wavetext = "WAVE " + currentwave;
				else
					wavetext = "FINAL WAVE";

				threadActive = true;
				thread = new Thread(new Runnable() {

					@Override
					public void run() {

						switch (currentwave) {
						case 1:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime(), 50,
									1f, 0, 0f, 0, 0f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 20,
									20, 1f, 0, 0f, 0, 0f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 40,
									30, 1f, 0, 3f, 0, 0.0f, world, map));
							break;
						case 2:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime(), 30,
									0.2f, 0, 0f, 0, 0f, world, map));
							break;
						case 3:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime(), 40,
									0.1f, 0, 0f, 0, 0f, world, map));
							break;
						case 4:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime(), 60,
									0.4f, 0, 0, 0, 0, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 15,
									60, 0.4f, 0, 0, 0, 0, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 25,
									70, 0.4f, 0, 0, 0, 0, world, map));
							break;
						case 5:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 5,
									45, 0, 0, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 10,
									50, 0, 0, world, map));
							break;
						case 6:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime(), 45,
									0, 0, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 15,
									50, 0, 0, world, map));

							break;
						case 7:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime(), 50,
									0, 0, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 15,
									55, 0, 0, world, map));

							break;
						case 8:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime(), 55,
									0, 0, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 15,
									60, 0, 0, world, map));
							break;
						case 9:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime(), 60,
									0, 0, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 15,
									70, 0, 0, world, map));
							break;
						case 10:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime(), 80,
									0, 0, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 15,
									90, 0, 0, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 25,
									100, 0, 0, world, map));
							break;
						}

						threadActive = false;

					}
				});
				thread.start();
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

	public void updateWaves2() {

		// if all enemies are active (this means no enemy is invisible) and dead
		if (!threadActive && allEnemiesAreActive() && allEnemiesDead()) {
			// and the current wave is the maximum wave
			if (currentwave >= totalwaves) {
				// and all enemies are dead
				LevelVictory();
			} else {
				// else load the next wave
				currentwave++;
				scoreBoard.setWaveNumber(currentwave);
				System.out.println("Starte Wave " + currentwave);

				if (currentwave < totalwaves)
					wavetext = "WAVE " + currentwave;
				else
					wavetext = "FINAL WAVE";

				timeforwavetext = 10;

				threadActive = true;
				new Thread(new Runnable() {

					@Override
					public void run() {

						switch (currentwave) {
						case 1:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime(), 30,
									1f, 3, 1f, 0, 0, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 50,
									10, 0.5f, 4, 1f, 0, 0, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 65,
									10, 0.2f, 5, 1f, 0, 0, world, map));
							break;
						case 2:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime(), 10,
									0.2f, 2, 1f, 0, 0.0f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 15,
									10, 0.2f, 2, 1f, 0, 0.0f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 25,
									10, 0.2f, 2, 1f, 0, 0.0f, world, map));

							break;
						case 3:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime(), 50,
									1f, 0, 1f, 0, 0.0f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 55,
									0, 0.0f, 8, 1f, 0, 0.0f, world, map));
							break;
						case 4:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime(), 10,
									0.1f, 0, 1f, 0, 0.0f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 10,
									0, 0.0f, 2, 1f, 0, 0.0f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 15,
									50, 1f, 0, 1f, 0, 0.0f, world, map));
							break;
						case 5:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime(), 10,
									0.1f, 0, 1f, 0, 0.0f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 10,
									0, 0.0f, 4, 1f, 0, 0.0f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 15,
									50, 1f, 0, 1f, 0, 0.0f, world, map));
							break;
						case 6:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime(), 150,
									0, 0, world, map));
							break;
						case 7:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime(), 0,
									15, 0, world, map));
							break;
						case 8:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime(), 100,
									1f, 0, 1f, 0, 0.0f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime(), 0,
									0.0f, 10, 3f, 0, 0.0f, world, map));
							break;
						case 9:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 5,
									200, 0.5f, 0, 1f, 0, 0.0f, world, map));
							break;
						case 10:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime(), 20,
									0.5f, 0, 1f, 0, 0.0f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 10,
									00, 0.5f, 20, 1f, 0, 0.0f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 40,
									20, 0.5f, 0, 1f, 0, 0.0f, world, map));
							break;
						}

					}
				});
			}
		}
	}

	public void updateWaves3() {

		// if all enemies are active (this means no enemy is invisible) and dead
		if (!threadActive && allEnemiesAreActive() && allEnemiesDead()) {
			// and the current wave is the maximum wave
			if (currentwave >= totalwaves) {
				// and all enemies are dead
				GameVictory();
			} else {
				// else load the next wave
				currentwave++;
				scoreBoard.setWaveNumber(currentwave);
				System.out.println("Starte Wave " + currentwave);

				if (currentwave < totalwaves)
					wavetext = "WAVE " + currentwave;
				else
					wavetext = "FINAL WAVE";

				timeforwavetext = 10;

				threadActive = true;
				new Thread(new Runnable() {

					@Override
					public void run() {

						switch (currentwave) {
						case 1:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 5,
									00, 0f, 0, 1f, 5, 0.2f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 20,
									00, 0f, 0, 1f, 10, 0.2f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 30,
									00, 0f, 0, 1f, 10, 0.2f, world, map));
							break;
						case 2:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 5,
									10, 0.2f, 2, 1f, 5, 0.0f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 15,
									10, 0.2f, 2, 1f, 10, 0.0f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 25,
									10, 0.2f, 2, 1f, 0, 0.0f, world, map));

							break;
						case 3:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 5,
									50, 1f, 0, 1f, 10, 0.0f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 55,
									0, 0.0f, 8, 1f, 10, 0.0f, world, map));
							break;
						case 4:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 5,
									10, 0.1f, 0, 1f, 10, 0.0f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 10,
									0, 0.0f, 2, 1f, 10, 0.0f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 15,
									50, 1f, 0, 1f, 0, 0.0f, world, map));
							break;
						case 5:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 5,
									10, 0.1f, 0, 1f, 0, 0.0f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 10,
									0, 0.0f, 4, 1f, 0, 0.0f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 15,
									50, 1f, 0, 1f, 0, 20.0f, world, map));
							break;
						case 6:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 5,
									150, 0, 30, world, map));
							break;
						case 7:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 5,
									0, 15, 10, world, map));
							break;
						case 8:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 5,
									100, 1f, 0, 1f, 50, 0.0f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 5,
									0, 0.0f, 10, 3f, 0, 0.0f, world, map));
							break;
						case 9:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 5,
									200, 0.5f, 0, 1f, 0, 0.0f, world, map));
							break;
						case 10:
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 15,
									200, 0.1f, 0, 1f, 0, 0.0f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 5,
									0, 0.1f, 20, 1f, 0, 0.0f, world, map));
							enemies.addAll(EnemyWaveEntry.createEnemyEntries2(map.getSpawn(), scoreBoard.getTime() + 5,
									0, 0.1f, 0, 1f, 50, 0.0f, world, map));

							break;
						}

					}
				});
			}
		}
	}

	public void startNewLevel() {

	}

	public void LevelVictory() {
		System.out.println("Level finished " + MainGame.level);
		wavetext = "LEVEL CLEAR!";
		timeforwavetext = 2f;
		currentwave = 0;
		MainGame.level++;
		loadLevel(MainGame.level);
		if (soundon)
			victorysound.play();

	}

	public void GameVictory() {
		wongame = true;
		if (soundon)
			victorysound.play();
	}

	public boolean allEnemiesDead() {
		for (final Enemy e : enemies) {
			if (!e.isTot())
				return false;
		}
		System.out.println("All enemies are dead");
		return true;
	}

	public static void enemyHitsYourHome(float damage) {
		scoreBoard.reduceLife(damage);
	}
}
