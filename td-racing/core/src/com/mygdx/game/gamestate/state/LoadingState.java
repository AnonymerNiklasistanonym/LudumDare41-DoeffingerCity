package com.mygdx.game.gamestate.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.GameStateMethods;

public class LoadingState extends GameState {

	private final Texture backgroundLoading;
	private final String loadingText;
	private final Vector2 loadingTextPosition;
	private final int level;

	private boolean changeToGame;

	public LoadingState(final GameStateManager gameStateManager, final int level) {
		super(gameStateManager);

		// set font scale to the correct size and disable to use integers for scaling
		MainGame.fontUpperCaseBig.getData().setScale(0.5f);
		MainGame.fontUpperCaseBig.setUseIntegerPositions(false);

		// load loading screen
		this.backgroundLoading = new Texture(Gdx.files.internal("background/background_loading.png"));

		// set camera to game width/height
		this.camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

		// calculate text coordinates
		this.loadingText = "LOADING";
		this.loadingTextPosition = GameStateMethods.calculateCenteredTextPositon(MainGame.fontUpperCaseBig, loadingText,
				MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

		// do not instantly go to the game but draw one frame
		this.changeToGame = false;

		// save level to load
		this.level = level;
	}

	@Override
	public void handleInput() {
		// Do nothing
	}

	@Override
	public void update(final float deltaTime) {
		if (changeToGame)
			gameStateManager.setGameState(new PlayState(gameStateManager, level));
		else
			changeToGame = true;
	}

	@Override
	public void render(final SpriteBatch spriteBatch) {
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();

		// draw loading screen
		spriteBatch.draw(backgroundLoading, 0, 0);

		// draw loading text
		MainGame.fontUpperCaseBig.draw(spriteBatch, loadingText, loadingTextPosition.x, loadingTextPosition.y);

		spriteBatch.end();
	}

	@Override
	public void dispose() {
		backgroundLoading.dispose();
	}

}