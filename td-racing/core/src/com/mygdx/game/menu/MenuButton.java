package com.mygdx.game.menu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MainGame;

public class MenuButton {

	public static Texture textureActive;
	public static Texture textureNotActive;

	private boolean activated;

	private final Sprite button;
	private final String content;
	private final int id;
	private final float fontX, fontY;

	public MenuButton(final int id, final float xPosition, final float yPosition, final String content,
			final boolean activated) {
		this.activated = activated;
		this.content = content;
		this.id = id;
		this.button = new Sprite(this.activated ? textureActive : textureNotActive);
		this.button.setSize(textureActive.getWidth(), textureActive.getHeight());
		this.button.setPosition(xPosition - this.button.getWidth() / 2, yPosition - this.button.getHeight() / 2);

		final GlyphLayout layout = new GlyphLayout(MainGame.fontBig, this.content);
		this.fontX = xPosition - layout.width / 2;
		this.fontY = yPosition + layout.height / 2;
	}

	public void setActive(final boolean activated) {
		this.activated = activated;
		this.button.setTexture(this.activated ? textureActive : textureNotActive);
	}

	public boolean isActive() {
		return this.activated;
	}

	public void draw(final SpriteBatch spriteBatch) {
		this.button.draw(spriteBatch);
		MainGame.fontBig.draw(spriteBatch, this.content, this.fontX, this.fontY);
	}

	public boolean contains(final Vector3 touchPos) {
		return (touchPos.x > this.button.getX() && touchPos.x < this.button.getX() + this.button.getWidth())
				&& (touchPos.y > this.button.getY() && touchPos.y < this.button.getY() + this.button.getHeight());
	}

	public int getId() {
		return this.id;
	}

}
