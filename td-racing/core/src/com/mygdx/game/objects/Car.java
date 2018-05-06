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
import com.mygdx.game.gamestate.states.PlayState;

public class Car implements Disposable {

	private static final float SPEED_MAX = 15;
	private static final float ACCELERATION_FORWARD = 2000f;
	private static final float BRAKE_POWER = 5000f;
	private static final float ACCELERATION_BACK = 1000f;
	private static final float STEER_POWER = 1800;

	private final Body body;
	private final Sprite sprite;

	private float deltaTime;

	public Car(final World world, final Sprite sprite, final float xPostion, final float yPosition) {
		final BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.DynamicBody;
		bodydef.position.set(xPostion * PlayState.PIXEL_TO_METER, yPosition * PlayState.PIXEL_TO_METER);
		body = world.createBody(bodydef);
		final PolygonShape carBox = new PolygonShape();
		carBox.setAsBox(sprite.getWidth() * 0.5f, sprite.getHeight() * 0.5f);
		final FixtureDef fdef = new FixtureDef();
		fdef.shape = carBox;
		fdef.density = 1f;
		fdef.friction = 1f;
		fdef.filter.categoryBits = PlayState.ENEMY_BOX;
		body.createFixture(fdef);
		body.setUserData(this);
		body.setAngularDamping(2);
		this.sprite = sprite;
		deltaTime = 0;

		// turn the car at the beginning
		body.setTransform(body.getPosition(), (float) Math.toRadians(180));
	}

	public void accelarate() {
		final Vector2 acc = new Vector2(ACCELERATION_FORWARD * deltaTime, 0);
		acc.rotateRad(body.getAngle());
		body.applyForceToCenter(acc, true);
	}

	public void brake() {
		final Vector2 acc = new Vector2(
				((getForwardVelocity().x >= 0) ? BRAKE_POWER : ACCELERATION_BACK) * -1 * deltaTime, 0);
		acc.rotateRad(body.getAngle());
		body.applyForceToCenter(acc, true);
	}

	public void steerLeft() {
		body.applyTorque(STEER_POWER * deltaTime * getTurnFactor(), true);
	}

	public void steerRight() {
		body.applyTorque(STEER_POWER * -1 * deltaTime * getTurnFactor(), true);
	}

	private float getNormalizedSpeed() {
		final float mult = (getForwardVelocity().x < 0) ? -1 : 1;
		final float ns = getForwardVelocity().x / SPEED_MAX;
		return ns * mult;
	}

	private float getTurnFactor() {
		final float mult = (getForwardVelocity().x < 0) ? -1 : 1;
		final float x = Math.abs(getNormalizedSpeed() * 2);
		final float factor = (float) (1 - Math.exp(-3 * MathUtils.clamp(x, 0.05f, 1)));

		if (factor < -1 || factor > 1)
			System.out.println("Turnfactor ist falsch!");

		return factor * mult;
	}

	public void update(final float deltaTime) {
		this.deltaTime = deltaTime;
		reduceToMaxSpeed(SPEED_MAX);
		killLateral(0.95f);
		sprite.setPosition(getX(), getY());
		sprite.setRotation(body.getAngle() * MathUtils.radDeg);
	}

	private void reduceToMaxSpeed(float maxspeed) {
		float speed = getForwardVelocity().x;
		if (speed < maxspeed * -1)
			speed = maxspeed * -1;
		if (speed > maxspeed)
			speed = maxspeed;

		final Vector2 newSpeed = new Vector2(speed, getForwardVelocity().y);
		newSpeed.rotateRad(body.getAngle());
		body.setLinearVelocity(newSpeed);
	}

	private void killLateral(float drift) {
		float lat = getVelocityVector().dot(getOrthogonal());
		body.applyLinearImpulse(getOrthogonal().scl(drift).scl(lat).scl(-1), body.getPosition(), true);
	}

	private Vector2 getForwardVelocity() {
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
		return new Vector2(body.getAngle(), 0);
	}

	private Vector2 getVelocityVector() {
		return body.getLinearVelocity();
	}

	private Vector2 getOrthogonal() {
		final Vector2 ort = new Vector2(1, 0);
		ort.rotateRad(body.getAngle());
		ort.rotate90(1);
		return ort;
	}

	public float hitEnemy(final Enemy e) {
		float damage = Math.abs(getForwardVelocity().x * 1.5f);
		if (damage > 0.1f)
			e.takeDamage(damage);
		return e.health;
	}

	@Override
	public void dispose() {
		sprite.getTexture().dispose();
	}

}
