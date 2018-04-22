package com.mygdx.game.objects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Enemy;
import com.mygdx.game.gamestate.state.PlayState;

public abstract class Tower {

	protected float turnspeed;
	protected float maxHealth;
	protected float damage;
	protected float speed;
	protected float power;
	protected float range;
	protected float firingSpriteTime=0.3f;
	protected Vector2 center;
	protected Sprite amunition;
	protected Animation<TextureRegion> destroyAnimation;
	protected Sprite spriteBody;
	protected Sprite spriteUpperBody;
	protected Sprite spriteFiring;
	protected float timesincelastshot;
	boolean healthBar;
	Sound soundShoot;
	Enemy target = null;
	Array<Enemy> enemies;

	public void draw(final SpriteBatch spriteBatch) {
		spriteBody.draw(spriteBatch);
		if(firingSpriteTime>timesincelastshot)
			spriteFiring.draw(spriteBatch);
		else
			spriteUpperBody.draw(spriteBatch);
		if (healthBar)
			drawHealthBar();
	}

	protected Tower(final float xPosition, final float yPosition, final Texture spriteBody,
			final Texture spriteUpperBody, final Texture spriteFiring, Array<Enemy> enemies, final Sound soundShoot) {
		this.timesincelastshot = 10;
		this.enemies = enemies;
		this.soundShoot = soundShoot;
		
		this.spriteBody = new Sprite(spriteBody);
		this.spriteBody.setSize(spriteBody.getWidth() * PlayState.PIXEL_TO_METER,
				spriteBody.getHeight() * PlayState.PIXEL_TO_METER);
		this.spriteBody.setOriginCenter();
		
		this.spriteUpperBody = new Sprite(spriteUpperBody);
		this.spriteUpperBody.setSize(spriteUpperBody.getWidth() * PlayState.PIXEL_TO_METER,
				spriteUpperBody.getHeight() * PlayState.PIXEL_TO_METER);
		this.spriteUpperBody.setOriginCenter();
		
		this.spriteFiring = new Sprite(spriteFiring);
		this.spriteFiring.setSize(spriteFiring.getWidth() * PlayState.PIXEL_TO_METER,
				spriteFiring.getHeight() * PlayState.PIXEL_TO_METER);
		this.spriteFiring.setOriginCenter();
		
		final float middleOfSpriteBody = spriteBody.getWidth() / 2 * PlayState.PIXEL_TO_METER;
		final float widthOfUpperBody = spriteUpperBody.getHeight() / 2 * PlayState.PIXEL_TO_METER;
		final float widthOfFiringBody = spriteFiring.getHeight() / 2 * PlayState.PIXEL_TO_METER;
		center = new Vector2(xPosition + middleOfSpriteBody, yPosition + middleOfSpriteBody);
		this.spriteBody.setPosition(xPosition, yPosition);
		this.spriteUpperBody.setPosition(xPosition + middleOfSpriteBody - widthOfUpperBody,
				yPosition + middleOfSpriteBody - widthOfUpperBody);
		this.spriteFiring.setPosition(xPosition + middleOfSpriteBody - widthOfFiringBody,
				yPosition + middleOfSpriteBody - widthOfFiringBody);
		this.healthBar = false;
		this.damage = 0;
	}

	public void tryshoot(Enemy e) {
		if (getAngleToEnemy(e) > getDegrees()) {
			setDegrees(getDegrees() + turnspeed);
		} else {
			setDegrees(getDegrees() - turnspeed);
		}
		if (Math.abs(getDegrees() - getAngleToEnemy(e)) < 1) {
			if (timesincelastshot > speed)
				shoot(e);
		}

		if (e.tot)
			target = null;

	}

	public void shoot(Enemy e) {
		e.takeDamage(power);
		soundShoot.play();
		timesincelastshot = 0;
		
	}

	public float getAngleToEnemy(Enemy e) {
		float angle = 0;
		Vector2 epos = new Vector2(center.x, center.y);
		Vector2 tpos = new Vector2(e.getBodyX(), e.getBodyY());

		angle = center.angle(epos);
		angle = (float) ((Math.atan2(epos.x - tpos.x, -(epos.y - tpos.y)) * 180.0d / Math.PI));
		return angle;

	}

	public float getDegrees() {
		return this.spriteUpperBody.getRotation();
	}

	public void setDegrees(float degrees) {
		this.spriteUpperBody.setRotation(degrees);
		this.spriteFiring.setRotation(degrees);
	}

	public void rotate(float degrees) {
		spriteUpperBody.rotate(degrees);
		spriteFiring.rotate(degrees);
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

	public void update(float delta) {
		timesincelastshot = timesincelastshot + delta;
		if (target == null)
			selectNewTarget();
		else
			tryshoot(target);
	}

	private void selectNewTarget() {

		Enemy best = null;
		for (Enemy e : enemies) {
			if (best == null)
				if (e.tot == false)
					if (isTargetInRange(e))
						best = e;
			if (best != null)
				if (e.getScore() > best.getScore() && e.tot == false)
					if (isTargetInRange(e))
						best = e;
		}
		target = best;
	}

	private boolean isTargetInRange(Enemy e) {
		Vector2 epos = new Vector2(e.getBodyX(), e.getBodyY());
		Vector2 tpos = new Vector2(center.x, center.y);
		float dist = epos.dst(tpos);
		boolean inrange = false;
		if (dist < range)
			inrange = true;
		return inrange;
	}

	public float getX() {
		return spriteBody.getX();
	}

	public float getY() {
		return spriteBody.getY();
	}

	public float getRange() {
		return range;
	}

	public Sprite getSpriteBody() {
		return spriteBody;
	}

	public Vector2 getCenter() {
		return center;
	}

	public void setCenter(Vector2 center) {
		this.center = center;
	}

}
