package com.mygdx.game.gamestate.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.GameStateMethods;
import com.mygdx.game.listener.controller.ControllerHelperMenu;
import com.mygdx.game.listener.controller.ControllerMenuCallbackInterface;
import com.mygdx.game.listener.controller.ControllerWiki;
import com.mygdx.game.unsorted.PreferencesManager;

public class GameWonState extends GameState implements ControllerMenuCallbackInterface {

	private static final String STATE_NAME = "Game Won";

	private final Texture backgroundGameWon;
	private final Sound victorySound;

	private float controllerTimeHelper;
	private final ControllerListener controllerHelperMenu;

	private final PreferencesManager preferencesManager;

	private final int score;
	private final int level;

	public GameWonState(final GameStateManager gameStateManager, final int score, final int level) {
		super(gameStateManager, STATE_NAME);

		// save score
		this.score = score;
		this.level = level;

		// set font scale
		MainGame.fontUpperCaseBig.getData().setScale(1);

		// set camera to a scenery of 1280x720
		camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

		// load background texture
		backgroundGameWon = new Texture(Gdx.files.internal("fullscreens/victorycard.png"));

		// controller setup
		controllerHelperMenu = new ControllerHelperMenu(this);
		Controllers.addListener(controllerHelperMenu);
		controllerTimeHelper = 0;

		// get preferences manager
		preferencesManager = new PreferencesManager();

		// play sound
		victorySound = Gdx.audio.newSound(Gdx.files.internal("sounds/level_victory.wav"));
		if (new PreferencesManager().getSoundSetting())
			victorySound.play();
	}

	@Override
	public void handleInput() {
		GameStateMethods.toggleFullScreen(true);

		// If a button is touched do something or Space or Enter continue to next state
		if (Gdx.input.justTouched()
				|| (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)))
			goForward();

		// if escape or back is pressed quit
		if (Gdx.input.isCatchBackKey() || Gdx.input.isKeyJustPressed(Keys.ESCAPE))
			goBack();
	}

	private void goBack() {
		gameStateManager.setGameState(new MenuState(gameStateManager));
	}

	private void goForward() {
		if (preferencesManager.scoreIsInTop5(score))
			gameStateManager.setGameState(new HighscoreNameState(gameStateManager, score, level, true));
		else
			gameStateManager.setGameState(new CreditState(gameStateManager));
	}

	@Override
	public void update(final float deltaTime) {
		controllerTimeHelper += deltaTime;
	}

	@Override
	public void render(final SpriteBatch spriteBatch) {
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		spriteBatch.draw(backgroundGameWon, 0, 0);
		spriteBatch.end();
	}

	@Override
	public void dispose() {
		Controllers.removeListener(controllerHelperMenu);
		backgroundGameWon.dispose();
		victorySound.dispose();
	}

	@Override
	public void backCallback() {
		goBack();
	}

	@Override
	public void selectCallback(final int buttonId) {
		if (controllerTimeHelper < 0.2)
			return;
		if (buttonId == ControllerWiki.BUTTON_A)
			goForward();
		if (buttonId == ControllerWiki.BUTTON_START)
			GameStateMethods.toggleFullScreen();
	}

	@Override
	public void dPadCallback(final PovDirection direction) {
		// Do nothing
	}

	@Override
	public void stickMoved(final boolean xAxis, final float value) {
		// Do nothing
	}

}
