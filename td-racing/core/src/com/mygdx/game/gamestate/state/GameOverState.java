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

public class GameOverState extends GameState {

	private final MenuButton playAgainButton;
	private final MenuButton highScoreButton;

	private final Texture backgroundGameOver;
	private final Texture backgroundLoading;

	private Vector3 touchPos;

	private boolean loading, changeToPlayState;

	public GameOverState(GameStateManager gameStateManager) {
		super(gameStateManager);
		
		MenuButton.textureActive = new Texture(Gdx.files.internal("buttons/button_menu_active.png"));
		MenuButton.textureNotActive = new Texture(Gdx.files.internal("buttons/button_menu_not_active.png"));

		backgroundGameOver = new Texture(Gdx.files.internal("background/background_game_over.png"));
		backgroundLoading = new Texture(Gdx.files.internal("background/background_loading.png"));

		touchPos = new Vector3();
		loading = false;
		changeToPlayState = false;

		camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

		highScoreButton = new MenuButton(MainGame.GAME_WIDTH / 2, MainGame.GAME_HEIGHT / 8 * 2, "HIGHSCORES", false);
		playAgainButton = new MenuButton(MainGame.GAME_WIDTH / 2, MainGame.GAME_HEIGHT /8 * 4, "RESTART LEVEL", true);

		System.out.println("Menu state entered");
	}

	@Override
	public void handleInput() {

		// If enter, space or screen touched do something
		if (Gdx.input.justTouched()
				|| (Gdx.input.isKeyJustPressed(Keys.SPACE) || Gdx.input.isKeyJustPressed(Keys.ENTER))) {
			if (highScoreButton.contains(touchPos)) System.out.println("IDK?");
			else if (playAgainButton.contains(touchPos)) loading = true;
		}

		if (Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.UP)) {
			if (highScoreButton.isActive()) {
				highScoreButton.setActive(false);
				playAgainButton.setActive(true);
			} else if (playAgainButton.isActive()) {
				highScoreButton.setActive(true);
				playAgainButton.setActive(false);
			}
		}

		if (Gdx.input.isCatchBackKey() || Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			Gdx.app.exit();
		}

		touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(touchPos);
		if (highScoreButton.contains(touchPos)) {
			highScoreButton.setActive(true);
			playAgainButton.setActive(false);
		} else if (playAgainButton.contains(touchPos)) {
			playAgainButton.setActive(true);
			highScoreButton.setActive(false);
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
			spriteBatch.draw(backgroundGameOver, 0, 0);
			playAgainButton.draw(spriteBatch);
			highScoreButton.draw(spriteBatch);
			MainGame.font.draw(spriteBatch, "GAME OVER", MainGame.GAME_WIDTH / 2, MainGame.GAME_WIDTH / 6 * 5);
		}
		spriteBatch.end();
	}

	@Override
	public void dispose() {
		backgroundGameOver.dispose();
		backgroundLoading.dispose();
		MenuButton.textureActive.dispose();
		MenuButton.textureNotActive.dispose();
		
		System.out.println("Menu state disposed");
	}

}
