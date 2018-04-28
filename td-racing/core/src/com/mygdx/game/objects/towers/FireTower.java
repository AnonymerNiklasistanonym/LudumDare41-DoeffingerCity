package com.mygdx.game.objects.towers;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.gamestate.state.PlayState;
import com.mygdx.game.objects.Enemy;
import com.mygdx.game.objects.Flame;
import com.mygdx.game.objects.Tower;

public class FireTower extends Tower {

	// static properties
	public static Texture groundTower;
	public static Texture upperTower;
	public static Texture towerFiring;
	public static Texture tflame;
	public static Sound soundShoot;

	// static final properties
	private static final int RANGE = 7;
	public static final int COST = 300;

	private final Sprite sflame;
	private final World world;
	private final Array<Flame> flames;

	public FireTower(final float xPosition, final float yPosition, final Array<Enemy> enemies, final World world) {
		super(xPosition, yPosition, groundTower, upperTower, towerFiring, enemies, world, RANGE, soundShoot);

		this.sflame = new Sprite(tflame);
		this.sflame.setSize(this.sflame.getWidth() * PlayState.PIXEL_TO_METER,
				this.sflame.getHeight() * PlayState.PIXEL_TO_METER);
		this.maxHealth = -1;
		this.speed = 0.04f;
		this.firingSpriteTime = 0.2f;
		this.power = 0.15f;
		this.turnspeed = 700;
		this.permanentsound = true;
		this.cost = COST;
		this.world = world;
		this.flames = new Array<Flame>();
		this.color = new Color(1, 0, 0, 0.3f);
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

			final Flame flame = new Flame(body.getPosition().x * PlayState.METER_TO_PIXEL,
					body.getPosition().y * PlayState.METER_TO_PIXEL, sflame, world, power);
			flame.getBody().applyForceToCenter(aim, true);
			flames.add(flame);
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
		this.sflame.getTexture().dispose();
		for (final Flame flame : this.flames)
			flame.dispose();
	}

}