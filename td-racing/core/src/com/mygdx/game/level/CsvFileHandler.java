package com.mygdx.game.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class CsvFileHandler {

	/**
	 * Read a CSV file and convert it to an Array of an Array of floats
	 * 
	 * @param file
	 *            (CSV level file)
	 * @return All float values in an array of arrays
	 */
	public static float[][] readEnemyCsvFile(final FileHandle file) {
		final String[] textLines = file.readString().split("\n");
		if (textLines.length == 0) {
			System.out.println("NOOOOO - You need to load levels");
			return new float[0][0];
		}
		final float[][] information = new float[textLines.length - 1][10];
		// skip the first line because it is not important
		for (int i = 1; i < textLines.length; i++) {
			final String[] stringArray = textLines[i].trim().split(",");
			final float[] test = new float[stringArray.length];
			for (int j = 0; j < test.length; j++)
				test[j] = Float.parseFloat(stringArray[j]);
			information[i - 1] = test;
		}
		return information;
	}
	
	/**
	 * Read a CSV file and convert it to an Array of Strings which contain the map name
	 * 
	 * @param file
	 *            (CSV level file)
	 * @return All map names in an array
	 */
	public static String[] readMapCsvFile(final FileHandle file) {
		final String[] textLines = file.readString().split("\n");
		final String[] information = new String[textLines.length - 1];
		// skip the first line because it is not important
		for (int i = 1; i < textLines.length; i++) {
			final String[] stringArray = textLines[i].trim().split(",");
			information[i - 1] = stringArray[1];
		}
		return information;
	}

	/**
	 * Get all Level CSV files
	 * 
	 * @return Array which contains all CSV level files
	 */
	public static FileHandle[] getAllLevelFiles() {
		return Gdx.files.internal("./bin/level").list();
	}

}
