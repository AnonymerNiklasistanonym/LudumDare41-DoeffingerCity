package com.mygdx.game.level;

import com.badlogic.gdx.Gdx;

public class MapHandler {

	public static String[][] getMaps() {
		return CsvFileHandler.readMapCsvFile(Gdx.files.internal("maps/levelInfo.csv"));
	}

	public static Level[] addMapsInformationToLevel(final Level[] level) {
		final String[][] mapInformation = getMaps();

		for (int i = 0; i < level.length; i++) {
			level[i].setMapName(mapInformation[i][0]);
			level[i].setCarPos(Float.parseFloat(mapInformation[i][1]), Float.parseFloat(mapInformation[i][2]));
			level[i].setSpawnPont(Float.parseFloat(mapInformation[i][3]),
					Float.parseFloat(mapInformation[i][4]));
			level[i].setFinishLinePosition(Float.parseFloat(mapInformation[i][5]),
					Float.parseFloat(mapInformation[i][6]));
			level[i].setPitStopPosition(Float.parseFloat(mapInformation[i][7]),
					Float.parseFloat(mapInformation[i][8]));
			level[i].setCheckpoints(Float.parseFloat(mapInformation[i][9]),
					Float.parseFloat(mapInformation[i][10]), Float.parseFloat(mapInformation[i][11]),
					Float.parseFloat(mapInformation[i][12]), Float.parseFloat(mapInformation[i][13]),
					Float.parseFloat(mapInformation[i][14]), Float.parseFloat(mapInformation[i][15]),
					Float.parseFloat(mapInformation[i][16]));
			level[i].setTowerUnlocks(Boolean.parseBoolean(mapInformation[i][17]),
					Boolean.parseBoolean(mapInformation[i][18]), Boolean.parseBoolean(mapInformation[i][19]),
					Boolean.parseBoolean(mapInformation[i][20]));
		}
		return level;
	}

}
