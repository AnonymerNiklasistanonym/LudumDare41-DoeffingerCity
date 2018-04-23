package com.mygdx.game.objects.tower;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Enemy;
import com.mygdx.game.objects.Tower;

public class MGTower extends Tower {

	public static Texture groundTower;
	public static Texture upperTower;
	public static Texture towerFiring;
	public static final int range = 100;
	public static int costTower = 200;


	public MGTower(final float xPosition, final float yPosition, final Array<Enemy> enemies, final Sound soundShoot,
			World w) {
		super(xPosition, yPosition, groundTower, upperTower, towerFiring, enemies, soundShoot, w, range);

		this.maxHealth = -1;
		this.speed = 0.2f;
		this.firingSpriteTime = 0.1f;
		this.power = 1;
		this.turnspeed = 50;
		this.cost = MGTower.costTower;

	}

}