package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.gamestate.state.PlayState;
import com.mygdx.game.objects.Enemy;
import com.mygdx.game.objects.Tower;
import com.mygdx.game.objects.tower.FireTower;
import com.mygdx.game.objects.tower.LaserTower;
import com.mygdx.game.objects.tower.MgTower;

public class TurmMenu {

	public static Texture cannonButton;
	public static Texture laserButton;
	public static Texture flameButton;

	float startx = 30;
	float starty = 0;

	Tower buildingtower;

	ScoreBoard scoreboard;

	boolean[] towerUnlocked;
	Sprite[] sprites;

	World world;
	Array<Enemy> enemies;
	private boolean[] towerSelected;

	public TurmMenu(World world, Array<Enemy> enemies, ScoreBoard scoreboard) {
		this.world = world;
		this.enemies = enemies;
		this.scoreboard = scoreboard;
		this.sprites = new Sprite[] { new Sprite(cannonButton), new Sprite(laserButton), new Sprite(flameButton) };
		this.towerSelected = new boolean[sprites.length];
		this.towerUnlocked = new boolean[sprites.length];

		for (final Sprite sprite : sprites) {
			sprite.setSize(sprite.getWidth() * PlayState.PIXEL_TO_METER, sprite.getHeight() * PlayState.PIXEL_TO_METER);
			sprite.setOriginCenter();
		}

		float versatz = sprites[0].getWidth();
		float x = startx;
		final float y = starty;

		for (final Sprite sprite : sprites) {
			sprite.setPosition(x, y);
			x += versatz;
		}

		updateAlpha();
	}

	public void draw(final SpriteBatch batch) {
		for (final Sprite sprite : sprites)
			sprite.draw(batch);
	}

	public void unlockTower(int i) {
		towerUnlocked[i] = true;
		updateAlpha();
	}

	public void selectTower(int i) {
		boolean unselect = false;
		
		if (towerSelected[i])
			unselect = true;

		for (int j = 0; j < towerSelected.length; j++)
			towerSelected[j] = false;

		if (!unselect) {
			if(canAfford(i))
			towerSelected[i] = true;
		} else {
			world.destroyBody(buildingtower.body);
			buildingtower = null;
		}

		if (towerSelected[i] && towerUnlocked[i]) {
			switch (i) {
			case 0:
				buildingtower = new MgTower(10, 10, enemies, world);
				break;
			case 1:
				buildingtower = new LaserTower(10, 10, enemies, world);
				break;
			case 2:
				buildingtower = new FireTower(10, 10, enemies, world);
				break;
			}
			buildingtower.activateRange(true);
		}

		updateAlpha();
	}

	public void updateAlpha() {
		for (int i = 0; i < towerUnlocked.length; i++) {
			sprites[i].setColor(1, 1, 1, 0);
			if (towerUnlocked[i]) {
				sprites[i].setColor(1, 1, 1, 0.5f);
			if(canAfford(i))
				sprites[i].setColor(1, 1, 1, 1f);
			
			if (towerSelected[i] && towerUnlocked[i]) 
				sprites[i].setColor(0.25f, 1, 0.25f, 1);
			}
		}
	}

	public Tower getCurrentTower() {
		return buildingtower;
	}

	public boolean canAfford(int i) {
		int price = 0;
		switch (i) {
		case 0:
			price = MgTower.costTower;
			break;
		case 1:
			price = LaserTower.costTower;
			break;
		case 2:
			price = FireTower.costTower;
			break;

		default:
			break;
		}

		if (scoreboard.getMoney() >= price)
			return true;
		else
			return false;
	}

	public void unselectAll() {
		for (int i = 0; i < towerSelected.length; i++)
			towerSelected[i] = false;
		if (buildingtower != null) {
			world.destroyBody(buildingtower.body);
		}
		buildingtower = null;
		updateAlpha();
	}

	public void updateMenu(World w, Array<Enemy> enemies) {
		this.world = w;
		this.enemies = enemies;
	}

	public boolean contains(final float xPos, final float yPos) {
		float towerMenuWidth = 0;
		for (int i = 0; i < towerUnlocked.length; i++) {
			if (towerUnlocked[i])
				towerMenuWidth += sprites[i].getWidth();
		}
		return (xPos >= startx && xPos <= startx + towerMenuWidth)
				&& (yPos >= starty && yPos <= starty + ((sprites.length > 0) ? sprites[0].getHeight() : 0));
	}

}
