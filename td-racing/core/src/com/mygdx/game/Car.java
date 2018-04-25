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
import com.mygdx.game.objects.Enemy;

public class Car {
	private Body body;
	private Sprite sprite;
	private float maxspeed = 15;
	private float acceleration = 2000f;
	private float armor = 0.3f;
	private float brakepower = 5000f;
	private float backacc = 1000f;
	private float steerpower = 1500;
	private float friction = 0.99f;
	private float health = 100;
	private float delta = 0;

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
		body.setTransform(body.getPosition(), (float) Math.toRadians(180));
		sprite = scar;

	}

	public void accelarate() {
		final Vector2 acc = new Vector2(acceleration * delta, 0);
		acc.rotateRad(body.getAngle());
		body.applyForceToCenter(acc, true);
	}

	public void brake() {
		final Vector2 acc = new Vector2(((getForwardVelocity().x >= 0) ? brakepower : backacc) * -1 * delta, 0);
		acc.rotateRad(body.getAngle());
		body.applyForceToCenter(acc, true);
	}

	public void steerLeft() {		
		//this.body.applyTorque(this.steerpower * this.delta * ((getForwardVelocity().x < 0) ? -1 : 1), true);
		
		this.body.applyTorque(this.steerpower * this.delta *getSpeedFactor(), true);
	}

	public void steerRight() {
		this.body.applyTorque(this.steerpower * -1 * this.delta *getSpeedFactor(), true);
	}
	
	public float getNormalizedSpeed() {
		float mult=1;
		if(getForwardVelocity().x<0)
			mult=-1;
		float ns=getForwardVelocity().x;
		ns=ns/maxspeed;
		return ns*mult;
	}
	
	public float getSpeedFactor() {
		float mult=1;
		if(getForwardVelocity().x<0)
			mult=-1;
		float factor=Math.abs(getNormalizedSpeed());
		factor=factor-1;
		factor=factor*factor;
		factor=1-factor;
		if(factor<-1||factor>1) {
			System.out.println("Speedfactor ist falsch!");
		}
		return factor*mult;
	}

	public void update(float delta) {
		this.delta = delta;
		reduceToMaxSpeed(maxspeed);
		killLateral(0.95f);
		sprite.setPosition(getX(), getY());
		sprite.setRotation(body.getAngle() * MathUtils.radDeg);
	}

	public void reduceToMaxSpeed(float maxspeed) {
		float speed = getForwardVelocity().x;
		if (speed < maxspeed * -1)
			speed = maxspeed * -1;
		if (speed > maxspeed)
			speed = maxspeed;

		final Vector2 newSpeed = new Vector2(speed, getForwardVelocity().y);
		newSpeed.rotateRad(body.getAngle());
		body.setLinearVelocity(newSpeed);
	}

	public void killLateral(float drift) {
		float lat = getVelocityVector().dot(getOrthogonal());
		Vector2 vlat = getOrthogonal();
		vlat.scl(drift);
		vlat.scl(lat);
		vlat = vlat.scl(-1);
		body.applyLinearImpulse(vlat, body.getPosition(), true);
	}

	public Vector2 getForwardVelocity() {
		Vector2 velo = getVelocityVector();
		velo.rotateRad(body.getAngle() * -1);
		return velo;
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
		sprite.draw(spriteBatch);
	}

	public Vector2 getForward() {
		final Vector2 fwd = new Vector2(0, 0);
		fwd.x = this.body.getAngle();
		return fwd;
	}

	public Vector2 getVelocityVector() {
		return body.getLinearVelocity();
	}

	public Vector2 getOrthogonal() {
		final Vector2 ort = new Vector2(1, 0);
		ort.rotateRad(this.body.getAngle());
		ort.rotate90(1);
		return ort;
	}

	public void hitEnemy(final Enemy e) {
		e.takeDamage(Math.abs(getForwardVelocity().x * 4));
	}

}
