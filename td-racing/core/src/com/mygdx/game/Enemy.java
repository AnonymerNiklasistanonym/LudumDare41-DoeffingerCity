package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;

public abstract class Enemy {
	Body body;
	float speed = 80;
	float health = 0;
	Texture aussehen;
	float[][] position;
	
	public abstract void move();
	
	public abstract void die();
	
	public abstract float[][] givePosition();
	
	public abstract void takeDamage(float amount);
}
