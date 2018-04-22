package com.mygdx.game.gamestate;

import java.util.Stack;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameStateManager {

	private final Stack<GameState> gameStateStack;

	public GameStateManager() {
		this.gameStateStack = new Stack<GameState>();
	}

	/**
	 * Push a new state on the stack
	 * 
	 * @param gameState
	 */
	public void pushState(final GameState gameState) {
		gameStateStack.push(gameState);
	}

	/**
	 * Set instantly a new state
	 * 
	 * @param gameState
	 */
	public void setGameState(final GameState gameState) {
		this.popGameState();
		this.pushState(gameState);
	}

	/**
	 * Directly dispose state after popping/removing it
	 */
	public void popGameState() {
		gameStateStack.pop().dispose();
	}

	/**
	 * Update everything
	 * 
	 * @param deltaTime
	 *            (time between last frame and this)
	 */
	public void update(final float deltaTime) {
		gameStateStack.peek().update(deltaTime);
	}

	/**
	 * Render everything
	 * 
	 * @param spriteBatch
	 *            (contains every sprite)
	 */
	public void render(final SpriteBatch spriteBatch) {
		gameStateStack.peek().render(spriteBatch);
	}

}