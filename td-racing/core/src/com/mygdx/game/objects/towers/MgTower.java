package com.mygdx.game.objects.towers;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.objects.Enemy;
import com.mygdx.game.objects.Tower;

public class MgTower extends Tower {

	// static properties
	public static Texture groundTower;
	public static Texture upperTower;
	public static Texture towerFiring;
	public static Sound soundShoot;

	// static final properties
	public static final int RANGE = 10;
	public static final int COST = 100;

	public MgTower(final float xPosition, final float yPosition, final Array<Enemy> enemies, final World world) {
		super(xPosition, yPosition, groundTower, upperTower, towerFiring, enemies, world, RANGE, soundShoot);
		this.maxHealth = -1;
		this.speed = 0.4f;
		this.firingSpriteTime = 0.1f;
		this.power = 1.5f;
		this.turnspeed = 50;
		this.cost = COST;
		this.color = new Color(0.5f, 0.1f, 0.7f, 0.3f);
	}

	@Override
	public void drawProjectileShape(final ShapeRenderer shapeRenderer) {
		shapeRenderer.setColor(Color.YELLOW);
		shapeRenderer.rectLine(center, shotposition, 0.2f);
	}

	@Override
	public void dispose() {
		super.disposeMedia();
	}

}