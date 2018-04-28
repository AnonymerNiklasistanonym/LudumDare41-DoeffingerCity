package com.mygdx.game.objects.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MainMap;
import com.mygdx.game.gamestate.state.PlayState;
import com.mygdx.game.objects.Enemy;

public class EnemyBicycle extends Enemy {

	public static Texture damageTexture;
	public static Texture deadTexture;
	public static Texture normalTexture;

	public EnemyBicycle(final float xPos, final float yPos, final World world, final MainMap map, final float time) {
		super(xPos, yPos, world, normalTexture, deadTexture, damageTexture, map, time);
		this.damage = 2;
		this.health = 10;
		this.money = 2;
		this.speed = 5f;
	}

	@Override
	protected void createBody(float x, float y, World w) {
		final BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.DynamicBody;
		// bodydef.position.set(MathUtils.random(1280)*PlayState.PIXEL_TO_METER,
		// MathUtils.random(720)*PlayState.PIXEL_TO_METER);
		bodydef.position.set(x * PlayState.PIXEL_TO_METER, y * PlayState.PIXEL_TO_METER);

		while (w.isLocked()) {
		}
		this.body = w.createBody(bodydef);
		while (w.isLocked()) {
		}
		this.body.setActive(false);


		final PolygonShape zBox = new PolygonShape();
		zBox.setAsBox(spriteAlive.getWidth() * 0.4f, spriteAlive.getHeight() * 0.4f);
		final FixtureDef fdef = new FixtureDef();
		fdef.shape = zBox;
		fdef.density = 1f;
		// fdef.isSensor=true;
		fdef.filter.categoryBits = PlayState.ENEMY_BOX;
		fdef.filter.categoryBits = PlayState.PLAYER_BOX;

		this.body.createFixture(fdef);
		this.body.setUserData(this);
	}
	
	@Override
	public void dispose() {
		super.disposeMedia();
	}
}
