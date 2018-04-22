package com.mygdx.game.objects.tower;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Enemy;
import com.mygdx.game.objects.Tower;

public class EmptyTower extends Tower {

	public EmptyTower(float xPosition, float yPosition, Array<Enemy> enemies) {
		super(xPosition, yPosition, new Texture("tower/tower_empty.png"), new Texture("tower/tower_empty_upper.png"),
				enemies);

		maxHealth = -1;
		speed = -1;
		power = -1;
		range = 10;
		turnspeed = 10;
	}

}
