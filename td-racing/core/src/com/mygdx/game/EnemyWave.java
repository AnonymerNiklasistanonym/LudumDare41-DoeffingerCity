package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.World;

public class EnemyWave {
	
	private final int time;
	private final Enemy[] enemies;
	
	public EnemyWave(final int timeInSeconds, final Enemy[] enemies ) {
		this.time =  timeInSeconds;
		this.enemies = enemies;
	}
	
	public Enemy[] getEnemies() {
		return this.enemies;
	}
	
	public int getTime() {
		return this.time;
	}
	
	public static Enemy[] createEnemies(final World world, final MainMap map, final int smallEnemies, final int fatEnemies) {
		final Enemy[] newEnemies = new Enemy[smallEnemies + fatEnemies];
		for (int i = 0; i < smallEnemies; i++) {
			newEnemies[i] = new Enemy_small(220, 20, world, map);
		}
		if (smallEnemies != 0) {
		for (int i = smallEnemies - 1; i < smallEnemies + fatEnemies; i++) {
			newEnemies[i] = new Enemy_fat(220, 40, world, map);
		}
		}
		for (int i = 0; i < newEnemies.length; i++) {
			if (newEnemies[i] == null)
			System.out.println("enemy is null in waves");
		} 
		return newEnemies;
	}

}
