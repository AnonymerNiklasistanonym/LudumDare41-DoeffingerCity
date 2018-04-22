package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.gamestate.state.PlayState;

public class ScoreBoard {
	
	private float score;
	private float time;
	private float timeBegin;
	private int enemyNumber;
	private int waveNumber;
	
	
	public ScoreBoard() {
		MainGame.fontTest.getData().setScale(MainGame.fontTest.getScaleX() * PlayState.PIXEL_TO_METER);		
	}
	
	public void draw(final SpriteBatch spriteBatch) {
		
		System.out.println("Money: " + );
				
		// String completeText= "In this text there are multi lines";
		
		// GlyphLayout layout = new GlyphLayout();
		// MainGame.font.draw(spriteBatch, completeText, 800 * PlayState.PIXEL_TO_METER, 200 * PlayState.PIXEL_TO_METER);
		// layout.setText(MainGame.fontTest, "100");
		// MainGame.fontTest.draw(spriteBatch, "Play", Gdx.graphics.getWidth() / 2 - (layout.width / 2), Gdx.graphics.getHeight() - 300);        

		// MainGame.fontTest.draw(spriteBatch, "100", 800 * PlayState.PIXEL_TO_METER, 100 * PlayState.PIXEL_TO_METER);
		// MainGame.smallFont.draw(spriteBatch, str, x, y, start, end, targetWidth, halign, wrap, truncate)
		
	}
	
	
	

}
