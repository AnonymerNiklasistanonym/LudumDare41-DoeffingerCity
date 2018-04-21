package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Enemy;
import com.mygdx.game.gamestate.state.PlayState;

public abstract class Tower {

	protected float maxHealth;
	protected float damage;
	protected float speed;
	protected float power;
	protected float range;
	protected Sprite amunition;
	protected Animation<TextureRegion> destroyAnimation;
	protected Sprite spriteBody;
	protected Sprite spriteUpperBody;
	boolean healthBar;
	Enemy target=null;
	Array<Enemy> enemies;

	public void draw(final SpriteBatch spriteBatch) {
		spriteBody.draw(spriteBatch);
		spriteUpperBody.draw(spriteBatch);
		if (healthBar)
			drawHealthBar();
	}

	protected Tower(final float xPosition, final float yPosition, final Texture spriteBody, final Texture spriteUpperBody, Array<Enemy> enemies) {
		this.enemies=enemies;
		this.spriteBody = new Sprite(spriteBody);
		this.spriteUpperBody = new Sprite(spriteUpperBody);
		this.spriteBody.setSize(spriteBody.getWidth() * PlayState.PIXEL_TO_METER, spriteBody.getHeight() * PlayState.PIXEL_TO_METER);
		this.spriteBody.setOriginCenter();
		this.spriteUpperBody.setSize(spriteUpperBody.getWidth() * PlayState.PIXEL_TO_METER, spriteUpperBody.getHeight() * PlayState.PIXEL_TO_METER);
		this.spriteUpperBody.setOriginCenter();
		final float middleOfSpriteBody = spriteBody.getWidth() / 2*PlayState.PIXEL_TO_METER;
		final float widthOfUpperBody = spriteUpperBody.getHeight() / 2*PlayState.PIXEL_TO_METER;
		this.spriteBody.setPosition(xPosition, yPosition);
		this.spriteUpperBody.setPosition(xPosition +middleOfSpriteBody- widthOfUpperBody, yPosition +middleOfSpriteBody- widthOfUpperBody);
		// this.spriteUpperBody.setPosition(xPosition, yPosition);
		
		this.healthBar = false;
		this.damage = 0;
	}

	public void shoot(Enemy enemy) {
		
		enemy.takeDamage(20);
		if(enemy.tot)
			target=null;
		
	}
	
	public float getDegrees() {
		return this.spriteUpperBody.getRotation();
	}
	
	public void setDegrees(float degrees) {
		this.spriteUpperBody.setRotation(degrees);
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
	
	public void update() {
		if(target==null)
			selectNewTarget();
		else
			shoot(target);
	}

	private void selectNewTarget() {
		
		Enemy best=null;
		for (Enemy e : enemies) {
			if(best==null&&e.tot==false)
				best=e;
			else
				if(best!=null)
			if(e.getScore()>best.getScore()&&e.tot==false)
				if(isTargetInRange(e))
				best=e;
		}
		target=best;
	}

	private boolean isTargetInRange(Enemy e) {
		Vector2 epos=new Vector2(e.getBodyX(),e.getBodyY());
		Vector2 tpos=new Vector2(spriteBody.getX()*PlayState.PIXEL_TO_METER,spriteBody.getY()*PlayState.PIXEL_TO_METER);
		float dist=epos.dst(tpos);
		return dist<range;
	}
}
