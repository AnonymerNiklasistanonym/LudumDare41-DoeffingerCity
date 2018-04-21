package com.mygdx.game.gamestates;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class GameState {

	/**
	 * Game screen camera
	 */
	private final OrthographicCamera camera;
	/**
	 * Game state manager
	 */
	private final GameStateManager gameStateManager;
	
	/**
	 * Constructor
	 * @param gameStateManager
	 */
	public GameState(final GameStateManager gameStateManager) {
		this.gameStateManager = gameStateManager;
		this.camera = new OrthographicCamera();
	}

	/**
	 * Handle input
	 */
	public abstract void handleInput();

	/**
	 * Update everything to the current frame
	 * 
	 * @param deltaTime
	 */
	public abstract void update(float deltaTime);

	/**
	 * Render method
	 * 
	 * @param spriteBatch
	 *            (contains everything that needs to be drawn)
	 */
	public abstract void render(SpriteBatch spriteBatch);

	/**
	 * Dispose any resource for a better memory management
	 */
	public abstract void dispose();

}
