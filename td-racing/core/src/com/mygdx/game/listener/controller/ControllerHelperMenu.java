package com.mygdx.game.listener.controller;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

public class ControllerHelperMenu implements ControllerListener {

	private final ControllerMenuCallbackInterface controllerMenuCallbackInterface;

	public ControllerHelperMenu(ControllerMenuCallbackInterface controllerMenuCallbackInterface) {
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
		final boolean isXAxis = axisCode == ControllerWiki.AXIS_LEFT_X || axisCode == ControllerWiki.AXIS_RIGHT_X;
		controllerMenuCallbackInterface.stickMoved(isXAxis, value);
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
			if (pressed)
				controllerMenuCallbackInterface.selectCallback(buttonId);
			break;
		case ControllerWiki.BUTTON_B:
			if (pressed)
				controllerMenuCallbackInterface.backCallback();
			break;
		case ControllerWiki.BUTTON_X:
			if (pressed)
				controllerMenuCallbackInterface.selectCallback(buttonId);
			break;
		case ControllerWiki.BUTTON_Y:
			if (pressed)
				controllerMenuCallbackInterface.selectCallback(buttonId);
			break;
		case ControllerWiki.BUTTON_BACK:
			if (pressed)
				controllerMenuCallbackInterface.backCallback();
			break;
		case ControllerWiki.BUTTON_START:
			if (pressed)
				controllerMenuCallbackInterface.selectCallback(buttonId);
			break;
		default:
			// not important
		}
	}

}
