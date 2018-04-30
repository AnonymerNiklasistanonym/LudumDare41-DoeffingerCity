package com.mygdx.game.controller;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class ControllerHelper implements ControllerListener {

	private boolean buttonPressedX, buttonPressedY, buttonPressedA, buttonPressedB, buttonPressedBack,
			buttonPressedStart;
	private float rightLeftTrigger;
	private Vector2 leftPad, rightPad;

	private final ControllerCallbackInterface controllerCallbackInterface;

	public ControllerHelper(ControllerCallbackInterface controllerCallbackInterface) {
		this.controllerCallbackInterface = controllerCallbackInterface;

		leftPad = new Vector2();
		rightPad = new Vector2();

	}

	public void buttonPressed(int buttonId) {
		buttonManager(buttonId, true);
	}

	public void buttonFreed(int buttonId) {
		buttonManager(buttonId, false);
	}

	public void update() {
		// control if the car drives forwards or backwards
		if (rightLeftTrigger > 0.2f)
			controllerCallbackInterface.accelerateCar(false);
		if (rightLeftTrigger < -0.2f)
			controllerCallbackInterface.accelerateCar(true);

		// control the car turn
		if (leftPad.x < -0.2f)
			controllerCallbackInterface.steerCar(true);
		if (leftPad.x > 0.2f)
			controllerCallbackInterface.steerCar(false);
	}

	private void buttonManager(int buttonId, boolean pressed) {
		switch (buttonId) {
		case ControllerWiki.BUTTON_A:
			buttonPressedA = pressed;
			break;
		case ControllerWiki.BUTTON_B:
			buttonPressedB = pressed;
			controllerCallbackInterface.startBuildingMode(buttonPressedB);
			break;
		case ControllerWiki.BUTTON_X:
			buttonPressedX = pressed;
			break;
		case ControllerWiki.BUTTON_Y:
			buttonPressedY = pressed;
			break;
		case ControllerWiki.BUTTON_START:
			if (!buttonPressedStart && pressed)
				controllerCallbackInterface.fullScreenCallback();
			buttonPressedStart = pressed;
			break;
		case ControllerWiki.BUTTON_BACK:
			if (!buttonPressedBack && pressed)
				controllerCallbackInterface.backCallback();
			buttonPressedBack = pressed;
			break;
		default:
			// not important
		}
	}

	public void axisChanged(int axisId, float value) {
		switch (axisId) {
		case ControllerWiki.AXIS_RIGHT_LEFT_TRIGGER:
			rightLeftTrigger = value;
			break;
		case ControllerWiki.AXIS_LEFT_X:
			leftPad = new Vector2(value, leftPad != null ? leftPad.y : 0);
			break;
		case ControllerWiki.AXIS_LEFT_Y:
			leftPad = new Vector2(leftPad != null ? leftPad.x : 0, value);
			break;
		case ControllerWiki.AXIS_RIGHT_X:
			rightPad = new Vector2(value, rightPad != null ? rightPad.y : 0);
			break;
		case ControllerWiki.AXIS_RIGHT_Y:
			rightPad = new Vector2(rightPad != null ? rightPad.y : 0, value);
			break;
		default:
			// not important
		}
	}

	@Override
	public void connected(Controller controller) {
		System.out.println("Controller connected" + controller.getName());
	}

	@Override
	public void disconnected(Controller controller) {
		System.out.println("Controller disconnected" + controller.getName());
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		buttonPressed(buttonCode);
		return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		buttonFreed(buttonCode);
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		axisChanged(axisCode, value);
		return false;
	}

	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		// TODO Auto-generated method stub
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
}
