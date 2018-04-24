package com.mygdx.game.gamestate.state;

import java.util.LinkedList;
import java.util.TimerTask;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.mygdx.game.Enemy;
import com.mygdx.game.EnemyWaveEntry;
import com.mygdx.game.Enemy_Lincoln;
import com.mygdx.game.Enemy_bicycle;
import com.mygdx.game.Enemy_fat;
import com.mygdx.game.Enemy_small;
import com.mygdx.game.MainGame;
import com.mygdx.game.MainMap;
import com.mygdx.game.Node;
import com.mygdx.game.PreferencesManager;
import com.mygdx.game.ScoreBoard;
import com.mygdx.game.TurmMenu;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.objects.Checkpoint;
import com.mygdx.game.objects.FinishLine;
import com.mygdx.game.objects.Tower;
import com.mygdx.game.objects.checkpoints.NormalCheckpoint;
import com.mygdx.game.objects.tower.FireTower;
import com.mygdx.game.objects.tower.Flame;
import com.mygdx.game.objects.tower.LaserTower;
import com.mygdx.game.objects.tower.MGTower;

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

	private TurmMenu turmmenu;

	private PreferencesManager preferencesManager;

	private MainMap map;
	private Sprite pitStop;

	public static ScoreBoard scoreBoard;
	private Tower buildingtower;

	private int moneyPerLap = 50;

	private Sound splatt, money, carsound;

	/**
	 * Time since last physic Steps
	 */

	int currentwave = 0;

	boolean infiniteenemies = false;

	private float physicsaccumulator = 0f;
	private Box2DDebugRenderer debugRender;

	private float timeforwavetext=3f;
	private String wavetext="";
	
	/**
	 * Time for physic Steps
	 */
	public final static float TIME_STEP = 1 / 60f;
	public final static float PIXEL_TO_METER = 0.05f;
	public final static float METER_TO_PIXEL = 20f;
	public final static float RESOLUTION_WIDTH = 1280f;
	public final static float RESOLUTION_HEIGHT = 720f;

	private Checkpoint[] checkpoints;
	
	private Music backgroundMusic;

	// Zur identifizierung von Collisions Entitys
	public final static short PLAYER_BOX = 0x1; // 0001
	public final static short ENEMY_BOX = 0x1 << 1; // 0010 or 0x2 in hex

	public Array<EnemyWaveEntry> currentEnemyWaves;

	public PlayState(GameStateManager gameStateManager, int level) {
		super(gameStateManager);
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

		final Sprite s1 = createScaledSprite("buttons/cannonbutton.png");
		final Sprite s2 = createScaledSprite("buttons/laserbutton.png");
		final Sprite s3 = createScaledSprite("buttons/flamebutton.png");
		final Sprite s4 = createScaledSprite("buttons/cannonbutton.png");
		final Sprite s5 = createScaledSprite("buttons/cannonbutton.png");

		// set STATIC textures
		NormalCheckpoint.normalCheckPointActivated = new Texture(
				Gdx.files.internal("checkpoints/checkpoint_normal_activated.png"));
		NormalCheckpoint.normalCheckPointDisabled = new Texture(
				Gdx.files.internal("checkpoints/checkpoint_normal_disabled.png"));

		Tower.circleTexture = new Texture(Gdx.files.internal("tower/range.png"));

		MGTower.groundTower = new Texture(Gdx.files.internal("tower/tower_empty.png"));
		MGTower.upperTower = new Texture(Gdx.files.internal("tower/tower_empty_upper.png"));
		MGTower.towerFiring = new Texture(Gdx.files.internal("tower/tower_mg_firing.png"));
		MGTower.soundShoot = Gdx.audio.newSound(Gdx.files.internal("sounds/mgturret.wav"));

		LaserTower.groundTower = new Texture(Gdx.files.internal("tower/tower_laser_bottom.png"));
		LaserTower.upperTower = new Texture(Gdx.files.internal("tower/tower_laser_upper.png"));
		LaserTower.towerFiring = new Texture(Gdx.files.internal("tower/tower_laser_firing.png"));
		LaserTower.soundShoot = Gdx.audio.newSound(Gdx.files.internal("sounds/laser.wav"));

		FireTower.groundTower = new Texture(Gdx.files.internal("tower/tower_fire_bottom.png"));
		FireTower.upperTower = new Texture(Gdx.files.internal("tower/tower_fire_upper.png"));
		FireTower.towerFiring = new Texture(Gdx.files.internal("tower/tower_fire_firing.png"));
		FireTower.tflame = new Texture(Gdx.files.internal("tower/flame.png"));
		FireTower.soundShoot = Gdx.audio.newSound(Gdx.files.internal("sounds/mgturret.wav"));

		Enemy_small.normalTexture = new Texture(Gdx.files.internal("zombies/zombie_standard.png"));
		Enemy_small.deadTexture = new Texture(Gdx.files.internal("zombies/zombie_standard_dead.png"));
		Enemy_small.damageTexture = new Texture(Gdx.files.internal("zombies/zombie_blood.png"));

		Enemy_fat.normalTexture = new Texture(Gdx.files.internal("zombies/zombie_fat.png"));
		Enemy_fat.deadTexture = new Texture(Gdx.files.internal("zombies/zombie_fat_dead.png"));
		Enemy_fat.damageTexture = new Texture(Gdx.files.internal("zombies/zombie_blood.png"));

		Enemy_bicycle.normalTexture = new Texture(Gdx.files.internal("zombies/zombie_bicycle.png"));
		Enemy_bicycle.deadTexture = new Texture(Gdx.files.internal("zombies/zombie_bicycle_dead.png"));
		Enemy_bicycle.damageTexture = new Texture(Gdx.files.internal("zombies/zombie_blood.png"));

		Enemy_Lincoln.normalTexture = new Texture(Gdx.files.internal("zombies/zombie_lincoln.png"));
		Enemy_Lincoln.deadTexture = new Texture(Gdx.files.internal("zombies/zombie_lincoln_dead.png"));
		Enemy_Lincoln.damageTexture = new Texture(Gdx.files.internal("zombies/zombie_blood.png"));

		splatt = Gdx.audio.newSound(Gdx.files.internal("sounds/splatt.wav"));
		money = Gdx.audio.newSound(Gdx.files.internal("sounds/cash.wav"));
		carsound = Gdx.audio.newSound(Gdx.files.internal("sounds/car_sound2.wav"));
		// Sets this camera to an orthographic projection, centered at (viewportWidth/2,
		// viewportHeight/2), with the y-axis pointing up or down.
		camera.setToOrtho(false, MainGame.GAME_WIDTH * PIXEL_TO_METER, MainGame.GAME_HEIGHT * PIXEL_TO_METER);

		enemies = new Array<Enemy>();
		towers = new Array<Tower>();
		collis = new CollisionListener(this);

		world = new World(new Vector2(0, 0), true);
		world.setContactListener(collis);
		debugRender = new Box2DDebugRenderer();

		car = new Car(world, smaincar, 440, 220);
		finishline = new FinishLine(world, sfinishline, 380, 220);

		debugBox2D = false;
		debugCollision = false;
		debugWay = false;
		debugEntfernung = false;

		turmmenu = new TurmMenu(s1, s2, s3, s4, s5, world, enemies);

		checkpoints = new Checkpoint[4];
		float[][] checkPointPosition = { { 300, 230 }, { 320, 600 }, { 850, 600 }, { 850, 230 } };
		for (int i = 0; i < checkpoints.length; i++)
			checkpoints[i] = new NormalCheckpoint(world, checkPointPosition[i][0] * PIXEL_TO_METER,
					checkPointPosition[i][1] * PIXEL_TO_METER);

		pitStop = createScaledSprite("pit_stop/pit_stop_01.png");
		//pitStop.setPosition(100, 100);



		loadLevel(level);
		loadLevel(3);

		
		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/zombiecar3.wav"));
		backgroundMusic.setLooping(true);
		backgroundMusic.setVolume(0.6f);
		if (soundon) backgroundMusic.play();

	}

	public void loadLevel(int i) {
		scoreBoard.setLevel(i);
		switch (i) {
		case 1:
			world = new World(new Vector2(), true);
			world.setContactListener(collis);
			debugRender = new Box2DDebugRenderer();
			car = new Car(world, smaincar, 440, 220);
			finishline = new FinishLine(world, sfinishline, 380, 220);
			
			map = new MainMap("track1", world, finishline.body);
			map.setSpawn(new Vector2(220, 20));
			scurrenttrack = strack1;
			pitStop.setPosition(165 * PIXEL_TO_METER, -5 * PIXEL_TO_METER);
			float[][] checkPointPosition = { { 300, 230 }, { 320, 600 }, { 850, 600 }, { 850, 230 } };
			for (int j = 0; j < checkpoints.length; j++)
				checkpoints[j] = new NormalCheckpoint(world, checkPointPosition[j][0] * PIXEL_TO_METER,
						checkPointPosition[j][1] * PIXEL_TO_METER);
			break; 
		case 2:
			world = new World(new Vector2(), true);
			world.setContactListener(collis);
			debugRender = new Box2DDebugRenderer();
			car = new Car(world, smaincar, 440, 220);
			finishline = new FinishLine(world, sfinishline, 360, 240);
			
			map = new MainMap("track2", world, finishline.body);
			map.setSpawn(new Vector2(230, 50));
			scurrenttrack = strack2;
			pitStop.setPosition(165 * PIXEL_TO_METER, -5 * PIXEL_TO_METER);
			float[][] checkPointPosition1 = { { 300, 230 }, { 320, 600 }, { 850, 600 }, { 850, 230 } };
			for (int j = 0; j < checkpoints.length; j++)
				checkpoints[j] = new NormalCheckpoint(world, checkPointPosition1[j][0] * PIXEL_TO_METER,
						checkPointPosition1[j][1] * PIXEL_TO_METER);
			turmmenu.tower2unlocked=true;
			turmmenu.updateAlpha();
			break;
		case 3:
			world = new World(new Vector2(), true);
			world.setContactListener(collis);
			debugRender = new Box2DDebugRenderer();
			car = new Car(world, smaincar, 440, 220);
			finishline = new FinishLine(world, sfinishline, 350, 150);
			
			map = new MainMap("track3", world, finishline.body);
			map.setSpawn(new Vector2(170, 100));
			scurrenttrack = strack3;
			pitStop.setPosition(100 * PIXEL_TO_METER, -5 * PIXEL_TO_METER);

			float[][] checkPointPosition11 = { { 300, 170 }, { 320, 570 }, { 850, 570 }, { 850, 170 } };
			for (int j = 0; j < checkpoints.length; j++)
				checkpoints[j] = new NormalCheckpoint(world, checkPointPosition11[j][0] * PIXEL_TO_METER,
						checkPointPosition11[j][1] * PIXEL_TO_METER);
			turmmenu.tower3unlocked=true;
			turmmenu.updateAlpha();
			break;

		default:
			break;
		}

		currentEnemyWaves = map.getEnemyWaves();
		System.out.println("Play state entered");

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
		for (int i = 0; i < cornerPoints.length; i++)
			isAllowed = this.map.isInBody(cornerPoints[i][0], cornerPoints[i][1]);
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
			if (soundon == false && backgroundMusic.isPlaying()) backgroundMusic.pause();
			if (soundon == true) backgroundMusic.play();
		}	
		if (Gdx.input.isKeyJustPressed(Keys.F))
			enemies.add(new Enemy_small(220, 20, world, map));
		if (Gdx.input.isKeyJustPressed(Keys.G))
			enemies.add(new Enemy_fat(220, 20, world, map));
		if (Gdx.input.isKeyJustPressed(Keys.H))
			enemies.add(new Enemy_bicycle(220, 20, world, map));
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
		if (Gdx.input.isKeyJustPressed(Keys.NUM_1))
			turmmenu.selectTower(1);
		if (Gdx.input.isKeyJustPressed(Keys.NUM_2))
			turmmenu.selectTower(2);
		if (Gdx.input.isKeyJustPressed(Keys.NUM_3))
			turmmenu.selectTower(3);
		if (Gdx.input.isKeyJustPressed(Keys.NUM_4))
			turmmenu.selectTower(4);
		if (Gdx.input.isKeyJustPressed(Keys.NUM_5))
			turmmenu.selectTower(5);
		if (Gdx.input.justTouched() && this.buildingtower != null)
			buildTowerIfAllowed();
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

		mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(mousePos);

		handleInput();

		car.update(deltaTime);

		if (soundon)
			if (car.getForward().x != 0 && !carsoundPlaying) {
				carsound.loop();
				carsoundPlaying = true;
			} else {
				if (car.getForward().x == 0) {
					carsound.stop();
					carsoundPlaying = false;

				}
			}

		if (buildingtower == null) {
			buildingtower = turmmenu.getCurrentTower();
			if (buildingtower != null)
				startBuilding(buildingtower);
		}

		if (buildingtower != null) {
			buildingtower.update(deltaTime, mousePos);
			buildingtower = turmmenu.getCurrentTower();
			if (buildingtower == null) {
				stopBuilding();
			}
		}
		for (final Tower t : towers)
			t.update(deltaTime, mousePos);

		for (final EnemyWaveEntry entry : currentEnemyWaves) {
			if (entry.getTimeInSeconds() < scoreBoard.getTime()) {
				enemies.addAll(EnemyWaveEntry.createEnemy(entry, world, map));
				currentEnemyWaves.removeValue(entry, true);
			}
		}
		if (infiniteenemies) {
			if (MathUtils.random(1000) > 950) {
				Enemy e = new Enemy_small(220, 20, world, map);
				enemies.add(e);
			}
			if (MathUtils.random(1000) > 990) {
				Enemy e = new Enemy_bicycle(220, 20, world, map);
				enemies.add(e);
			}
			if (MathUtils.random(1000) > 995) {
				Enemy e = new Enemy_fat(220, 20, world, map);
				enemies.add(e);
			}

			// for (final EnemyWaveEntry entry : currentEnemyWaves) {
			// if (entry.getTimeInSeconds() < scoreBoard.getTime()) {
			// enemies.addAll(EnemyWaveEntry.createEnemy(entry, world, map));
			// currentEnemyWaves.removeValue(entry, true);
			// }
			// }
			if (infiniteenemies) {
				if (MathUtils.random(1000) > 950) {
					Enemy e = new Enemy_small(220, 20, world, map);
					enemies.add(e);
				}
				if (MathUtils.random(1000) > 990) {
					Enemy e = new Enemy_bicycle(220, 20, world, map);
					enemies.add(e);
				}
				if (MathUtils.random(1000) > 995) {
					Enemy e = new Enemy_fat(220, 20, world, map);
					enemies.add(e);
				}

			}
		}
		scoreBoard.update(deltaTime);
		camera.update();
			
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

	}

	@Override
	public void render(SpriteBatch spriteBatch) {

		// set projection matrix
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		scurrenttrack.draw(spriteBatch);
		finishline.draw(spriteBatch);

		// draw checkpoints
		if (debugBox2D)
			for (final Checkpoint checkpoint : checkpoints)
				checkpoint.draw(spriteBatch);
		// draw tower
		pitStop.draw(spriteBatch);
		if (buildingtower != null)
			buildingtower.draw(spriteBatch);
		for (final Tower tower : towers)
			tower.draw(spriteBatch);

		// draw enemies
		for (Enemy e : enemies) {
			e.update(Gdx.graphics.getDeltaTime());
			e.draw(spriteBatch);
		}
		// draw pitstop
		pitStop.draw(spriteBatch);

		for (final Tower tower : towers) {
			tower.drawProjectile(spriteBatch);
			tower.drawUpperBuddy(spriteBatch);
		}

		if (debugCollision) {
			Node[][] test = this.map.getNodesList();
			MainGame.font.getData().setScale(0.1f);
			for (int i = 0; i < MainGame.GAME_WIDTH; i = i + 10) {
				for (int j = 0; j < MainGame.GAME_HEIGHT; j = j + 10) {
					if (test[i][j].getNoUse())
						MainGame.font.draw(spriteBatch, "O", i * PlayState.PIXEL_TO_METER,
								j * PlayState.PIXEL_TO_METER);
					else
						MainGame.font.draw(spriteBatch, "T", i * PlayState.PIXEL_TO_METER,
								j * PlayState.PIXEL_TO_METER);
				}
			}
		}

		if (debugEntfernung) {
			Node[][] test = this.map.getNodesList();
			MainGame.font.getData().setScale(0.01f);
			for (int i = 0; i < MainGame.GAME_WIDTH; i = i + 10) {
				for (int j = 0; j < MainGame.GAME_HEIGHT; j = j + 10)
					MainGame.font.draw(spriteBatch, test[i][j].getH() + "", i * PlayState.PIXEL_TO_METER,
							j * PlayState.PIXEL_TO_METER);
			}
		}

		if (debugWay) {
			MainGame.font.getData().setScale(0.02f);
			for (Enemy e : enemies) {
				// e.findWay();
				final LinkedList<Node> weg;
				weg = e.getWeg();
				for (Node node : weg)
					MainGame.font.draw(spriteBatch, "x", node.getX() * PlayState.PIXEL_TO_METER,
							node.getY() * PlayState.PIXEL_TO_METER);
			}
		}

		// draw car
		car.draw(spriteBatch);

		scoreBoard.draw(spriteBatch);

		turmmenu.draw(spriteBatch);
		if(timeforwavetext>0) {
		MainGame.waveFont.draw(spriteBatch,wavetext,20,25);
		
		timeforwavetext=timeforwavetext-Gdx.graphics.getDeltaTime();
		}spriteBatch.end();

		if (debugBox2D)
			debugRender.render(world, camera.combined);

		updatePhysics(Gdx.graphics.getDeltaTime());
		
	}

	public void updatePhysics(float deltaTime) {
		float frameTime = Math.min(deltaTime, 0.25f);
		physicsaccumulator += frameTime;
		while (physicsaccumulator >= TIME_STEP) {
			world.step(TIME_STEP, 6, 2);
			physicsaccumulator -= TIME_STEP;
		}
		Array<Enemy> toremove = new Array<Enemy>();
		for (final Enemy enemy : enemies) {
			if (enemy.justDied) {
				enemy.body.setActive(false);
				enemy.justDied = false;
				scoreBoard.killedEnemy(enemy.getScore(), enemy.getMoney());
			}
			if (enemy.delete) {
				toremove.add(enemy);
				world.destroyBody(enemy.body);
			}
		}
		for (Enemy e : toremove) {
			enemies.removeValue(e, true);
		}

		for (Tower t : towers) {
			Array<Body> ab = new Array<Body>();
			Array<Body> rb = new Array<Body>();
			rb = t.removeProjectiles();
			if (rb != null)
				ab.addAll(rb);
			for (Body body : ab) {

				world.destroyBody(body);
			}
		}

	}

	@Override
	protected void dispose() {
		// dispose STATIC textures
		NormalCheckpoint.normalCheckPointActivated.dispose();
		NormalCheckpoint.normalCheckPointDisabled.dispose();
		Tower.circleTexture.dispose();
		MGTower.groundTower.dispose();
		MGTower.upperTower.dispose();
		MGTower.towerFiring.dispose();
		MGTower.soundShoot.dispose();
		LaserTower.groundTower.dispose();
		LaserTower.upperTower.dispose();
		LaserTower.towerFiring.dispose();
		LaserTower.soundShoot.dispose();
		Enemy_small.normalTexture.dispose();
		Enemy_small.deadTexture.dispose();
		Enemy_fat.normalTexture.dispose();
		Enemy_fat.deadTexture.dispose();
		Enemy_bicycle.normalTexture.dispose();
		Enemy_bicycle.deadTexture.dispose();
		backgroundMusic.dispose();
		carsound.dispose();
		MainGame.waveFont.getData().setScale(10f);
	}

	@Override
	public void collisionCarEnemy(Car car, Enemy enemy) {
		car.hitEnemy(enemy);
		if (enemy.health < 0) {
			if (soundon)
				splatt.play(1, MathUtils.random(0.5f, 2f), 0);
		}
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
	public void collisionFlameEnemy(Enemy e, Flame f) {
		// TODO Auto-generated method stub

	}

	public void updateWaves1() {

		int totalwaves = 0;
		if (currentEnemyWaves.size == 0) {
			currentwave++;
			if (currentwave > totalwaves)
				LevelVictory();
			else {
				scoreBoard.setWaveNumber(currentwave);
				System.out.println("Starte Wave" + currentwave);
				switch (currentwave) {
				case 1:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 15, 10, 0.0f, 2, 4f, 0, 0.0f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 25, 20, 0.0f, 3, 3f, 0, 0.0f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 40, 30, 0.5f, 4, 3f, 0, 0.0f));
					break;
				case 2:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 25, 0, 0, 3, 4f, 0, 0));
					break;
				case 3:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 35, 0, 0, 5, 4f, 0, 0));
					break;
				case 4:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 60, 0.4f, 0, 0, 0, 0));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 15, 60, 0.4f, 0, 0, 0, 0));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 25, 70, 0.4f, 0, 0, 0, 0));
					break;
				case 5:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 45, 0, 0));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 10, 50, 0, 0));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 20, 50, 0, 0));
					break;
				case 6:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 45, 0, 0));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 15, 50, 0, 0));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 25, 50, 0, 0));
					break;
				case 7:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 50, 0, 0));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 15, 55, 0, 0));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 25, 60, 0, 0));
					break;
				case 8:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 55, 0, 0));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 15, 60, 0, 0));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 25, 70, 0, 0));
					break;
				case 9:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 60, 0, 0));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 15, 70, 0, 0));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 25, 80, 0, 0));
					break;
				case 10:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 80, 0, 0));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 15, 90, 0, 0));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 25, 100, 1, 0));
					break;

				default:

					break;
				}
			}
		}
	}

	public void updateWaves2() {

		int totalwaves = 10;
		if (currentEnemyWaves.size == 0) {
			currentwave++;
			if (currentwave > totalwaves) {
				LevelVictory();
			}
			else {
				wavetext="WAVE "+currentwave;
				if(currentwave==totalwaves){
					wavetext="FINAL WAVE";
					
				}
				timeforwavetext=2;
				scoreBoard.setWaveNumber(currentwave);
				System.out.println("Starte Wave" + currentwave);
				switch (currentwave) {
				case 1:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 30, 1f, 3, 1f, 0, 0));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 50, 10, 0.5f, 4, 1f, 0, 0));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 65, 10, 0.2f, 5, 1f, 0, 0));
					break;
				case 2:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 10, 0.2f, 2, 1f, 0, 0.0f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 15, 10, 0.2f, 2, 1f, 0, 0.0f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 25, 10, 0.2f, 2, 1f, 0, 0.0f));
					
					break;
				case 3:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 50, 1f, 0, 1f, 0, 0.0f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 55, 0, 0.0f, 8, 1f, 0, 0.0f));
					break;
				case 4:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 10, 0.1f, 0, 1f, 0, 0.0f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 10, 0, 0.0f, 2, 1f, 0, 0.0f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 15, 50, 1f, 0, 1f, 0, 0.0f));
					break;
				case 5:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 10, 0.1f, 0, 1f, 0, 0.0f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 10, 0, 0.0f, 4, 1f, 0, 0.0f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 15, 50, 1f, 0, 1f, 0, 0.0f));
					break;
				case 6:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 150, 0, 0));
					break;
				case 7:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 0, 15, 0));
					break;
				case 8:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 100, 1f, 0, 1f, 0, 0.0f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 0, 0.0f, 10, 3f, 0, 0.0f));
					break;
				case 9:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 200, 0.5f, 0, 1f, 0, 0.0f));
					break;
				case 10:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 20, 0.5f, 0, 1f, 0, 0.0f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 15, 00, 0.5f, 20, 1f, 0, 0.0f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 40, 20, 0.5f, 0, 1f, 0, 0.0f));
					break;

				default:

					break;
				}
			}
		}
	}
	
	
	public void updateWaves3() {

		int totalwaves = 10;
		if (currentEnemyWaves.size == 0) {
			currentwave++;
			if (currentwave > totalwaves) {
				GameVictory();
			}
			else {
				wavetext="WAVE "+currentwave;
				if(currentwave==totalwaves){
					wavetext="FINAL WAVE";
					
				}
				timeforwavetext=2;
				scoreBoard.setWaveNumber(currentwave);
				System.out.println("Starte Wave" + currentwave);
				switch (currentwave) {
				case 1:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 00, 0f, 0, 1f, 5, 0.2f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 20, 00, 0f, 0, 1f, 10, 0.2f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 30, 00, 0f, 0, 1f, 10, 0.2f));
						break;
				case 2:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 10, 0.2f, 2, 1f, 5, 0.0f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 15, 10, 0.2f, 2, 1f, 10, 0.0f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 25, 10, 0.2f, 2, 1f, 0, 0.0f));
					
					break;
				case 3:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 50, 1f, 0, 1f, 0, 0.0f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 55, 0, 0.0f, 8, 1f, 0, 0.0f));
					break;
				case 4:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 10, 0.1f, 0, 1f, 0, 0.0f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 10, 0, 0.0f, 2, 1f, 0, 0.0f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 15, 50, 1f, 0, 1f, 0, 0.0f));
					break;
				case 5:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 10, 0.1f, 0, 1f, 0, 0.0f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 10, 0, 0.0f, 4, 1f, 0, 0.0f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 15, 50, 1f, 0, 1f, 0, 0.0f));
					break;
				case 6:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 150, 0, 0));
					break;
				case 7:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 0, 15, 0));
					break;
				case 8:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 100, 1f, 0, 1f, 0, 0.0f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 0, 0.0f, 10, 3f, 0, 0.0f));
					break;
				case 9:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 200, 0.5f, 0, 1f, 0, 0.0f));
					break;
				case 10:
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 5, 20, 0.5f, 0, 1f, 0, 0.0f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 15, 00, 0.5f, 20, 1f, 0, 0.0f));
					currentEnemyWaves.addAll(EnemyWaveEntry.createEnemyEntries(map.getSpawn(),
							(int) scoreBoard.getTime() + 40, 20, 0.5f, 0, 1f, 0, 0.0f));
					break;

				default:

					break;
				}
			}
		}
	}

	public void startNewLevel() {

	}

	public void LevelVictory() {
		wavetext="LEVEL CLEAR!";
		timeforwavetext=2f;
		currentwave=0;
		Timer.schedule(new Timer.Task() {
			
			@Override
			public void run() {
				MainGame.level++;
				loadLevel(MainGame.level);;
			}
		},3f);
	}
	
	public void GameVictory() {
		
	}

}
