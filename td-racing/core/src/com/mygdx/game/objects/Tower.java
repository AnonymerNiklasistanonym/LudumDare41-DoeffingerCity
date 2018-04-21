package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.Enemy;

public abstract class Tower {

	protected float maxHealth;
	protected float damage;
	protected float speed;
	protected float power;
	protected Sprite amunition;
	protected Animation<TextureRegion> destroyAnimation;
	protected Sprite spriteBody;
	protected Sprite spriteUpperBody;
	boolean healthBar;

	public void draw(final SpriteBatch spriteBatch) {
		spriteBody.draw(spriteBatch);
		spriteUpperBody.draw(spriteBatch);
		if (healthBar)
			drawHealthBar();
	}

	protected Tower(final int xPosition, final int yPosition, final Texture spriteBody, final Texture spriteUpperBody) {
		this.spriteBody = new Sprite(spriteBody);
		this.spriteUpperBody = new Sprite(spriteUpperBody);
		final float middleOfSpriteBody = spriteBody.getWidth() / 2;
		final float widthOfUpperBody = spriteUpperBody.getHeight() / 2;
		this.spriteBody.setPosition(xPosition, yPosition);
		this.spriteUpperBody.setPosition(xPosition + middleOfSpriteBody - widthOfUpperBody, yPosition + middleOfSpriteBody - widthOfUpperBody);
		this.healthBar = false;
		this.damage = 0;
	}

	public void shoot(Enemy enemy) {
		// TODO
	}

	public void rotate(float degrees) {
		spriteUpperBody.rotate(degrees);
	}

	public void destroyAnimation() {
		// TODO
		System.out.println("Destroyed");
	}

	public void dispose() {
		spriteBody.getTexture().dispose();
		spriteUpperBody.getTexture().dispose();
	}

	public void activateHealthBar(boolean activate) {
		healthBar = activate;
	}

	private void drawHealthBar() {
		// TODO
	}

	public void takeDamage(float amount) {
		damage += amount;
		if (damage >= maxHealth) {
			this.destroyAnimation();
		}
	}

}
