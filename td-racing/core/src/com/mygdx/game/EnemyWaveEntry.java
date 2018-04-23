package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class EnemyWaveEntry {
	
	private final float timeInSeconds;
	private final Vector2 positon;
	private final int id;
	public static final int ENEMY_SMALL = 0;
	public static final int ENEMY_FAT = 1;
	public static final int ENEMY_BYCICLE = 2;

	public float getTimeInSeconds() {
		return timeInSeconds;
	}

	public Vector2 getPositon() {
		return positon;
	}
	
	public int getId() {
		return id;
	}

	public EnemyWaveEntry(final float timeInSeconds, final Vector2 positon, final int id) {
		this.timeInSeconds =  timeInSeconds;
		this.positon = positon;
		this.id = id;
	}
	
	public static EnemyWaveEntry[] createEnemyEntries(final Vector2[] small, final float smallTime,
			final Vector2[] fat, final float bigTime,
			final Vector2[] bicycle, final float bicycleTime) {
		final EnemyWaveEntry[] entries = new EnemyWaveEntry[small.length + fat.length + bicycle.length];
		final float countingTime = 0.2f;
		int counter = 0;
		for (int i = 0; i < small.length; i++)
			entries[i] = new EnemyWaveEntry(smallTime + counter++ * countingTime, small[i], ENEMY_SMALL);
		for (int i = 0 + small.length; i < fat.length + small.length; i++)
			entries[i] = new EnemyWaveEntry(smallTime + counter++ * countingTime, small[i], ENEMY_FAT);
		for (int i = 0 + small.length + fat.length; i < bicycle.length + small.length + fat.length; i++)
			entries[i] = new EnemyWaveEntry(smallTime + counter++ * countingTime, small[i], ENEMY_BYCICLE);
		return entries;
	}
	
	public static Enemy createEnemy(final EnemyWaveEntry entry, final World world, final MainMap map) {
		switch (entry.getId()) {
		case ENEMY_SMALL:
			return new Enemy_small(entry.getPositon().x, entry.getPositon().y, world, map);
		case ENEMY_FAT:
			return new Enemy_fat(entry.getPositon().x, entry.getPositon().y, world, map);
		case ENEMY_BYCICLE:
			return new Enemy_bicycle(entry.getPositon().x, entry.getPositon().y, world, map);
		}
		return null;
	}

}
