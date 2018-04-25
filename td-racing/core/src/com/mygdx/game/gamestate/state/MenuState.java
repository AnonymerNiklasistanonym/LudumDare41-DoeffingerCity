package com.mygdx.game.gamestate.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.GameStateMethods;
import com.mygdx.game.menu.MenuButton;
import com.mygdx.game.menu.MenuButtonBig;
import com.mygdx.game.menu.MenuButtonSmall;

public class MenuState extends GameState {

	private final MenuButton[] menuButtons;

	private final Texture backgroundStars;
	private final Texture title;

	private final static int START_ID = 0;
	private final static int HIGHSCORES_ID = 1;
	private final static int ABOUT_ID = 2;

	private final Vector3 touchPos;

	public MenuState(GameStateManager gameStateManager) {
		super(gameStateManager);

		MenuButtonBig.textureActive = new Texture(Gdx.files.internal("buttons/button_menu_active.png"));
		MenuButtonBig.textureNotActive = new Texture(Gdx.files.internal("buttons/button_menu_not_active.png"));
		MenuButtonSmall.textureActive = new Texture(Gdx.files.internal("buttons/button_menu_active_small.png"));
		MenuButtonSmall.textureNotActive = new Texture(Gdx.files.internal("buttons/button_menu_not_active_small.png"));
		backgroundStars = new Texture(Gdx.files.internal("background/background_stars.png"));
		title = new Texture(Gdx.files.internal("buttons/titel.png"));

		touchPos = new Vector3();

		camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

		menuButtons = new MenuButton[] {
				new MenuButtonBig(START_ID, MainGame.GAME_WIDTH / 2, MainGame.GAME_HEIGHT / 6 * 2.8f, "START", true),
				new MenuButtonSmall(ABOUT_ID, MainGame.GAME_WIDTH / 4, MainGame.GAME_HEIGHT / 6 * 1, "ABOUT"),
				new MenuButtonSmall(HIGHSCORES_ID, MainGame.GAME_WIDTH / 2 + MainGame.GAME_WIDTH / 4,
						MainGame.GAME_HEIGHT / 6 * 1, "HIGHSCORES") };
	}

	@Override
	public void handleInput() {
		GameStateMethods.toggleFullScreen(Keys.F11);

		// map touch position to the camera resolution
		touchPos.set(GameStateMethods.getMousePosition(camera));

		// determine on which button the mouse cursor is and select this button
		boolean oneIsSelected = false;
		for (final MenuButton menuButton : menuButtons) {
			if (menuButton.contains(touchPos))
				oneIsSelected = true;
		}
		if (oneIsSelected) {
			for (final MenuButton menuButton : menuButtons)
				menuButton.setActive(menuButton.contains(touchPos));
		}

		// If a button is touched do something or Space or Enter is pressed execute the
		// action for the selected button
		if (Gdx.input.justTouched()
				|| (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE))) {
			for (final MenuButton menuButton : menuButtons) {
				if (menuButton.isActive()) {
					switch (menuButton.getId()) {
					case START_ID:
						gameStateManager.setGameState(new LoadingState(gameStateManager, MainGame.level));
						break;
					case HIGHSCORES_ID:
						gameStateManager.setGameState(new HighscoreState(gameStateManager));
						break;
					case ABOUT_ID:
						gameStateManager.setGameState(new CreditState(gameStateManager));
						break;
					}
				}
			}
		}

		// if escape or back is pressed quit
		if (Gdx.input.isCatchBackKey() || Gdx.input.isKeyJustPressed(Keys.ESCAPE))
			Gdx.app.exit();
	}

	@Override
	public void update(final float deltaTime) {
		// Nothing to update
	}

	@Override
	public void render(final SpriteBatch spriteBatch) {
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();

		spriteBatch.draw(backgroundStars, 0, 0);
		for (final MenuButton menuButton : menuButtons)
			menuButton.draw(spriteBatch);

		spriteBatch.draw(title, 0, 0);
		spriteBatch.end();
	}

	@Override
	public void dispose() {
		backgroundStars.dispose();
		title.dispose();
		MenuButtonBig.textureActive.dispose();
		MenuButtonBig.textureNotActive.dispose();
		MenuButtonSmall.textureActive.dispose();
		MenuButtonSmall.textureNotActive.dispose();
	}

}
