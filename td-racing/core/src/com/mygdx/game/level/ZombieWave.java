package com.mygdx.game.level;

public class ZombieWave {

	private final float entryTime, smallTimeDelta, fatZombieDelta, bycicleTimeDelta, spiderTimeDelta, lincolnTimeDelta;
	private final int smallZombieNumber, fatZombieNumber, bycicleZombieNumber, spiderZombieNumber, lincolnZombieNumber;

	public ZombieWave(final float entryTime, final int smallZombieNumber, final float smallTimeDelta,
			final int fatZombieNumber, final float fatZombieDelta, final int bycicleZombieNumber,
			final float bycicleTimeDelta, final int spiderZombieNumber, final float spiderTimeDelta,
			final int lincolnZombieNumber, final float lincolnTimeDelta) {
		this.entryTime = entryTime;
		this.smallZombieNumber = smallZombieNumber;
		this.smallTimeDelta = smallTimeDelta;
		this.fatZombieNumber = fatZombieNumber;
		this.fatZombieDelta = fatZombieDelta;
		this.bycicleZombieNumber = bycicleZombieNumber;
		this.bycicleTimeDelta = bycicleTimeDelta;
		this.spiderZombieNumber = spiderZombieNumber;
		this.spiderTimeDelta = spiderTimeDelta;
		this.lincolnZombieNumber = lincolnZombieNumber;
		this.lincolnTimeDelta = lincolnTimeDelta;
	}

	public void check(final int i) {
		System.out.println(">>>> ZombieWave #" + (i + 1));
		System.out.println("Time after wave started: " + entryTime + " Small Zombie #: " + smallZombieNumber
				+ " Small time delta: " + smallTimeDelta + " Fat Zombie #: " + fatZombieNumber + " Fat time delta: "
				+ fatZombieDelta + " Bicycle Zombie #: " + bycicleZombieNumber + " Bicycle time delta: "
				+ bycicleTimeDelta + " Spider Zombie #: " + spiderZombieNumber + " Spider time delta: "
				+ spiderTimeDelta + " Lincoln Zombie #: " + lincolnZombieNumber + " Lincoln time delta: "
				+ lincolnTimeDelta);
	}

	public float getBycicleTimeDelta() {
		return bycicleTimeDelta;
	}

	public int getBycicleZombieNumber() {
		return bycicleZombieNumber;
	}

	public float getEntryTime() {
		return entryTime;
	}

	public float getFatTimeDelta() {
		return fatZombieDelta;
	}

	public int getFatZombieNumber() {
		return fatZombieNumber;
	}

	public float getLincolnTimeDelta() {
		return lincolnTimeDelta;
	}

	public int getLincolnZombieNumber() {
		return lincolnZombieNumber;
	}

	public float getSpiderTimeDelta() {
		return spiderTimeDelta;
	}

	public int getSpiderZombieNumber() {
		return spiderZombieNumber;
	}

	public float getSmallTimeDelta() {
		return smallTimeDelta;
	}

	public int getSmallZombieNumber() {
		return smallZombieNumber;
	}

}