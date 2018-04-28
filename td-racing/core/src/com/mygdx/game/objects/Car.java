package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.gamestate.state.PlayState;

public class Car implements Disposable {

	private Body body;
	private Sprite sprite;
	private final float maxspeed = 15;
	private final float acceleration = 2000f;
	private final float brakepower = 5000f;
	private final float backacc = 1000f;
	private final float steerpower = 1500;
	private float deltaTime = 0;

	public Car(final World world, final Sprite sprite, final float xPostion, final float yPosition) {
		final BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.DynamicBody;
		bodydef.position.set(xPostion * PlayState.PIXEL_TO_METER, yPosition * PlayState.PIXEL_TO_METER);
		this.body = world.createBody(bodydef);
		final PolygonShape carBox = new PolygonShape();
		carBox.setAsBox(sprite.getWidth() * 0.5f, sprite.getHeight() * 0.5f);
		final FixtureDef fdef = new FixtureDef();
		fdef.shape = carBox;
		fdef.density = 1f;
		fdef.friction = 1f;
		fdef.filter.categoryBits = PlayState.PLAYER_BOX;
		fdef.filter.categoryBits = PlayState.ENEMY_BOX;
		this.body.createFixture(fdef);
		this.body.setUserData(this);
		this.body.setAngularDamping(2);
		this.sprite = sprite;

		// turn the car at the beginning
		this.body.setTransform(body.getPosition(), (float) Math.toRadians(180));
	}

	public void accelarate() {
		final Vector2 acc = new Vector2(acceleration * deltaTime, 0);
		acc.rotateRad(body.getAngle());
		body.applyForceToCenter(acc, true);
	}

	public void brake() {
		final Vector2 acc = new Vector2(((getForwardVelocity().x >= 0) ? brakepower : backacc) * -1 * deltaTime, 0);
		acc.rotateRad(body.getAngle());
		body.applyForceToCenter(acc, true);
	}

	public void steerLeft() {
		this.body.applyTorque(this.steerpower * this.deltaTime * getSpeedFactor(), true);
	}

	public void steerRight() {
		this.body.applyTorque(this.steerpower * -1 * this.deltaTime * getSpeedFactor(), true);
	}

	public float getNormalizedSpeed() {
		final float mult = (getForwardVelocity().x < 0) ? -1 : 1;
		final float ns = getForwardVelocity().x / maxspeed;
		return ns * mult;
	}

	public float getSpeedFactor() {
		final float mult = (getForwardVelocity().x < 0) ? -1 : 1;
		final float x = Math.abs(getNormalizedSpeed() * 2);
		final float factor = (float) (1 - Math.exp(-3 * MathUtils.clamp(x, 0, 1)));

		if (factor < -1 || factor > 1)
			System.out.println("Speedfactor ist falsch!");

		return factor * mult;
	}

	public void update(final float deltaTime) {
		this.deltaTime = deltaTime;
		reduceToMaxSpeed(this.maxspeed);
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
		body.applyLinearImpulse(getOrthogonal().scl(drift).scl(lat).scl(-1), body.getPosition(), true);
	}

	public Vector2 getForwardVelocity() {
		final Vector2 velo = getVelocityVector();
		velo.rotateRad(body.getAngle() * -1);
		return velo;
	}

	public float getX() {
		return body.getPosition().x - sprite.getWidth() / 2;
	}

	public float getY() {
		return body.getPosition().y - sprite.getHeight() / 2;
	}

	public void draw(SpriteBatch spriteBatch) {
		sprite.draw(spriteBatch);
	}

	public Vector2 getForward() {
		return new Vector2(this.body.getAngle(), 0);
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

	public float hitEnemy(final Enemy e) {
		e.takeDamage(Math.abs(getForwardVelocity().x * 4));
		return e.health;
	}

	@Override
	public void dispose() {
		this.sprite.getTexture().dispose();
	}

}
