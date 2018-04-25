package com.mygdx.game.objects.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MainMap;
import com.mygdx.game.objects.Enemy;

public class EnemyLincoln extends Enemy {

	public static Texture damageTexture;
	public static Texture deadTexture;
	public static Texture normalTexture;

	public EnemyLincoln(final float xPos, final float yPos, final World world, final MainMap map, final float time) {
		super(xPos, yPos, world, normalTexture, deadTexture, damageTexture, map, time);
		this.damage = 20;
		this.setHealth(100);
		this.money = 10000;
		this.speed = 0.9f;
	}
}
