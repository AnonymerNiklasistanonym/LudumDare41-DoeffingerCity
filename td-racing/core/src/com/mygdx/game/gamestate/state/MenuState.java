package com.mygdx.game.gamestate.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.menu.MenuButton;

public class MenuState extends GameState {

	private MenuButton startButton;
	private MenuButton aboutButton;
	
	private Texture backgroundStars;
	private Texture backgroundLoading;


	private Vector3 touchPos = new Vector3();
	
	private boolean started, die;

	public MenuState(GameStateManager gameStateManager) {
		super(gameStateManager);

		// BACKGROUND = new Texture(Gdx.files.internal("background.png"));
		MenuButton.textureActive = new Texture(Gdx.files.internal("buttons/button_active.png"));
		MenuButton.textureNotActive = new Texture(Gdx.files.internal("buttons/button_not_active.png"));
		backgroundStars = new Texture(Gdx.files.internal("background/background_stars.png"));
		backgroundLoading = new Texture(Gdx.files.internal("background/background_loading.png"));
		
		started = false;
		die = false;

		camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

		aboutButton = new MenuButton(MainGame.GAME_WIDTH / 2, MainGame.GAME_HEIGHT / 6 * 2, "ABOUT", true);
		startButton = new MenuButton(MainGame.GAME_WIDTH / 2, MainGame.GAME_HEIGHT / 6 * 4, "START", false);

		System.out.println("Menu state entered");
	}

	@Override
	public void handleInput() {

		// Check if somehow the screen was touched
		if (Gdx.input.justTouched()
				|| (Gdx.input.isKeyJustPressed(Keys.SPACE) || Gdx.input.isKeyJustPressed(Keys.ENTER))) {
			if (aboutButton.contains(touchPos)) {
				System.out.println("IDK?");
			} else if (startButton.contains(touchPos)) {
				started = true;
			}
		}

		if (Gdx.input.isKeyJustPressed(Keys.DOWN)) {
			if (aboutButton.isActive()) {
				aboutButton.setActive(false);
				startButton.setActive(true);
			} else if (startButton.isActive()) {
				aboutButton.setActive(true);
				startButton.setActive(false);
			}
		}

		if (Gdx.input.isKeyJustPressed(Keys.UP)) {
			if (aboutButton.isActive()) {
				aboutButton.setActive(false);
				startButton.setActive(true);
			} else if (startButton.isActive()) {
				aboutButton.setActive(true);
				startButton.setActive(false);
			}
		}

		if (Gdx.input.isCatchBackKey() || Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			Gdx.app.exit();
		}

		touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(touchPos);
		if (aboutButton.contains(touchPos)) {
			aboutButton.setActive(true);
			startButton.setActive(false);
		} else if (startButton.contains(touchPos)) {
			startButton.setActive(true);
			aboutButton.setActive(false);
		}
	}

	@Override
	public void update(float number) {
		handleInput();
		
		if (die) {
			gameStateManager.setGameState(new PlayState(gameStateManager));
		}

	}

	@Override
	public void render(final SpriteBatch spriteBatch) {

		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		if (started) {
			spriteBatch.draw(backgroundLoading, 0, 0);
			die = true;
		} else {
			spriteBatch.draw(backgroundStars, 0, 0);
			startButton.draw(spriteBatch);
			aboutButton.draw(spriteBatch);
		}
		spriteBatch.end();

	}

	@Override
	public void dispose() {
		// BACKGROUND.dispose();
		System.out.println("Menu state disposed");
	}

}
