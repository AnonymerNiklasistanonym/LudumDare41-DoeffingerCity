package com.mygdx.game.objects.tower;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.gamestate.state.PlayState;

public class Flame {
	Body body;
	public Flame(float x, float y,Sprite sprite, World w) {
		
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
		body.createFixture(fdef);
		body.setUserData(this);

	}
}
