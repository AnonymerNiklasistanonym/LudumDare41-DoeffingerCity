package com.mygdx.game.gamestate.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.GameStateMethods;
import com.mygdx.game.listener.controller.ControllerHelperMenu;
import com.mygdx.game.listener.controller.ControllerMenuCallbackInterface;
import com.mygdx.game.listener.controller.ControllerWiki;

public class CreditState extends GameState implements ControllerMenuCallbackInterface {

	private final String[] textContent;
	private final Vector2[] textContentPosition;

	private final ControllerHelperMenu controllerHelperMenu;
	private float controllerTimeHelper;

	private static final String STATE_NAME = "Credits";

	private final Music backgroundMusic;

	public CreditState(final GameStateManager gameStateManager) {
		super(gameStateManager, STATE_NAME);

		// set font scale to the correct size and disable to use integers for scaling
		MainGame.fontUpperCaseBig.getData().setScale(0.5f);
		MainGame.fontUpperCaseBig.setUseIntegerPositions(false);

		// set camera to a scenery of 1280x720
		camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

		// array with the String lines that should be displayed
		textContent = new String[] { "THIS GAME WAS MADE BY", "DANIEL CZEPPEL", "NIKLAS MIKELER", "PATRICK ULMER", "",
				"MUSIC BY SASCHA CZEPPEL" };

		// calculate the text positions so that every line is centered
		textContentPosition = GameStateMethods.calculateCenteredMultiLineTextPositons(MainGame.fontUpperCaseBig,
				textContent, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

		// add controller listener
		controllerHelperMenu = new ControllerHelperMenu(this);
		Controllers.addListener(controllerHelperMenu);
		controllerTimeHelper = 0;

		// add music
		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/theme.mp3"));
		backgroundMusic.setLooping(true);
		backgroundMusic.play();
	}

	@Override
	protected void handleInput() {
		GameStateMethods.toggleFullScreen(true);

		// on touch or escape or back go back to the menu
		if (Gdx.input.justTouched() || (Gdx.input.isKeyJustPressed(Keys.ESCAPE) || Gdx.input.isCatchBackKey()))
			goBack();
	}

	@Override
	protected void update(final float deltaTime) {
		controllerTimeHelper += deltaTime;
	}

	@Override
	protected void render(final SpriteBatch spriteBatch) {
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();

		// render the text that should be displayed
		for (int i = 0; i < textContent.length; i++)
			MainGame.fontUpperCaseBig.draw(spriteBatch, textContent[i], textContentPosition[i].x,
					textContentPosition[i].y);

		spriteBatch.end();
	}

	@Override
	protected void dispose() {
		Controllers.removeListener(controllerHelperMenu);
		backgroundMusic.dispose();
	}

	private void goBack() {
		gameStateManager.setGameState(new MenuState(gameStateManager));
	}

	@Override
	public void backCallback() {
		goBack();
	}

	@Override
	public void selectCallback(final int buttonId) {
		if (controllerTimeHelper <= 0.2)
			return;
		// go back to the menu
		if (buttonId == ControllerWiki.BUTTON_A || buttonId == ControllerWiki.BUTTON_B)
			goBack();
		// toggle full screen
		if (buttonId == ControllerWiki.BUTTON_START)
			GameStateMethods.toggleFullScreen();
	}

	@Override
	public void dPadCallback(final PovDirection direction) {
		// Nothing to do
	}

	@Override
	public void stickMoved(final boolean xAxis, final float value) {
		// Nothing to do
	}

}
