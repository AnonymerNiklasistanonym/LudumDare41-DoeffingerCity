package com.mygdx.game.gamestate.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.Car;
import com.mygdx.game.CollisionCallbackInterface;
import com.mygdx.game.CollisionListener;
import com.mygdx.game.Enemy;
import com.mygdx.game.Enemy_small;
import com.mygdx.game.MainGame;
import com.mygdx.game.MainMap;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.objects.Checkpoint;
import com.mygdx.game.objects.Tower;
import com.mygdx.game.objects.checkpoints.NormalCheckpoint;
import com.mygdx.game.objects.tower.EmptyTower;
import com.mygdx.game.objects.tower.MGTower;

public class PlayState extends GameState implements CollisionCallbackInterface {

	CollisionListener collis;
	private Sprite smaincar;
	private Sprite steststrecke;
	private Sprite srangecircle;
	private World world;
	private Car car;
	private Array<Enemy> enemies;
	private Array<Tower> towers;
	private boolean debugBox2D;

	private Sound soundmgshoot;

	private MainMap map;
	private Sprite pitStop;

	public static boolean soundon = false;

	private int money = 100;
	private int moneyperlap = 100;
	private float timeperlap = 0;

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

	public PlayState(GameStateManager gameStateManager) {
		super(gameStateManager);

		// import textures
		steststrecke = createScaledSprite("maps/test.png");
		smaincar = createScaledSprite("cars/car_standard.png");
		srangecircle = createScaledSprite("tower/range.png");

		// set STATIC textures
		NormalCheckpoint.normalCheckPointActivated = new Texture(
				Gdx.files.internal("checkpoints/checkpoint_normal_activated.png"));
		NormalCheckpoint.normalCheckPointDisabled = new Texture(
				Gdx.files.internal("checkpoints/checkpoint_normal_disabled.png"));
		MGTower.groundTower = new Texture(Gdx.files.internal("tower/tower_empty.png"));
		MGTower.upperTower = new Texture(Gdx.files.internal("tower/tower_empty_upper.png"));
		MGTower.towerFiring = new Texture(Gdx.files.internal("tower/tower_mg_firing.png"));
		Enemy_small.normalTexture = new Texture(Gdx.files.internal("zombies/zombie_standard.png"));
		Enemy_small.deadTexture = new Texture(Gdx.files.internal("zombies/zombie_standard_tot.png"));

		enemies = new Array<Enemy>();

		towers = new Array<Tower>();

		collis = new CollisionListener(this);

		soundmgshoot = Gdx.audio.newSound(Gdx.files.internal("sounds/mgturret.wav"));

		// Sets this camera to an orthographic projection, centered at (viewportWidth/2,
		// viewportHeight/2), with the y-axis pointing up or down.
		camera.setToOrtho(false, MainGame.GAME_WIDTH * PIXEL_TO_METER, MainGame.GAME_HEIGHT * PIXEL_TO_METER);

		debugBox2D = false;

		world = new World(new Vector2(0, 0), true);

		world.setContactListener(collis);
		debugRender = new Box2DDebugRenderer();

		map = new MainMap("test", world, RESOLUTION_WIDTH, PIXEL_TO_METER);
		car = new Car(world, smaincar, 600, 600);

		for (int i = 0; i < 20; i++) {
			Enemy e = new Enemy_small(world, map);

			e.startMove();
			enemies.add(e);
		}

		// create example checkpoints
		checkpoints = new Checkpoint[4];
		float[][] checkPointPosition = { { 300, 230 }, { 320, 600 }, { 850, 600 }, { 850, 230 } };
		for (int i = 0; i < checkpoints.length; i++) {
			checkpoints[i] = new NormalCheckpoint(world, checkPointPosition[i][0] * PIXEL_TO_METER,
					checkPointPosition[i][1] * PIXEL_TO_METER);
		}

		// create example towers

		MGTower t = new MGTower(850 * PIXEL_TO_METER, 350 * PIXEL_TO_METER, enemies, soundmgshoot);
		towers.add(t);

		// create example pit stop
		pitStop = new Sprite(new Texture(Gdx.files.internal("pit_stop/pit_stop_01.png")));
		pitStop.setPosition(100, 100);

		System.out.println("Play state entered");

	}

