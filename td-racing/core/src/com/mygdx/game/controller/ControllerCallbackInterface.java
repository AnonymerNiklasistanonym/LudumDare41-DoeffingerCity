package com.mygdx.game.controller;

import com.badlogic.gdx.math.Vector3;

public interface ControllerCallbackInterface {

	void accelerateCar(boolean forwards);

	void steerCar(boolean left);
	
	void backCallback();

	void fullScreenCallback();

	void toggleSound();

	void startBuildingMode(int towerId);

	void controllerMouseChanged(Vector3 rightPad);

	void buildTower();

	void togglePause();

}
