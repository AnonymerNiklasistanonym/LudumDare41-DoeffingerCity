package com.mygdx.game.gamestate.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.GameStateMethods;

public class CreditState extends GameState {

	private final String[] textContent;
	private final Vector2[] textContentPosition;

	private static final String STATE_NAME = "Credits";

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
	}

	@Override
	protected void handleInput() {
		GameStateMethods.toggleFullScreen(Keys.F11);

		// on touch or escape or back go back to the menu
		if (Gdx.input.justTouched() || (Gdx.input.isKeyJustPressed(Keys.ESCAPE) || Gdx.input.isCatchBackKey()))
			gameStateManager.setGameState(new MenuState(gameStateManager));
	}

	@Override
	protected void update(final float deltaTime) {
		// Nothing to update
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
		// Nothing to dispose
	}

}
