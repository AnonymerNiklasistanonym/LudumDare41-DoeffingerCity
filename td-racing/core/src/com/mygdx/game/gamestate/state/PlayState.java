package com.mygdx.game.gamestate.state;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.Car;
import com.mygdx.game.CollisionCallbackInterface;
import com.mygdx.game.CollisionListener;
import com.mygdx.game.Enemy;
import com.mygdx.game.EnemyWaveEntry;
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
	private Sprite strack1top;
	private World world;
	private Car car;
	private FinishLine finishline;
	private Array<Enemy> enemies;
	private Array<Tower> towers;

	private boolean debugBox2D;
	private boolean debugCollision;
	private boolean debugEntfernung;

	public static boolean soundon = false;
	private boolean debugWay;
	
	private TurmMenu turmmenu;
	
	private PreferencesManager preferencesManager;

	private MainMap map;
	private Sprite pitStop;
	
	public static ScoreBoard scoreBoard;
	private Tower buildingtower;

	private int moneyPerLap = 100;

	private float laptime=0f;
	
	/**
	 * Time since last physic Steps
	 */

	private float physicsaccumulator = 0f;
	private Box2DDebugRenderer debugRender;

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

	public PlayState(GameStateManager gameStateManager) {
		super(gameStateManager);
	
		scoreBoard = new ScoreBoard(this);
		scoreBoard.reset(500);
		
		preferencesManager = new PreferencesManager();
		preferencesManager.checkHighscore();
		
		// import textures
		strack1 = createScaledSprite("maps/track1.png");
		strack1top = createScaledSprite("maps/track1top.png");
		smaincar = createScaledSprite("cars/car_standard.png");
		sfinishline = createScaledSprite("maps/finishline.png");
		
		final Sprite s1=createScaledSprite("buttons/cannonbutton.png");
		final Sprite s2=createScaledSprite("buttons/laserbutton.png");
		final Sprite s3=createScaledSprite("buttons/flamebutton.png");
		final Sprite s4=createScaledSprite("buttons/cannonbutton.png");
		final Sprite s5=createScaledSprite("buttons/cannonbutton.png");
		
		// set STATIC textures
		NormalCheckpoint.normalCheckPointActivated = new Texture(
				Gdx.files.internal("checkpoints/checkpoint_normal_activated.png"));
		NormalCheckpoint.normalCheckPointDisabled = new Texture(
				Gdx.files.internal("checkpoints/checkpoint_normal_disabled.png"));
		
		Tower.circleTexture = new Texture(Gdx.files.internal("tower/range.png"));
		
		MGTower.groundTower = new Texture(Gdx.files.internal("tower/tower_empty.png"));
		MGTower.upperTower = new Texture(Gdx.files.internal("tower/tower_empty_upper.png"));
		MGTower.towerFiring = new Texture(Gdx.files.internal("tower/tower_mg_firing.png"));
		MGTower.soundShoot=Gdx.audio.newSound(Gdx.files.internal("sounds/mgturret.wav"));
		
		LaserTower.groundTower = new Texture(Gdx.files.internal("tower/tower_laser_bottom.png"));
		LaserTower.upperTower = new Texture(Gdx.files.internal("tower/tower_laser_upper.png"));
		LaserTower.towerFiring = new Texture(Gdx.files.internal("tower/tower_laser_firing.png"));
		LaserTower.soundShoot=Gdx.audio.newSound(Gdx.files.internal("sounds/mgturret.wav"));
		
		FireTower.groundTower = new Texture(Gdx.files.internal("tower/tower_fire_bottom.png"));
		FireTower.upperTower = new Texture(Gdx.files.internal("tower/tower_fire_upper.png"));
		FireTower.towerFiring = new Texture(Gdx.files.internal("tower/tower_fire_firing.png"));
		FireTower.tflame = new Texture(Gdx.files.internal("tower/flame.png"));
		FireTower.soundShoot=Gdx.audio.newSound(Gdx.files.internal("sounds/mgturret.wav"));
		
		Enemy_small.normalTexture = new Texture(Gdx.files.internal("zombies/zombie_standard.png"));
		Enemy_small.deadTexture = new Texture(Gdx.files.internal("zombies/zombie_standard_dead.png"));
		Enemy_small.damageTexture = new Texture(Gdx.files.internal("zombies/zombie_blood.png"));
		
		Enemy_fat.normalTexture = new Texture(Gdx.files.internal("zombies/zombie_fat.png"));
		Enemy_fat.deadTexture = new Texture(Gdx.files.internal("zombies/zombie_fat_dead.png"));
		Enemy_fat.damageTexture = new Texture(Gdx.files.internal("zombies/zombie_blood.png"));
		
		Enemy_bicycle.normalTexture = new Texture(Gdx.files.internal("zombies/zombie_bicycle.png"));
		Enemy_bicycle.deadTexture = new Texture(Gdx.files.internal("zombies/zombie_bicycle_dead.png"));
		Enemy_bicycle.damageTexture = new Texture(Gdx.files.internal("zombies/zombie_blood.png"));
		
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

		map = new MainMap("test", world,finishline.body);
		turmmenu = new TurmMenu(s1, s2, s3, s4, s5, world, enemies);
	
		checkpoints = new Checkpoint[4];
		float[][] checkPointPosition = { { 300, 230 }, { 320, 600 }, { 850, 600 }, { 850, 230 } };
		for (int i = 0; i < checkpoints.length; i++)
			checkpoints[i] = new NormalCheckpoint(world, checkPointPosition[i][0] * PIXEL_TO_METER,
					checkPointPosition[i][1] * PIXEL_TO_METER);

		Tower t = new MGTower(850 * PIXEL_TO_METER, 350 * PIXEL_TO_METER, enemies, world);
		t.activate();
		towers.add(t);
		t=new LaserTower(550*PIXEL_TO_METER,350*PIXEL_TO_METER,enemies,world);
		t.activate();
		towers.add(t);
		
		pitStop = new Sprite(new Texture(Gdx.files.internal("pit_stop/pit_stop_01.png")));
		pitStop.setPosition(100, 100);
		
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
		if (Gdx.input.isKeyJustPressed(Keys.U))
			soundon = !soundon;
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
		if(Gdx.input.justTouched() && this.buildingtower != null)
				buildTowerIfAllowed();
	}
	
	public void buildTowerIfAllowed() {
		// if position and money is ok build it
		if (buildingMoneyIsEnough(this.buildingtower) && buildingPositionIsAllowed(this.buildingtower)) {
			// Add tower to the tower list		
			turmmenu.unselectAll();
			scoreBoard.addMoney(-this.buildingtower.getCost());
			final Tower newTower = this.buildingtower;
			buildingtower=null;
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
			
		if(buildingtower==null){
			buildingtower=turmmenu.getCurrentTower();
			if(buildingtower!=null)
			startBuilding(buildingtower);
		}

		if (buildingtower != null) {
			buildingtower.update(deltaTime, mousePos);
			buildingtower = turmmenu.getCurrentTower();
		}
		for (final Tower t : towers)
			t.update(deltaTime, mousePos);
		
		for (final EnemyWaveEntry entry : currentEnemyWaves) {
			if (entry.getTimeInSeconds() < scoreBoard.getTime()) {
				System.out.println("entry.getTimeInSeconds()" + entry.getTimeInSeconds() + "> scoreBoard.getTime()" + scoreBoard.getTime());
				enemies.addAll(EnemyWaveEntry.createEnemy(entry, world, map));
				currentEnemyWaves.removeValue(entry, true);
			}
		}
		
		
		scoreBoard.update(deltaTime);
		
		camera.update();
	}

	@Override
	public void render(SpriteBatch spriteBatch) {

		// set projection matrix
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		strack1.draw(spriteBatch);
		finishline.draw(spriteBatch);
		strack1top.draw(spriteBatch);
		// draw checkpoints
		if(debugBox2D)
		for (final Checkpoint checkpoint : checkpoints)
			checkpoint.draw(spriteBatch);
		// draw tower
		pitStop.draw(spriteBatch);
		if (buildingtower != null) buildingtower.draw(spriteBatch);
		for (final Tower tower : towers)
			tower.draw(spriteBatch);
		// draw pitstop
		pitStop.draw(spriteBatch);
		// draw enemies
		for (Enemy e : enemies) {
			e.update(Gdx.graphics.getDeltaTime());
			e.draw(spriteBatch);
		}
		
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
						MainGame.font.draw(spriteBatch, test[i][j].getH()+"", i * PlayState.PIXEL_TO_METER,
								j * PlayState.PIXEL_TO_METER);
			}
		}

		if (debugWay) {
			MainGame.font.getData().setScale(0.02f);
			for (Enemy e : enemies) {
//				e.findWay();
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
		
		spriteBatch.end();

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
		Array<Enemy> toremove=new Array<Enemy>();
		for (final Enemy enemy : enemies) {
			if (enemy.justDied) {
				enemy.body.setActive(false);
				enemy.justDied = false;
				scoreBoard.killedEnemy(enemy.getScore(), enemy.getMoney());
			}
			if(enemy.delete) {
				toremove.add(enemy);
				world.destroyBody(enemy.body);
			}	
		}
		for (Enemy e : toremove) {
			enemies.removeValue(e, true);
		}
		
		for (Tower t : towers) {
			Array<Body> ab=new Array<Body>();
			Array<Body> rb=new Array<Body>();
			rb=t.removeProjectiles();
			if(rb!=null)
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
	}

	@Override
	public void collisionCarEnemy(Car car, Enemy enemy) {
		car.hitEnemy(enemy);
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
		if(allCheckpointsChecked) {
			final int fastBonus = (100-(int)laptime*2);
			scoreBoard.newLap((fastBonus > 0) ? moneyPerLap + fastBonus : moneyPerLap);
		}
		
	}

	@Override
	public void collisionCarFinishLine(Car car, FinishLine finishLine) {
		lapFinished();
	}

	public void playIsDeadCallback() {
		Gdx.input.getTextInput(new TextInputListener() {
			public void input(String text) {
				preferencesManager.saveHighscore(text.trim(), scoreBoard.getScore());	
				gameStateManager.setGameState(new HighscoreState(gameStateManager));
			}
			public void canceled() {
				gameStateManager.setGameState(new HighscoreState(gameStateManager));
			}
		}, "Enter your name", "", "");
	}

	@Override
	public void collisionFlameEnemy(Enemy e, Flame f) {
		// TODO Auto-generated method stub
		
	}

}
