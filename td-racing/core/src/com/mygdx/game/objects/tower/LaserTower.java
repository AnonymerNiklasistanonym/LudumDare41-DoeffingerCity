package com.mygdx.game.objects.tower;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.objects.Enemy;

public class LaserTower extends Tower {

	public static Texture groundTower;
	public static Texture towerFiring;
	public static Texture upperTower;
	public static Sound soundShoot;
	public static int costTower = 150;
	public static final int range = 6;

	public LaserTower(final float xPosition, final float yPosition, final Array<Enemy> enemies, World w) {
		super(xPosition, yPosition, groundTower, upperTower, towerFiring, enemies, w, range, soundShoot);
		this.maxHealth = -1;
		this.speed = 0.0f;
		this.firingSpriteTime = 0.1f;
		this.power = 0.1f;
		this.turnspeed = 500;
		this.permanentsound = true;
		this.cost = LaserTower.costTower;
	}

	@Override
	public void drawProjectileShape(final ShapeRenderer shapeRenderer) {
		shapeRenderer.setColor(Color.ORANGE);
		shapeRenderer.rectLine(center, shotposition, 0.4f);
	}

}