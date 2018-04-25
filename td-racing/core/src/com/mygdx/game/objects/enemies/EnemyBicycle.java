package com.mygdx.game.objects.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MainMap;
import com.mygdx.game.objects.Enemy;

public class EnemyBicycle extends Enemy {

	public static Texture damageTexture;
	public static Texture deadTexture;
	public static Texture normalTexture;

	public EnemyBicycle(final float xPos, final float yPos, final World world, final MainMap map, final float time) {
		super(xPos, yPos, world, normalTexture, deadTexture, damageTexture, map, time);
		this.damage = 2;
		this.setHealth(10);
		this.money = 2;
		this.speed = 5f;
	}
}
