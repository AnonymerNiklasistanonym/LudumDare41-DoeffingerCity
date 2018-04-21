package com.mygdx.game.gamestate.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Car;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;

public class PlayState extends GameState {

	private Texture teststrecke;
	private Texture maincar;
	private World world;
	private Car car;
	private Body carbody;
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

	public PlayState(GameStateManager gameStateManager) {
		super(gameStateManager);

		teststrecke = new Texture("maps/test.png");
		maincar = new Texture("cars/car_standard.png");

		// Sets this camera to an orthographic projection, centered at (viewportWidth/2,
		// viewportHeight/2), with the y-axis pointing up or down.
		camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

		debugBox2D = true;

		/*
		 * // create new camera camera = new OrthographicCamera( MainGame.GAME_WIDTH,
		 * MainGame.GAME_HEIGHT); // move camera to the bottom left camera.position.x =
		 * MainGame.GAME_WIDTH / 2; camera.position.y = MainGame.GAME_HEIGHT / 2; //
		 * register and update camera camera.update();
		 */

		world = new World(new Vector2(0, 0), true);
		BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.DynamicBody;
		bodydef.position.set(500, 500);
		carbody = world.createBody(bodydef);
		PolygonShape carBox = new PolygonShape();
		carBox.setAsBox(maincar.getWidth(), maincar.getHeight());
		carbody.createFixture(carBox, 0f);
		debugRender = new Box2DDebugRenderer();
		
		
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

	}

	@Override
	protected void update(float deltaTime) {

		// handle input
		handleInput();

		// do other things

		// update camera if camera has changed
		camera.update();

	}

	@Override
	public void render(SpriteBatch spriteBatch) {

		// set projection matrix
		spriteBatch.setProjectionMatrix(camera.combined);

		spriteBatch.begin();

		spriteBatch.draw(teststrecke, 10, 10);
		float carx = carbody.getPosition().x;
		float cary = carbody.getPosition().y;
		spriteBatch.draw(maincar, 10, 10);
		spriteBatch.draw(maincar, carx, cary);
		pitStop.draw(spriteBatch);
		spriteBatch.end();

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

	@Override
	protected void dispose() {
		teststrecke.dispose();
	}

}
