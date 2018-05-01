package com.mygdx.game.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.objects.Enemy;
import com.mygdx.game.objects.Map;
import com.mygdx.game.objects.enemies.EnemyBicycle;
import com.mygdx.game.objects.enemies.EnemyFat;
import com.mygdx.game.objects.enemies.EnemyLincoln;
import com.mygdx.game.objects.enemies.EnemySmall;
import com.mygdx.game.objects.enemies.EnemySpider;

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

	public Array<Enemy> createEnemies(final Vector2 entryPosition, final World world, final Map map,
			final float currentTime) {

		final Array<Enemy> allEnemies = new Array<Enemy>();

		for (int wave = 0; wave < zombieWaves.size; wave++) {
			int counter = 0;

			for (int j = 0; j < zombieWaves.get(wave).getSpiderZombieNumber(); j++) {
				allEnemies.add(
						new EnemySpider(entryPosition, world, map, currentTime + zombieWaves.get(wave).getEntryTime()
								+ counter++ * zombieWaves.get(wave).getSpiderTimeDelta()));
			}

			for (int j = 0; j < zombieWaves.get(wave).getBycicleZombieNumber(); j++) {
				allEnemies.add(
						new EnemyBicycle(entryPosition, world, map, currentTime + zombieWaves.get(wave).getEntryTime()
								+ counter++ * zombieWaves.get(wave).getBycicleTimeDelta()));
			}

			for (int j = 0; j < zombieWaves.get(wave).getFatZombieNumber(); j++) {
				allEnemies.add(new EnemyFat(entryPosition, world, map, currentTime
						+ zombieWaves.get(wave).getEntryTime() + counter++ * zombieWaves.get(wave).getFatTimeDelta()));
			}

			for (int j = 0; j < zombieWaves.get(wave).getSmallZombieNumber(); j++) {
				allEnemies.add(
						new EnemySmall(entryPosition, world, map, currentTime + zombieWaves.get(wave).getEntryTime()
								+ counter++ * zombieWaves.get(wave).getSmallTimeDelta()));
			}

			for (int j = 0; j < zombieWaves.get(wave).getLincolnZombieNumber(); j++) {
				allEnemies.add(
						new EnemyLincoln(entryPosition, world, map, currentTime + zombieWaves.get(wave).getEntryTime()
								+ counter++ * zombieWaves.get(wave).getLincolnTimeDelta()));
			}

		}

		return allEnemies;
	}

	public Array<ZombieWave> getZombieWaves() {
		return this.zombieWaves;
	}
}
