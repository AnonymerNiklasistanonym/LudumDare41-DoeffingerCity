package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.states.MenuState;

public class MainGame extends ApplicationAdapter {

	/**
	 * Height of the game screen (the window)
	 */
	public final static int GAME_HEIGHT = 720;
	/**
	 * Width of the game screen (the window)
	 */
	public final static int GAME_WIDTH = 1280;
	/**
	 * Name of the game
	 */
	public final static String GAME_NAME = "Tracks `n Towers)";
	/**
	 * Returns if this is an release or development
	 */
	public static final boolean DEVELOPER_MODE = true;

	public static BitmapFont font70;
	public static BitmapFont font;
	public static BitmapFont fontBig;
	public static BitmapFont fontOutline;
	public static BitmapFont fontUpperCaseBig;
	public static int level;

	private GameStateManager gameStateManager;
	private SpriteBatch spriteBatch;

	@Override
	public void create() {
		font = new BitmapFont(Gdx.files.internal("fonts/font_cornerstone.fnt"));
		font.setUseIntegerPositions(false);
		font70 = new BitmapFont(Gdx.files.internal("fonts/font_cornerstone_70.fnt"));
		font70.setUseIntegerPositions(false);
		fontBig = new BitmapFont(Gdx.files.internal("fonts/font_cornerstone_big.fnt"));
		fontBig.setUseIntegerPositions(false);
		fontOutline = new BitmapFont(Gdx.files.internal("fonts/font_cornerstone_outline.fnt"));
		fontOutline.setUseIntegerPositions(false);
		fontUpperCaseBig = new BitmapFont(Gdx.files.internal("fonts/font_cornerstone_upper_case_big.fnt"));
		fontUpperCaseBig.setUseIntegerPositions(false);

		spriteBatch = new SpriteBatch();
		gameStateManager = new GameStateManager();

		// starting level
		level = 1;

		// start in the menu
		gameStateManager.pushState(new MenuState(gameStateManager));
	}

	@Override
	public void dispose() {
		font.dispose();
		font70.dispose();
		fontBig.dispose();
		fontUpperCaseBig.dispose();
		spriteBatch.dispose();
	}

	@Override
	public void render() {
		// wipes the screen clear with a black color
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// update state (deltaTime gives the time between render times)
		gameStateManager.update(Gdx.graphics.getDeltaTime());
		// render the current state
		gameStateManager.render(spriteBatch);
	}
}
