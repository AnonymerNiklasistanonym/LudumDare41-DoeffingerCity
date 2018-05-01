package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.states.PlayState;

public class ScoreBoard {

	private float score;
	private float money;
	private int waveNumber;
	private float wholeTime;
	private float currentTime;
	private int lapNumber;
	private int killCount;
	private float healthPoints;
	private float maxHealthPoints;
	private final ScoreBoardCallbackInterface playState;
	private final int COLUMN;
	private int level;
	private boolean debugDisplay;

	public ScoreBoard(final ScoreBoardCallbackInterface playState) {
		this.playState = playState;
		MainGame.font.getData().setScale(PlayState.PIXEL_TO_METER);
		MainGame.fontOutline.getData().setScale(PlayState.PIXEL_TO_METER);

		COLUMN = 53;
		healthPoints = 100;
		maxHealthPoints = healthPoints;
		debugDisplay = true;
		reset(0);
	}

	public void draw(final SpriteBatch spriteBatch) {

		MainGame.fontOutline.getData().setScale(PlayState.PIXEL_TO_METER);
		MainGame.font.getData().setScale(PlayState.PIXEL_TO_METER);

		if (MainGame.DEVELOPER_MODE && debugDisplay) {
			MainGame.fontOutline.setColor(1, 0, 0, 1);
			MainGame.fontOutline.draw(spriteBatch, "Spawn Small Enemy: F", 0.2f, 32);
			MainGame.fontOutline.draw(spriteBatch, "Spawn Fat Enemy: G", 0.2f, 31);
			MainGame.fontOutline.draw(spriteBatch, "Spawn Bycicle Enemy: H", 0.2f, 30);
			MainGame.fontOutline.draw(spriteBatch, "Spawn Spider Enemy: J", 0.2f, 29);
			MainGame.fontOutline.draw(spriteBatch, "Spawn Lincoln Enemy: K", 0.2f, 28);

			MainGame.fontOutline.draw(spriteBatch, "Debug Box2D: X", 0.2f, 26);
			MainGame.fontOutline.draw(spriteBatch, "Debug Collision: C", 0.2f, 25);
			MainGame.fontOutline.draw(spriteBatch, "Debug Way: V", 0.2f, 24);
			MainGame.fontOutline.draw(spriteBatch, "Debug Distance: B", 0.2f, 23);

			MainGame.fontOutline.draw(spriteBatch, "Go to the next level: 5", 0.2f, 21);
			MainGame.fontOutline.draw(spriteBatch, "Kill all enemies/Next wave: 9", 0.2f, 20);
			MainGame.fontOutline.draw(spriteBatch, "Get Money: 7", 0.2f, 19);
			MainGame.fontOutline.draw(spriteBatch, "Die instantly: 8", 0.2f, 18);
			MainGame.fontOutline.draw(spriteBatch, "Advance Tutorial: 0", 0.2f, 17);

			MainGame.fontOutline.draw(spriteBatch, "Speed up the world + 1: M", 0.2f, 15);
			MainGame.fontOutline.draw(spriteBatch, "Reset world speed (=1): N", 0.2f, 14);

			MainGame.fontOutline.draw(spriteBatch, "Activate all enemies: E", 0.2f, 13);
			MainGame.fontOutline.draw(spriteBatch, "Unlock all towers: T", 0.2f, 12);
			MainGame.fontOutline.draw(spriteBatch, "Add 1000 to score: R", 0.2f, 11);
			MainGame.fontOutline.draw(spriteBatch, "Toggle this display: F8", 0.2f, 9);
		}

		MainGame.font.setColor(1, 1, 1, 1);

		MainGame.font.draw(spriteBatch, "SOUND: U", 58, 35);
		MainGame.font.draw(spriteBatch, "EXIT: ESC", 58, 34);
		MainGame.font.draw(spriteBatch, "PAUSE: P", 58, 33);

		MainGame.font.draw(spriteBatch, "Score: " + (int) score, 0.2f, 35);
		MainGame.font.draw(spriteBatch, "Kills: " + (int) killCount, 0.2f, 34);

		MainGame.font.draw(spriteBatch, "Level: " + level, 0.2f, 2);
		MainGame.font.draw(spriteBatch, "Wave: " + waveNumber, 0.2f, 1);

		MainGame.font.draw(spriteBatch, "Life: " + (int) healthPoints, COLUMN, 3);
		MainGame.font.draw(spriteBatch, "Money: " + (int) money + " $", COLUMN, 2);
		MainGame.font.draw(spriteBatch, "Lap: " + (int) currentTime + " sec (#" + lapNumber + ")", COLUMN, 1);
	}

	public void reduceLife(float damage) {
		healthPoints -= damage;
		if (healthPoints <= 0)
			playState.playerIsDeadCallback();
	}

	public void update(final float deltaTime) {
		wholeTime += deltaTime;
		currentTime += deltaTime;
	}

	public void killedEnemy(final float score, final float money) {
		killCount++;
		this.score += score;
		this.money += money;
	}

	public void newLap(final int newMoney) {
		lapNumber++;
		currentTime = 0;
		money += newMoney;
	}

	public void newWave() {
		waveNumber++;
	}

	public void reset(final int money) {
		currentTime = 0f;
		score = 0;
		killCount = 0;
		this.money = money;
		wholeTime = 0;
		currentTime = 0f;
		lapNumber = 0;
		waveNumber = 0;
		healthPoints = 100;
	}

	public int getMoney() {
		return (int) money;
	}

	public void addMoney(final int money) {
		this.money += money;
	}

	public float getTime() {
		return wholeTime;
	}

	public int getScore() {
		return (int) score;
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

	public void setLevel(final int level) {
		this.level = level;
	}

	public float getHealth() {
		return healthPoints;
	}

	public float getMaxHealth() {
		return maxHealthPoints;
	}

	public int getLevel() {
		return level;
	}

	public void addScore(int i) {
		score += i;
	}

	public boolean getDebugDisplay() {
		return debugDisplay;
	}

	public void setDebugDisplay(boolean debugDisplay) {
		this.debugDisplay = debugDisplay;
	}

}
