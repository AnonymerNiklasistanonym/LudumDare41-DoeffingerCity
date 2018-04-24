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
	float versatz = 0;

	Tower buildingtower;

	boolean[] towerUnlocked;
	Sprite[] sprites;

	World world;
	Array<Enemy> enemies;
	private boolean[] towerSelected;

	public TurmMenu(World world, Array<Enemy> enemies) {
		this.world = world;
		this.enemies = enemies;

		this.towerSelected = new boolean[3];
		this.towerUnlocked = new boolean[3];
		this.sprites = new Sprite[] { new Sprite(cannonButton), new Sprite(laserButton), new Sprite(flameButton) };
		for (Sprite sprite : sprites) {
			sprite.setSize(sprite.getWidth() * PlayState.PIXEL_TO_METER, sprite.getHeight() * PlayState.PIXEL_TO_METER);
			sprite.setOriginCenter();
		}
		this.versatz = sprites[0].getWidth();
		float x = startx;
		final float y = starty;

		for (Sprite sprite : sprites) {
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
			towerSelected[i] = true;
		} else {
			buildingtower = null;
		}

		updateAlpha();
	}

	public void updateAlpha() {
		for (int i = 0; i < towerUnlocked.length; i++) {
			sprites[i].setColor(1, 1, 1, 0);
			if (towerUnlocked[i])
				sprites[i].setColor(1, 1, 1, 0.5f);
			if (towerSelected[i] && towerUnlocked[i]) {
				sprites[i].setColor(1, 1, 1, 1);
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
			}
		}
	}

	public Tower getCurrentTower() {
		return buildingtower;
	}

	public void unselectAll() {
		for (int i = 0; i < towerSelected.length; i++)
			towerSelected[i] = false;
		buildingtower = null;
		updateAlpha();
	}

}
