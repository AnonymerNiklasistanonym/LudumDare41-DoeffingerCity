package com.mygdx.game.menu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MenuButton {
	
	public static Texture textureActive;
	public static Texture textureNotActive;
	
	private Sprite button;
	private boolean activated;
	
	public MenuButton(final float xPosition, final float yPosition, final String content, final boolean activated) {
		this.activated = activated;
		this.button = new Sprite(this.activated ? textureActive : textureNotActive);
		this.button.setSize(textureActive.getWidth(), textureActive.getHeight());
	}
	
	public void setActive(final boolean activated) {
		this.activated = activated;
		this.button.setTexture(this.activated ? textureActive : textureNotActive);
	}
	
	public void draw(final SpriteBatch spriteBatch) {
		this.button.draw(spriteBatch);
	}


}
