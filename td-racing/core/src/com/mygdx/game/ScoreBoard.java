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
	
	private final int COLUMN;

	public ScoreBoard() {
		MainGame.font.getData().setScale(PlayState.PIXEL_TO_METER);
		COLUMN = 53;
		reset(0);
	}

	public void draw(final SpriteBatch spriteBatch) {
		MainGame.font.draw(spriteBatch, "Score: " + (int) this.score, COLUMN, 7);
		MainGame.font.draw(spriteBatch, "Kill Count: " + (int) this.killCount, COLUMN, 6);
		MainGame.font.draw(spriteBatch, "Money: " + (int) this.money + "$", COLUMN, 5);
		MainGame.font.draw(spriteBatch, "Whole Time: " + String.format("%.2f", this.wholeTime) + "s", COLUMN, 4);
		MainGame.font.draw(spriteBatch, "Lap Time: " + String.format("%.2f", this.currentTime) + "s", COLUMN, 3);
		MainGame.font.draw(spriteBatch, "Lap: #" + this.lapNumber + "s", COLUMN, 2);
		MainGame.font.draw(spriteBatch, "Wave: #" + this.waveNumber, COLUMN, 1);
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
	}

}
