package com.mygdx.game.gamestate.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

	Texture tteststrecke;
	Texture tmaincar;
	Sprite smaincar;
	World world;
	Car car;

	Body carbody;
	boolean debugBox2D;

	/**
	 * Time since last physic Steps
	 */
	float physicsaccumulator = 0f;

	Box2DDebugRenderer debugRender;

	/**
	 * Time for physic Steps
	 */
	public final static float TIME_STEP = 1 / 60f;

	public final static float SCALE_TO_BOX = 0.5f;

	public PlayState(GameStateManager gameStateManager) {
		super(gameStateManager);

		tteststrecke = new Texture("maps/test.png");
		tmaincar = new Texture("cars/car_standard.png");

		smaincar = new Sprite(tmaincar);

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
		carBox.setAsBox(tmaincar.getWidth() * SCALE_TO_BOX, tmaincar.getHeight() * SCALE_TO_BOX);
		carbody.createFixture(carBox, 0f);
		debugRender = new Box2DDebugRenderer();
		car = new Car(carbody);
		System.out.println("Play state entered");

	}

	@Override
	protected void handleInput() {

		// Check if somehow the screen was touched
		if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			System.out.println("Do something");
		}
		if (Gdx.input.isKeyJustPressed(Keys.W)) {
			car.accelarate();
		}
		if (Gdx.input.isKeyJustPressed(Keys.S)) {
			car.brake();
		}
		if (Gdx.input.isKeyJustPressed(Keys.A)) {
			car.steerLeft();
		}
		if (Gdx.input.isKeyJustPressed(Keys.D)) {
			car.steerRight();
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
		spriteBatch.draw(tteststrecke, 0, 0);

		float carx = carbody.getPosition().x;
		float cary = carbody.getPosition().y;
		carx = carx - smaincar.getWidth() / 2;
		cary = cary - smaincar.getHeight() / 2;
		smaincar.setPosition(carx, cary);

		smaincar.draw(spriteBatch);

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
	}

	@Override
	protected void dispose() {
		tteststrecke.dispose();
	}

}
