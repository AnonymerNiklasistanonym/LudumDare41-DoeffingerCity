package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;

public class Enemy_small extends Enemy {
	
	public static Texture normalTexture;
	public static Texture deadTexture;

	public Enemy_small(World w) {
		super(w, normalTexture, deadTexture);
		this.speed = 2;
		this.health = 10;
	}

	public float[][] givePosition() {
		return position;
	}

}
