package com.mygdx.game.gamestate.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.GameStateMethods;
import com.mygdx.game.gamestate.states.resources.HighscoreButton;
import com.mygdx.game.listener.controller.ControllerHelperMenu;
import com.mygdx.game.listener.controller.ControllerMenuCallbackInterface;
import com.mygdx.game.listener.controller.ControllerWiki;
import com.mygdx.game.unsorted.PreferencesManager;
import com.mygdx.game.unsorted.PreferencesManager.HighscoreEntry;

public class HighscoreListState extends GameState implements ControllerMenuCallbackInterface {

	private static final String STATE_NAME = "Highscore > List";

	private final PreferencesManager preferencesManager;

	private HighscoreButton[] highscoreButtons;
	private float controllerTimeHelper;
	private final ControllerListener controllerHelperMenu;

	public HighscoreListState(GameStateManager gameStateManager) {
		super(gameStateManager, STATE_NAME);

		// load static texture for high score entry
		HighscoreButton.texture = new Texture(Gdx.files.internal("buttons/button_highscore.png"));

		// set camera to 1280x720
		camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

		// create a preferences manager for loading/clearing high score entries
		preferencesManager = new PreferencesManager();
		loadHighScoreList();

		// controller setup
		controllerHelperMenu = new ControllerHelperMenu(this);
		Controllers.addListener(controllerHelperMenu);
		controllerTimeHelper = 0;
	}

	private void loadHighScoreList() {
		preferencesManager.checkHighscore();
		// load and display high score entries
		HighscoreEntry[] entries = preferencesManager.retrieveHighscore();
		this.highscoreButtons = new HighscoreButton[] {
				new HighscoreButton(1, entries[0].getScore(), entries[0].getName(), MainGame.GAME_WIDTH / 2,
						MainGame.GAME_HEIGHT / 6 * 5),
				new HighscoreButton(2, entries[1].getScore(), entries[1].getName(), MainGame.GAME_WIDTH / 2,
						MainGame.GAME_HEIGHT / 6 * 4),
				new HighscoreButton(3, entries[2].getScore(), entries[2].getName(), MainGame.GAME_WIDTH / 2,
						MainGame.GAME_HEIGHT / 6 * 3),
				new HighscoreButton(4, entries[3].getScore(), entries[3].getName(), MainGame.GAME_WIDTH / 2,
						MainGame.GAME_HEIGHT / 6 * 2),
				new HighscoreButton(5, entries[4].getScore(), entries[4].getName(), MainGame.GAME_WIDTH / 2,
						MainGame.GAME_HEIGHT / 6 * 1) };
	}

	@Override
	protected void handleInput() {
		GameStateMethods.toggleFullScreen(true);

		// go back to the menu state
		if (Gdx.input.justTouched() || (Gdx.input.isKeyJustPressed(Keys.ESCAPE) || Gdx.input.isCatchBackKey()))
			goBack();

		// clear high score list
		if (Gdx.input.isKeyJustPressed(Keys.C)) {
			preferencesManager.clearHighscore();
			loadHighScoreList();
		}
	}

	private void goBack() {
		gameStateManager.setGameState(new MenuState(gameStateManager));
	}

	@Override
	protected void update(final float deltaTime) {
		controllerTimeHelper += deltaTime;
	}

	@Override
	protected void render(final SpriteBatch spriteBatch) {
		spriteBatch.begin();

		// draw high score (entry) buttons
		for (final HighscoreButton highscoreButton : highscoreButtons)
			highscoreButton.draw(spriteBatch);

		// draw message to inform how the list can be cleared
		MainGame.font.getData().setScale(1);
		MainGame.font.setColor(1, 1, 1, 1);
		MainGame.font.draw(spriteBatch, "Clear List: C", 10, 30);

		spriteBatch.end();
	}

	@Override
	protected void dispose() {
		Controllers.removeListener(controllerHelperMenu);
		HighscoreButton.texture.dispose();
		for (final HighscoreButton highscoreButton : highscoreButtons)
			highscoreButton.dispose();
	}

	@Override
	public void backControllerCallback() {
		goBack();
	}

	@Override
	public void selectControllerCallback(int buttonId) {
		if (controllerTimeHelper < 0.2)
			return;
		if (buttonId == ControllerWiki.BUTTON_A)
			goBack();
		if (buttonId == ControllerWiki.BUTTON_START)
			GameStateMethods.toggleFullScreen();
	}

	@Override
	public void dPadButtonsControllerCallback(PovDirection direction) {
		// Nothing to do
	}

	@Override
	public void stickMovedControllerCallback(final boolean xAxis, final float value) {
		// Nothing to do
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
