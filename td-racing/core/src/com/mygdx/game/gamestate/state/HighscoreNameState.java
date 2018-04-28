package com.mygdx.game.gamestate.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MainGame;
import com.mygdx.game.PreferencesManager;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.GameStateManager;
import com.mygdx.game.gamestate.GameStateMethods;
import com.mygdx.game.menu.HighscoreCharacterButton;

public class HighscoreNameState extends GameState {

	private static final String STATE_NAME = "Highscore > Name";

	private final HighscoreCharacterButton[] highscoreCharacterButtons;
	private final ShapeRenderer shapeRenderer;
	private final PreferencesManager preferencesManager;
	private final String highscoreText, scoreText;
	private final Vector2 highscoreTextPosition, scoreTextPosition;
	private final int score;

	private int currentIndex = 0;

	public HighscoreNameState(GameStateManager gameStateManager, final int score) {
		super(gameStateManager, STATE_NAME);

		this.score = score;

		shapeRenderer = new ShapeRenderer();
		preferencesManager = new PreferencesManager();

		camera.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);

		highscoreCharacterButtons = new HighscoreCharacterButton[6];
		char startChar = 'A';
		for (int i = 0; i < highscoreCharacterButtons.length; i++) {
			highscoreCharacterButtons[i] = new HighscoreCharacterButton(startChar++,
					MainGame.GAME_WIDTH / 8 * ((i + 1) * 2 + 1));
		}

		highscoreCharacterButtons[currentIndex].activate(true);

		MainGame.fontUpperCaseBig.getData().setScale(0.65f);
		MainGame.fontUpperCaseBig.setUseIntegerPositions(false);
		this.highscoreText = "YOU REACHED THE TOP 10!";
		this.highscoreTextPosition = GameStateMethods.calculateCenteredTextPositon(MainGame.fontUpperCaseBig,
				highscoreText, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT / 3 * 5);
		this.scoreText = "" + score;
		this.scoreTextPosition = GameStateMethods.calculateCenteredTextPositon(MainGame.fontUpperCaseBig, scoreText,
				MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT / 3);

		final char[] name = preferencesManager.getName();
		if (name != null && name.length == highscoreCharacterButtons.length) {
			for (int i = 0; i < highscoreCharacterButtons.length; i++)
				highscoreCharacterButtons[i].setNewCharacter(name[i]);
		}

	}

	@Override
	protected void handleInput() {
		GameStateMethods.toggleFullScreen(Keys.F11);

		if (Gdx.input.isKeyJustPressed(Keys.LEFT))
			currentIndex = (currentIndex - 1 < 0) ? highscoreCharacterButtons.length - 1 : currentIndex - 1;
		if (Gdx.input.isKeyJustPressed(Keys.RIGHT))
			currentIndex = (currentIndex + 1 == highscoreCharacterButtons.length) ? 0 : currentIndex + 1;

		if (Gdx.input.isKeyJustPressed(Keys.LEFT) || Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
			for (final HighscoreCharacterButton highscoreCharacterButton : highscoreCharacterButtons)
				highscoreCharacterButton.activate(false);
			highscoreCharacterButtons[currentIndex].activate(true);
		}

		if (Gdx.input.isKeyJustPressed(Keys.UP)) {
			final char currentChar = highscoreCharacterButtons[currentIndex].getCurrentCharacter();
			highscoreCharacterButtons[currentIndex]
					.setNewCharacter((currentChar + 1 > 'Z') ? 'A' : (char) (currentChar + 1));
		}
		if (Gdx.input.isKeyJustPressed(Keys.DOWN)) {
			final char currentChar = highscoreCharacterButtons[currentIndex].getCurrentCharacter();
			highscoreCharacterButtons[currentIndex]
					.setNewCharacter((currentChar - 1 < 'A') ? 'Z' : (char) (currentChar - 1));
		}

		if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
			String name = "";
			for (final HighscoreCharacterButton highscoreCharacterButton : highscoreCharacterButtons)
				name += highscoreCharacterButton.getCurrentCharacter();
			preferencesManager.saveHighscore(name, this.score);
			gameStateManager.setGameState(new HighscoreListState(gameStateManager));
		}

		if (Gdx.input.justTouched() || (Gdx.input.isKeyJustPressed(Keys.ESCAPE) || Gdx.input.isCatchBackKey()))
			gameStateManager.setGameState(new MenuState(gameStateManager));
	}

	@Override
	protected void update(final float deltaTime) {
		// Nothing to update
	}

	@Override
	protected void render(final SpriteBatch spriteBatch) {
		spriteBatch.setProjectionMatrix(this.camera.combined);
		spriteBatch.begin();
		MainGame.fontUpperCaseBig.draw(spriteBatch, highscoreText, highscoreTextPosition.x, highscoreTextPosition.y);
		MainGame.fontUpperCaseBig.draw(spriteBatch, scoreText, scoreTextPosition.x, scoreTextPosition.y);
		for (final HighscoreCharacterButton highscoreCharacterButton : highscoreCharacterButtons)
			highscoreCharacterButton.draw(spriteBatch);
		spriteBatch.end();
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(1, 1, 1, 1);
		for (final HighscoreCharacterButton highscoreCharacterButton : highscoreCharacterButtons)
			highscoreCharacterButton.drawTriangels(shapeRenderer);
		shapeRenderer.end();
	}

	@Override
	protected void dispose() {
		// Nothing to dispose
	}

}
