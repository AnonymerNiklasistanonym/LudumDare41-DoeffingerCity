package com.mygdx.game.objects.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MainMap;
import com.mygdx.game.gamestate.state.PlayState;
import com.mygdx.game.objects.Enemy;

public class EnemyBicycle extends Enemy {

	private static final float DAMAGE = 2;
	private static final float HEALTH = 2;
	private static final float MONEY = 2;
	private static final float SPEED = 2;
	private static final float SCORE = 20;
	private static final boolean HEALTH_BAR = true;

	public static Texture damageTexture;
	public static Texture deadTexture;
	public static Texture normalTexture;

	public EnemyBicycle(final Vector2 position, final World world, final MainMap map, final float time) {
		super(position, world, normalTexture, deadTexture, damageTexture, map, time);
		damage = DAMAGE;
		health = HEALTH;
		maxHealth = HEALTH;
		money = MONEY;
		speed = SPEED;
		score = SCORE;
		healthBar = HEALTH_BAR;
	}

	@Override
	protected FixtureDef createFixture() {
		final PolygonShape zBox = new PolygonShape();
		zBox.setAsBox(normalTexture.getWidth() * PlayState.PIXEL_TO_METER * 0.4f,
				normalTexture.getHeight() * PlayState.PIXEL_TO_METER * 0.4f);
		final FixtureDef fdef = new FixtureDef();
		fdef.shape = zBox;
		fdef.density = 1f;
		// fdef.isSensor=true;
		fdef.filter.categoryBits = PlayState.ENEMY_BOX;
		fdef.filter.categoryBits = PlayState.PLAYER_BOX;
		return fdef;
	}

	@Override
	public void dispose() {
		super.disposeMedia();
	}
}
