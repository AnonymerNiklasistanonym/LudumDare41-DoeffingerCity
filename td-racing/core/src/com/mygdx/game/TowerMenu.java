package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.gamestate.state.PlayState;
import com.mygdx.game.objects.Enemy;
import com.mygdx.game.objects.Tower;
import com.mygdx.game.objects.towers.FireTower;
import com.mygdx.game.objects.towers.LaserTower;
import com.mygdx.game.objects.towers.MgTower;
import com.mygdx.game.objects.towers.SniperTower;

public class TowerMenu implements Disposable {

	public static Texture cannonButton;
	public static Texture laserButton;
	public static Texture flameButton;
	public static Texture sniperButton;

	public float startx = 30;
	public float starty = 0;

	Vector3 mousepos;
	Tower buildingtower;

	ScoreBoard scoreboard;

	boolean[] towerUnlocked;
	Sprite[] sprites;

	World world;
	Array<Enemy> enemies;
	private boolean[] towerSelected;

	public TowerMenu(final World world, final ScoreBoard scoreboard) {
		this.mousepos = new Vector3(0, 0, 0);
		this.world = world;
		this.scoreboard = scoreboard;
		this.sprites = new Sprite[] { new Sprite(cannonButton), new Sprite(laserButton), new Sprite(flameButton),
				new Sprite(sniperButton) };
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

	public void selectTower(int i, final Vector3 mousePos, final Array<Enemy> enemies) {
		boolean unselect = false;
		this.mousepos = mousePos;
		this.enemies = enemies;
		if (!towerUnlocked[i])
			unselect = true;

		if (towerSelected[i])
			unselect = true;

		for (int j = 0; j < towerSelected.length; j++)
			towerSelected[j] = false;

		if (!unselect) {
			if (canAfford(i))
				towerSelected[i] = true;
		} else {
			if (buildingtower != null && buildingtower.body != null)
				System.out.println("Test 3");
			world.destroyBody(buildingtower.body);
			if (buildingtower != null)
				buildingtower = null;
		}

		if (towerSelected[i] && towerUnlocked[i]) {
			buildingtower = getTower(i);
			buildingtower.activateRange(true);
		}

		updateAlpha();
	}

	public Tower getTower(int i) {
		switch (i) {
		case 0:
			return new MgTower(mousepos.x, mousepos.y, enemies, world);

		case 1:
			return new LaserTower(mousepos.x, mousepos.y, enemies, world);

		case 2:
			return new FireTower(mousepos.x, mousepos.y, enemies, world);
		case 3:
			return new SniperTower(mousepos.x, mousepos.y, enemies, world);

		}
		System.out.println("ERROR: not found correct Tower at getTower");
		return null;
	}

	public void updateAlpha() {
		for (int i = 0; i < towerUnlocked.length; i++) {
			sprites[i].setColor(1, 1, 1, 0);
			if (towerUnlocked[i]) {
				sprites[i].setColor(1, 1, 1, 0.5f);
				if (canAfford(i))
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
			price = MgTower.COST;
			break;
		case 1:
			price = LaserTower.COST;
			break;
		case 2:
			price = FireTower.COST;
			break;
		case 3:
			price = SniperTower.COST;
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
			System.out.println("Test2");
			// world.destroyBody(buildingtower.body);

		}
		buildingtower = null;
		updateAlpha();
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

	@Override
	public void dispose() {
		for (final Sprite sprite : sprites)
			sprite.getTexture().dispose();
	}

	public void lockTower(int i) {
		towerUnlocked[i] = false;
		updateAlpha();
	}

}
