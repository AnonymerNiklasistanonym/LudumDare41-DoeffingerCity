package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.gamestate.state.PlayState;

public class Enemy_small extends Enemy{	
	
	
	public Enemy_small(World w, Sprite sprite) {
		
		this.speed = 80;
		this.health = 100;
		this.saussehen = sprite;
		
		BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.DynamicBody;
		bodydef.position.set(300*PlayState.PIXEL_TO_METER, 300*PlayState.PIXEL_TO_METER);
		enemyBody = w.createBody(bodydef);
		CircleShape enemyCircle = new CircleShape();
		enemyCircle.setRadius(saussehen.getHeight()*0.35f);;
		enemyBody.createFixture(enemyCircle, 0f);
	}
	
	
	public void die() {
		
	}
	
	public float[][] givePosition(){
		return position;
	}
	
}
