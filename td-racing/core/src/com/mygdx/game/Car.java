package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Car {
	Body body;
	float maxspeed = 80;
	float accelarition = 10000;
	float armor = 0;
	float brakepower = 20000;
	float steerpower = 500000;

	public Car(Body b) {
		this.body = b;
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

	public void hitEnemy(Enemy e) {

	}
}
