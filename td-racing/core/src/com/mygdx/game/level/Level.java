package com.mygdx.game.level;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.CsvReader;

public class Level {

	private Array<Wave> waves;

	public Level(final String csvFileName) {
		waves = new Array<Wave>();
		readLevel(csvFileName);
	}

	public void readLevel(final String csvFileName) {
		// read CSV table
		final float[][] csvContent = CsvReader.readFloatCsvFile(csvFileName);

		// iterate through each line and collect information
		int walkingWaveNumber = 1;
		Wave walkingWave = new Wave();

		// if a new wave number was found add wave to waves
		for (final float[] entry : csvContent) {
			if (walkingWaveNumber != (int) entry[0]) {
				walkingWaveNumber = (int) entry[0];
				waves.add(walkingWave);
				walkingWave = new Wave();
			}
			// if not a new wave number was found add a new ZombieWave to wave
			walkingWave.addNewZombieWave(new ZombieWave(entry[1], (int) entry[2], entry[3], (int) entry[4], entry[5],
					(int) entry[6], entry[7], (int) entry[8], entry[9]));
		}
	}

}
