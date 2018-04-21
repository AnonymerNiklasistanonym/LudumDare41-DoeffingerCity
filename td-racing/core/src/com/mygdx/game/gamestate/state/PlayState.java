package com.mygdx.game.gamestate.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
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

public class PlayState extends GameState implements CollisionCallbackInterface {

	CollisionListener collis;
	private Sprite smaincar;
	private Sprite szombie1;
	private Sprite steststrecke;
	private Sprite szombie1dead;
	private World world;
	private Car car;
	private Array<Enemy> enemies;
	private Array<Tower> towers;
	private boolean debugBox2D;

	private MainMap map;
	private Sprite pitStop;

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
	
	
	private Checkpoint[] checkpoints;

//Zur identifizierung von Collisions Entitys
	public final static short PLAYER_BOX = 0x1;    // 0001
	public final static short ENEMY_BOX = 0x1 << 1; // 0010 or 0x2 in hex
	
	public PlayState(GameStateManager gameStateManager) {
		super(gameStateManager);

		enemies=new Array<Enemy>();

		towers=new Array<Tower>();


		collis=new CollisionListener(this);

		steststrecke=createScaledSprite("maps/test.png");
		smaincar=createScaledSprite("cars/car_standard.png");
		szombie1=createScaledSprite("zombies/zombie_standard.png");
		szombie1dead=createScaledSprite("zombies/zombie_standard_tot.png");
		
		// Sets this camera to an orthographic projection, centered at (viewportWidth/2,
		// viewportHeight/2), with the y-axis pointing up or down.
		camera.setToOrtho(false, MainGame.GAME_WIDTH * PIXEL_TO_METER, MainGame.GAME_HEIGHT * PIXEL_TO_METER);

		debugBox2D = true;

		world = new World(new Vector2(0, 0), true);
			
		world.setContactListener(collis);
		debugRender = new Box2DDebugRenderer();
		
		car = new Car(world,smaincar);
		for (int i = 0; i < 50; i++) {
			Enemy e = new Enemy_small(world,szombie1,szombie1dead);
			e.startMove();
			enemies.add(e);		
		}
		map = new MainMap("test",world,RESOLUTION_WIDTH,PIXEL_TO_METER);
				
		
		
		// create example checkpoints
		checkpoints = new Checkpoint[4];
		float[][] checkPointPosition = { { 100, 100 }, { 100, 300 }, {900, 100 }, { 900, 300 } };
		for (int i = 0; i < checkpoints.length; i++) {
			checkpoints[i] = new NormalCheckpoint(world, checkPointPosition[i][0] * PIXEL_TO_METER, checkPointPosition[i][1] * PIXEL_TO_METER);
		}
		
		// create example towers
		
		float test = 50;
		for (int i = 0; i < 10; i++) {
			Tower t= new EmptyTower(test * PIXEL_TO_METER, 10 * PIXEL_TO_METER,enemies);
			towers.add(t);
			test += 65;
		}

		// create example pit stop
		pitStop = new Sprite(new Texture("pit_stop/pit_stop_01.png"));
		pitStop.setPosition(100, 100);

		System.out.println("Play state entered");

	}

	public static Sprite createScaledSprite(String location) {
		Texture t=new Texture(location);
		Sprite s=new Sprite(t);
		s.setSize(s.getWidth() * PIXEL_TO_METER, s.getHeight() * PIXEL_TO_METER);
		s.setOriginCenter();
		//t.dispose();
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

	}

	@Override
	protected void update(float deltaTime) {

	
		handleInput();
		car.update(deltaTime);
		
		for (Tower t : towers) {
			t.update();
		}

		// update camera if camera has changed
		camera.update();

	}

	@Override
	public void render(SpriteBatch spriteBatch) {

		// set projection matrix
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		steststrecke.draw(spriteBatch);
		for( Enemy e: enemies )
		{
			e.update(Gdx.graphics.getDeltaTime());
			e.draw(spriteBatch);
		}
		car.draw(spriteBatch);
		
		// draw checkpoints
		for (Checkpoint checkpoint : checkpoints) checkpoint.draw(spriteBatch);
		
		// draw tower
		pitStop.draw(spriteBatch);
		for (Tower tower : towers) tower.draw(spriteBatch);
		
		// draw pitstop
		pitStop.draw(spriteBatch);

		spriteBatch.end();
		
		if (debugBox2D) {
			debugRender.render(world,camera.combined);
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
				if(enemy.tot){
					enemy.body.setActive(false);
					
				}
			}
	}
	

	@Override
	protected void dispose() {
		
	}

	@Override
	public void collisionCarEnemy(Car car, Enemy enemy) {
		enemy.takeDamage(20);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void collisionCarCheckpoint(Car car, Checkpoint checkpoint) {
		// TODO Auto-generated method stub
		checkpoint.setActivated(true);
		
	}

	@Override
	public void collisionCarTower(Car car, Tower tower) {
		// TODO Auto-generated method stub
		
	}

}
