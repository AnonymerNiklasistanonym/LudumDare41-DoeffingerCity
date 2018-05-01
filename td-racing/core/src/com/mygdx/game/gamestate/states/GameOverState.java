package com.mygdx.game.gamestate.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.GameStateMethods;
import com.mygdx.game.gamestate.states.resources.MenuButton;
import com.mygdx.game.gamestate.states.resources.MenuButtonBig;
import com.mygdx.game.gamestate.states.resources.MenuButtonSmall;
import com.mygdx.game.listener.controller.ControllerHelperMenu;
import com.mygdx.game.listener.controller.ControllerMenuCallbackInterface;
import com.mygdx.game.listener.controller.ControllerWiki;

public class GameOverState extends GameState implements ControllerMenuCallbackInterface {

	private final MenuButton[] menuButtons;

	private final Texture backgroundGameOver;

	private final static int PLAY_AGAIN_ID = 0;
	private final static int PLAY_LEVEL_AGAIN_ID = 1;
	private final static int HIGHSCORE_ID = 2;
	private final static int ABOUT_ID = 3;

	private static final String STATE_NAME = "Game Over";

	private final Vector3 touchPos;

	private String loadingText;

	private Vector2 loadingTextPosition;

	private boolean blockStickInput = false;
	private float stickTimeHelper;
	private float controllerTimeHelper;
	private final ControllerListener controllerHelperMenu;
	
	

	private final int level;

	public GameOverState(final GameStateManager gameStateManager, final int level) {
		super(gameStateManager, STATE_NAME);

		this.level = level;

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
				new MenuButtonSmall(PLAY_AGAIN_ID, MainGame.GAME_WIDTH / 4, MainGame.GAME_HEIGHT / 6 * 3, "RESTART",
						true),
				new MenuButtonSmall(PLAY_LEVEL_AGAIN_ID, MainGame.GAME_WIDTH - MainGame.GAME_WIDTH / 4,
						MainGame.GAME_HEIGHT / 6 * 3, "...LEVEL"),
				new MenuButtonSmall(HIGHSCORE_ID, MainGame.GAME_WIDTH / 4, MainGame.GAME_HEIGHT / 6 * 1, "HIGHSCORES"),
				new MenuButtonSmall(ABOUT_ID, MainGame.GAME_WIDTH - MainGame.GAME_WIDTH / 4,
						MainGame.GAME_HEIGHT / 6 * 1, "ABOUT") };

		// controller setup
		controllerHelperMenu = new ControllerHelperMenu(this);
		Controllers.addListener(controllerHelperMenu);
		blockStickInput = false;
		stickTimeHelper = 0;
		controllerTimeHelper = 0;
	}

	@Override
	public void handleInput() {
		GameStateMethods.toggleFullScreen(true);
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
						gameStateManager.setGameState(new LoadingState(gameStateManager, 1));
						break;
					case PLAY_LEVEL_AGAIN_ID:
						gameStateManager.setGameState(new LoadingState(gameStateManager, level));
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
		controllerTimeHelper += deltaTime;
		stickTimeHelper += deltaTime;
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
		Controllers.removeListener(controllerHelperMenu);
		backgroundGameOver.dispose();
		MenuButtonBig.textureActive.dispose();
		MenuButtonBig.textureNotActive.dispose();
		MenuButtonSmall.textureActive.dispose();
		MenuButtonSmall.textureNotActive.dispose();
	}

	private void openSelectedMenuButton() {
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

	@Override
	public void backCallback() {
		// exit application
		Gdx.app.exit();
	}

	@Override
	public void selectCallback(int buttonId) {
		if (controllerTimeHelper < 0.2)
			return;
		// open selected button
		if (buttonId == ControllerWiki.BUTTON_A)
			openSelectedMenuButton();
		if (buttonId == ControllerWiki.BUTTON_START)
			GameStateMethods.toggleFullScreen();
	}

	private void selectNextButton(boolean below) {
		for (int i = 0; i < menuButtons.length; i++) {
			if (menuButtons[i].isActive()) {
				menuButtons[i].setActive(false);
				if (below)
					menuButtons[(i + 1) % menuButtons.length].setActive(true);
				else
					menuButtons[(i - 1 + menuButtons.length) % menuButtons.length].setActive(true);
				return;
			}
		}
	}

	@Override
	public void dPadCallback(PovDirection direction) {
		// select next button
		if (direction == ControllerWiki.BUTTON_DPAD_DOWN || direction == ControllerWiki.BUTTON_DPAD_RIGHT)
			selectNextButton(true);
		if (direction == ControllerWiki.BUTTON_DPAD_UP || direction == ControllerWiki.BUTTON_DPAD_LEFT)
			selectNextButton(false);

	}

	@Override
	public void stickMoved(final boolean xAxis, final float value) {
		// select next button
		if (blockStickInput && stickTimeHelper >= 0.3)
			blockStickInput = false;
		if (!blockStickInput && (value > 0.3 || value < -0.3)) {
			selectNextButton(value > 0.3);
			stickTimeHelper = 0;
			blockStickInput = true;
		}
	}

}
