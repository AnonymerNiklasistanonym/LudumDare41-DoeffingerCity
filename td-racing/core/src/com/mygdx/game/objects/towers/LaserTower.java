package com.mygdx.game.objects.towers;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.objects.Enemy;
import com.mygdx.game.objects.Tower;

public class LaserTower extends Tower {

	// static properties
	public static Texture groundTower;
	public static Texture towerFiring;
	public static Texture upperTower;
	public static Sound soundShoot;

	// static final properties
	public static final int RANGE = 7;
	public static final int COST = 150;

	public LaserTower(final Vector2 position, final Array<Enemy> enemies, final World world) {
		super(position, groundTower, upperTower, towerFiring, enemies, world, RANGE, soundShoot);
		color = new Color(0, 0, 1, 0.3f);
		cost = COST;
		firingSpriteTime = 0.1f;
		maxHealth = -1;
		permanentsound = true;
		power = 0.1f;
		speed = 0.0f;
		turnspeed = 500;
		soundVolumne = 0.05f;
	}

	@Override
	public void drawProjectileShape(final ShapeRenderer shapeRenderer) {
		shapeRenderer.setColor(Color.ORANGE);
		shapeRenderer.rectLine(center, shotposition, 0.4f);
	}

	@Override
	public void dispose() {
		super.disposeMedia();
	}

}