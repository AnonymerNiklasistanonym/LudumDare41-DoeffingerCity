package com.mygdx.game.gamestate.states.resources;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.GameStateMethods;

public abstract class MenuButton implements Disposable {

	private final Texture textureActive;
	private final Texture textureNotActive;

	private boolean activated;

	private final Sprite button;
	private final String content;
	private final int id;
	private final Vector2 position;
	private final float scale;

	public MenuButton(final int id, final float xPosition, final float yPosition, final String content,
			final Texture textureActive, final Texture textureNotActive, final float scale, final boolean activated) {
		this.activated = activated;
		this.content = content;
		this.id = id;
		this.button = new Sprite(this.activated ? textureActive : textureNotActive);
		this.button.setSize(textureActive.getWidth(), textureActive.getHeight());
		this.button.setPosition(xPosition - this.button.getWidth() / 2, yPosition - this.button.getHeight() / 2);
		this.textureActive = textureActive;
		this.textureNotActive = textureNotActive;
		this.scale = scale;

		MainGame.fontBig.getData().setScale(this.scale);
		this.position = GameStateMethods.calculateCenteredTextPositon(MainGame.fontBig, this.content, xPosition * 2,
				yPosition * 2);
	}

	public MenuButton(final int id, final float xPosition, final float yPosition, final String content,
			final Texture textureActive, final Texture textureNotActive, final float scale) {
		this(id, xPosition, yPosition, content, textureActive, textureNotActive, scale, false);
	}

	public void setActive(final boolean activated) {
		this.activated = activated;
		this.button.setTexture(this.activated ? this.textureActive : this.textureNotActive);
	}

	public boolean isActive() {
		return this.activated;
	}

	public void draw(final SpriteBatch spriteBatch) {
		this.button.draw(spriteBatch);
		MainGame.fontBig.getData().setScale(this.scale);
		MainGame.fontBig.draw(spriteBatch, this.content, this.position.x, this.position.y);
	}

	public boolean contains(final Vector3 touchPos) {
		return (touchPos.x > this.button.getX() && touchPos.x < this.button.getX() + this.button.getWidth())
				&& (touchPos.y > this.button.getY() && touchPos.y < this.button.getY() + this.button.getHeight());
	}

	public int getId() {
		return this.id;
	}

	public void disposeMedia() {
		this.button.getTexture().dispose();
		this.textureActive.dispose();
		this.textureNotActive.dispose();
	}

}
