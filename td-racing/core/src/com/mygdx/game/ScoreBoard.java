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

	public ScoreBoard() {
		MainGame.font.getData().setScale(PlayState.PIXEL_TO_METER);
		reset();
	}

	public void draw(final SpriteBatch spriteBatch) {
		MainGame.font.draw(spriteBatch, "Score: " + (int) this.score, 50, 7);
		MainGame.font.draw(spriteBatch, "Kill Count: " + (int) this.killCount, 50, 6);
		MainGame.font.draw(spriteBatch, "Money: " + (int) this.money + "$", 50, 5);
		MainGame.font.draw(spriteBatch, "Whole Time: " + this.wholeTime + "s", 50, 4);
		MainGame.font.draw(spriteBatch, "Lap Time: " + this.currentTime + "s", 50, 3);
		MainGame.font.draw(spriteBatch, "Lap: #" + this.lapNumber + "s", 50, 2);
		MainGame.font.draw(spriteBatch, "Wave: #" + this.waveNumber, 50, 1);
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

	public void reset() {
		this.currentTime = 0f;
		this.score = 0;
		this.killCount = 0;
		this.money = 0;
		this.wholeTime = 0;
		this.currentTime = 0f;
		this.lapNumber = 0;
		this.waveNumber = 0;
	}

}
