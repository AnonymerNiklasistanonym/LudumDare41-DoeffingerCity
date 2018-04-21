package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.gamestate.state.PlayState;

public class Car {
	Body body;
	Sprite sprite;
	float maxspeed = 80;
	float accelarition = 10000;
	float armor = 0;
	float brakepower = 20000;
	float steerpower = 5000000;

	public Car(World w, Sprite scar) {
		BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.DynamicBody;
		bodydef.position.set(500, 500);
		body = w.createBody(bodydef);
		PolygonShape carBox = new PolygonShape();
		carBox.setAsBox(scar.getWidth() * PlayState.SCALE_TO_BOX, scar.getHeight() * PlayState.SCALE_TO_BOX);
		body.createFixture(carBox, 0f);
		sprite=scar;
	}

	public void accelarate() {
		body.applyForceToCenter(new Vector2(accelarition, 0), true);
	}

	public void brake() {
		body.applyForceToCenter(new Vector2(brakepower * -1, 0), true);
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
		cary = cary - sprite.getWidth() / 2;
		return cary;
	}

	public void draw(SpriteBatch spriteBatch) {
		sprite.setPosition(getX(), getY());
		sprite.draw(spriteBatch);
		
	}
}
