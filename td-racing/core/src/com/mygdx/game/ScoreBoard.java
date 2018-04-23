package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.gamestate.state.PlayState;

public class ScoreBoard {

	private float score;
	private float money;
	private float timeWhole;
	private float timeRound;
	private float timeBegin;
	private int enemyNumber;
	private int waveNumber;

	public ScoreBoard() {
		MainGame.font.getData().setScale(PlayState.PIXEL_TO_METER);

	}

	public void draw(final SpriteBatch spriteBatch) {

		MainGame.font.draw(spriteBatch, "TEXTTEST", MainGame.GAME_WIDTH * PlayState.PIXEL_TO_METER / 2,
				MainGame.GAME_HEIGHT * PlayState.PIXEL_TO_METER / 2);

		// System.out.println("Money: " + this.money + ", timeRound: " + this.timeWhole + ", ");

		String completeText= "In this text there are multi lines";

		GlyphLayout layout = new GlyphLayout();
		MainGame.font.draw(spriteBatch, completeText, 800 * PlayState.PIXEL_TO_METER,
		200 * PlayState.PIXEL_TO_METER);
		layout.setText(MainGame.font, "100");
		MainGame.font.draw(spriteBatch, "Play", Gdx.graphics.getWidth() / 2 -
		(layout.width / 2), Gdx.graphics.getHeight() - 300);

		// MainGame.fontTest.draw(spriteBatch, "100", 800 * PlayState.PIXEL_TO_METER,
		// MainGame.font.draw(spriteBatch, "100", 800 * PlayState.PIXEL_TO_METER,
		// 100 * PlayState.PIXEL_TO_METER);
		// MainGame.smallFont.draw(spriteBatch, str, x, y, start, end, targetWidth,
		// halign, wrap, truncate)

	}

}
