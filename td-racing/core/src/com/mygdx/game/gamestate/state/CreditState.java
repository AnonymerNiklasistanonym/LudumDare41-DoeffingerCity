package com.mygdx.game.gamestate.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;

public class CreditState extends GameState {

	private final String[] contentToDisplay;
	private final Vector2[] contentPositions;

	public CreditState(GameStateManager gameStateManager) {
		super(gameStateManager);
		camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

		contentToDisplay = new String[] { "", "This game was made by", "Daniel Czeppel", "Niklas Mikeler",
				"Patrick Ulmer", "", "Music by Sascha Czeppel", "", };

		final Vector2[] testVector = new Vector2[contentToDisplay.length];
		for (int i = 0; i < contentToDisplay.length; i++) {
			final GlyphLayout temp = new GlyphLayout(MainGame.highscoreFont, contentToDisplay[i]);
			testVector[i] = new Vector2(MainGame.GAME_WIDTH / 2 - temp.width / 2,
					(MainGame.GAME_HEIGHT / contentToDisplay.length) * (contentToDisplay.length - i) + temp.height / 2);
		}
		contentPositions = testVector;
	}

	@Override
	protected void handleInput() {
		if (Gdx.input.justTouched() || (Gdx.input.isKeyJustPressed(Keys.ESCAPE) || Gdx.input.isCatchBackKey()))
			gameStateManager.setGameState(new MenuState(gameStateManager));

		if (Gdx.input.isKeyJustPressed(Keys.F11)) {
			if (Gdx.graphics.isFullscreen())
				Gdx.graphics.setWindowedMode(MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);
			else
				Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		}
	}

	@Override
	protected void update(float deltaTime) {
	}

	@Override
	protected void render(SpriteBatch spriteBatch) {
		spriteBatch.begin();
		for (int i = 0; i < contentPositions.length; i++)
			MainGame.highscoreFont.draw(spriteBatch, contentToDisplay[i], contentPositions[i].x, contentPositions[i].y);
		spriteBatch.end();
	}

	@Override
	protected void dispose() {
		// TODO dispose static button

	}

}
