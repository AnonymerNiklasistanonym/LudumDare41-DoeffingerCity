package com.mygdx.game.level;

import com.badlogic.gdx.utils.Array;

public class Wave {

	private final Array<ZombieWave> zombieWaves;

	public Wave() {
		this.zombieWaves = new Array<ZombieWave>();
	}

	public void addNewZombieWave(final ZombieWave zombieWave) {
		this.zombieWaves.add(zombieWave);
	}

	public void check(final int i) {
		System.out.println(">>> Wave #" + (i + 1));
		for (int j = 0; j < zombieWaves.size; j++)
			zombieWaves.get(j).check(j);
	}

	public Array<ZombieWave> getZombieWaves() {
		return this.zombieWaves;
	}

}
