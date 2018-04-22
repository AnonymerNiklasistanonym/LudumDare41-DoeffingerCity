package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.state.MenuState;

public class MainGame extends ApplicationAdapter {
	
	/**
	 * Normal/Big game font "Cornerstone"
	 */
	public static BitmapFont font;
	/**
	 * Small game font "Cornerstone"
	 */
	public static BitmapFont smallFont;
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
	
	public SpriteBatch spriteBatch;
	private GameStateManager gameStateManager;

	@Override
	public void create() {
		font = new BitmapFont(Gdx.files.internal("fonts/font_cornerstone.fnt"));
		smallFont = new BitmapFont(Gdx.files.internal("fonts/font_cornerstone_small.fnt"));

		spriteBatch = new SpriteBatch();
		gameStateManager = new GameStateManager();
		gameStateManager.pushState(new MenuState(gameStateManager));
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
	}

	@Override
	public void render() {
		// wipes the screen clear
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// update state (deltaTime gives the time between render/ed times/frames)
		gameStateManager.update(Gdx.graphics.getDeltaTime());
		// render state
		gameStateManager.render(spriteBatch);
	}
}
