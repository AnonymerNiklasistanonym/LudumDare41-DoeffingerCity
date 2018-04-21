package com.mygdx.game.gamestate;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.MainGame;

public abstract class GameState {

	/**
	 * Game screen camera
	 */
	protected final OrthographicCamera camera;
	/**
	 * Game state manager
	 */
	private final GameStateManager gameStateManager;
	
	/**
	 * Constructor
	 * @param gameStateManager
	 */
	protected GameState(final GameStateManager gameStateManager) {
		this.gameStateManager = gameStateManager;
		this.camera = new OrthographicCamera();
	}

	/**
	 * Handle input
	 */
	protected abstract void handleInput();

	/**
	 * Update everything to the current frame
	 * 
	 * @param deltaTime
	 */
	protected abstract void update(float deltaTime);

	/**
	 * Render method
	 * 
	 * @param spriteBatch
	 *            (contains everything that needs to be drawn)
	 */
	protected abstract void render(SpriteBatch spriteBatch);

	/**
	 * Dispose any resource for a better memory management
	 */
	protected abstract void dispose();

}
