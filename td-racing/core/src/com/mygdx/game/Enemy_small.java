package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;

public class Enemy_small extends Enemy {

	public Enemy_small(World w, Sprite sprite, Sprite deadsprite) {
		super(w, sprite, deadsprite);
		this.speed = 80;
		this.health = 10;
	}

	public float[][] givePosition() {
		return position;
	}

}
