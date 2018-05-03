package com.mygdx.game.gamestate.states;

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
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.GameStateMethods;
import com.mygdx.game.level.Level;
import com.mygdx.game.level.LevelHandler;
import com.mygdx.game.level.Wave;
import com.mygdx.game.listener.collisions.CollisionCallbackInterface;
import com.mygdx.game.listener.collisions.CollisionListener;
import com.mygdx.game.listener.controller.ControllerCallbackInterface;
import com.mygdx.game.listener.controller.ControllerHelper;
import com.mygdx.game.objects.Car;
import com.mygdx.game.objects.Checkpoint;
import com.mygdx.game.objects.Enemy;
import com.mygdx.game.objects.EnemyCallbackInterface;
import com.mygdx.game.objects.FinishLine;
import com.mygdx.game.objects.Flame;
import com.mygdx.game.objects.Map;
import com.mygdx.game.objects.ScoreBoard;
import com.mygdx.game.objects.ScoreBoardCallbackInterface;
import com.mygdx.game.objects.Tower;
import com.mygdx.game.objects.TowerMenu;
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
import com.mygdx.game.unsorted.Node;
import com.mygdx.game.unsorted.PreferencesManager;

public class PlayState extends GameState implements CollisionCallbackInterface, ControllerCallbackInterface,
		ScoreBoardCallbackInterface, EnemyCallbackInterface {

	private final static String STATE_NAME = "Play";

	// Identify collision entities
	public final static short PLAYER_BOX = 0x1; // 0001
	public final static short ENEMY_BOX = 0x1 << 1; // 0010 or 0x2 in hex
	public final static float TIME_STEP = 1 / 60f; // time for physics step
	public final static float PIXEL_TO_METER = 0.05f;
	public final static float METER_TO_PIXEL = 20f;

	private final Music backgroundMusic, carSound;
	private final Sound splatt, soundmoney, carSoundStart, victorySound;
	private final ControllerHelper controllerHelper;
	private final ScoreBoard scoreBoard;
	private final Array<Enemy> enemies;
	private final Array<Enemy> enemiesdead;
	private final Array<Tower> towers;
	private final Array<Sprite> trailersmoke;
	private float timesincesmoke=0;
	private final PreferencesManager preferencesManager;
	private final Sprite spritePitStop, spriteCar, spriteFinishLine;
	private final ShapeRenderer shapeRenderer;
	private final Level[] level;

	private final Sound soundDamage;

	private Tower buildingtower;
	private Checkpoint[] checkpoints;
	private CollisionListener collis;
	private World world;
	private Car car;
	private FinishLine finishline;
	private TowerMenu towerMenu;
	private Map map;
	private Box2DDebugRenderer debugRender;
	private String waveText;
	private Vector3 mousePos, padPos;
	private Vector2 trailerpos;
	private float tutorialtimer, physicsaccumulator, timeforwavetext;
	private boolean pause, lastPause, disableBgMusic, lastSound, soundOn, debugBox2D, debugCollision, debugDistance, debugWay,
			unlockAllTowers, padActivated;
	private int tutorialState, checkPointsCleared, speedFactor;

	private final Sprite spriteSmoke;

	public PlayState(final GameStateManager gameStateManager, final int levelNumber) {
		super(gameStateManager, STATE_NAME);

		// scale used font correctly
		MainGame.font70.getData().setScale(0.10f);

		// set static dependencies
		Enemy.callbackInterface = this;

		// create sprite(s)
		spriteCar = createScaledSprite("cars/car_standard.png");
		spriteFinishLine = createScaledSprite("maps/finishline.png");
		spritePitStop = createScaledSprite("pit_stop/pit_stop_01.png");
		spriteSmoke=createScaledSprite("maps/smoke.png");

		// set textures
		TowerMenu.cannonButton = new Texture(Gdx.files.internal("buttons/cannonbutton.png"));
		TowerMenu.laserButton = new Texture(Gdx.files.internal("buttons/laserbutton.png"));
		TowerMenu.flameButton = new Texture(Gdx.files.internal("buttons/flamebutton.png"));
		TowerMenu.sniperButton = new Texture(Gdx.files.internal("buttons/sniperbutton.png"));
		MgTower.groundTower = new Texture(Gdx.files.internal("tower/tower_empty.png"));
		MgTower.upperTower = new Texture(Gdx.files.internal("tower/tower_empty_upper.png"));
		MgTower.towerFiring = new Texture(Gdx.files.internal("tower/tower_mg_firing.png"));
		SniperTower.groundTower = new Texture(Gdx.files.internal("tower/tower_sniper_bottom.png"));
		SniperTower.upperTower = new Texture(Gdx.files.internal("tower/tower_sniper_upper.png"));
		SniperTower.towerFiring = new Texture(Gdx.files.internal("tower/tower_sniper_firing.png"));
		LaserTower.groundTower = new Texture(Gdx.files.internal("tower/tower_laser_bottom.png"));
		LaserTower.upperTower = new Texture(Gdx.files.internal("tower/tower_laser_upper.png"));
		LaserTower.towerFiring = new Texture(Gdx.files.internal("tower/tower_laser_firing.png"));
		FireTower.groundTower = new Texture(Gdx.files.internal("tower/tower_fire_bottom.png"));
		FireTower.upperTower = new Texture(Gdx.files.internal("tower/tower_fire_upper.png"));
		FireTower.towerFiring = new Texture(Gdx.files.internal("tower/tower_fire_firing.png"));
		FireTower.tflame = new Texture(Gdx.files.internal("tower/flame.png"));
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

		// set audio files
		MgTower.soundShoot = Gdx.audio.newSound(Gdx.files.internal("sounds/mgturret.wav"));
		SniperTower.soundShoot = Gdx.audio.newSound(Gdx.files.internal("sounds/sniper.wav"));
		LaserTower.soundShoot = Gdx.audio.newSound(Gdx.files.internal("sounds/laser_beam.mp3"));
		FireTower.soundShoot = Gdx.audio.newSound(Gdx.files.internal("sounds/flamethrower.wav"));
		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/theme.mp3"));
		carSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/sound_car_engine.mp3"));
		splatt = Gdx.audio.newSound(Gdx.files.internal("sounds/splatt.wav"));
		soundmoney = Gdx.audio.newSound(Gdx.files.internal("sounds/cash.wav"));
		carSoundStart = Gdx.audio.newSound(Gdx.files.internal("sounds/sound_car_engine_start.mp3"));
		victorySound = Gdx.audio.newSound(Gdx.files.internal("sounds/level_victory.wav"));
		soundDamage =Gdx.audio.newSound(Gdx.files.internal("sounds/trailerdamage.wav"));
		// Sets this camera to an orthographic projection, centered at (viewportWidth/2,
		// viewportHeight/2), with the y-axis pointing up or down.
		camera.setToOrtho(false, MainGame.GAME_WIDTH * PIXEL_TO_METER, MainGame.GAME_HEIGHT * PIXEL_TO_METER);

		// instantiate global fields
		speedFactor = 1;
		checkPointsCleared = 0;
		tutorialtimer = 0;
		unlockAllTowers = false;
		tutorialState = 0;
		physicsaccumulator = 0f;
		pause = false;
		lastPause = !pause;
		padActivated = false;
		debugBox2D = false;
		debugCollision = false;
		debugWay = false;
		debugDistance = false;

		// instantiate global objects
		level = LevelHandler.loadLevels();
		scoreBoard = new ScoreBoard(this);
		controllerHelper = new ControllerHelper(this);
		Controllers.addListener(controllerHelper);
		preferencesManager = new PreferencesManager();
		preferencesManager.checkHighscore();
		enemies = new Array<Enemy>();
		towers = new Array<Tower>();
		collis = new CollisionListener(this);
		checkpoints = new Checkpoint[4];
		shapeRenderer = new ShapeRenderer();
		mousePos = new Vector3();
		trailerpos = new Vector2(0, 0);
		padPos = new Vector3(MainGame.GAME_WIDTH * PIXEL_TO_METER / 2, MainGame.GAME_HEIGHT * PIXEL_TO_METER, 0);
		trailersmoke=new Array<Sprite>();
		enemiesdead=new Array<Enemy>();

		// activate background music
		backgroundMusic.setLooping(true);
		backgroundMusic.setVolume(0.75f);
		carSound.setLooping(true);
		carSound.setVolume(1f);

		// things to do in developer mode and not
		soundOn = preferencesManager.getSoundSetting();
		lastSound = !soundOn;

		// load level
		loadLevel(levelNumber);
	}

	private void loadLevel(int levelNumber) {
		long time = System.currentTimeMillis();
		System.out.print("Load Level #" + levelNumber);
		// set/save level number
		scoreBoard.setLevel(levelNumber);

		if (levelNumber > 1) {
			tutorialState = -1;
		}

		// if the level number is bigger than the level number game is won
		if (levelNumber > this.level.length) {
			victoryGame();
			return;
		}

		if (soundOn)
			carSoundStart.play();

		// decrement level number because everything needs to be inconsistent
		levelNumber = levelNumber - 1;
		// clear all enemies and tower
		this.enemies.clear();
		this.towers.clear();
		this.enemiesdead.clear();
		this.trailersmoke.clear();

		// create a new world and add contact listener to the new world
		this.world = new World(new Vector2(), true);
		this.world.setContactListener(this.collis);
		// create a new debug renderer
		this.debugRender = new Box2DDebugRenderer(); // needed?
		// setup new car
		this.car = new Car(this.world, this.spriteCar, this.level[levelNumber].getCarPos().x,
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
		this.finishline = new FinishLine(this.world, spriteFinishLine,
				this.level[levelNumber].getFinishLinePosition().x, this.level[levelNumber].getFinishLinePosition().y);
		this.map = new Map(this.level[levelNumber], this.world, this.finishline.getBody(),spritePitStop.getHeight());
		this.map.setSpawnPosition(this.level[levelNumber].getSpawnPoint());
		trailerpos.set(map.getTargetPosition().x, map.getTargetPosition().y);
		this.spritePitStop.setPosition(this.level[levelNumber].getPitStopPosition().x * PIXEL_TO_METER,
				this.level[levelNumber].getPitStopPosition().y * PIXEL_TO_METER);
		for (int j = 0; j < this.level[levelNumber].getCheckPoints().length; j++)
			this.checkpoints[j] = new NormalCheckpoint(this.world,
					this.level[levelNumber].getCheckPoints()[j].x * PIXEL_TO_METER,
					this.level[levelNumber].getCheckPoints()[j].y * PIXEL_TO_METER);
		scoreBoard.reset(0);

		System.out.println(" - " + (System.currentTimeMillis() - time) + "ms");
	}

	private static Sprite createScaledSprite(String location) {
		final Sprite s = new Sprite(new Texture(Gdx.files.internal(location)));
		s.setSize(s.getWidth() * PIXEL_TO_METER, s.getHeight() * PIXEL_TO_METER);
		s.setOriginCenter();
		return s;
	}

	private void startBuilding(Tower t) {
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
		towerMenu.unselectAll();
		for (final Tower tower : towers)
			tower.activateRange(false);
	}

	private void goBack() {
		gameStateManager.setGameState(new MenuState(gameStateManager));
	}

	@Override
	protected void handleInput() {
		GameStateMethods.toggleFullScreen(true);
		mousePos = GameStateMethods.getMousePosition(camera);

		// go back
		if (Gdx.input.isCatchBackKey() || Gdx.input.isKeyJustPressed(Keys.ESCAPE))
			goBack();

		// control the car
		if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP))
			car.accelarate();
		if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN))
			car.brake();
		if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT))
			car.steerLeft();
		if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT))
			car.steerRight();

		// toggle pause
		if (Gdx.input.isKeyJustPressed(Keys.P))
			pause = !pause;

		// toggle sound
		if (Gdx.input.isKeyJustPressed(Keys.U))
			soundOn = !soundOn;
		// toggle background music
		if (Gdx.input.isKeyJustPressed(Keys.M))
			disableBgMusic = !disableBgMusic;


		// select tower
		if (Gdx.input.isKeyJustPressed(Keys.NUM_1))
			towerMenu.selectTower(0, mousePos, enemies);
		if (Gdx.input.isKeyJustPressed(Keys.NUM_2))
			towerMenu.selectTower(1, mousePos, enemies);
		if (Gdx.input.isKeyJustPressed(Keys.NUM_3))
			towerMenu.selectTower(2, mousePos, enemies);
		if (Gdx.input.isKeyJustPressed(Keys.NUM_4))
			towerMenu.selectTower(3, mousePos, enemies);

		// build tower if in building mode
		if (Gdx.input.justTouched() && buildingtower != null)
			buildTowerIfAllowed(true);

		// turn on developer shortcuts
		if (MainGame.DEVELOPER_MODE)
			debugInputs();
	}

	private void debugInputs() {
		// toggle developer score board
		if (Gdx.input.isKeyJustPressed(Keys.F8))
			scoreBoard.setDebugDisplay(!scoreBoard.getDebugDisplay());

		// manually instantiate enemies
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

		// debug renderer
		if (Gdx.input.isKeyJustPressed(Keys.X))
			debugBox2D = !debugBox2D;
		if (Gdx.input.isKeyJustPressed(Keys.C))
			debugCollision = !debugCollision;
		if (Gdx.input.isKeyJustPressed(Keys.V))
			debugWay = !debugWay;
		if (Gdx.input.isKeyJustPressed(Keys.B))
			debugDistance = !debugDistance;

		// manipulate current level
		if (Gdx.input.isKeyJustPressed(Keys.NUM_5))
			loadLevel(scoreBoard.getLevel() + 1);
		if (Gdx.input.isKeyJustPressed(Keys.NUM_7))
			scoreBoard.addMoney(1000);
		if (Gdx.input.isKeyJustPressed(Keys.NUM_8))
			scoreBoard.reduceLife(scoreBoard.getHealth());
		if (Gdx.input.isKeyJustPressed(Keys.NUM_9)) {
			for (int i = 0; i < enemies.size; i++) {
				final Enemy enemy = enemies.get(i);
				if (!enemy.isActivated())
					enemy.activateEnemy();
				if (!enemy.isTot())
					enemy.takeDamage(enemy.getHealth());
			}
		}
		if (Gdx.input.isKeyJustPressed(Keys.NUM_0))
			tutorialState++;



		// other things
		if (Gdx.input.isKeyJustPressed(Keys.T)) {
			unlockAllTowers = !unlockAllTowers;
			if (unlockAllTowers) {
				for (int i = 0; i < 4; i++)
					towerMenu.unlockTower(i);
			} else {
				final boolean[] towersUnlockedForThisLevel = level[scoreBoard.getLevel() - 1].getTowersUnlocked();
				for (int i = 0; i < towersUnlockedForThisLevel.length; i++)
					towerMenu.unlockTower(i, towersUnlockedForThisLevel[i]);
			}
		}
		if (Gdx.input.isKeyJustPressed(Keys.E)) {
			for (final Enemy e : enemies)
				e.activateEnemy();
		}
		if (Gdx.input.isKeyJustPressed(Keys.R))
			scoreBoard.addScore(1000);
		if (Gdx.input.isKeyJustPressed(Keys.COMMA))
			speedFactor = 1;
		if (Gdx.input.isKeyJustPressed(Keys.PERIOD))
			speedFactor += 1;
	}

	private void buildTowerIfAllowed(final boolean userClicked) {
		// if position and money are OK build it
		if (buildingMoneyIsEnough(buildingtower) && buildingPositionIsAllowed(buildingtower)) {
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
				buildingtower.setBlockBuildingMode(false);
			}
		} else {
			buildingtower.setBlockBuildingMode(true);
		}
	}

	@Override
	protected void update(float deltaTime) {

		// if the pause settings change
		if (lastPause != pause) {
			lastPause = pause;
			// pause or resume all sounds
			if (pause && soundOn) {
				backgroundMusic.pause();
				soundmoney.pause();
				carSound.pause();
				carSoundStart.pause();
			} else if (!pause && soundOn) {
				if (!disableBgMusic)
					backgroundMusic.play();
				carSound.play();
				carSoundStart.resume();
				soundmoney.resume();
			}
		}

		if (pause)
			return;

		// if the sound settings change
		if (lastSound != soundOn) {
			lastSound = soundOn;
			preferencesManager.saveSoundSetting(soundOn);
			// turn tower sound on and of
			Tower.setSoundOn(soundOn);
			for (final Tower tower : towers)
				tower.updateSound();
			// turn background music on/off
			if (soundOn) {
				if (!disableBgMusic)
					backgroundMusic.play();
				carSound.play();
				carSoundStart.resume();
				soundmoney.resume();
			} else {
				backgroundMusic.pause();
				soundmoney.pause();
				carSound.pause();
				carSoundStart.pause();
			}
		}

		// minimize time for wave text - only if it's not pause
		timeforwavetext -= deltaTime;

		// mark a tower red if the build position is not correct
		if (buildingtower != null)
			buildTowerIfAllowed(false);

		// update objects
		controllerHelper.update();
		towerMenu.update();
		scoreBoard.update(deltaTime * speedFactor);
		car.update(deltaTime);
		for (final Tower tower : towers)
			tower.update(deltaTime, mousePos);

		// check additionally if enemies should be activated
		for (int i = 0; i < enemies.size; i++) {
			final Enemy enemy = enemies.get(i);
			enemy.update(deltaTime);
			if (!enemy.isActivated() && enemy.getTime() < scoreBoard.getTime())
				enemy.activateEnemy();
		}

		// update building tower
		if (buildingtower == null) {
			buildingtower = towerMenu.getCurrentTower();
			if (buildingtower != null)
				startBuilding(buildingtower);
		} else {
			buildingtower.update(deltaTime, padActivated ? padPos : mousePos);
			buildingtower = towerMenu.getCurrentTower();
			if (buildingtower == null)
				stopBuilding();
		}

		// garbage collect tower projectiles that should be deleted
		for (final Tower tower : towers) {
			Array<Body> rb = tower.removeProjectiles();
			if (rb != null) {
				for (final Body body : rb) {
					// why compare world?, there is only one
					if (body.getWorld() == world)
						world.destroyBody(body);
				}
			}
		}

		// garbage collect enemies
		for (final Enemy enemy : enemies) {
			// if enemy has a body
			if (!enemy.isBodyDeleted()) {
				// and it's body or itself should be deleted
				if (enemy.isDeleteBody() || enemy.isDelete()) {
					// remove body from the world
					world.destroyBody(enemy.getBody());
					enemy.setBodyDeleted(true);
				}
				// if enemy should be deleted delete the from the list
				if (enemy.isDelete()) {
					enemies.removeValue(enemy, true);
				}
			}
			// If the enemy is dead add him to the other enemy list
			if(enemy.isTot() && !enemy.isDelete()) {
				enemies.removeValue(enemy, true);
				enemiesdead.add(enemy);
			}
		}

		// check if the current wave is dead and a new one should start
		updateWaves();

		updateSmoke();

		// update box2D physics
		updatePhysics(deltaTime);
	}

	private void updateSmoke() {
		if(pause||scoreBoard.getHealth()==100)
			return;
		float smokeseconds=15/(100f-scoreBoard.getHealth());
		timesincesmoke=timesincesmoke+Gdx.graphics.getDeltaTime();
		while(timesincesmoke>smokeseconds) {
			spawnSmoke();
			timesincesmoke=timesincesmoke-smokeseconds;
		}
		Array<Sprite> deadsmoke=new Array<Sprite>();
		for (Sprite s : trailersmoke) {
			s.setPosition(s.getX()+MathUtils.random(0.05f), s.getY()+0.05f);
			if(s.getWidth()>spriteSmoke.getWidth())
			s.setColor(1,1,1,s.getColor().a-0.0000001f);
			s.setSize(s.getWidth()+0.02f, s.getHeight()+0.02f);
			s.setRotation(s.getRotation()+MathUtils.random(-2.5f,-0.5f));
			if(s.getColor().a<0.1f)
				deadsmoke.add(s);

		}
		for (Sprite s : deadsmoke) {
			trailersmoke.removeValue(s, true);
		}
	}

	private void spawnSmoke() {
		Sprite s=new Sprite(spriteSmoke);
		s.setRotation(MathUtils.random(360));
		s.setSize(s.getWidth()/8, s.getHeight()/8);
		s.setPosition(map.getTargetPosition().x+MathUtils.random(-2f,0), map.getTargetPosition().y-4f);
		trailersmoke.add(s);
	}


	@Override
	public void render(final SpriteBatch spriteBatch) {

		// set projection matrices
		spriteBatch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(spriteBatch.getProjectionMatrix());

		// draw map
		spriteBatch.begin();
		map.draw(spriteBatch);
		spriteBatch.end();

		// draw transparent tower range shapes
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.begin(ShapeType.Filled);
		for (final Tower tower : towers)
			tower.drawRange(shapeRenderer);
		if (buildingtower != null)
			buildingtower.drawRange(shapeRenderer, new Color(1, 0, 0, 0.4f));
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);

		// draw enemies
		spriteBatch.begin();
		for (final Enemy e : enemiesdead)
			e.draw(spriteBatch);
		for (final Enemy e : enemies)
			e.draw(spriteBatch);
		spriteBatch.end();

		// draw enemy health bars
		shapeRenderer.begin(ShapeType.Filled);
		for (final Enemy e : enemies)
			e.drawHealthBar(shapeRenderer);
		shapeRenderer.end();

		// draw car and tower menu
		spriteBatch.begin();
		car.draw(spriteBatch);
		towerMenu.draw(spriteBatch);

		// draw normal tower / tower ground
		for (final Tower tower : towers)
			tower.draw(spriteBatch);
		spriteBatch.end();

		// draw tower shooting lines
		shapeRenderer.begin(ShapeType.Filled);
		for (final Tower tower : towers)
			tower.drawLine(shapeRenderer);
		shapeRenderer.end();

		// draw if shooting tower shooting top else normal top
		spriteBatch.begin();
		for (final Tower tower : towers)
			tower.drawUpperBuddy(spriteBatch);


		//draw smoke from damaged trailer
		for (Sprite s : trailersmoke) {
			s.draw(spriteBatch);
		}


		// draw building tower on top of them all
		if (buildingtower != null) {
			buildingtower.draw(spriteBatch);
			buildingtower.drawUpperBuddy(spriteBatch);
		}

		// draw pitStop and score board above the rest
		spritePitStop.draw(spriteBatch);
		scoreBoard.draw(spriteBatch);

		// render tutorial
		renderTutorial(spriteBatch);

		// draw pause overlay
		if (pause) {
			spriteBatch.end();
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 0.4f));
			shapeRenderer.rect(0, 0, MainGame.GAME_WIDTH * PIXEL_TO_METER, MainGame.GAME_HEIGHT * PIXEL_TO_METER);
			drawPlayerHealthBar(shapeRenderer);
			shapeRenderer.end();
			Gdx.gl.glDisable(GL20.GL_BLEND);
			spriteBatch.begin();
		}

		// draw centered pause or custom wave text
		if (pause || timeforwavetext > 0) {
			final Vector2 wavePosition = GameStateMethods.calculateCenteredTextPositon(MainGame.font70,
					pause ? "PAUSE" : waveText, MainGame.GAME_WIDTH * PIXEL_TO_METER,
					MainGame.GAME_HEIGHT * PIXEL_TO_METER);
			MainGame.font70.draw(spriteBatch, pause ? "PAUSE" : waveText, wavePosition.x, wavePosition.y);
		}
		spriteBatch.end();

		// render health bar
		shapeRenderer.begin(ShapeType.Filled);
		drawPlayerHealthBar(shapeRenderer);
		shapeRenderer.end();

		// render also the following if in developer mode
		if (MainGame.DEVELOPER_MODE) {
			spriteBatch.begin();

			MainGame.font.setColor(1, 1, 1, 1);
			MainGame.font.getData().setScale(PIXEL_TO_METER);
			MainGame.font.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 30, 35.5f);

			if (debugCollision)
				renderDebugCollision(spriteBatch);
			if (debugDistance)
				renderDebugEntfernung(spriteBatch);
			if (debugWay)
				renderDebugWay(spriteBatch);

			spriteBatch.end();

			if (debugBox2D)
				debugRender.render(world, camera.combined);
		}
	}

	private void drawPlayerHealthBar(final ShapeRenderer shapeRenderer) {
		shapeRenderer.setColor(new Color(1, 0, 0, 1));
		shapeRenderer.rect(map.getHealthBarPos().x * PIXEL_TO_METER, map.getHealthBarPos().y * PIXEL_TO_METER,
				200 * PlayState.PIXEL_TO_METER, 6 * PlayState.PIXEL_TO_METER);
		shapeRenderer.setColor(new Color(0, 1, 0, 1));
		shapeRenderer.rect(map.getHealthBarPos().x * PIXEL_TO_METER, map.getHealthBarPos().y * PIXEL_TO_METER,
				200 * PlayState.PIXEL_TO_METER * (scoreBoard.getHealth() / scoreBoard.getMaxHealth()),
				6 * PlayState.PIXEL_TO_METER);
	}

	private void renderDebugWay(SpriteBatch spriteBatch) {
		MainGame.font.getData().setScale(0.06f);
		for (final Enemy e : enemies) {
			if (e.isActivated() && !e.isTot()) {
				MainGame.font.setColor(e.getColor());
				for (final Node node : e.getWeg())
					MainGame.font.draw(spriteBatch, "x", node.getPosition().x * PlayState.PIXEL_TO_METER,
							node.getPosition().y * PlayState.PIXEL_TO_METER);
			}
		}
	}

	private void renderDebugEntfernung(SpriteBatch spriteBatch) {
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

	private void renderDebugCollision(SpriteBatch spriteBatch) {
		final Node[][] test = this.map.getNodesList();
		MainGame.font.getData().setScale(0.05f);
		for (int i = 0; i < MainGame.GAME_WIDTH; i = i + 10) {
			for (int j = 0; j < MainGame.GAME_HEIGHT; j = j + 10) {
				if (test[i][j].getNoUse()) {
					MainGame.font.setColor(0, 0, 1, 0.5f);
					MainGame.font.draw(spriteBatch, "O", i * PlayState.PIXEL_TO_METER, j * PlayState.PIXEL_TO_METER);
				} else {
					MainGame.font.setColor(1, 0, 0, 0.5f);
					MainGame.font.draw(spriteBatch, "I", i * PlayState.PIXEL_TO_METER, j * PlayState.PIXEL_TO_METER);
				}
			}
		}
	}

	/**
	 * Draws the Tutorial
	 *
	 * -1: Tutorial disabled/finished 0: Learn how to drive the car 1: Finish laps
	 * to earn money 2: Keep finishing laps until enough money for towers 3: Select
	 * a tower 4: Build a tower 5: Learn what to protect
	 *
	 * @param spriteBatch
	 */
	private void renderTutorial(SpriteBatch spriteBatch) {
		if (tutorialState == -1)
			return;

		// check appropriate stage
		switch (tutorialState) {
		case 0:
			if (checkPointsCleared > 1)
				tutorialState++;
			break;
		case 1:
			// Checked in lineFinished();
			break;
		case 2:
			if (scoreBoard.getMoney() > 100)
				tutorialState++;
			break;
		case 3:
			if (buildingtower != null)
				tutorialState++;
			break;
		case 4:
			if (towers.size > 0) {
				tutorialState++;
				tutorialtimer = 60f;
			}
			break;
		case 5:
			if (tutorialtimer > 0)
				tutorialtimer = tutorialtimer - Gdx.graphics.getDeltaTime();
			else
				tutorialState++;
			break;
		case 6:
			tutorialState = -1;
			break;
		}

		MainGame.fontOutline.getData().setScale(PlayState.PIXEL_TO_METER + 0.05f);
		MainGame.fontOutline.setColor(1, 1, 1, 1);

		// Then write the text
		switch (tutorialState) {
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
					towerMenu.getStart().y + 7);
			break;
		case 4:
			MainGame.fontOutline.draw(spriteBatch, "LEFT CLICK TO BUILD", mousePos.x - 5.5f, mousePos.y - 1);
			break;
		case 5:
			MainGame.fontOutline.draw(spriteBatch, "PROTECT YOUR TRAILER FROM THE ZOMBIES!", trailerpos.x - 20.5f,
					trailerpos.y + 0.5f);
			break;
		}

	}

	private void updatePhysics(final float deltaTime) {
		if (pause)
			return;

		physicsaccumulator += Math.min(deltaTime, 0.25f);
		while (physicsaccumulator >= TIME_STEP) {
			world.step(TIME_STEP * this.speedFactor, 6, 2);
			physicsaccumulator -= TIME_STEP;
		}
	}

	@Override
	protected void dispose() {
		// remove all listeners
		Controllers.removeListener(controllerHelper);
		// dispose loaded objects
		for (final Enemy enemy : enemies)
			enemy.dispose();
		enemies.clear();
		for (final Tower tower : towers)
			tower.dispose();
		towers.clear();
		car.dispose();
		towerMenu.dispose();
		// dispose images, sounds and other resources
		MgTower.groundTower.dispose();
		MgTower.upperTower.dispose();
		MgTower.towerFiring.dispose();
		LaserTower.groundTower.dispose();
		LaserTower.upperTower.dispose();
		LaserTower.towerFiring.dispose();
		EnemySmall.normalTexture.dispose();
		EnemySmall.deadTexture.dispose();
		EnemyFat.normalTexture.dispose();
		EnemyFat.deadTexture.dispose();
		EnemyBicycle.normalTexture.dispose();
		EnemyBicycle.deadTexture.dispose();
		TowerMenu.cannonButton.dispose();
		TowerMenu.laserButton.dispose();
		TowerMenu.flameButton.dispose();
		MgTower.soundShoot.dispose();
		LaserTower.soundShoot.dispose();
		backgroundMusic.dispose();
		splatt.dispose();
		soundmoney.dispose();
		carSound.dispose();
		victorySound.dispose();
		shapeRenderer.dispose();
	}

	@Override
	public void collisionCallbackCarEnemy(final Car car, final Enemy enemy) {
		// if the new health after the hit is smaller than 0 play kill sound
		if ((!enemy.isBodyDeleted() && car.hitEnemy(enemy) <= 0) && soundOn)
			splatt.play(1, MathUtils.random(0.5f, 2f), 0);
	}

	@Override
	public void collisionCallbackCarCheckpoint(final Car car, final Checkpoint checkpoint) {
		// activate checkpoints when the car collides with them
		checkpoint.setActivated(true);
		// count checkpoints
		checkPointsCleared++;
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
			int lapmoney = (int)level[scoreBoard.getLevel() - 1].getMoneyPerLap() - towers.size;
			final int fastBonus = (int) (level[scoreBoard.getLevel() - 1].getTimebonus()
					- scoreBoard.getCurrentTime() * 2);
			scoreBoard.newLap((fastBonus > 0) ? lapmoney + fastBonus : lapmoney);
			// play cash sound if sound activated
			if (soundOn)
				soundmoney.play(1);

			if (tutorialState == 1)
				tutorialState++;
		}
	}

	@Override
	public void collisionCallbackCarFinishLine(final Car car, final FinishLine finishLine) {
		lapFinished();
	}

	@Override
	public void collisionCallbackFlameEnemy(final Enemy enemy, final Flame flame) {
		enemy.takeDamage(flame.getDamage());
	}

	private void updateWaves() {

		// if all enemies are active (this means no enemy is invisible) and dead
		if (allEnemiesAreActive() && allEnemiesDead()) {
			final Array<Wave> currentLevelWaves = level[scoreBoard.getLevel() - 1].getWaves();
			final int currentWave = scoreBoard.getWaveNumber();
			// and the current wave is the maximum wave
			if (currentWave >= currentLevelWaves.size) {
				// means the level is finished
				victoryLevel();
			} else {
				// if not the maximum wave load the next wave
				scoreBoard.setWaveNumber(currentWave + 1);
				// display feedback of new wave on screen
				if (currentWave + 1 < currentLevelWaves.size)
					setWaveText("WAVE " + (currentWave + 1));
				else
					setWaveText("FINAL WAVE");
				// create and add all enemies of the current wave to all enemies
				System.out.println();
				enemies.addAll(currentLevelWaves.get(currentWave).createEnemies(map.getSpawnPosition(), world, map,
						scoreBoard.getTime()));
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

	private void setWaveText(final String waveText) {
		this.waveText = waveText;
		timeforwavetext = 2f;
	}

	private void victoryLevel() {
		System.out.println("Level finished " + MainGame.level);

		// play victory sound
		if (soundOn)
			victorySound.play();

		// load a new level
		loadLevel(scoreBoard.getLevel() + 1);
	}

	/**
	 * Game was won
	 */
	private void victoryGame() {
		gameStateManager.setGameState(new GameWonState(gameStateManager, scoreBoard.getScore(), scoreBoard.getLevel()));
	}

	/**
	 * @return all enemies are dead
	 */
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
			gameStateManager.setGameState(
					new HighscoreNameState(gameStateManager, scoreBoard.getScore(), scoreBoard.getLevel(), false));
		else {
			gameStateManager.setGameState(new GameOverState(gameStateManager, scoreBoard.getLevel()));
		}

	}

	@Override
	public void enemyHitsHomeCallback(final Enemy enemy) {
		scoreBoard.reduceLife(enemy.getDamadge());
		enemy.setDelete(true);
	    if (soundOn)
				soundDamage.play(1,MathUtils.random(1f, 1.1f),0f);
	    spawnSmoke();
	}

	@Override
	public void controllerCallbackSteerCar(boolean left) {
		if (left)
			car.steerLeft();
		else
			car.steerRight();
	}

	@Override
	public void controllerCallbackStartBuildingMode(final int towerId) {
		if (towerId == -1) {
			// stop building
			padActivated = false;
			stopBuilding();
		} else {
			padActivated = towerMenu.selectTower(towerId, padPos, enemies);
		}
	}

	@Override
	public void controllerCallbackAccelerateCar(final boolean forwards) {
		if (forwards)
			car.accelarate();
		else
			car.brake();
	}

	@Override
	public void controllerCallbackBackButtonPressed() {
		goBack();
	}

	@Override
	public void controllerCallbackToggleFullScreen() {
		GameStateMethods.toggleFullScreen();
	}

	@Override
	public void controllerCallbackToggleSound() {
		soundOn = !soundOn;
	}

	@Override
	public void controllerCallbackTogglePause() {
		pause = !pause;
	}

	@Override
	public void controllerMouseChanged(final Vector3 rightPad) {
		if (!padActivated)
			return;

		if (((padPos.x + rightPad.x >= 0) && (padPos.x + rightPad.x <= MainGame.GAME_WIDTH * PIXEL_TO_METER))
				&& ((padPos.y + rightPad.y >= 0) && (padPos.y + rightPad.y <= MainGame.GAME_HEIGHT * PIXEL_TO_METER))) {
			padPos.mulAdd(rightPad, 1);
		}

		if (buildingtower != null)
			buildingtower.update(0, padPos);
	}

	@Override
	public void buildTower() {
		// when in building mode try build the current tower
		if (buildingtower != null)
			buildTowerIfAllowed(true);
	}

	@Override
	public void enemyDied(final Enemy enemy) {
		scoreBoard.killedEnemy(enemy.getScore(), enemy.getMoney());
	}
}
