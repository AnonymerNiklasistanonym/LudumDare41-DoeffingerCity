package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

public class Flame implements Disposable {

	private final Body body;
	private final Sprite sprite;
	private final float originalsize;
	private final float damage;

	private float spriteScale;
	private float lifetime;
	private boolean killme;

	public Flame(float xPosition, float yPosition, final Sprite sprite, final World world, final float damage) {
		System.out.println("Bodies in world before adding flame: " + world.getBodyCount());
		// add flame as dynamic body to the box2D world
		final BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.DynamicBody;
		bodydef.position.set(xPosition, yPosition);
		this.body = world.createBody(bodydef);

		// add a fixture to the body (to recognize collisions)
		CircleShape flameCircle = new CircleShape();
		flameCircle.setRadius(sprite.getHeight() * 0.45f);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = flameCircle;
		fdef.density = 1f;
		fdef.isSensor = true;
		body.createFixture(fdef);

		// add class to the body for collisions
		body.setUserData(this);

		// set properties
		this.sprite = new Sprite(sprite);
		this.originalsize = this.sprite.getWidth();
		this.damage = damage;
		this.spriteScale = 0.1f;
		this.lifetime = 0.5f;
		this.killme = false;
		System.out.println("Bodies in world after adding flame: " + world.getBodyCount());
	}

	public void update(final float deltaTime) {
		spriteScale += deltaTime;
		if (spriteScale > 1)
			spriteScale = 1;
		lifetime -= deltaTime;
		if (lifetime < 0)
			this.killme = true;

	}

	public void draw(final SpriteBatch spriteBatch) {
		sprite.setSize(spriteScale * originalsize, spriteScale * originalsize);
		sprite.setOriginCenter();
		sprite.setPosition(getX(), getY());
		sprite.draw(spriteBatch);
	}

	public float getX() {
		return body.getPosition().x - sprite.getWidth() / 2;
	}

	public float getY() {
		return body.getPosition().y - sprite.getWidth() / 2;
	}

	public float getDamage() {
		return damage;
	}

	public boolean isKillme() {
		return killme;
	}

	public Body getBody() {
		return body;
	}

	@Override
	public void dispose() {
		this.sprite.getTexture().dispose();
	}
}