package com.mygdx.game.listener.controller;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class ControllerHelper implements ControllerListener {

	private float rightLeftTrigger;
	private Vector2 leftPad;
	Vector3 rightPad;

	private final ControllerCallbackInterface controllerCallbackInterface;

	public ControllerHelper(ControllerCallbackInterface controllerCallbackInterface) {
		this.controllerCallbackInterface = controllerCallbackInterface;

		leftPad = new Vector2();
		rightPad = new Vector3();

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
			controllerCallbackInterface.controllerCallbackAccelerateCar(false);
		if (rightLeftTrigger < -0.2f)
			controllerCallbackInterface.controllerCallbackAccelerateCar(true);

		// control the car turn
		if (leftPad.x < -0.2f)
			controllerCallbackInterface.controllerCallbackSteerCar(true);
		if (leftPad.x > 0.2f)
			controllerCallbackInterface.controllerCallbackSteerCar(false);

		// control the mouse cursor
		controllerCallbackInterface.controllerMouseChanged(rightPad);
	}

	private void buttonManager(int buttonId, boolean pressed) {
		switch (buttonId) {
		case ControllerWiki.BUTTON_A:
			controllerCallbackInterface.buildTower();
			break;
		case ControllerWiki.BUTTON_B:
			controllerCallbackInterface.controllerCallbackStartBuildingMode(-1);
			break;
		case ControllerWiki.BUTTON_RB:
			if (pressed)
				controllerCallbackInterface.controllerCallbackToggleSound();
			break;
		case ControllerWiki.BUTTON_LB:
			if (pressed)
				controllerCallbackInterface.controllerCallbackTogglePause();
			break;
		case ControllerWiki.BUTTON_START:
			if (pressed)
				controllerCallbackInterface.controllerCallbackToggleFullScreen();
			break;
		case ControllerWiki.BUTTON_BACK:
			if (pressed)
				controllerCallbackInterface.controllerCallbackBackButtonPressed();
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
			if (value < 0.15 && value > -0.15)
				rightPad = new Vector3(0, rightPad != null ? rightPad.y : 0, 0);
			else
				rightPad = new Vector3(value, rightPad != null ? rightPad.y : 0, 0);
			break;
		case ControllerWiki.AXIS_RIGHT_Y:
			if (value < 0.15 && value > -0.15)
				rightPad = new Vector3(rightPad != null ? rightPad.y : 0, 0, 0);
			else
				rightPad = new Vector3(rightPad != null ? rightPad.y : 0, -value, 0);
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
		if (value == ControllerWiki.BUTTON_DPAD_LEFT)
			controllerCallbackInterface.controllerCallbackStartBuildingMode(0);
		if (value == ControllerWiki.BUTTON_DPAD_UP)
			controllerCallbackInterface.controllerCallbackStartBuildingMode(1);
		if (value == ControllerWiki.BUTTON_DPAD_RIGHT)
			controllerCallbackInterface.controllerCallbackStartBuildingMode(2);
		if (value == ControllerWiki.BUTTON_DPAD_DOWN)
			controllerCallbackInterface.controllerCallbackStartBuildingMode(3);
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
