package com.mygdx.game.controller;

public interface ControllerMenuCallbackInterface {

	/**
	 * B or Back button was pressed
	 */
	public void backCallback();

	/**
	 * Another main button was pressed (A,X,Y)
	 * 
	 * @param buttonId
	 *            (ControllerWiki.BUTTON...)
	 */
	public void selectCallback(int buttonId);

}
