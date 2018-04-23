package com.mygdx.game.gamestate.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.menu.MenuButton;

public class GameOverState extends GameState {

	private final MenuButton[] menuButtons;

	private final Texture backgroundGameOver;
	private final Texture backgroundLoading;

	private final static int PLAY_AGAIN_ID = 0;
	private final static int HIGHSCORE_ID = 1;
	private final static int ABOUT_ID = 2;

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

		final MenuButton playAgainButton = new MenuButton(PLAY_AGAIN_ID, MainGame.GAME_WIDTH / 2,
				MainGame.GAME_HEIGHT / 6 * 5, "RESTART LEVEL", true);
		final MenuButton highScoreButton = new MenuButton(HIGHSCORE_ID, MainGame.GAME_WIDTH / 2,
				MainGame.GAME_HEIGHT / 6 * 3, "HIGHSCORES", false);
		final MenuButton aboutButton = new MenuButton(ABOUT_ID, MainGame.GAME_WIDTH / 2, MainGame.GAME_HEIGHT / 6 * 1,
				"ABOUT", false);
		menuButtons = new MenuButton[] { playAgainButton, highScoreButton, aboutButton };

		System.out.println("Menu state entered");
	}

	@Override
	public void handleInput() {

		touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(touchPos);

		// determine on which button the mouse cursor is and select this button
		boolean oneIsSelected = false;
		for (final MenuButton menuButton : menuButtons) {
			if (menuButton.contains(touchPos))
				oneIsSelected = true;
		}
		if (oneIsSelected) {
			for (final MenuButton menuButton : menuButtons)
				menuButton.setActive(menuButton.contains(touchPos));
		}

		// If a button is touched do something or Space or Enter is pressed execute the
		// action for the selected button
		if (Gdx.input.justTouched()) {
			for (final MenuButton menuButton : menuButtons) {
				if (menuButton.isActive() && menuButton.contains(touchPos)) {
					switch (menuButton.getId()) {
					case PLAY_AGAIN_ID:
						loading = true;
						break;
					case HIGHSCORE_ID:
						gameStateManager.setGameState(new HighscoreState(gameStateManager));
						break;
					case ABOUT_ID:
						System.out.println("ABOUT_ID IDK?");
						break;
					}
				}
			}
		}
		if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			for (final MenuButton menuButton : menuButtons) {
				if (menuButton.isActive()) {
					switch (menuButton.getId()) {
					case PLAY_AGAIN_ID:
						loading = true;
						break;
					case HIGHSCORE_ID:
						gameStateManager.setGameState(new HighscoreState(gameStateManager));
						break;
					case ABOUT_ID:
						System.out.println("ABOUT_ID IDK?");
						break;
					}
				}
			}
		}

		// if escape or back is pressed quit
		if (Gdx.input.isCatchBackKey() || Gdx.input.isKeyJustPressed(Keys.ESCAPE))
			Gdx.app.exit();
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
			final GlyphLayout test = new GlyphLayout(MainGame.fontBig, "LOADING");
			MainGame.fontBig.draw(spriteBatch, "LOADING", MainGame.GAME_WIDTH / 2 - test.width / 2, MainGame.GAME_HEIGHT / 2 + test.height / 2);
			changeToPlayState = true;
		} else {
			spriteBatch.draw(backgroundGameOver, 0, 0);
			for (final MenuButton menuButton : menuButtons)
				menuButton.draw(spriteBatch);
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
