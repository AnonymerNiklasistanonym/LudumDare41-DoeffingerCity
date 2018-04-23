package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;

public class Enemy_bicycle extends Enemy {
	public static Texture normalTexture;
	public static Texture deadTexture;
	public static Texture damageTexture;

	public Enemy_bicycle(World w, MainMap map) {
		super(w, normalTexture, deadTexture,damageTexture,map);
		this.speed = 0.2f;
		this.health = 100;
	}
}