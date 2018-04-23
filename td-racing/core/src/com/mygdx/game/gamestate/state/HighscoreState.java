package com.mygdx.game.gamestate.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.menu.HighscoreButton;

public class HighscoreState extends GameState {
	
	private HighscoreButton[] highscoreButtons;

	public HighscoreState(GameStateManager gameStateManager) {
		super(gameStateManager);
		
		HighscoreButton.texture = new Texture(Gdx.files.internal("buttons/button_highscore.png"));
		
		camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

		
		// TODO
		// get scores
		final HighscoreButton buttonOne = new HighscoreButton(1, 100, "Bestguy", MainGame.GAME_WIDTH / 2,
				MainGame.GAME_HEIGHT / 6 * 5);
		final HighscoreButton buttonTwo = new HighscoreButton(2, 100, "Bestguy", MainGame.GAME_WIDTH / 2,
				MainGame.GAME_HEIGHT / 6 * 4);
		final HighscoreButton buttonThree = new HighscoreButton(3, 100, "Bestguy", MainGame.GAME_WIDTH / 2,
				MainGame.GAME_HEIGHT / 6 * 3);
		final HighscoreButton buttonFour = new HighscoreButton(4, 100, "Bestguy", MainGame.GAME_WIDTH / 2,
				MainGame.GAME_HEIGHT / 6 * 2);
		final HighscoreButton buttonFiveOne = new HighscoreButton(5, 100, "Bestguy", MainGame.GAME_WIDTH / 2,
				MainGame.GAME_HEIGHT / 6 * 1);
		this.highscoreButtons = new HighscoreButton[] {buttonOne, buttonTwo, buttonThree, buttonFour, buttonFiveOne};
	}

	@Override
	protected void handleInput() {
		// TODO Do nothing right now
		if (Gdx.input.justTouched() || (Gdx.input.isKeyJustPressed(Keys.ESCAPE) || Gdx.input.isCatchBackKey())) {
			gameStateManager.setGameState(new MenuState(gameStateManager));
		}
		
	}

	@Override
	protected void update(float deltaTime) {
		// TODO Do nothing right now
		
	}

	@Override
	protected void render(SpriteBatch spriteBatch) {
		spriteBatch.begin();
		for (final HighscoreButton highscoreButton : highscoreButtons)
			highscoreButton.draw(spriteBatch);
		spriteBatch.end();
	}

	@Override
	protected void dispose() {
		// TODO dispose static button
		
	}

}
