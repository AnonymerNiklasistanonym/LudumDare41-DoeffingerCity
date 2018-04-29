package com.mygdx.game;

import com.mygdx.game.objects.Car;
import com.mygdx.game.objects.Checkpoint;
import com.mygdx.game.objects.Enemy;
import com.mygdx.game.objects.FinishLine;
import com.mygdx.game.objects.Flame;

public interface CollisionCallbackInterface {

	public void collisionCarEnemy(final Car car, final Enemy enemy);

	public void collisionCarCheckpoint(final Car car, final Checkpoint checkpoint);

	public void collisionCarFinishLine(final Car car, final FinishLine finishLine);

	public void collisionFlameEnemy(final Enemy enemy, final Flame flame);

}
