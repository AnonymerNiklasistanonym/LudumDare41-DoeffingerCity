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
import com.mygdx.game.gamestate.GameStateMethods;
import com.mygdx.game.menu.MenuButton;
import com.mygdx.game.menu.MenuButtonBig;
import com.mygdx.game.menu.MenuButtonSmall;

public class GameOverState extends GameState {

	private final MenuButton[] menuButtons;

	private final Texture backgroundGameOver;

	private final static int PLAY_AGAIN_ID = 0;
	private final static int HIGHSCORE_ID = 1;
	private final static int ABOUT_ID = 2;

	private static final String STATE_NAME = "Game Over";

	private final Vector3 touchPos;

	private String loadingText;

	private Vector2 loadingTextPosition;

	public GameOverState(GameStateManager gameStateManager) {
		super(gameStateManager, STATE_NAME);

		// set font scale to the correct size and disable to use integers for scaling
		MainGame.fontUpperCaseBig.getData().setScale(1);

		// set camera to a scenery of 1280x720
		camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

		// load button textures
		MenuButtonBig.textureActive = new Texture(Gdx.files.internal("buttons/button_menu_active.png"));
		MenuButtonBig.textureNotActive = new Texture(Gdx.files.internal("buttons/button_menu_not_active.png"));
		MenuButtonSmall.textureActive = new Texture(Gdx.files.internal("buttons/button_menu_active_small.png"));
		MenuButtonSmall.textureNotActive = new Texture(Gdx.files.internal("buttons/button_menu_not_active_small.png"));
		backgroundGameOver = new Texture(Gdx.files.internal("background/background_game_over.png"));

		touchPos = new Vector3();

		camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

		// calculate text coordinates
		this.loadingText = "GAME OVER";
		this.loadingTextPosition = GameStateMethods.calculateCenteredTextPositon(MainGame.fontUpperCaseBig, loadingText,
				MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT / 5 * 8);

		menuButtons = new MenuButton[] {
				new MenuButtonBig(PLAY_AGAIN_ID, MainGame.GAME_WIDTH / 2, MainGame.GAME_HEIGHT / 6 * 3, "RESTART LEVEL",
						true),
				new MenuButtonSmall(HIGHSCORE_ID, MainGame.GAME_WIDTH / 4, MainGame.GAME_HEIGHT / 6 * 1, "HIGHSCORES"),
				new MenuButtonSmall(ABOUT_ID, MainGame.GAME_WIDTH - MainGame.GAME_WIDTH / 4,
						MainGame.GAME_HEIGHT / 6 * 1, "ABOUT") };
	}

	@Override
	public void handleInput() {
		GameStateMethods.toggleFullScreen(Keys.F11);
		touchPos.set(GameStateMethods.getMousePosition(camera));

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
		if (Gdx.input.justTouched()
				|| (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE))) {
			for (final MenuButton menuButton : menuButtons) {
				if (menuButton.isActive()) {
					switch (menuButton.getId()) {
					case PLAY_AGAIN_ID:
						gameStateManager.setGameState(new LoadingState(gameStateManager, MainGame.level));
						break;
					case HIGHSCORE_ID:
						gameStateManager.setGameState(new HighscoreListState(gameStateManager));
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
	}

	@Override
	public void update(final float deltaTime) {
		// Do nothing
	}

	@Override
	public void render(final SpriteBatch spriteBatch) {
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();

		spriteBatch.draw(backgroundGameOver, 0, 0);
		for (final MenuButton menuButton : menuButtons)
			menuButton.draw(spriteBatch);
		MainGame.fontUpperCaseBig.draw(spriteBatch, loadingText, loadingTextPosition.x, loadingTextPosition.y);
		spriteBatch.end();
	}

	@Override
	public void dispose() {
		backgroundGameOver.dispose();
		MenuButtonBig.textureActive.dispose();
		MenuButtonBig.textureNotActive.dispose();
		MenuButtonSmall.textureActive.dispose();
		MenuButtonSmall.textureNotActive.dispose();
	}

}
