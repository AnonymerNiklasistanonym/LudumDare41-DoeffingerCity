package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.gamestate.state.PlayState;

public abstract class Enemy extends BodyDef{
	public Body body;
	float speed = 80;
	float health = 0;
	Texture taussehen;
	Sprite saussehen;
	Sprite stot;
	float[][] position;
	World world;
	
	public boolean tot=false;
	
	public Enemy(World w, Sprite sprite, Sprite deadsprite) {
		this.speed = 80;
		this.health = 10;
		this.saussehen = sprite;
		this.stot=deadsprite;
		
		BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.DynamicBody;
		bodydef.position.set(MathUtils.random(1280)*PlayState.PIXEL_TO_METER, MathUtils.random(720)*PlayState.PIXEL_TO_METER);

		body = w.createBody(bodydef);
		CircleShape enemyCircle = new CircleShape();
		enemyCircle.setRadius(saussehen.getHeight()*0.35f);;
		FixtureDef fdef = new FixtureDef();
		fdef.shape = enemyCircle;
		fdef.filter.categoryBits=PlayState.ENEMY_BOX;
		fdef.filter.categoryBits=PlayState.PLAYER_BOX;
		
		body.createFixture(fdef);
		body.setUserData(this);
		this.world=w;
	}
	
	public void startMove() {
		body.applyForceToCenter(new Vector2(speed, 0), true);
	}
	
	public void endMove() {
		body.applyForceToCenter(new Vector2(speed * -1, 0), true);
	}
	
	public void steerLeft() {
		body.applyTorque(45, true);
	}

	public void steerRight() {
		body.applyTorque(45 * -1, true);
	}
	
	public void die() {
		tot=true;
		speed=0;
		saussehen=stot;
		
	}
	
	public abstract float[][] givePosition();
	

	public void takeDamage(float amount) {
		health =- amount;
		
		
	}
	
	public void findWay() {
		
	}
	
	public float getX() {
		float carx = body.getPosition().x;
		carx = carx - saussehen.getWidth() / 2;
		return carx;
	}
	
	public float getY() {
		float cary = body.getPosition().y;
		cary = cary - saussehen.getWidth() / 2;
		return cary;
	}
	
	public void update(float delta) {
		if(health<0) {
			this.die();
		}
	}
	
	public void draw(SpriteBatch spriteBatch) {
		saussehen.setPosition(getX(), getY());
		saussehen.draw(spriteBatch);
	}
}
