package com.mygdx.game.gamestate.states.resources;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.GameStateMethods;

public class HighscoreCharacterButton {

	private char content;

	private static final float SCALE = 1f;
	private final Vector2 position;
	private final float x1, x2, x3, y1up, y1down, y23up, y23down;

	private boolean activated;

	public HighscoreCharacterButton(final char content, final int width) {
		this.content = content;
		MainGame.fontBig.getData().setScale(SCALE);
		this.position = GameStateMethods.calculateCenteredTextPositon(MainGame.fontBig, "" + this.content, width,
				MainGame.GAME_HEIGHT);

		this.x1 = this.position.x + 20f;
		this.x2 = x1 - 35f;
		this.x3 = x1 + 35f;
		this.y1up = this.position.y + 70f;
		this.y1down = this.position.y - 150f;
		this.y23up = y1up - 20f;
		this.y23down = y1down + 20f;

		this.activated = false;
	}

	public void activate(final boolean b) {
		this.activated = b;
	}

	public char getCurrentCharacter() {
		return this.content;
	}

	public void setNewCharacter(final char character) {
		this.content = character;
	}

	public void draw(final SpriteBatch spriteBatch) {
		MainGame.fontBig.getData().setScale(SCALE);
		MainGame.fontBig.draw(spriteBatch, "" + this.content, this.position.x, this.position.y);
	}

	public void drawTriangels(ShapeRenderer shapeRenderer) {

		if (activated) {
			shapeRenderer.triangle(x1, y1up, x2, y23up, x3, y23up);
			shapeRenderer.triangle(x1, y1down, x2, y23down, x3, y23down);
		}
	}

}
