package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.state.MenuState;

public class MainGame extends ApplicationAdapter {

	public static int level = 1;

	/**
	 * Normal/Big game font "Cornerstone"
	 */
	public static BitmapFont font;
	/**
	 * Small game font "Cornerstone"
	 */
	public static BitmapFont smallFont;
	/**
	 * Small game font "Cornerstone"
	 */
	public static BitmapFont fontBig;
	/**
	 * Highscore/Credits size game font "Cornerstone"
	 */
	public static BitmapFont highscoreFont;
	/**
	 * Wave size game font "Cornerstone"
	 */
	public static BitmapFont waveFont;
	public static BitmapFont fontUpperCaseBig;
	/**
	 * Name of the game
	 */
	public final static String GAME_NAME = "LudumDare 41 - TnT (Track `n Towers)";
	/**
	 * Width of the game screen (the window)
	 */
	public final static int GAME_WIDTH = 1280;
	/**
	 * Height of the game screen (the window)
	 */
	public final static int GAME_HEIGHT = 720;

	private SpriteBatch spriteBatch;
	private GameStateManager gameStateManager;

	@Override
	public void create() {
		fontUpperCaseBig = new BitmapFont(Gdx.files.internal("fonts/font_cornerstone_upper_case_big.fnt"));

		font = new BitmapFont(Gdx.files.internal("fonts/font_cornerstone.fnt"));
		font.getRegion().getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		font.setUseIntegerPositions(false);
		smallFont = new BitmapFont(Gdx.files.internal("fonts/font_cornerstone_small.fnt"));
		highscoreFont = new BitmapFont(Gdx.files.internal("fonts/font_cornerstone_highscore.fnt"));
		fontBig = new BitmapFont(Gdx.files.internal("fonts/font_cornerstone_big.fnt"));
		waveFont = new BitmapFont(Gdx.files.internal("fonts/font_cornerstone_wave.fnt"));
		fontUpperCaseBig = new BitmapFont(Gdx.files.internal("fonts/font_cornerstone_upper_case_big.fnt"));
		spriteBatch = new SpriteBatch();
		gameStateManager = new GameStateManager();
		gameStateManager.pushState(new MenuState(gameStateManager));
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		fontUpperCaseBig.dispose();
		waveFont.dispose();
		font.dispose();
		smallFont.dispose();
		highscoreFont.dispose();
		fontBig.dispose();
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
