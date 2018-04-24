package com.mygdx.game.objects.tower;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.objects.Enemy;
import com.mygdx.game.objects.Tower;

public class MgTower extends Tower {

	public static Texture groundTower;
	public static Texture upperTower;
	public static Texture towerFiring;
	public static Sound soundShoot;
	public static final int range = 10;
	public static int costTower = 100;

	public MgTower(final float xPosition, final float yPosition, final Array<Enemy> enemies, World w) {
		super(xPosition, yPosition, groundTower, upperTower, towerFiring, enemies, w, range, soundShoot);

		this.maxHealth = -1;
		this.speed = 0.4f;
		this.firingSpriteTime = 0.1f;
		this.power = 1.5f;
		this.turnspeed = 50;
		this.cost = MgTower.costTower;

	}

}