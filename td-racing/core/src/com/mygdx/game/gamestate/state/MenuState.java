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

public class MenuState extends GameState {

	private final MenuButton[] menuButtons;

	private final Texture backgroundStars;
	private final Texture backgroundLoading;
	private final Texture title;

	private final static int START_ID = 0;
	private final static int HIGHSCORES_ID = 1;
	private final static int ABOUT_ID = 2;

	private Vector3 touchPos;

	private boolean loading, changeToPlayState;

	public MenuState(GameStateManager gameStateManager) {
		super(gameStateManager);

		MenuButton.textureActive = new Texture(Gdx.files.internal("buttons/button_menu_active.png"));
		MenuButton.textureNotActive = new Texture(Gdx.files.internal("buttons/button_menu_not_active.png"));
		backgroundStars = new Texture(Gdx.files.internal("background/background_stars.png"));
		backgroundLoading = new Texture(Gdx.files.internal("background/background_loading.png"));
		title = new Texture(Gdx.files.internal("buttons/titel.png"));

		touchPos = new Vector3();
		loading = false;
		changeToPlayState = false;

		camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

		final MenuButton startButton = new MenuButton(START_ID, MainGame.GAME_WIDTH / 2,
				MainGame.GAME_HEIGHT / 6 * 2.5f, "START", true);
		final MenuButton highscoreButton = new MenuButton(HIGHSCORES_ID, MainGame.GAME_WIDTH / 2,
				MainGame.GAME_HEIGHT / 6 * 3, "HIGHSCORES", false);
		final MenuButton aboutButton = new MenuButton(ABOUT_ID, MainGame.GAME_WIDTH / 2, MainGame.GAME_HEIGHT / 6 * 1,
				"ABOUT", false);
		// if (Gdx.app.getType() != ApplicationType.WebGL) {
		// menuButtons = new MenuButton[] { startButton, highscoreButton, aboutButton };
		// } else {
		menuButtons = new MenuButton[] { startButton, aboutButton };
		// }

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
					case START_ID:
						loading = true;
						break;
					case HIGHSCORES_ID:
						gameStateManager.setGameState(new HighscoreState(gameStateManager));
						break;
					case ABOUT_ID:
						gameStateManager.setGameState(new CreditState(gameStateManager));
						break;
					}
				}
			}
		}
		if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			for (final MenuButton menuButton : menuButtons) {
				if (menuButton.isActive()) {
					switch (menuButton.getId()) {
					case START_ID:
						loading = true;
						break;
					case HIGHSCORES_ID:
						gameStateManager.setGameState(new HighscoreState(gameStateManager));
						break;
					case ABOUT_ID:
						gameStateManager.setGameState(new CreditState(gameStateManager));
						break;
					}
				}
			}
		}

		// if escape or back is pressed quit
		if (Gdx.input.isCatchBackKey() || Gdx.input.isKeyJustPressed(Keys.ESCAPE))
			Gdx.app.exit();
		if (Gdx.input.isKeyJustPressed(Keys.F11)) {
			if (Gdx.graphics.isFullscreen())
				Gdx.graphics.setWindowedMode(MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);
			else
				Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		}
	}

	@Override
	public void update(float number) {
		if (changeToPlayState)
			gameStateManager.setGameState(new PlayState(gameStateManager, MainGame.level));
	}

	@Override
	public void render(final SpriteBatch spriteBatch) {
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		if (loading) {
			spriteBatch.draw(backgroundLoading, 0, 0);
			final GlyphLayout test = new GlyphLayout(MainGame.fontBig, "LOADING");
			MainGame.fontBig.draw(spriteBatch, "LOADING", MainGame.GAME_WIDTH / 2 - test.width / 2,
					MainGame.GAME_HEIGHT / 2 + test.height / 2);
			changeToPlayState = true;
		} else {
			spriteBatch.draw(backgroundStars, 0, 0);
			for (final MenuButton menuButton : menuButtons)
				menuButton.draw(spriteBatch);
		}
		spriteBatch.draw(title, 0, 0);
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
