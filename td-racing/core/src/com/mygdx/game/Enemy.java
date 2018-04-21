package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

public abstract class Enemy extends BodyDef{
	Body body;
	float speed = 80;
	float health = 0;
	Texture taussehen;
	Sprite saussehen;
	Sprite stot;
	float[][] position;
	Body enemyBody;
	
	

	
	public void startMove() {
		enemyBody.applyForceToCenter(new Vector2(speed, 0), true);
	}
	
	public void endMove() {
		enemyBody.applyForceToCenter(new Vector2(speed * -1, 0), true);
	}
	
	public void steerLeft() {
		enemyBody.applyTorque(45, true);
	}

	public void steerRight() {
		enemyBody.applyTorque(45 * -1, true);
	}
	
	public abstract void die();
	
	public abstract float[][] givePosition();
	

	public void takeDamage(float amount) {
		health =- amount;
		if(health<0) {
			this.die();
		}
	}
	
	public void findWay() {
		
	}
	
	public float getX() {
		float carx = enemyBody.getPosition().x;
		carx = carx - saussehen.getWidth() / 2;
		return carx;
	}
	
	public float getY() {
		float cary = enemyBody.getPosition().y;
		cary = cary - saussehen.getWidth() / 2;
		return cary;
	}
	
	public void draw(SpriteBatch spriteBatch) {
		saussehen.setPosition(getX(), getY());
		saussehen.draw(spriteBatch);
	}
}
