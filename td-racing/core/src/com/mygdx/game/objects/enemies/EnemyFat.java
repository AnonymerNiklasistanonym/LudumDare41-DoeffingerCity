package com.mygdx.game.objects.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MainMap;
import com.mygdx.game.objects.Enemy;

public class EnemyFat extends Enemy {

	private static final float DAMAGE = 20;
	private static final float HEALTH = 100;
	private static final float MONEY = 4;
	private static final float SPEED = 0.8f;
	private static final float SCORE = 40;

	public static Texture damageTexture;
	public static Texture deadTexture;
	public static Texture normalTexture;

	public EnemyFat(final Vector2 position, final World world, final MainMap map, final float time) {
		super(position, world, normalTexture, deadTexture, damageTexture, map, time);
		this.damage = DAMAGE;
		this.health = HEALTH;
		this.money = MONEY;
		this.speed = SPEED;
		this.score = SCORE;
	}

	@Override
	public void dispose() {
		super.disposeMedia();
	}
}
