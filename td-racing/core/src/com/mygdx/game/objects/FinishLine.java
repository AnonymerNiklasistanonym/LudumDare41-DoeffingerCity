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

	Sprite sprite;
	public Body body;

	public FinishLine(World w, Sprite s, float xPosition, float yPosition) {
		BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.DynamicBody;
		bodydef.position.set(xPosition * PlayState.PIXEL_TO_METER, yPosition * PlayState.PIXEL_TO_METER);
		body = w.createBody(bodydef);
		PolygonShape carBox = new PolygonShape();
		carBox.setAsBox(s.getWidth() * 0.5f, s.getHeight() * 0.5f);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = carBox;
		fdef.density = 1f;
		fdef.friction = 1f;
		fdef.isSensor = true;
		body.createFixture(fdef);
		body.setUserData(this);
		body.setAngularDamping(2);
		sprite = s;
	}

	public float getX() {
		float carx = body.getPosition().x;
		carx = carx - sprite.getWidth() / 2;
		return carx;
	}

	public float getY() {
		float cary = body.getPosition().y;
		cary = cary - sprite.getHeight() / 2;
		return cary;
	}

	public void draw(SpriteBatch spriteBatch) {
		sprite.setPosition(getX(), getY());
		sprite.setRotation(body.getAngle() * MathUtils.radDeg);
		sprite.draw(spriteBatch);

	}
}
