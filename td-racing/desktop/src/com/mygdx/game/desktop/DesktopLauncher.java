package com.mygdx.game.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MainGame;

public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		// set width and height of window
		config.height = MainGame.GAME_HEIGHT;
		config.width = MainGame.GAME_WIDTH;
		// set window name
		config.title = MainGame.GAME_NAME;
		
		// add application icon that will be shown if the program is running
		for (int size : new int[] { 16, 32, 128 }) {
			config.addIcon("icon/icon_" + size + ".png", FileType.Internal);
		}
		new LwjglApplication(new MainGame(), config);
	}
}
