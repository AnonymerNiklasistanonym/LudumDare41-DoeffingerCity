package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.state.PlayState;

public class MainGame extends ApplicationAdapter {
	public SpriteBatch batch;
	public static BitmapFont font;

	/**
	 * Name of the game
	 */
	public final static String GAME_NAME = "td-racing";
	/**
	 * Width of the game screen (the window)
	 */
	public final static int GAME_WIDTH = 1280;
	/**
	 * Height of the game screen (the window)
	 */
	public final static int GAME_HEIGHT = 720;

	private GameStateManager gameStateManager;

	@Override
	public void create() {
		batch = new SpriteBatch();
		gameStateManager = new GameStateManager();
		gameStateManager.pushState(new PlayState(gameStateManager));
		font = new BitmapFont(Gdx.files.internal("fonts/Cornerstone.fnt"));
	}

	@Override
	public void render() {
		// wipes the screen clear
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// update state ( deltaTime gives the delta between render times)
		gameStateManager.update(Gdx.graphics.getDeltaTime());
		// render state
		gameStateManager.render(batch);
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}
