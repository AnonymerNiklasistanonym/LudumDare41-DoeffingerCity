package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.gamestate.states.PlayState;
import com.mygdx.game.objects.towers.FireTower;
import com.mygdx.game.objects.towers.LaserTower;
import com.mygdx.game.objects.towers.MgTower;
import com.mygdx.game.objects.towers.SniperTower;

public class TowerMenu implements Disposable {

	public static Texture cannonButton;
	public static Texture laserButton;
	public static Texture flameButton;
	public static Texture sniperButton;

	private static final float SCALE_FACTOR = 1;

	private static final Vector2 start = new Vector2(30, 0);

	private final Sprite[] sprites;
	private final World world;
	private final ScoreBoard scoreboard;

	private Tower buildingtower;
	private boolean[] towerUnlocked;
	private boolean[] towerSelected;

	public TowerMenu(final World world, final ScoreBoard scoreboard) {
		this.world = world;
		this.scoreboard = scoreboard;
		this.sprites = new Sprite[] { new Sprite(cannonButton), new Sprite(laserButton), new Sprite(flameButton),
				new Sprite(sniperButton) };
		this.towerSelected = new boolean[sprites.length];
		this.towerUnlocked = new boolean[sprites.length];

		for (final Sprite sprite : sprites) {
			sprite.setSize(sprite.getWidth() * PlayState.PIXEL_TO_METER * SCALE_FACTOR,
					sprite.getHeight() * PlayState.PIXEL_TO_METER * SCALE_FACTOR);
			sprite.setOriginCenter();
		}

		float versatz = sprites[0].getWidth();
		float x = start.x;

		for (final Sprite sprite : sprites) {
			sprite.setPosition(x, start.y);
			x += versatz;
		}
	}

	public void draw(final SpriteBatch batch) {
		for (final Sprite sprite : sprites)
			sprite.draw(batch);
	}

	public boolean selectTower(int i, final Vector3 mousePos, final Array<Enemy> enemies) {
		boolean unselect = false;
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
				world.destroyBody(buildingtower.body);
			if (buildingtower != null)
				buildingtower = null;
			return false;
		}

		if (towerSelected[i] && towerUnlocked[i]) {
			buildingtower = getTower(i, mousePos, enemies);
			buildingtower.activateRange(true);
			return true;
		}
		return false;
	}

	public Tower getTower(final int tower, final Vector3 mousePos, final Array<Enemy> enemies) {
		switch (tower) {
		case 0:
			return new MgTower(new Vector2(mousePos.x, mousePos.y), enemies, world);
		case 1:
			return new LaserTower(new Vector2(mousePos.x, mousePos.y), enemies, world);
		case 2:
			return new FireTower(new Vector2(mousePos.x, mousePos.y), enemies, world);
		case 3:
			return new SniperTower(new Vector2(mousePos.x, mousePos.y), enemies, world);
		}
		System.out.println("ERROR: not found correct Tower at getTower");
		return null;
	}

	public void update() {
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

	private boolean canAfford(int i) {
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
		}
		return (scoreboard.getMoney() >= price);
	}

	public void unselectAll() {
		for (int i = 0; i < towerSelected.length; i++)
			towerSelected[i] = false;
		buildingtower = null;
	}

	public boolean contains(final float xPos, final float yPos) {
		float towerMenuWidth = 0;
		for (int i = 0; i < towerUnlocked.length; i++)
			towerMenuWidth += (towerUnlocked[i]) ? sprites[i].getWidth() : 0;
		return (xPos >= start.x && xPos <= start.x + towerMenuWidth)
				&& (yPos >= start.y && yPos <= start.y + ((sprites.length > 0) ? sprites[0].getHeight() : 0));
	}

	@Override
	public void dispose() {
		for (final Sprite sprite : sprites)
			sprite.getTexture().dispose();
	}

	public void unlockTower(final int i) {
		unlockTower(i, true);
	}

	public void unlockTower(final int i, final boolean unLock) {
		towerUnlocked[i] = unLock;
	}

	public Vector2 getStart() {
		return start;
	}

}
