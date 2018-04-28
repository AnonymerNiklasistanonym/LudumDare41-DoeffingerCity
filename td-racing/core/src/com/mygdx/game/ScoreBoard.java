package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.gamestate.state.PlayState;

public class ScoreBoard {

	private float score;
	private float money;
	private int waveNumber;
	private float wholeTime;
	private float currentTime;
	private int lapNumber;
	private int killCount;
	private float healthPoints;
	private final PlayState playState;
	private final int COLUMN;
	private int level;
	private final boolean debug;

	public ScoreBoard(final PlayState playState, final boolean debug) {
		this.playState = playState;
		MainGame.font.getData().setScale(PlayState.PIXEL_TO_METER);
		MainGame.fontOutline.getData().setScale(PlayState.PIXEL_TO_METER);

		COLUMN = 53;
		this.healthPoints = 100;
		this.debug = debug;
		reset(0);
	}

	public void draw(final SpriteBatch spriteBatch) {

		MainGame.font.getData().setScale(PlayState.PIXEL_TO_METER);

		if (this.debug) {
			MainGame.fontOutline.setColor(1, 0, 0, 1);
			MainGame.fontOutline.draw(spriteBatch, "Spawn Small Enemy: F", 0.2f, 32);
			MainGame.fontOutline.draw(spriteBatch, "Spawn Fat Enemy: G", 0.2f, 31);
			MainGame.fontOutline.draw(spriteBatch, "Spawn Bycicle Enemy: H", 0.2f, 30);
			MainGame.fontOutline.draw(spriteBatch, "Spawn Lincoln Enemy: I", 0.2f, 29);

			MainGame.fontOutline.draw(spriteBatch, "Debug Box2D: X", 0.2f, 27);
			MainGame.fontOutline.draw(spriteBatch, "Debug Collision: C", 0.2f, 26);
			MainGame.fontOutline.draw(spriteBatch, "Debug Way: V", 0.2f, 25);
			MainGame.fontOutline.draw(spriteBatch, "Debug Distance: B", 0.2f, 24);

			MainGame.fontOutline.draw(spriteBatch, "Go to the next level: 5", 0.2f, 22);
			MainGame.fontOutline.draw(spriteBatch, "Go to the next wave: 6", 0.2f, 21);
			MainGame.fontOutline.draw(spriteBatch, "Get Money: 7", 0.2f, 20);
			MainGame.fontOutline.draw(spriteBatch, "Die instantly: 8", 0.2f, 19);
			MainGame.fontOutline.draw(spriteBatch, "Kill all enemies: 9", 0.2f, 18);

			MainGame.fontOutline.draw(spriteBatch, "Speed up the world + 1: Right arrow", 0.2f, 16);
			MainGame.fontOutline.draw(spriteBatch, "Reset world speed (=1): Left arrow", 0.2f, 15);

			MainGame.fontOutline.draw(spriteBatch, "Unlock all towers: T", 0.2f, 13);

			MainGame.font.setColor(1, 1, 1, 1);
		}

		MainGame.font.draw(spriteBatch, "SOUND: U", 58, 35);
		MainGame.font.draw(spriteBatch, "EXIT: ESC", 58, 34);
		MainGame.font.draw(spriteBatch, "PAUSE: P", 58, 33);

		MainGame.font.draw(spriteBatch, "Score: " + (int) this.score, 0.2f, 35);
		MainGame.font.draw(spriteBatch, "Kills: " + (int) this.killCount, 0.2f, 34);

		MainGame.font.draw(spriteBatch, "Level: " + this.level, 0.2f, 2);
		MainGame.font.draw(spriteBatch, "Wave: " + this.waveNumber, 0.2f, 1);

		MainGame.font.draw(spriteBatch, "Life: " + (int) this.healthPoints, COLUMN, 3);
		MainGame.font.draw(spriteBatch, "Money: " + (int) this.money + " $", COLUMN, 2);
		MainGame.font.draw(spriteBatch, "Lap: " + (int) this.currentTime + " sec (#" + this.lapNumber + ")", COLUMN, 1);
	}

	public void reduceLife(float damage) {
		this.healthPoints -= damage;
		if (this.healthPoints <= 0)
			playState.playerIsDeadCallback();
	}

	public void update(final float deltaTime) {
		this.wholeTime += deltaTime;
		this.currentTime += deltaTime;
	}

	public void killedEnemy(final float score, final float money) {
		this.killCount++;
		this.score += score;
		this.money += money;
	}

	public void newLap(final int newMoney) {
		this.lapNumber++;
		this.currentTime = 0;
		this.money += newMoney;
	}

	public void newWave() {
		this.waveNumber++;
	}

	public void reset(final int money) {
		this.currentTime = 0f;
		this.score = 0;
		this.killCount = 0;
		this.money = money;
		this.wholeTime = 0;
		this.currentTime = 0f;
		this.lapNumber = 0;
		this.waveNumber = 0;
		this.healthPoints = 100;
	}

	public int getMoney() {
		return (int) this.money;
	}

	public void addMoney(final int cost) {
		this.money += cost;
	}

	public float getTime() {
		return this.wholeTime;
	}

	public int getScore() {
		return (int) this.score;
	}

	public float getCurrentTime() {
		return currentTime;
	}

	public int getWaveNumber() {
		return waveNumber;
	}

	public void setWaveNumber(int waveNumber) {
		this.waveNumber = waveNumber;
	}

	public void setLevel(final int i) {
		this.level = i;
	}

	public float getHelath() {
		return this.healthPoints;
	}

	public int getLevel() {
		return this.level;
	}

}
