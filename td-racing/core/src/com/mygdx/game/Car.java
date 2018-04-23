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
	float maxspeed = 15;
	float acceleration = 2000f;
	float armor = 0.3f;
	float brakepower = 5000f;
	float backacc = 1000f;
	float steerpower = 2000;
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
		Vector2 acc=new Vector2(acceleration*delta,0);
		acc.rotateRad(body.getAngle());
		body.applyForceToCenter(acc,true);
	}

	public void brake() {
		Vector2 acc=new Vector2(0,0);
		if(getForwardVelocity().x>=0) {
		acc=new Vector2(brakepower*-1*delta,0);
		}
		else
		{
			acc=new Vector2(backacc*-1*delta,0);
		}
		acc.rotateRad(body.getAngle());
		body.applyForceToCenter(acc,true);
	}

	public void steerLeft() {
		int i=1;
		if(getForwardVelocity().x<0)
			i=i*-1;
		body.applyTorque(steerpower*delta*i, true);

	}

	public void steerRight() {
		int i=1;
		if(getForwardVelocity().x<0)
			i=i*-1;
		body.applyTorque(steerpower* -1*delta*i, true);
	}

	public void update(float delta) {
		this.delta=delta;
		
		
		reduceToMaxSpeed(maxspeed);
		killLateral(0.95f);
	}
	
	public void steerZero() {
		
	}
	
	public void reduceToMaxSpeed(float maxspeed) {
		float speed=getForwardVelocity().x;
		if (speed < maxspeed * -1)
			speed = maxspeed * -1;
		if (speed > maxspeed)
			speed = maxspeed;
	
		Vector2 newSpeed=new Vector2(speed,getForwardVelocity().y);
		newSpeed.rotateRad(body.getAngle());
		body.setLinearVelocity(newSpeed);
	}
	
	public void killLateral(float drift) {
		float lat=getVelocityVector().dot(getOrthogonal());
		Vector2 vlat=getOrthogonal();
		vlat.scl(drift);
		vlat.scl(lat);
		vlat=vlat.scl(-1);
		body.applyLinearImpulse(vlat,body.getPosition(),true);
	}
	
	public Vector2 getForwardVelocity() {
		Vector2 velo=getVelocityVector();
		velo.rotateRad(body.getAngle()*-1);
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
		return body.getLinearVelocity();
	}


	public Vector2 getOrthogonal() {
		Vector2 ort = new Vector2(1, 0);
		ort.rotateRad(body.getAngle());
		ort.rotate90(1);
		return ort;
	}

	public void hitCheckpoint(Checkpoint checkpoint) {
		// TODO Auto-generated method stub
	}

	public void hitEnemy(Enemy e) {
		e.takeDamage(getForwardVelocity().x);
		
	}

}
