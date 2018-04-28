package com.mygdx.game.level;

import com.badlogic.gdx.utils.Array;

public class Wave {

	private Array<ZombieWave> zombieWaves;

	public void addNewZombieWave(final ZombieWave zombieWave) {
		zombieWaves.add(zombieWave);
	}

}
