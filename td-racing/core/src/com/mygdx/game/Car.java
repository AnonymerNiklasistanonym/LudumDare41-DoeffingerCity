package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.gamestate.state.PlayState;

public class Car {
	Body body;
	Sprite sprite;
	float maxspeed = 80;
	float acceleration = 1000000;
	float armor = 0;
	float brakepower = 2000000;
	float steerpower = 250;

	public Car(World w, Sprite scar) {
		BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.DynamicBody;
		bodydef.position.set(500, 500);
		body = w.createBody(bodydef);
		PolygonShape carBox = new PolygonShape();
		carBox.setAsBox(scar.getWidth() * PlayState.PIXEL_TO_METER, scar.getHeight() * PlayState.PIXEL_TO_METER);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = carBox;
		fdef.density = 1f;
		fdef.friction = 1f;
		body.createFixture(fdef);
		body.setAngularDamping(2);
		sprite = scar;
	}

	public void accelarate() {
		Vector2 velo = new Vector2(acceleration, 0);
		velo.rotateRad(body.getAngle());
		body.applyForceToCenter(velo, true);
		}

	public void brake() {
		Vector2 velo = new Vector2(brakepower * -1, 0);
		velo.rotateRad(body.getAngle());
		body.applyForceToCenter(velo, true);
	}

	public void steerLeft() {
		body.applyTorque(steerpower, true);

	}

	public void steerRight() {
		body.applyTorque(steerpower * -1, true);
	}

	public void update(float delta) {

	}

	public void hitEnemy(Enemy e) {

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
