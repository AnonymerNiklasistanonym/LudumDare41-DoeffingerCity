package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class CsvReader {

	public static float[][] readFloatCsvFile(final String fileName) {

		final FileHandle file = Gdx.files.internal(fileName);
		final String text = file.readString();
		final String[] textLines = text.split("\n");
		final float[][] information = new float[textLines.length - 1][10];
		for (int i = 1; i < textLines.length; i++) {
			String[] stringArray = textLines[i].split(",");
			float[] test = new float[10];
			for (int j = 0; j < 10; j++)
				test[j] = Float.parseFloat(stringArray[i]);
			information[i - 1] = test;
		}
//		for (final String[] test : information)
//			System.out.println("Wave: " + test[0] + " Time after wave started: " + test[1] + " Small Zombie #: "
//					+ test[2] + " Small time delta: " + test[3] + " Fat Zombie #: " + test[4] + " Fat time delta: "
//					+ test[5] + " Bicycle Zombie #: " + test[6] + "Bicycle time delta: " + test[7]
//					+ " Lincoln Zombie #: " + test[8] + "	Lincoln time delta: " + test[9]);
		return information;
	}

}
