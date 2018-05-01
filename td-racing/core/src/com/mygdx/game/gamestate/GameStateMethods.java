package com.mygdx.game.gamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MainGame;

public class GameStateMethods {

	/**
	 * Standardized full screen toggle key
	 */
	public static final int standardizedFullScreenToggleKey = Keys.F11;

	/**
	 * Toggle full screen
	 */
	public static void toggleFullScreen() {
		toggleFullScreen(false);
	}

	/**
	 * Toggle full screen on press of the given standardized key
	 */
	public static void toggleFullScreen(final boolean useStandardizedKey) {
		if (!useStandardizedKey || Gdx.input.isKeyJustPressed(standardizedFullScreenToggleKey)) {
			if (Gdx.graphics.isFullscreen())
				Gdx.graphics.setWindowedMode(MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);
			else
				Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		}
	}

	/**
	 * Get the current mouse position
	 *
	 * @param camera
	 *            (State camera)
	 * @return mouse position
	 */
	public static Vector3 getMousePosition(final Camera camera) {
		return camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
	}

	/**
	 * Get the coordinates to render text centered
	 *
	 * @param font
	 *            - BitmapFont which will be used to draw the text
	 * @param text
	 *            - text that should be displayed
	 * @param width
	 *            - Width of screen
	 * @param height
	 *            - Height of screen
	 * @return x and y coordinate for drawing the text
	 */
	public static Vector2 calculateCenteredTextPositon(final BitmapFont font, final String text, final float width,
			final float height) {
		final GlyphLayout temp = new GlyphLayout(font, text);
		return new Vector2(width / 2 - temp.width / 2, height / 2 + temp.height / 2);
	}

	/**
	 * Get the coordinates to render text centered
	 *
	 * @param font
	 *            - BitmapFont which will be used to draw the text
	 * @param text
	 *            - texts that should be displayed
	 * @param width
	 *            - Width of screen
	 * @param height
	 *            - Height of screen
	 * @return x and y coordinates for drawing the texts
	 */
	public static Vector2[] calculateCenteredMultiLineTextPositons(final BitmapFont font, final String[] text,
			final float width, final float height) {
		final Vector2[] positions = new Vector2[text.length];
		for (int i = 0; i < text.length; i++) {
			final GlyphLayout temp = new GlyphLayout(font, text[i]);
			positions[i] = new Vector2(width / 2 - temp.width / 2,
					height / (text.length + 1) * (text.length - i) + temp.height / 2);
		}
		return positions;
	}

}
