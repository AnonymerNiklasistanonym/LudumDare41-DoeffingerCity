package com.mygdx.game.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Level {

	private Array<Wave> waves;
	private String mapName;
	private Vector2 carPos;
	private boolean[] towersUnlocked;
	private Vector2[] checkPoints;
	private Vector2 spawnPoint;
	private Vector2 pitStopPosition;
	private Vector2 finishLinePosition;

	public Level() {
		waves = new Array<Wave>();
	}

	public Array<Wave> getWaves() {
		return this.waves;
	}

	public String getMapName() {
		return mapName;
	}

	public Vector2 getCarPos() {
		return this.carPos;
	}

	public boolean[] getTowersUnlocked() {
		return towersUnlocked;
	}

	public Vector2[] getCheckPoints() {
		return checkPoints;
	}

	public Vector2 getSpawnPoint() {
		return spawnPoint;
	}

	public Vector2 getPitStopPosition() {
		return pitStopPosition;
	}

	public Vector2 getFinishLinePosition() {
		return finishLinePosition;
	}

	public void check(final int i) {
		System.out.println(">> Level #" + (i + 1) + " (Map name: " + ((this.mapName == null) ? "NULL" : this.mapName)
				+ " | CarPosition: " + this.carPos.toString() + " | SpawnPoint: " + this.spawnPoint.toString()
				+ " | FinishLinePos: " + this.finishLinePosition.toString() + " | PitStopPos: "
				+ this.pitStopPosition.toString() + ")");
		for (int j = 0; j < this.checkPoints.length; j++)
			System.out.print("Checkpoint #" + j + ": " + this.checkPoints[j] + ", ");
		System.out.println();
		for (int j = 0; j < this.towersUnlocked.length; j++)
			System.out.print("Tower #" + j + " unlocked: " + Boolean.toString(this.towersUnlocked[j]) + ", ");
		System.out.println();

		for (int j = 0; j < this.waves.size; j++)
			this.waves.get(j).check(j);
	}

	public void addWave(final Wave wave) {
		this.waves.add(wave);
		this.checkPoints = new Vector2[4];
		this.towersUnlocked = new boolean[4];
	}

	public void setMapName(final String mapName) {
		this.mapName = mapName;
	}

	public void setCarPos(final float xPosition, final float yPosition) {
		this.carPos = new Vector2(xPosition, yPosition);
	}

	public void setTowerUnlocks(boolean tower1, boolean tower2, boolean tower3, boolean tower4) {
		this.towersUnlocked[0] = tower1;
		this.towersUnlocked[1] = tower2;
		this.towersUnlocked[2] = tower3;
		this.towersUnlocked[3] = tower4;
	}

	public void setCheckpoints(float f, float g, float h, float i, float j, float k, float l, float m) {
		this.checkPoints[0] = new Vector2(f, g);
		this.checkPoints[1] = new Vector2(h, i);
		this.checkPoints[2] = new Vector2(j, k);
		this.checkPoints[3] = new Vector2(l, m);
	}

	public void setSpawnPont(float f, float g) {
		this.spawnPoint = new Vector2(f, g);
	}

	public void setPitStopPosition(float f, float g) {
		this.pitStopPosition = new Vector2(f, g);
	}

	public void setFinishLinePosition(float f, float g) {
		this.finishLinePosition = new Vector2(f, g);
	}

}
