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
import com.mygdx.game.Car;
import com.mygdx.game.Enemy_small;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;

public class PlayState extends GameState {

	Texture tteststrecke;
	Texture tmaincar;
	Sprite smaincar;


	private Sprite steststrecke;
	private World world;
	private Car car;
	private Enemy_small[] aEnemySmall = new Enemy_small[1];
	private boolean debugBox2D;

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
	
	private Tower[] towers;


	public PlayState(GameStateManager gameStateManager) {
		super(gameStateManager);

		tteststrecke = new Texture("maps/test.png");
		tmaincar = new Texture("cars/car_standard.png");
		Texture tzombie1 = new Texture("zombies/zombie_standard.png");
		
		Sprite szombie1=new Sprite(tzombie1);
		szombie1.setSize(szombie1.getWidth() * PIXEL_TO_METER, szombie1.getHeight() * PIXEL_TO_METER);
		szombie1.setOriginCenter();
		steststrecke = new Sprite(tteststrecke);
		steststrecke.setSize(tteststrecke.getWidth() * PIXEL_TO_METER, tteststrecke.getHeight() * PIXEL_TO_METER);

		smaincar = new Sprite(tmaincar);
		smaincar.setSize(smaincar.getWidth() * PIXEL_TO_METER, smaincar.getHeight() * PIXEL_TO_METER);
		smaincar.setOriginCenter();

		// Sets this camera to an orthographic projection, centered at (viewportWidth/2,
		// viewportHeight/2), with the y-axis pointing up or down.
		camera.setToOrtho(false, MainGame.GAME_WIDTH * PIXEL_TO_METER, MainGame.GAME_HEIGHT * PIXEL_TO_METER);

		debugBox2D = true;

		world = new World(new Vector2(0, 0), true);
			
		debugRender = new Box2DDebugRenderer();
		
		car = new Car(world,smaincar);
		
		aEnemySmall[0] = new Enemy_small(world,szombie1);
		aEnemySmall[0].startMove();
		
		towers = new EmptyTower[10];
		float test = 50;
		for (int i = 0; i < towers.length; i++) {
			towers[i] = new EmptyTower(test * PIXEL_TO_METER, 10 * PIXEL_TO_METER);
			test += 65;
		}

		pitStop = new Sprite(new Texture("pit_stop/pit_stop_01.png"));
		pitStop.setPosition(100, 100);

		System.out.println("Play state entered");

	}

	@Override
	protected void handleInput() {

		// Check if somehow the screen was touched
		if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			System.out.println("Do something");
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

		// handle input
		handleInput();
		
		// update car
		car.update(deltaTime);
		
		// do other things

		// update camera if camera has changed
		camera.update();

	}

	@Override
	public void render(SpriteBatch spriteBatch) {

		// set projection matrix
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		steststrecke.draw(spriteBatch);
		car.draw(spriteBatch);
		zombieUpdate(spriteBatch);
		pitStop.draw(spriteBatch);
		for (Tower tower : towers) tower.draw(spriteBatch);
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
	}
	
	public void zombieUpdate(SpriteBatch spriteBatch) {

		for( Enemy_small k: aEnemySmall )
		{
			k.draw(spriteBatch);
		}
		
	}

	@Override
	protected void dispose() {
		tteststrecke.dispose();
	}

}
