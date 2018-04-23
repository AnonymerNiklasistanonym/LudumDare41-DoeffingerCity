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
import com.mygdx.game.objects.Checkpoint;

public class Car {
	public Body body;
	Sprite sprite;
	float maxspeed = 10;
	float acceleration = 5f;
	float armor = 0.3f;
	float brakepower = 3f;
	float backacc = 1f;
	float steerpower = 5;
	float speed = 0;
	float friction = 0.99f;
	float health=100;
	float delta=0;

	public Car(World w, Sprite scar, final float xPostion, final float yPosition) {
		BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.DynamicBody;
		bodydef.position.set(xPostion * PlayState.PIXEL_TO_METER, yPosition * PlayState.PIXEL_TO_METER);
		body = w.createBody(bodydef);
		PolygonShape carBox = new PolygonShape();
		carBox.setAsBox(scar.getWidth() * 0.5f, scar.getHeight() * 0.5f);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = carBox;
		fdef.density = 1f;
		fdef.friction = 1f;
		fdef.filter.categoryBits = PlayState.PLAYER_BOX;
		fdef.filter.categoryBits = PlayState.ENEMY_BOX;
		body.createFixture(fdef);
		body.setUserData(this);
		body.setAngularDamping(2);
		body.setTransform(body.getPosition(), (float) Math.toRadians( 180 ));
		sprite = scar;
		
	}

	public void accelarate() {
		speed = speed + acceleration*delta;
		if (speed > maxspeed)
			speed = maxspeed;
	}

	public void brake() {
		if (speed > 0)
			speed = speed - brakepower*delta;
		else
			speed = speed - backacc*delta;
	}

	public void steerLeft() {
		body.applyTorque(steerpower*speed, true);

	}

	public void steerRight() {
		body.applyTorque(steerpower*speed * -1, true);
	}

	public void update(float delta) {
		this.delta=delta;
		if (speed < maxspeed * -1)
			speed = maxspeed * -1;
		if (speed > maxspeed)
			speed = maxspeed;
		Vector2 velo = new Vector2(speed, 0);
		velo.rotateRad(body.getAngle());
		body.setLinearVelocity(velo);
		speed = speed * friction;
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


	public Vector2 getForward() {
		Vector2 fwd = new Vector2(0, 0);
		fwd.x = body.getAngle();
		return fwd;
	}

	public Vector2 getVelocityVector() {
		Vector2 vv = new Vector2(speed, 0);
		return vv;
	}

	public Vector2 getOrthogonal() {
		Vector2 ort = new Vector2(0, 0);
		ort.x = body.getAngle();
		ort.rotate90(1);
		return ort;
	}

	public void hitCheckpoint(Checkpoint checkpoint) {
		// TODO Auto-generated method stub
	}

	public void hitEnemy(Enemy e) {
		float oldspeed=speed;
		float collisionpower=speed-e.health;
		if(collisionpower<0)
			collisionpower=0;
		//speed=speed-e.health*armor;
		e.takeDamage(oldspeed*2);
		health=health-collisionpower*armor;
		
	}

}
