package com.mygdx.game.level;

import com.badlogic.gdx.utils.Array;

public class Level {

	private Array<Wave> waves;

	public Level() {
		waves = new Array<Wave>();
	}

	public void addWave(final Wave wave) {
		this.waves.add(wave);
	}

	public Array<Wave> getWaves() {
		return this.waves;
	}

	public void check(final int i) {
		System.out.println(">> Level #" + (i + 1));
		for (int j = 0; j < this.waves.size; j++)
			this.waves.get(j).check(j);
	}

}
