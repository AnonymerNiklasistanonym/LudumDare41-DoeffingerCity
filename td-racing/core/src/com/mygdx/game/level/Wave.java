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

		final Thread[][] threads = new Thread[zombieWaves.size][4];

		for (int i = 0; i < zombieWaves.size; i++) {

			final int wave = i;

			threads[wave][0] = new Thread(new Runnable() {
				@Override
				public void run() {
					int counter = 0;
					for (int j = 0; j < zombieWaves.get(wave).getBycicleZombieNumber(); j++) {
						allEnemies.add(new EnemyBicycle(entryPosition.x, entryPosition.y, world, map,
								currentTime + zombieWaves.get(wave).getEntryTime()
										+ counter++ * zombieWaves.get(wave).getBycicleTimeDelta()));
					}
				}
			});

			threads[wave][1] = new Thread(new Runnable() {
				@Override
				public void run() {
					int counter = 0;
					for (int j = 0; j < zombieWaves.get(wave).getFatZombieNumber(); j++) {
						allEnemies.add(new EnemyFat(entryPosition.x, entryPosition.y, world, map,
								currentTime + zombieWaves.get(wave).getEntryTime()
										+ counter++ * zombieWaves.get(wave).getFatTimeDelta()));
					}
				}
			});
			threads[wave][2] = new Thread(new Runnable() {
				@Override
				public void run() {
					int counter = 0;
					for (int j = 0; j < zombieWaves.get(wave).getSmallZombieNumber(); j++) {
						allEnemies.add(new EnemySmall(entryPosition.x, entryPosition.y, world, map,
								currentTime + zombieWaves.get(wave).getEntryTime()
										+ counter++ * zombieWaves.get(wave).getSmallTimeDelta()));
					}
				}
			});
			threads[wave][3] = new Thread(new Runnable() {
				@Override
				public void run() {
					int counter = 0;

					for (int j = 0; j < zombieWaves.get(wave).getLincolnZombieNumber(); j++) {
						allEnemies.add(new EnemyLincoln(entryPosition.x, entryPosition.y, world, map,
								currentTime + zombieWaves.get(wave).getEntryTime()
										+ counter++ * zombieWaves.get(wave).getLincolnTimeDelta()));
					}
				}
			});

		}
		for (int i = 0; i < threads.length; i++) {
			for (int j = 0; j < threads[i].length; j++) {
				threads[i][j].start();
			}
		}
		try {
			for (int i = 0; i < threads.length; i++) {
				for (int j = 0; j < threads[i].length; j++) {
					threads[i][j].join();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return allEnemies;
	}
}
