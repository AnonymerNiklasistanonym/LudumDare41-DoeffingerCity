package com.mygdx.game.objects.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MainMap;
import com.mygdx.game.objects.Enemy;

public class EnemySmall extends Enemy {

	public static Texture damageTexture;
	public static Texture deadTexture;
	public static Texture normalTexture;

	public EnemySmall(float xPos, float yPos, World world, MainMap map, final float time) {
		super(xPos, yPos, world, normalTexture, deadTexture, damageTexture, map, time);
		this.health = 10;
		this.damage = 1;
		this.speed = 2;
		this.money = 1;
	}

	@Override
	public void dispose() {
		super.disposeMedia();
	}

}
