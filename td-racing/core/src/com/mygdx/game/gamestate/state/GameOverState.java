package com.mygdx.game.gamestate.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;

public class GameOverState extends GameState {

	private final Texture BACKGROUND;
	private final Texture PLAYBUTTON;

	public GameOverState(final GameStateManager gameStateManager) {
		super(gameStateManager);
		
		BACKGROUND = new Texture("background.png");
		PLAYBUTTON = new Texture("button/start.png");
		
		// paint text

		camera.setToOrtho(false, MainGame.GAME_WIDTH / 2, MainGame.GAME_HEIGHT / 2);

		System.out.println("Game over state entered");
	}

	@Override
	protected void handleInput() {
		
		if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			System.out.println("Do something");
			gameStateManager.setGameState(new PlayState(gameStateManager));
		}

		if (Gdx.input.isCatchBackKey() || Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			dispose();
			Gdx.app.exit();
		}

	}

	@Override
	public void update(float number) {
		handleInput();

	}

	@Override
	public void render(SpriteBatch spriteBatch) {

		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		spriteBatch.draw(BACKGROUND, 0, 0);
		spriteBatch.draw(PLAYBUTTON, camera.position.x - PLAYBUTTON.getWidth() / 2, camera.position.y / 2);
		spriteBatch.end();

	}

	@Override
	public void dispose() {
		BACKGROUND.dispose();
		PLAYBUTTON.dispose();
		System.out.println("Game over  state disposed");
	}

}

