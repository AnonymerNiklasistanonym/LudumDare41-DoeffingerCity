package com.mygdx.game.level;

import com.badlogic.gdx.files.FileHandle;

public class LevelHandler {

	public static Level[] loadLevels() {
		// get all level CSV files
		final FileHandle[] files = CsvFileHandler.getAllLevelFiles();
		// create an array of the same length for all levels
		final Level[] level = new Level[files.length];
		// fill the empty array with the CSV file information
		for (int i = 0; i < level.length; i++)
			level[i] = readLevel(files[i]);
		return level;
	}

	private static Level readLevel(final FileHandle csvFile) {
		// create a new empty level
		final Level level = new Level();
		// create a *walking* wave number which begins at 1
		int walkingWaveNumber = 1;
		// create a new empty *walking* wave
		Wave walkingWave = new Wave();

		// iterate through each line of the CSV file and collect information
		for (final float[] entry : CsvFileHandler.readFloatCsvFile(csvFile)) {
			// if a new wave number was found add the current wave to level
			if (walkingWaveNumber != (int) entry[0]) {
				System.out.println("walkingWaveNumber: " + walkingWaveNumber + ", entry[0]: " + (int) entry[0]);
				walkingWaveNumber = (int) entry[0];
				level.addWave(walkingWave);
				walkingWave = new Wave();
			}
			// also add the current found ZombieWave to the current wave
			walkingWave.addNewZombieWave(new ZombieWave(entry[1], (int) entry[2], entry[3], (int) entry[4], entry[5],
					(int) entry[6], entry[7], (int) entry[8], entry[9]));
		}

		// check if there is still a not empty walking wave and add it to the level
		if (walkingWave.getZombieWaves().size != 0)
			level.addWave(walkingWave);

		// return the loaded wave
		return level;
	}

}
