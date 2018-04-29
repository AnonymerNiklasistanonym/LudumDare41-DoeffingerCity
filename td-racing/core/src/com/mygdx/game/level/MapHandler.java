package com.mygdx.game.level;

import com.badlogic.gdx.Gdx;

public class MapHandler {
	
	public static String[] getMaps() {
		return CsvFileHandler.readMapCsvFile(Gdx.files.internal("maps/levelInfo.csv"));
	}

}
