package com.mygdx.game.listener.controller;

import com.badlogic.gdx.controllers.PovDirection;

public interface ControllerMenuCallbackInterface {

	/**
	 * B or Back button was pressed
	 */
	void controllerCallbackBackPressed();

	/**
	 * Another main button was pressed (A,X,Y)
	 *
	 * @param buttonId
	 *            (ControllerWiki.BUTTON...)
	 */
	void controllerCallbackButtonPressed(final int buttonId);

	/**
	 * DPad button was pressed
	 *
	 * @param buttonId
	 *            (ControllerWiki.BUTTON...)
	 */
	void controllerCallbackDPadButtonPressed(final PovDirection direction);

	/**
	 * One of the sticks was moved
	 *
	 * @param xAxis
	 *            (xAxis or yAxis changed)
	 * @param value
	 *            (how fast was the stick moved)
	 */
	void controllerCallbackStickMoved(final boolean xAxis, final float value);

}
