package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.gamestate.state.PlayState;

public class Enemy_small extends Enemy{	
	
	

	
	
	
	
	public Enemy_small(World w, Sprite sprite, Sprite deadsprite) {
		super(w, sprite, deadsprite);
		this.speed = 80;
		this.health = 10;
	}

	public float[][] givePosition(){
		return position;
	}
	
}
