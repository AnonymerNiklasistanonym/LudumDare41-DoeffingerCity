package com.mygdx.game.objects.tower;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Enemy;
import com.mygdx.game.objects.Tower;

public class LaserTower extends Tower {


	public static Texture groundTower;
	public static Texture upperTower;
	public static Texture towerFiring;

	public LaserTower(final float xPosition, final float yPosition, final Array<Enemy> enemies, final Sound soundShoot, World w) {
		super(xPosition, yPosition, groundTower, upperTower, towerFiring, enemies, soundShoot,w);

		maxHealth = -1;
		speed = 0.0f;
		firingSpriteTime=0.1f;
		power = 1;
		range = 10;
		turnspeed = 200;
		permanentsound=true;
		
		
	}
	@Override
	public void drawLine(final SpriteBatch spriteBatch) {
		spriteBatch.end();
		sRender.setProjectionMatrix(spriteBatch.getProjectionMatrix());
		sRender.begin(ShapeType.Filled);
		sRender.setColor(new Color(1, 0.3f, 0.3f, 0.9f));
		sRender.rectLine(center, shotposition, 0.4f);
		sRender.end();
		spriteBatch.begin();
	}

}