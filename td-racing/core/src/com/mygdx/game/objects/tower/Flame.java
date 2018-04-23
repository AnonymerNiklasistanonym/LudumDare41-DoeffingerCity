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
	float spritescale=0.1f;
	float originalsize=0;
	float lifetime=1.2f;
	Sprite sprite;
	public boolean killme=false;
	public Flame(float x, float y,Sprite sprite, World w, float damage) {
		System.out.println("Created flame");
		BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.DynamicBody;
		// bodydef.position.set(MathUtils.random(1280)*PlayState.PIXEL_TO_METER,
		// MathUtils.random(720)*PlayState.PIXEL_TO_METER);
		bodydef.position.set(x * PlayState.PIXEL_TO_METER, y * PlayState.PIXEL_TO_METER);
		body = w.createBody(bodydef);
		CircleShape enemyCircle = new CircleShape();
		enemyCircle.setRadius(sprite.getHeight() * 0.35f);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = enemyCircle;
		fdef.density = 1f;
		fdef.isSensor=true;
		body.createFixture(fdef);
		body.setUserData(this);
		this.sprite=sprite;
		originalsize=sprite.getWidth();
	}
	
	public void update(float delta) {
		spritescale=spritescale+delta;
		if(spritescale>1)
			spritescale=1;
		lifetime=lifetime-delta;
		if(lifetime<0)
			killme=true;
	}
	
	public void draw(SpriteBatch batch) {
		sprite.setPosition(getX(),getY());
		//sprite.setSize(spritescale*originalsize, spritescale*originalsize);
		sprite.setOriginCenter();
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
}
