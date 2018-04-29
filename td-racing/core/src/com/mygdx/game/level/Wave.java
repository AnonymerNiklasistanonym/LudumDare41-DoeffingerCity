package com.mygdx.game.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MainMap;
import com.mygdx.game.objects.Enemy;
import com.mygdx.game.objects.enemies.EnemyBicycle;
import com.mygdx.game.objects.enemies.EnemyFat;
import com.mygdx.game.objects.enemies.EnemyLincoln;
import com.mygdx.game.objects.enemies.EnemySmall;

public class Wave {

	private final Array<ZombieWave> zombieWaves;

	public Wave() {
		this.zombieWaves = new Array<ZombieWave>();
	}

	public void addNewZombieWave(final ZombieWave zombieWave) {
		this.zombieWaves.add(zombieWave);
	}

	public void check(final int i) {
		System.out.println(">>> Wave #" + (i + 1));
		for (int j = 0; j < zombieWaves.size; j++)
			zombieWaves.get(j).check(j);
	}

	public Array<ZombieWave> getZombieWaves() {
		return this.zombieWaves;
	}

	public Array<Enemy> createEnemies(final Vector2 entryPosition, final World world, final MainMap map,
			final float currentTime) {

		final Array<Enemy> allEnemies = new Array<Enemy>();

		for (int i = 0; i < this.zombieWaves.size; i++) {

			int counter = 0;

			for (int j = 0; j < this.zombieWaves.get(i).getBycicleZombieNumber(); j++) {
				allEnemies.add(new EnemyBicycle(entryPosition.x, entryPosition.y, world, map,
						currentTime + this.zombieWaves.get(i).getEntryTime()
								+ counter++ * this.zombieWaves.get(i).getBycicleTimeDelta()));
			}
			for (int j = 0; j < this.zombieWaves.get(i).getFatZombieNumber(); j++) {
				allEnemies.add(new EnemyFat(entryPosition.x, entryPosition.y, world, map,
						currentTime + this.zombieWaves.get(i).getEntryTime()
								+ counter++ * this.zombieWaves.get(i).getFatTimeDelta()));
			}
			for (int j = 0; j < this.zombieWaves.get(i).getSmallZombieNumber(); j++) {
				allEnemies.add(new EnemySmall(entryPosition.x, entryPosition.y, world, map,
						currentTime + this.zombieWaves.get(i).getEntryTime()
								+ counter++ * this.zombieWaves.get(i).getSmallTimeDelta()));
			}
			for (int j = 0; j < this.zombieWaves.get(i).getLincolnZombieNumber(); j++) {
				allEnemies.add(new EnemyLincoln(entryPosition.x, entryPosition.y, world, map,
						currentTime + this.zombieWaves.get(i).getEntryTime()
								+ counter++ * this.zombieWaves.get(i).getLincolnTimeDelta()));
			}

		}
		return allEnemies;
	}
}
