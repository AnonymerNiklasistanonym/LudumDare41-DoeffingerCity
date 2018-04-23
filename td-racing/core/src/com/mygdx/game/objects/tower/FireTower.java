package com.mygdx.game.objects.tower;

import java.util.Vector;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Enemy;
import com.mygdx.game.gamestate.state.PlayState;
import com.mygdx.game.objects.Tower;

public class FireTower extends Tower {

	public static Texture groundTower;
	public static Texture upperTower;
	public static Texture towerFiring;
	public static Texture tflame;
	public static Sprite sflame;
	public static Sound soundShoot;
	public static final int range = 7;
	public static int costTower = 200;
	World w;
	Array<Flame> flames;
	public FireTower(final float xPosition, final float yPosition, final Array<Enemy> enemies, World w) {
		super(xPosition, yPosition, groundTower, upperTower, towerFiring, enemies, w, range, soundShoot);
		
		this.sflame = new Sprite(tflame);
		this.sflame.setSize(sflame.getWidth() * PlayState.PIXEL_TO_METER,
				sflame.getHeight() * PlayState.PIXEL_TO_METER);
		
		this.maxHealth = -1;
		this.speed = 0.0f;
		this.firingSpriteTime = 0.1f;
		this.power = 0.2f;
		this.turnspeed = 700;
		this.permanentsound = true;
		this.cost = FireTower.costTower;
		this.w=w;
		flames=new Array<Flame>();
	}

	@Override
	public void drawLine(final SpriteBatch spriteBatch) {
//		sRender.setProjectionMatrix(spriteBatch.getProjectionMatrix());
//		sRender.begin(ShapeType.Filled);
//		sRender.setColor(Color.ORANGE);
//		sRender.rectLine(center, shotposition, 0.4f);
//		sRender.end();
	}
	
	@Override
	public void drawProjectile(final SpriteBatch spriteBatch) {
		
		for (Flame flame : flames) {
			
			flame.draw(spriteBatch);
		}
	}

	
	@Override
	public void updateProjectiles(float delta) {
		for (Flame flame : flames) {
			flame.update(delta);
		}
	}
	@Override
	public void shoot(Enemy e) {
		Flame f=new Flame(body.getPosition().x*PlayState.METER_TO_PIXEL, body.getPosition().y*PlayState.METER_TO_PIXEL, sflame, w, 1);
		flames.add(f);
		Vector2 aim=new Vector2(500,0);
		aim.rotateRad(getDegrees());
		aim.rotate90(-1);
		f.body.applyForceToCenter(aim, true);
	}
	
	@Override
	public Array<Body> removeProjectiles() {
		Array<Flame> toremove=new Array<Flame>();
		Array<Body> bodystoremove=new Array<Body>();
		for (Flame flame : flames) {
			if(flame.killme)
				toremove.add(flame);
		}
		for (Flame f : toremove) {
			flames.removeValue(f, true);
			bodystoremove.add(f.body);
			}
		return bodystoremove;
	}

	public Array<Flame> getFlames() {
		return flames;
	}
	
	

}