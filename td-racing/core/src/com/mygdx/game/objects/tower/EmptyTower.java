package com.mygdx.game.objects.tower;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.objects.Tower;

public class EmptyTower extends Tower{
	
	public EmptyTower(float xPosition, float yPosition) {
		super(xPosition, yPosition, new Texture("tower/tower_empty.png"), new Texture("tower/tower_empty_upper.png"));

		maxHealth = -1;
		speed = -1;
		power = -1;
		}

}
