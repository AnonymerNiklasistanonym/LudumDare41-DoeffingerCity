package com.mygdx.game.gamestate.states.resources;

import com.badlogic.gdx.graphics.Texture;

public class MenuButtonSmall extends MenuButton {

	public static Texture textureActive;
	public static Texture textureNotActive;

	public MenuButtonSmall(int id, float xPosition, float yPosition, String content, boolean activated) {
		super(id, xPosition, yPosition, content, textureActive, textureNotActive, 0.8f, activated);
	}

	public MenuButtonSmall(int id, float xPosition, float yPosition, String content) {
		super(id, xPosition, yPosition, content, textureActive, textureNotActive, 0.8f);
	}

	@Override
	public void dispose() {
		super.disposeMedia();
	}
}
