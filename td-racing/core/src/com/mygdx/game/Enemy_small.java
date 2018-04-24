package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;

public class Enemy_small extends Enemy {

	public static Texture normalTexture;
	public static Texture deadTexture;
	public static Texture damageTexture;

	public Enemy_small(float x, float y, World w, MainMap map) {
		super(x, y, w, normalTexture, deadTexture, damageTexture, map);
		this.speed = 2;
		this.health = 10;
		this.damage = 1;
	}

}
