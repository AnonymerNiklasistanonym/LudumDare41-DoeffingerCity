package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public abstract class Enemy {
	Body body;
	float speed = 80;
	float health = 0;
	Texture aussehen;
	float[][] position;
	
	public void startMove() {
		body.applyForceToCenter(new Vector2(speed, 0), true);
	}
	
	public void endMove() {
		body.applyForceToCenter(new Vector2(speed * -1, 0), true);
	}
	
	public void steerLeft() {
		body.applyTorque(45, true);
	}

	public void steerRight() {
		body.applyTorque(45 * -1, true);
	}
	
	public abstract void die();
	
	public abstract float[][] givePosition();
	
	public abstract void takeDamage(float amount);
}
