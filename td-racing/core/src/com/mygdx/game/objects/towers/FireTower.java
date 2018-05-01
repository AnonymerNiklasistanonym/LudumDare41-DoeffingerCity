package com.mygdx.game.objects.towers;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.gamestate.states.PlayState;
import com.mygdx.game.objects.Enemy;
import com.mygdx.game.objects.Flame;
import com.mygdx.game.objects.Tower;

public class FireTower extends Tower {

	// static properties
	public static Sound soundShoot;
	public static Texture groundTower;
	public static Texture tflame;
	public static Texture towerFiring;
	public static Texture upperTower;

	// static final properties
	private static final int RANGE = 7;
	public static final int COST = 300;

	private final Array<Flame> flames;
	private final Sprite sflame;
	private final World world;

	public FireTower(final Vector2 position, final Array<Enemy> enemies, final World world) {
		super(position, groundTower, upperTower, towerFiring, enemies, world, RANGE, soundShoot);

		this.world = world;

		color = new Color(1, 0, 0, 0.3f);
		cost = COST;
		firingSpriteTime = 0.2f;
		flames = new Array<Flame>();
		maxHealth = -1;
		permanentsound = true;
		power = 0.15f;
		sflame = new Sprite(tflame);
		sflame.setSize(sflame.getWidth() * PlayState.PIXEL_TO_METER, sflame.getHeight() * PlayState.PIXEL_TO_METER);
		speed = 0.08f;
		turnspeed = 700;
		this.soundVolume = 1;
	}

	@Override
	public void drawProjectile(final SpriteBatch spriteBatch) {
		for (final Flame flame : flames)
			flame.draw(spriteBatch);
	}

	@Override
	public void updateProjectiles(final float deltaTime) {
		for (final Flame flame : flames)
			flame.update(deltaTime);
	}

	@Override
	public void shoot(final Enemy enemy) {
		if (isTargetInRange(enemy)) {
			final Vector2 aim = new Vector2(2000, 0);
			aim.rotate(getDegrees());
			aim.rotate90(1);

			this.timesincelastshot = 0;

			final Flame flame = new Flame(body.getPosition(), sflame, world, power);
			flame.getBody().applyForceToCenter(aim, true);
			flames.add(flame);
			if (soundOn)
				soundShoot.play(soundVolume, MathUtils.random(1f, 1.1f), 0f);
		} else
			target = null;
	}

	@Override
	public Array<Body> removeProjectiles() {
		final Array<Body> bodystoremove = new Array<Body>();
		for (final Flame flame : flames) {
			if (flame.isKillme()) {
				flames.removeValue(flame, true);
				bodystoremove.add(flame.getBody());
			}
		}
		return bodystoremove;
	}

	@Override
	public void drawProjectileShape(final ShapeRenderer shapeRenderer) {
		// No shape to draw
	}

	@Override
	public void dispose() {
		super.disposeMedia();
		sflame.getTexture().dispose();
		for (final Flame flame : flames)
			flame.dispose();
	}

}