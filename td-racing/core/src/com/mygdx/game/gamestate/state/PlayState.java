package com.mygdx.game.gamestate.state;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.Car;
import com.mygdx.game.CollisionCallbackInterface;
import com.mygdx.game.CollisionListener;
import com.mygdx.game.Enemy;
import com.mygdx.game.EnemyWave;
import com.mygdx.game.Enemy_bicycle;
import com.mygdx.game.Enemy_fat;
import com.mygdx.game.Enemy_small;
import com.mygdx.game.MainGame;
import com.mygdx.game.MainMap;
import com.mygdx.game.Node;
import com.mygdx.game.ScoreBoard;
import com.mygdx.game.TurmMenu;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.objects.Checkpoint;
import com.mygdx.game.objects.FinishLine;
import com.mygdx.game.objects.Tower;
import com.mygdx.game.objects.checkpoints.NormalCheckpoint;
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

	private MainMap map;
	private Sprite pitStop;
	
	private ScoreBoard scoreBoard;
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
	
	public Array<EnemyWave> currentEnemyWaves;

	public PlayState(GameStateManager gameStateManager) {
		super(gameStateManager);
	
		scoreBoard = new ScoreBoard();
		scoreBoard.reset(500);
		
		// import textures
		strack1 = createScaledSprite("maps/track1.png");
		strack1top = createScaledSprite("maps/track1top.png");
		smaincar = createScaledSprite("cars/car_standard.png");
		sfinishline = createScaledSprite("maps/finishline.png");
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
		
		Enemy_small.normalTexture = new Texture(Gdx.files.internal("zombies/zombie_standard.png"));
		Enemy_small.deadTexture = new Texture(Gdx.files.internal("zombies/zombie_standard_dead.png"));
		Enemy_small.damageTexture = new Texture(Gdx.files.internal("zombies/zombie_blood.png"));
		
		Enemy_fat.normalTexture = new Texture(Gdx.files.internal("zombies/zombie_fat.png"));
		Enemy_fat.deadTexture = new Texture(Gdx.files.internal("zombies/zombie_fat_dead.png"));
		Enemy_fat.damageTexture = new Texture(Gdx.files.internal("zombies/zombie_blood.png"));
		
		Enemy_bicycle.normalTexture = new Texture(Gdx.files.internal("zombies/zombie_bicycle.png"));
		Enemy_bicycle.deadTexture = new Texture(Gdx.files.internal("zombies/zombie_bicycle_dead.png"));
		Enemy_bicycle.damageTexture = new Texture(Gdx.files.internal("zombies/zombie_blood.png"));

		
		
		enemies = new Array<Enemy>();

		towers = new Array<Tower>();

		collis = new CollisionListener(this);
		Sprite s1=createScaledSprite("buttons/cannonbutton.png");
		Sprite s2=createScaledSprite("buttons/laserbutton.png");
		Sprite s3=createScaledSprite("buttons/cannonbutton.png");
		Sprite s4=createScaledSprite("buttons/cannonbutton.png");
		Sprite s5=createScaledSprite("buttons/cannonbutton.png");

		
		
		
		// Sets this camera to an orthographic projection, centered at (viewportWidth/2,
		// viewportHeight/2), with the y-axis pointing up or down.
		camera.setToOrtho(false, MainGame.GAME_WIDTH * PIXEL_TO_METER, MainGame.GAME_HEIGHT * PIXEL_TO_METER);

		debugBox2D = false;
		debugCollision = false;
		debugWay = false;
		debugEntfernung = false;

		world = new World(new Vector2(0, 0), true);

		world.setContactListener(collis);
		debugRender = new Box2DDebugRenderer();

		car = new Car(world, smaincar, 440, 220);

		finishline = new FinishLine(world, sfinishline, 380, 220);

		map = new MainMap("test", world,finishline.body );

		for (int i = 0; i < 2; i++) {
			Enemy e = new Enemy_small(220,20,world, map);
			Enemy f= new Enemy_fat(220,25,world,map);
			Enemy b= new Enemy_bicycle(220,20,world,map);
			f.startMove();
			e.startMove();
			b.startMove();
			enemies.add(e);
			enemies.add(f);
			enemies.add(b);
//			Enemy e = new Enemy_small(220,20,world, map);
//			Enemy f= new Enemy_fat(220,20,world,map);
//			Enemy b= new Enemy_bicycle(220,20,world,map);
//			f.startMove();
//			e.startMove();
//			b.startMove();
//			enemies.add(e);
//			enemies.add(f);
//			enemies.add(b);
			turmmenu=new TurmMenu(s1, s2, s3, s4, s5,world,enemies);
		}

		// create example checkpoints
		checkpoints = new Checkpoint[4];
		float[][] checkPointPosition = { { 300, 230 }, { 320, 600 }, { 850, 600 }, { 850, 230 } };
		for (int i = 0; i < checkpoints.length; i++) {
			checkpoints[i] = new NormalCheckpoint(world, checkPointPosition[i][0] * PIXEL_TO_METER,
					checkPointPosition[i][1] * PIXEL_TO_METER);
		}

		// create example towers

		Tower t = new MGTower(850 * PIXEL_TO_METER, 350 * PIXEL_TO_METER, enemies, world);
		t.activate();
		towers.add(t);
		t=new LaserTower(550*PIXEL_TO_METER,350*PIXEL_TO_METER,enemies,world);
		t.activate();
		towers.add(t);
		// create example pit stop
		pitStop = new Sprite(new Texture(Gdx.files.internal("pit_stop/pit_stop_01.png")));
		pitStop.setPosition(100, 100);
		System.out.println("Play state entered");
		//startBuilding(new MGTower(Gdx.input.getX(), Gdx.input.getY(), enemies, world));

		
		currentEnemyWaves = map.getEnemyWaves();
	}

	public static Sprite createScaledSprite(String location) {
		Texture t = new Texture(Gdx.files.internal(location));
		Sprite s = new Sprite(t);
		s.setSize(s.getWidth() * PIXEL_TO_METER, s.getHeight() * PIXEL_TO_METER);
		s.setOriginCenter();
		// t.dispose();
		return s;
	}

	public void startBuilding(Tower t) {
		System.out.println("Start building");
		buildingtower = t;
		buildingtower.setBuildingMode(true);
		for (final Tower tower : towers) {
			tower.activateRange(true);
			System.out.println(tower.getCost());
		}
	}
	
	public boolean buildingPositionIsAllowed(final Tower tower) {
		System.out.println("buildingPositionIsAllowed");
		final float[][] cornerPoints = tower.getCornerPoints();
		boolean isAllowed = true;
		for (int i = 0; i < cornerPoints.length; i++) {
			isAllowed = this.map.isInBody(cornerPoints[i][0], cornerPoints[i][1]);
			System.out.println("isAllowed: " + isAllowed + ", x: " + cornerPoints[i][0] + ", y: " + cornerPoints[i][1]);
		}
		System.out.println("isAllowed: " + isAllowed);
		return isAllowed;
	}
	
	public boolean buildingMoneyIsEnough(final Tower tower) {
		System.out.println("buildingMoneyIsEnough");
		// if there is enough money return true
		if(tower.getCost() <= this.scoreBoard.getMoney()) {
				System.out.println("cost ok");
			 return true;
		} else {
			System.out.println("cost not ok");
			return false;
		}
	}

	public void stopBuilding() {
		turmmenu.unselectAll();
		for (final Tower tower : towers) {
			tower.activateRange(false);
		}
	}

	@Override
	protected void handleInput() {

		if (Gdx.input.isCatchBackKey() || Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			gameStateManager.setGameState(new MenuState(gameStateManager));
		}

		// Check if somehow the screen was touched
		if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			System.out.println("Do something");

			// turn checkpoint on
			checkpoints[0].setActivated(!checkpoints[0].isActivated());
		}
		if (Gdx.input.isKeyPressed(Keys.W)) {
			car.accelarate();
		}
		if (Gdx.input.isKeyPressed(Keys.S)) {
			car.brake();
		}
		if (Gdx.input.isKeyPressed(Keys.A)) {
			car.steerLeft();
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			car.steerRight();
		}
	

		if (Gdx.input.isKeyJustPressed(Keys.U)) {
			if (soundon)
				soundon = false;
			else
				soundon = true;
		}

		if (Gdx.input.isKeyJustPressed(Keys.I)) {
			if (debugBox2D)
				debugBox2D = false;
			else
				debugBox2D = true;
		}
		if (Gdx.input.isKeyJustPressed(Keys.K)) {
			if (debugCollision)
				debugCollision = false;
			else
				debugCollision = true;
		}
		if (Gdx.input.isKeyJustPressed(Keys.L)) {
			if (debugWay)
				debugWay = false;
			else
				debugWay = true;
		}
		if (Gdx.input.isKeyJustPressed(Keys.J)) {
			if (debugEntfernung)
				debugEntfernung = false;
			else
				debugEntfernung = true;
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_1)) {
			turmmenu.selectTower(1);
		}
		if (Gdx.input.isKeyJustPressed(Keys.NUM_2)) {
			turmmenu.selectTower(2);
		}
		if (Gdx.input.isKeyJustPressed(Keys.NUM_3)) {
			turmmenu.selectTower(3);
		}
		if (Gdx.input.isKeyJustPressed(Keys.NUM_4)) {
			turmmenu.selectTower(4);
		}
		if (Gdx.input.isKeyJustPressed(Keys.NUM_5)) {
			turmmenu.selectTower(5);
		}
		
		// B >> Activate building mode

		// Screen clicked >> Build tower
		if(Gdx.input.justTouched()) {
			// if in build mode build tower at the current mouse positin if allowed
			if (this.buildingtower != null)
				buildTowerIfAllowed();
		}

	}
	
	public void buildTowerIfAllowed() {
		// if position and money is ok build it
		if (buildingMoneyIsEnough(this.buildingtower) && buildingPositionIsAllowed(this.buildingtower)) {
			// Add tower to the tower list		
			turmmenu.unselectAll();
			this.scoreBoard.addMoney(-this.buildingtower.getCost());
			final Tower newTower = this.buildingtower;
			newTower.activate();
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
		laptime=laptime+deltaTime;
		handleInput();
		car.update(deltaTime);

		// get mouse position
		mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(mousePos);		
		buildingtower=turmmenu.getCurrentTower();
		
		// update towers
		if (buildingtower != null) {
			startBuilding(buildingtower);
			buildingtower.update(deltaTime, mousePos);
		}
		else
		{
			stopBuilding();
		}
		for (final Tower t : towers)
			t.update(deltaTime, mousePos);
		
		// update score board time information
		scoreBoard.update(deltaTime);
		
		// update enemy waves
//		for (int i = 0; i < currentEnemyWaves.size; i++) {
//			if (currentEnemyWaves.get(i).getTime() <= scoreBoard.getTime()) {
//				enemies.addAll(currentEnemyWaves.get(i).getEnemies());
//				currentEnemyWaves.removeIndex(i);
//			}
//		}

		// update camera
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
		
		for (final Tower tower : towers)
			tower.drawUpperBuddy(spriteBatch);

		if (debugCollision) {
			Node[][] test = this.map.getNodesList();
			MainGame.font.getData().setScale(0.1f);
			for (int i = 0; i < this.RESOLUTION_WIDTH; i = i + 10) {
				for (int j = 0; j < this.RESOLUTION_HEIGHT; j = j + 10) {
					if (test[i][j].getNoUse()) {
						MainGame.font.draw(spriteBatch, "O", i * PlayState.PIXEL_TO_METER,
								j * PlayState.PIXEL_TO_METER);

					} else {
						MainGame.font.draw(spriteBatch, "T", i * PlayState.PIXEL_TO_METER,
								j * PlayState.PIXEL_TO_METER);
					}
				}
			}
		}
		
		if (debugEntfernung) {
			Node[][] test = this.map.getNodesList();
			MainGame.font.getData().setScale(0.01f);
			for (int i = 0; i < this.RESOLUTION_WIDTH; i = i + 10) {
				for (int j = 0; j < this.RESOLUTION_HEIGHT; j = j + 10) {
						MainGame.font.draw(spriteBatch, test[i][j].getH()+"", i * PlayState.PIXEL_TO_METER,
								j * PlayState.PIXEL_TO_METER);

				}
			}
		}

		if (debugWay) {
			MainGame.font.getData().setScale(0.02f);
			for (Enemy e : enemies) {

				e.findWay();
				LinkedList<Node> weg;
				weg = e.getWeg();
				for (Node node : weg) {
					MainGame.font.draw(spriteBatch, "x", node.getX() * PlayState.PIXEL_TO_METER,
							node.getY() * PlayState.PIXEL_TO_METER);
				}
			}

		}

		// draw car
		car.draw(spriteBatch);
		
		scoreBoard.draw(spriteBatch);
		
		turmmenu.draw(spriteBatch);
		
		spriteBatch.end();

		if (debugBox2D) {
			debugRender.render(world, camera.combined);
		}

		updatePhysics(Gdx.graphics.getDeltaTime());

	}

	public void updatePhysics(float deltaTime) {
		float frameTime = Math.min(deltaTime, 0.25f);
		physicsaccumulator += frameTime;
		while (physicsaccumulator >= TIME_STEP) {
			world.step(TIME_STEP, 6, 2);
			physicsaccumulator -= TIME_STEP;

		}

		for (Enemy enemy : enemies) {
			if (enemy.justDied) {
				enemy.body.setActive(false);
				enemy.justDied = false;
				scoreBoard.killedEnemy(enemy.getScore(), enemy.getMoney());
			}
		}
	}

	@Override
	protected void dispose() {
		// dispose STATIC textures
		NormalCheckpoint.normalCheckPointActivated.dispose();
		NormalCheckpoint.normalCheckPointDisabled.dispose();
		MGTower.groundTower.dispose();
		MGTower.upperTower.dispose();
		MGTower.towerFiring.dispose();
		LaserTower.groundTower.dispose();
		LaserTower.upperTower.dispose();
		LaserTower.towerFiring.dispose();
		Tower.circleTexture.dispose();
		Enemy_small.normalTexture.dispose();
		Enemy_small.deadTexture.dispose();
	}

	@Override
	public void collisionCarEnemy(Car car, Enemy enemy) {

		car.hitEnemy(enemy);
		// TODO Auto-generated method stub

	}

	@Override
	public void collisionCarCheckpoint(Car car, Checkpoint checkpoint) {
		// TODO Auto-generated method stub
		checkpoint.setActivated(true);
	}

	public void lapFinished() {

		boolean allCheckpointsChecked = true;

		for (final Checkpoint checkpoint : this.checkpoints) {
			if (checkpoint.isActivated() == false) {
				allCheckpointsChecked = false;
			}
			checkpoint.setActivated(false);
		}
		if(allCheckpointsChecked) {
			final int fastBonus = (100-(int)laptime*2);
			scoreBoard.newLap((fastBonus > 0) ? moneyPerLap + fastBonus : moneyPerLap);
		}
		
	}

	@Override
	public void collisionCarTower(Car car, Tower tower) {
		// TODO Auto-generated method stub

	}

	@Override
	public void collisionCarFinishLine(Car car, FinishLine finishLine) {
		lapFinished();
	}

}
