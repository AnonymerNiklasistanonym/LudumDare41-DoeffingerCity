package com.mygdx.game.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Level {

	private final Array<Wave> waves;

	private Vector2 carPosition;
	private Vector2[] checkPoints;
	private Vector2 finishLinePosition;
	private String mapName;
	private Vector2 pitStopPosition;
	private Vector2 spawnPoint;
	private boolean[] towersUnlocked;
	private float timebonus;
	private Vector2 healthBarPosition;
	private float moneyPerLap;

	public Level() {
		waves = new Array<Wave>();
		checkPoints = new Vector2[4];
		towersUnlocked = new boolean[4];
	}

	public void addWave(final Wave wave) {
		waves.add(wave);
	}

	public void check(final int i) {
		System.out.println(
				">> Level #" + (i + 1) + " (Map name: " + ((mapName == null) ? "NULL" : mapName) + " | CarPosition: "
						+ carPosition.toString() + " | SpawnPoint: " + spawnPoint.toString() + " | FinishLinePos: "
						+ finishLinePosition.toString() + " | PitStopPos: " + pitStopPosition.toString() + ")");
		for (int j = 0; j < checkPoints.length; j++)
			System.out.print("Checkpoint #" + j + ": " + checkPoints[j] + ", ");
		System.out.println();
		for (int j = 0; j < towersUnlocked.length; j++)
			System.out.print("Tower #" + j + " unlocked: " + Boolean.toString(towersUnlocked[j]) + ", ");
		System.out.println();

		for (int j = 0; j < waves.size; j++)
			waves.get(j).check(j);
	}

	public Vector2 getCarPos() {
		return carPosition;
	}

	public Vector2[] getCheckPoints() {
		return checkPoints;
	}

	public Vector2 getFinishLinePosition() {
		return finishLinePosition;
	}

	public String getMapName() {
		return mapName;
	}

	public Vector2 getPitStopPosition() {
		return pitStopPosition;
	}

	public Vector2 getSpawnPoint() {
		return spawnPoint;
	}

	public boolean[] getTowersUnlocked() {
		return towersUnlocked;
	}

	public Array<Wave> getWaves() {
		return waves;
	}

	public void setCarPosition(final Vector2 carPosition) {
		this.carPosition = carPosition;
	}

	public void setCheckpoints(final Vector2 checkpointOnePosition, final Vector2 checkpointTwoPosition,
			final Vector2 checkpointThreePosition, final Vector2 checkpointFourPosition) {
		checkPoints[0] = checkpointOnePosition;
		checkPoints[1] = checkpointTwoPosition;
		checkPoints[2] = checkpointThreePosition;
		checkPoints[3] = checkpointFourPosition;
	}

	public void setFinishLinePosition(final Vector2 finishLinePosition) {
		this.finishLinePosition = finishLinePosition;
	}

	public void setMapName(final String mapName) {
		this.mapName = mapName;
	}

	public void setPitStopPosition(final Vector2 pitStopPosition) {
		this.pitStopPosition = pitStopPosition;
	}

	public void setSpawnPoint(final Vector2 spawnPoint) {
		this.spawnPoint = spawnPoint;
	}

	public void setTowerUnlocks(final boolean tower1, final boolean tower2, final boolean tower3,
			final boolean tower4) {
		towersUnlocked[0] = tower1;
		towersUnlocked[1] = tower2;
		towersUnlocked[2] = tower3;
		towersUnlocked[3] = tower4;
	}

	public float getTimebonus() {
		return timebonus;
	}

	public void setTimebonus(float timebonus) {
		this.timebonus = timebonus;
	}

	public void setHealthBarPosition(final Vector2 healthBarPosition) {
		this.healthBarPosition = healthBarPosition;
	}

	public Vector2 getHealthBarPosition() {
		return healthBarPosition;
	}

	public float getMoneyPerLap() {
		return moneyPerLap;
	}

	public void setMoneyPerLap(float moneyPerLap) {
		this.moneyPerLap = moneyPerLap;
	}
	
	

}
