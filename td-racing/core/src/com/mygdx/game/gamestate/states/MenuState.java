package com.mygdx.game.gamestate.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

public class MenuState extends GameState implements ControllerMenuCallbackInterface {

	private final MenuButton[] menuButtons;

	private final Texture backgroundStars;
	private final Texture title;

	private final ControllerHelperMenu controllerHelperMenu;

	private final static int START_ID = 0;
	private final static int HIGHSCORE_ID = 1;
	private final static int ABOUT_ID = 2;

	private static final String STATE_NAME = "Menu";

	private final Vector3 touchPos;

	private boolean blockStickInput = false;
	private float stickTimeHelper;
	private float controllerTimeHelper;

	public MenuState(GameStateManager gameStateManager) {
		super(gameStateManager, STATE_NAME);

		MenuButtonBig.textureActive = new Texture(Gdx.files.internal("buttons/button_menu_active.png"));
		MenuButtonBig.textureNotActive = new Texture(Gdx.files.internal("buttons/button_menu_not_active.png"));
		MenuButtonSmall.textureActive = new Texture(Gdx.files.internal("buttons/button_menu_active_small.png"));
		MenuButtonSmall.textureNotActive = new Texture(Gdx.files.internal("buttons/button_menu_not_active_small.png"));
		backgroundStars = new Texture(Gdx.files.internal("background/background_stars.png"));
		title = new Texture(Gdx.files.internal("buttons/titel.png"));

		touchPos = new Vector3();

		camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

		menuButtons = new MenuButton[] {
				new MenuButtonBig(START_ID, MainGame.GAME_WIDTH / 2, MainGame.GAME_HEIGHT / 6 * 2.8f, "START", true),
				new MenuButtonSmall(ABOUT_ID, MainGame.GAME_WIDTH / 4, MainGame.GAME_HEIGHT / 6 * 1, "ABOUT"),
				new MenuButtonSmall(HIGHSCORE_ID, MainGame.GAME_WIDTH / 2 + MainGame.GAME_WIDTH / 4,
						MainGame.GAME_HEIGHT / 6 * 1, "HIGHSCORES") };

		controllerHelperMenu = new ControllerHelperMenu(this);
		Controllers.addListener(controllerHelperMenu);
		blockStickInput = false;
		stickTimeHelper = 0;
	}

	@Override
	public void handleInput() {
		GameStateMethods.toggleFullScreen(true);

		// map touch position to the camera resolution
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
			openSelectedMenuButton();
		}

		// Go with the arrow keys through all visible buttons
		if (Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.RIGHT))
			selectNextButton(true);
		if (Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.LEFT))
			selectNextButton(false);

		// if escape or back is pressed quit
		if (Gdx.input.isCatchBackKey() || Gdx.input.isKeyJustPressed(Keys.ESCAPE))
			Gdx.app.exit();
	}

	@Override
	public void update(final float deltaTime) {
		stickTimeHelper += deltaTime;
		controllerTimeHelper += deltaTime;
	}

	@Override
	public void render(final SpriteBatch spriteBatch) {
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();

		spriteBatch.draw(backgroundStars, 0, 0);
		for (final MenuButton menuButton : menuButtons)
			menuButton.draw(spriteBatch);

		spriteBatch.draw(title, 0, 0);
		spriteBatch.end();
	}

	@Override
	public void dispose() {
		Controllers.removeListener(controllerHelperMenu);
		for (final MenuButton menuButton : menuButtons)
			menuButton.dispose();
		backgroundStars.dispose();
		title.dispose();
		MenuButtonBig.textureActive.dispose();
		MenuButtonBig.textureNotActive.dispose();
		MenuButtonSmall.textureActive.dispose();
		MenuButtonSmall.textureNotActive.dispose();
	}

	private void openSelectedMenuButton() {
		for (final MenuButton menuButton : menuButtons) {
			if (menuButton.isActive()) {
				switch (menuButton.getId()) {
				case START_ID:
					gameStateManager.setGameState(new LoadingState(gameStateManager, 1));
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
	public void backControllerCallback() {
		if (controllerTimeHelper < 0.2)
			return;
		// exit application
		Gdx.app.exit();
	}

	@Override
	public void selectControllerCallback(int buttonId) {
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
	public void dPadButtonsControllerCallback(PovDirection direction) {
		// select next button
		if (direction == ControllerWiki.BUTTON_DPAD_DOWN || direction == ControllerWiki.BUTTON_DPAD_RIGHT)
			selectNextButton(true);
		if (direction == ControllerWiki.BUTTON_DPAD_UP || direction == ControllerWiki.BUTTON_DPAD_LEFT)
			selectNextButton(false);

	}

	@Override
	public void stickMovedControllerCallback(final boolean xAxis, final float value) {
		// select next button
		if (blockStickInput && stickTimeHelper >= 0.3)
			blockStickInput = false;
		if (!blockStickInput && (value > 0.3 || value < -0.3)) {
			selectNextButton(value > 0.3);
			stickTimeHelper = 0;
			blockStickInput = true;
		}
	}

	@Override
	public void pause() {
		// Nothing to do

	}

	@Override
	public void resume() {
		// Nothing to do
	}

}
