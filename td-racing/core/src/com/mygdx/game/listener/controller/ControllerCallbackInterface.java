package com.mygdx.game.listener.controller;

import com.badlogic.gdx.math.Vector3;

public interface ControllerCallbackInterface {

	void controllerCallbackAccelerateCar(boolean forwards);

	void controllerCallbackSteerCar(boolean left);

	void controllerCallbackBackButtonPressed();

	void controllerCallbackToggleFullScreen();

	void controllerCallbackToggleSound();

	void controllerCallbackStartBuildingMode(int towerId);

	void controllerMouseChanged(Vector3 rightPad);

	void buildTower();

	void controllerCallbackTogglePause();

}
