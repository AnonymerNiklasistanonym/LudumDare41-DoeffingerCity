package com.mygdx.game.gamestate.states.resources;

import com.badlogic.gdx.graphics.Texture;

public class MenuButtonBig extends MenuButton {

	public static Texture textureActive;
	public static Texture textureNotActive;

	public MenuButtonBig(int id, float xPosition, float yPosition, String content, boolean activated) {
		super(id, xPosition, yPosition, content, textureActive, textureNotActive, 1, activated);
	}

	public MenuButtonBig(int id, float xPosition, float yPosition, String content) {
		super(id, xPosition, yPosition, content, textureActive, textureNotActive, 1);
	}

	@Override
	public void dispose() {
		super.disposeMedia();
	}

}
