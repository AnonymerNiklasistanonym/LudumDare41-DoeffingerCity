package com.mygdx.game.menu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.MainGame;

public class HighscoreButton {
	
	public static Texture texture;
	
	private final Sprite button;
	private final int place;
	private final String name;
	private final int score;	
	private final float fontXNumber, fontYNumber, fontXName, fontYName, fontXScore, fontYScore;
	
	public HighscoreButton(final int place, final int score, final String name, final float xPosition, final float yPosition) {
		this.place = place;
		this.name = name;
		this.score = score;
		this.button = new Sprite(texture);
		this.button.setSize(texture.getWidth() / 2, texture.getHeight() / 2);
		this.button.setPosition(xPosition - this.button.getWidth() / 2, yPosition - this.button.getHeight() / 2);
		this.fontXNumber = xPosition - this.button.getWidth() / 2 + 20;
		this.fontYNumber = yPosition + this.button.getHeight() / 5 * 2;
		this.fontXName = xPosition - this.button.getWidth() / 3;
		this.fontYName = yPosition + this.button.getHeight() / 5 * 2;
		this.fontXScore = xPosition + this.button.getWidth() / 8;
		this.fontYScore = yPosition + this.button.getHeight() / 5 * 2;
	}
	
	public void draw(final SpriteBatch spriteBatch) {
		this.button.draw(spriteBatch);
		MainGame.highscoreFont.draw(spriteBatch, "" + this.place, this.fontXNumber, this.fontYNumber);
		MainGame.highscoreFont.draw(spriteBatch, this.name, this.fontXName, this.fontYName);
		MainGame.highscoreFont.draw(spriteBatch, "" + this.score, this.fontXScore, this.fontYScore);
	}

}
