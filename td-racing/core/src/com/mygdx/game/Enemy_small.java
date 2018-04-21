package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;

public class Enemy_small extends Enemy{
	
	
	
	public Enemy_small(Texture iAussehen, float iSpeed, float iHealth) {
		this.aussehen = iAussehen;
		this.speed = iSpeed;
		this.health = iHealth;
	}
	
	public void move() {
		
	}
	
	public void die() {
		
	}
	
	public float[][] givePosition(){
		return position;
	}
	
	public void takeDamage(float amount) {
		health =- amount;
		if(health<0) {
			this.die();
		}
	}
}
