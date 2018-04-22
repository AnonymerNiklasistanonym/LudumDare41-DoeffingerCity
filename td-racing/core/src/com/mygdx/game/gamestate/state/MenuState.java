package com.mygdx.game.gamestate.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.menu.MenuButton;

public class MenuState extends GameState {

	private final MenuButton startButton;
	private final MenuButton aboutButton;

	private final Texture backgroundStars;
	private final Texture backgroundLoading;

	private Vector3 touchPos;

	private boolean loading, changeToPlayState;

	public MenuState(GameStateManager gameStateManager) {
		super(gameStateManager);

		MenuButton.textureActive = new Texture(Gdx.files.internal("buttons/button_active.png"));
		MenuButton.textureNotActive = new Texture(Gdx.files.internal("buttons/button_not_active.png"));
		backgroundStars = new Texture(Gdx.files.internal("background/background_stars.png"));
		backgroundLoading = new Texture(Gdx.files.internal("background/background_loading.png"));

		touchPos = new Vector3();
		loading = false;
		changeToPlayState = false;

		camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

		aboutButton = new MenuButton(MainGame.GAME_WIDTH / 2, MainGame.GAME_HEIGHT / 6 * 2, "ABOUT", false);
		startButton = new MenuButton(MainGame.GAME_WIDTH / 2, MainGame.GAME_HEIGHT / 6 * 4, "START", true);

		System.out.println("Menu state entered");
	}

	@Override
	public void handleInput() {

		// If enter, space or screen touched do something
		if (Gdx.input.justTouched()
				|| (Gdx.input.isKeyJustPressed(Keys.SPACE) || Gdx.input.isKeyJustPressed(Keys.ENTER))) {
			if (aboutButton.contains(touchPos)) System.out.println("IDK?");
			else if (startButton.contains(touchPos)) loading = true;
		}

		if (Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.UP)) {
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
		if (changeToPlayState)
			gameStateManager.setGameState(new PlayState(gameStateManager));
	}

	@Override
	public void render(final SpriteBatch spriteBatch) {
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		if (loading) {
			spriteBatch.draw(backgroundLoading, 0, 0);
			changeToPlayState = true;
		} else {
			spriteBatch.draw(backgroundStars, 0, 0);
			startButton.draw(spriteBatch);
			aboutButton.draw(spriteBatch);
		}
		spriteBatch.end();
	}

	@Override
	public void dispose() {
		backgroundStars.dispose();
		backgroundLoading.dispose();
		MenuButton.textureActive.dispose();
		MenuButton.textureNotActive.dispose();
		
		System.out.println("Menu state disposed");
	}

}
