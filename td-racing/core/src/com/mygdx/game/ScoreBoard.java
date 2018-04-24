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
	private int level ;

	public ScoreBoard(PlayState playState) {
		this.playState = playState;
		MainGame.font.getData().setScale(PlayState.PIXEL_TO_METER);
		COLUMN = 53;
		this.healthPoints = 100;
		reset(0);
	}

	public void draw(final SpriteBatch spriteBatch) {
		MainGame.font.draw(spriteBatch, "SOUND: U", 58, 35);
		MainGame.font.draw(spriteBatch, "EXIT: ESC", 58, 34);
		MainGame.font.draw(spriteBatch, "Whole Time: " + (int) this.wholeTime + " sec", 1, 35);
		MainGame.font.draw(spriteBatch, "Score: " + (int) this.score, 1, 34);
		MainGame.font.draw(spriteBatch, "Kills: " + (int) this.killCount, 1, 33);
		MainGame.font.draw(spriteBatch, "Level: " + this.level, 1, 32);
		MainGame.font.draw(spriteBatch, "Wave: " + this.waveNumber, 1, 31);
		MainGame.font.draw(spriteBatch, "Life: " + (int) this.healthPoints, COLUMN, 3);
		MainGame.font.draw(spriteBatch, "Money: " + (int) this.money + " $", COLUMN, 2);
		MainGame.font.draw(spriteBatch, "Lap: " + (int) this.currentTime + " sec (#" + this.lapNumber + ")", COLUMN, 1);		
		
	}

	public void reduceLife(float damage) {
		this.healthPoints -= damage;
		if (this.healthPoints < 0)
			playState.playIsDeadCallback();
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

	public void setLevel(int i) {
		this.level = i;
	}

}
