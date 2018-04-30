package com.mygdx.game.controller;

public interface ControllerCallbackInterface {

	void accelerateCar(boolean forwards);

	void steerCar(boolean left);

	void startBuildingMode(boolean start);
	
	void backCallback();

	void fullScreenCallback();
}
