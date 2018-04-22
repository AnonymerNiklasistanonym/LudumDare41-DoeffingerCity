package com.mygdx.game.gamestate.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.menu.MenuButton;

public class MenuState extends GameState {
	
	private MenuButton startButton;
	private MenuButton aboutButton;


	public MenuState(GameStateManager gameStateManager) {
		super(gameStateManager);
		
		//BACKGROUND = new Texture(Gdx.files.internal("background.png"));
		MenuButton.textureActive = new Texture(Gdx.files.internal("buttons/button_active.png"));
		MenuButton.textureNotActive = new Texture(Gdx.files.internal("buttons/button_not_active.png"));

		camera.setToOrtho(false, MainGame.GAME_WIDTH / 2, MainGame.GAME_HEIGHT / 2);
		
		startButton = new MenuButton(MainGame.GAME_WIDTH / 2, MainGame.GAME_HEIGHT / 6 * 2, "START", true);
		aboutButton = new MenuButton(MainGame.GAME_WIDTH / 2, MainGame.GAME_HEIGHT / 6 * 4, "ABOUT", false);

		System.out.println("Menu state entered");
	}

	@Override
	public void handleInput() {

		// Check if somehow the screen was touched
		if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			gameStateManager.setGameState(new PlayState(gameStateManager));
		}

		if (Gdx.input.isCatchBackKey() || Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
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
		// spriteBatch.draw(BACKGROUND, 0, 0);
		spriteBatch.draw(PLAYBUTTON, camera.position.x - PLAYBUTTON.getWidth() / 4, camera.position.y, PLAYBUTTON.getWidth() / 2, PLAYBUTTON.getHeight() / 2);
		spriteBatch.end();

	}

	@Override
	public void dispose() {
		// BACKGROUND.dispose();
		PLAYBUTTON.dispose();
		System.out.println("Menu state disposed");
	}

}

