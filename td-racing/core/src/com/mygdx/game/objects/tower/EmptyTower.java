package com.mygdx.game.objects.tower;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Enemy;
import com.mygdx.game.objects.Tower;

public class EmptyTower extends Tower {
	
	public static Texture groundTower;
	public static Texture upperTower;

	public EmptyTower(final float xPosition, final float yPosition, final Array<Enemy> enemies) {
		super(xPosition, yPosition, groundTower, upperTower, enemies);

		maxHealth = -1;
		speed = -1;
		power = -1;
		range = 10;
		turnspeed = 2;
	}

}
