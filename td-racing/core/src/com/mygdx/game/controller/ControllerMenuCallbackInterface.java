package com.mygdx.game.controller;

import com.badlogic.gdx.controllers.PovDirection;

public interface ControllerMenuCallbackInterface {

	/**
	 * B or Back button was pressed
	 */
	void backCallback();

	/**
	 * Another main button was pressed (A,X,Y)
	 * 
	 * @param buttonId
	 *            (ControllerWiki.BUTTON...)
	 */
	void selectCallback(int buttonId);
	
	/**
	 * DPad button was pressed
	 * 
	 * @param buttonId
	 *            (ControllerWiki.BUTTON...)
	 */
	void dPadCallback(PovDirection direction);

}
