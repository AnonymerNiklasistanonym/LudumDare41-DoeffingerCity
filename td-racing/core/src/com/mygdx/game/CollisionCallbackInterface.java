package com.mygdx.game;

import com.mygdx.game.objects.Car;
import com.mygdx.game.objects.Checkpoint;
import com.mygdx.game.objects.Enemy;
import com.mygdx.game.objects.FinishLine;
import com.mygdx.game.objects.Flame;

public interface CollisionCallbackInterface {

	public void collisionCarEnemy(Car car, Enemy enemy);

	public void collisionCarCheckpoint(Car car, Checkpoint checkpoint);

	public void collisionCarFinishLine(Car car, FinishLine finishLine);

	public void collisionFlameEnemy(Enemy e, Flame f);

}