	public static Sprite createScaledSprite(String location) {
		Texture t = new Texture(Gdx.files.internal(location));
		Sprite s = new Sprite(t);
		s.setSize(s.getWidth() * PIXEL_TO_METER, s.getHeight() * PIXEL_TO_METER);
		s.setOriginCenter();
		// t.dispose();
		return s;
	}

	@Override
	protected void handleInput() {

		// Check if somehow the screen was touched
		if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			System.out.println("Do something");

			// turn checkpoint on
			checkpoints[0].setActivated(!checkpoints[0].getActivated());
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

	}

	@Override
	protected void update(float deltaTime) {

		timeperlap = timeperlap + deltaTime;

		handleInput();
		car.update(deltaTime);

		for (Tower t : towers) {
			t.update(deltaTime);
		}

		camera.update();

	}

	@Override
	public void render(SpriteBatch spriteBatch) {

		// set projection matrix
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		steststrecke.draw(spriteBatch);

		// draw checkpoints
		for (Checkpoint checkpoint : checkpoints)
			checkpoint.draw(spriteBatch);

		// draw tower
		pitStop.draw(spriteBatch);
		for (Tower tower : towers) {
			srangecircle.setSize(tower.getRange() * 2, tower.getRange() * 2);
			srangecircle.setOriginCenter();
			srangecircle.setOriginBasedPosition(tower.getX() + tower.getSpriteBody().getWidth() / 2,
					tower.getY() + tower.getSpriteBody().getHeight() / 2);
			// srangecircle.draw(spriteBatch);
			tower.draw(spriteBatch);
		}

		// draw pitstop
		pitStop.draw(spriteBatch);

		// draw enemies
		for (Enemy e : enemies) {
			e.update(Gdx.graphics.getDeltaTime());
			e.draw(spriteBatch);
		}

		// draw car
		car.draw(spriteBatch);

		// MainGame.font.draw(spriteBatch, "Hi", MainGame.GAME_WIDTH *
		// PlayState.PIXEL_TO_METER / 2,
		// MainGame.GAME_HEIGHT * PlayState.PIXEL_TO_METER / 2);

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
			if (enemy.tot) {
				enemy.body.setActive(false);

			}
		}
	}

	@Override
	protected void dispose() {
		// dispose STATIC textures
		NormalCheckpoint.normalCheckPointActivated.dispose();
		NormalCheckpoint.normalCheckPointDisabled.dispose();
		EmptyTower.groundTower.dispose();
		EmptyTower.upperTower.dispose();
		Enemy_small.normalTexture.dispose();
		Enemy_small.deadTexture.dispose();
	}

	@Override
	public void collisionCarEnemy(Car car, Enemy enemy) {
		enemy.takeDamage(20);
		car.hitEnemy(enemy);
		// TODO Auto-generated method stub

	}

	@Override
	public void collisionCarCheckpoint(Car car, Checkpoint checkpoint) {
		// TODO Auto-generated method stub
		checkpoint.setActivated(true);

		// check if all checkpoints are activated
		boolean allCheckpointsactivated = true;
		for (Checkpoint checkpoint1 : checkpoints) {
			if (checkpoint1.getActivated() == false) {
				allCheckpointsactivated = false;
				break;
			}
		}
		// if they are schedule in 2 seconds to deactivate them all
		if (allCheckpointsactivated) {
			lapFinished();
		}

	}

	public void lapFinished() {

		for (Checkpoint checkpoint1 : checkpoints) {
			checkpoint1.setActivated(false);
		}
		money = money + moneyperlap;
		int timebonus = 100 - (int) timeperlap * 2;
		money = money + timebonus;
		System.out.println("Lap Finished, new Money: " + money);
	}

	@Override
	public void collisionCarTower(Car car, Tower tower) {
		// TODO Auto-generated method stub

	}

}
