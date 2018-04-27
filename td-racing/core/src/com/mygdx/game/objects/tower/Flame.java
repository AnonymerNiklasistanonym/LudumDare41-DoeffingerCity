package com.mygdx.game.objects.tower;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.gamestate.state.PlayState;

public class Flame {
	Body body;
	float spritescale = 0.1f;
	float originalsize = 0;
	float lifetime = 0.5f;
	Sprite sprite;
	float damage;
	public boolean killme = false;

	public Flame(float x, float y, Sprite sprite, World w, float damage) {

		BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.DynamicBody;
		bodydef.position.set(x * PlayState.PIXEL_TO_METER, y * PlayState.PIXEL_TO_METER);
		body = w.createBody(bodydef);
		CircleShape flameCircle = new CircleShape();
		flameCircle.setRadius(sprite.getHeight() * 0.45f);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = flameCircle;
		fdef.density = 1f;
		fdef.isSensor = true;
		body.createFixture(fdef);
		body.setUserData(this);
		this.sprite = new Sprite(sprite);
		originalsize = sprite.getWidth();
		this.damage = damage;
	}

	public void update(float delta) {
		spritescale = spritescale + delta;
		if (spritescale > 1)
			spritescale = 1;
		lifetime = lifetime - delta;
		if (lifetime < 0)
			killme = true;
	}

	public void draw(final SpriteBatch batch) {
		sprite.setSize(spritescale * originalsize, spritescale * originalsize);
		sprite.setOriginCenter();
		sprite.setPosition(getX(), getY());
		sprite.draw(batch);
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

	public float getDamage() {
		return damage;
	}
}