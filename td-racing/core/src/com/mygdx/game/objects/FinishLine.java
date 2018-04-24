package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.gamestate.state.PlayState;

public class FinishLine {

	private final Sprite sprite;
	private final Body body;

	public FinishLine(final World world, final Sprite sprite, final float xPos, final float yPos) {
		final BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.StaticBody;
		bodydef.position.set(xPos * PlayState.PIXEL_TO_METER, yPos * PlayState.PIXEL_TO_METER);
		this.body = world.createBody(bodydef);
		final PolygonShape carBox = new PolygonShape();
		carBox.setAsBox(sprite.getWidth() * 0.5f, sprite.getHeight() * 0.5f);
		final FixtureDef fdef = new FixtureDef();
		fdef.shape = carBox;
		fdef.density = 1f;
		fdef.friction = 1f;
		fdef.isSensor = true;
		this.body.createFixture(fdef);
		this.body.setUserData(this);
		this.body.setAngularDamping(2);
		this.sprite = sprite;
		sprite.setPosition(getX(), getY());
		sprite.setRotation(this.body.getAngle() * MathUtils.radDeg);
	}

	public float getX() {
		return this.body.getPosition().x - sprite.getWidth() / 2;
	}

	public float getY() {
		return this.body.getPosition().y - sprite.getHeight() / 2;
	}

	public void draw(SpriteBatch spriteBatch) {
		sprite.draw(spriteBatch);
	}

	public Body getBody() {
		return body;
	}
}
