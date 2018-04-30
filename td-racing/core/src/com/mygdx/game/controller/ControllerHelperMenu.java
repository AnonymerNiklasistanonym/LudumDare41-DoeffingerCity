package com.mygdx.game.controller;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

public class ControllerHelperMenu implements ControllerListener {

	private boolean buttonPressedX, buttonPressedY, buttonPressedA, buttonPressedB, buttonPressedBack;
	private final ControllerMenuCallbackInterface controllerMenuCallbackInterface;

	public ControllerHelperMenu(ControllerMenuCallbackInterface controllerMenuCallbackInterface) {
		buttonPressedX = false;
		buttonPressedY = false;
		buttonPressedA = false;
		buttonPressedB = false;
		buttonPressedBack = false;
		this.controllerMenuCallbackInterface = controllerMenuCallbackInterface;
	}

	@Override
	public void connected(Controller controller) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnected(Controller controller) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		buttonPressed(buttonCode);
		return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		buttonPressed(buttonCode, false);
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		controllerMenuCallbackInterface.dPadCallback(value);
		return false;
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		// TODO Auto-generated method stub
		return false;
	}

	private void buttonPressed(int buttonId) {
		buttonPressed(buttonId, true);
	}

	private void buttonPressed(int buttonId, boolean pressed) {
		switch (buttonId) {
		case ControllerWiki.BUTTON_A:
			if (!buttonPressedA)
				controllerMenuCallbackInterface.selectCallback(buttonId);
			buttonPressedA = pressed;
			break;
		case ControllerWiki.BUTTON_B:
			if (!buttonPressedB)
				controllerMenuCallbackInterface.backCallback();
			buttonPressedB = pressed;
			break;
		case ControllerWiki.BUTTON_X:
			if (!buttonPressedX)
				controllerMenuCallbackInterface.selectCallback(buttonId);
			buttonPressedX = pressed;
			break;
		case ControllerWiki.BUTTON_Y:
			if (!buttonPressedY)
				controllerMenuCallbackInterface.selectCallback(buttonId);
			buttonPressedY = pressed;
			break;
		case ControllerWiki.BUTTON_BACK:
			if (!buttonPressedA)
				controllerMenuCallbackInterface.backCallback();
			buttonPressedBack = pressed;
			break;
		default:
			// not important
		}
	}

}
