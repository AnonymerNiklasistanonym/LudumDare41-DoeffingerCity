package com.mygdx.game;

import com.mygdx.game.objects.Checkpoint;
import com.mygdx.game.objects.FinishLine;
import com.mygdx.game.objects.Tower;

public interface CollisionCallbackInterface {

	public void collisionCarEnemy(Car car, Enemy enemy);

	public void collisionCarCheckpoint(Car car, Checkpoint checkpoint);

	public void collisionCarTower(Car car, Tower tower);
	
	public void collisionCarFinishLine(Car car, FinishLine finishLine);

}
