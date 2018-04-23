package com.mygdx.game;

public class EnemyWave {
	
	private final int time;
	private final Enemy[] enemies;
	
	public EnemyWave(final int time, final Enemy[] enemies ) {
		this.time =  time;
		this.enemies = enemies;
	}
	
	public Enemy[] getEnemies() {
		return this.enemies;
	}
	
	public int getTime() {
		return this.time;
	}

}
