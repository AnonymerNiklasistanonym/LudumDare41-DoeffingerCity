package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.objects.Tower;
import com.mygdx.game.objects.tower.FireTower;
import com.mygdx.game.objects.tower.LaserTower;
import com.mygdx.game.objects.tower.MGTower;

public class TurmMenu {
	public boolean tower1unlocked = true;
	public boolean tower2unlocked = false;
	public boolean tower3unlocked = false;
	public boolean tower4unlocked = false;
	public boolean tower5unlocked = false;

	boolean tower1selected = false;
	boolean tower2selected = false;
	boolean tower3selected = false;
	boolean tower4selected = false;
	boolean tower5selected = false;

	float startx = 30;
	float starty = 0;
	float versatz = 0;

	Tower buildingtower;

	Sprite stower1button;
	Sprite stower2button;
	Sprite stower3button;
	Sprite stower4button;
	Sprite stower5button;

	World w;
	Array<Enemy> enemies;

	public TurmMenu(Sprite s1, Sprite s2, Sprite s3, Sprite s4, Sprite s5, World w, Array<Enemy> enemies) {
		this.w = w;
		this.enemies = enemies;
		stower1button = s1;
		stower2button = s2;
		stower3button = s3;
		stower4button = s4;
		stower5button = s5;
		versatz = s1.getWidth();
		float x = startx;
		float y = starty;
		
		stower1button.setPosition(x, y);
		x = x + versatz;
		stower2button.setPosition(x, y);
		x = x + versatz;
		stower3button.setPosition(x, y);
		x = x + versatz;
		stower4button.setPosition(x, y);
		x = x + versatz;
		stower5button.setPosition(x, y);
		x = x + versatz;
		
		updateAlpha();
	}

	public void draw(SpriteBatch batch) {
		stower1button.draw(batch);
		stower2button.draw(batch);
		stower3button.draw(batch);
		stower4button.draw(batch);
		stower5button.draw(batch);
	}

	public void unlockTower(int i) {
		switch (i) {
		case 1:
			tower1unlocked = true;
			break;
		case 2:
			tower2unlocked = true;
			break;
		case 3:
			tower3unlocked = true;
			break;
		case 4:
			tower4unlocked = true;
			break;
		case 5:
			tower5unlocked = true;
			break;

		default:
			break;
		}
		updateAlpha();
	}

	public void selectTower(int i) {
		boolean unselect = false;
		switch (i) {
		case 1:
			if (tower1selected)
				unselect = true;
			break;
		case 2:
			if (tower2selected)
				unselect = true;
			break;
		case 3:
			if (tower3selected)
				unselect = true;
			break;
		case 4:
			if (tower4selected)
				unselect = true;
			break;
		case 5:
			if (tower5selected)
				unselect = true;
			break;

		default:
			break;
		}

		tower1selected = false;
		tower2selected = false;
		tower3selected = false;
		tower4selected = false;
		tower5selected = false;
		if (unselect == false) {
			switch (i) {
			case 1:
				tower1selected = true;
				break;
			case 2:
				tower2selected = true;
				break;
			case 3:
				tower3selected = true;
				break;
			case 4:
				tower4selected = true;
				break;
			case 5:
				tower5selected = true;
				break;

			default:
				break;
			}
		} else {
			buildingtower = null;
		}
		updateAlpha();
	}

	public void updateAlpha() {
		stower1button.setColor(1,1,1,0);
		stower2button.setColor(1,1,1,0);
		stower3button.setColor(1,1,1,0);
		stower4button.setColor(1,1,1,0);
		stower5button.setColor(1,1,1,0);
		
		if(tower1unlocked)
			stower1button.setColor(1,1,1,0.5f);
		if(tower2unlocked)
			stower2button.setColor(1,1,1,0.5f);
		if(tower3unlocked)
			stower3button.setColor(1,1,1,0.5f);
		if(tower4unlocked)
			stower4button.setColor(1,1,1,0.5f);
		if(tower5unlocked)
			stower5button.setColor(1,1,1,0.5f);
		
		if(tower1selected)
			stower1button.setColor(1,1,1,1);
		if(tower2selected)
			stower2button.setColor(1,1,1,1);
		if(tower3selected)
			stower3button.setColor(1,1,1,1);
		if(tower4selected)
			stower4button.setColor(1,1,1,1);
		if(tower5selected)
			stower5button.setColor(1,1,1,1);
		
		if(tower1selected&&tower1unlocked)
			buildingtower=new MGTower(10, 10, enemies, w);
		if(tower2selected&&tower2unlocked)
			buildingtower=new LaserTower(10, 10, enemies, w);
		if(tower3selected&&tower3unlocked)
			buildingtower=new FireTower(10, 10, enemies, w);
		if(tower4selected&&tower4unlocked)
			buildingtower=new MGTower(10, 10, enemies, w);
		if(tower5selected&&tower5unlocked)
			buildingtower=new MGTower(10, 10, enemies, w);
	}

	public Tower getCurrentTower() {
		return buildingtower;
	}
	
	public void unselectAll() {
		tower1selected = false;
		tower2selected = false;
		tower3selected = false;
		tower4selected = false;
		tower5selected = false;
		buildingtower=null;
		updateAlpha();
	}

}
