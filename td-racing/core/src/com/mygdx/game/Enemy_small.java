package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.gamestate.state.PlayState;

public class Enemy_small extends Enemy{	
	
	
	public Enemy_small(World w, Sprite sprite, Sprite deadsprite) {
		
		this.speed = 80;
		this.health = 100;
		this.saussehen = sprite;
		this.stot=deadsprite;
		
		BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.DynamicBody;
		bodydef.position.set(300*PlayState.PIXEL_TO_METER, 300*PlayState.PIXEL_TO_METER);

		enemyBody = w.createBody(bodydef);
		CircleShape enemyCircle = new CircleShape();
		enemyCircle.setRadius(saussehen.getHeight()*0.35f);;
		FixtureDef fdef = new FixtureDef();
		fdef.shape = enemyCircle;
		fdef.filter.categoryBits=PlayState.ENEMY_BOX;
		fdef.filter.categoryBits=PlayState.PLAYER_BOX;
		enemyBody.createFixture(fdef);
		
	}
	
	
	public void die() {
		
	}
	
	public float[][] givePosition(){
		return position;
	}
	
}
