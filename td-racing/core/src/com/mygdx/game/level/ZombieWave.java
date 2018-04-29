package com.mygdx.game.level;

public class ZombieWave {
	private final float entryTime;
	private final int smallZombieNumber, fatZombieNumber, bycicleZombieNumber, lincolnZombieNumber;
	private final float smallTimeDelta, fatZombieDelta, bycicleTimeDelta, lincolnTimeDelta;

	public ZombieWave(final float entryTime, final int smallZombieNumber, final float smallTimeDelta,
			final int fatZombieNumber, final float fatZombieDelta, final int bycicleZombieNumber,
			final float bycicleTimeDelta, final int lincolnZombieNumber, final float lincolnTimeDelta) {
		this.entryTime = entryTime;
		this.smallZombieNumber = smallZombieNumber;
		this.smallTimeDelta = smallTimeDelta;
		this.fatZombieNumber = fatZombieNumber;
		this.fatZombieDelta = fatZombieDelta;
		this.bycicleZombieNumber = bycicleZombieNumber;
		this.bycicleTimeDelta = bycicleTimeDelta;
		this.lincolnZombieNumber = lincolnZombieNumber;
		this.lincolnTimeDelta = lincolnTimeDelta;
	}

	public float getEntryTime() {
		return entryTime;
	}

	public int getSmallZombieNumber() {
		return smallZombieNumber;
	}

	public int getFatZombieNumber() {
		return fatZombieNumber;
	}

	public int getBycicleZombieNumber() {
		return bycicleZombieNumber;
	}

	public int getLincolnZombieNumber() {
		return lincolnZombieNumber;
	}

	public float getSmallTimeDelta() {
		return smallTimeDelta;
	}

	public float getFatTimeDelta() {
		return fatZombieDelta;
	}

	public float getBycicleTimeDelta() {
		return bycicleTimeDelta;
	}

	public float getLincolnTimeDelta() {
		return lincolnTimeDelta;
	}

	public void check(final int i) {
		System.out.println(">>>> ZombieWave #" + (i + 1));
		System.out.println("Time after wave started: " + this.entryTime + " Small Zombie #: " + this.smallZombieNumber
				+ " Small time delta: " + this.smallTimeDelta + " Fat Zombie #: " + this.fatZombieNumber
				+ " Fat time delta: " + this.fatZombieDelta + " Bicycle Zombie #: " + this.bycicleZombieNumber
				+ " Bicycle time delta: " + this.bycicleTimeDelta + " Lincoln Zombie #: " + this.lincolnZombieNumber
				+ " Lincoln time delta: " + this.lincolnTimeDelta);
	}

}