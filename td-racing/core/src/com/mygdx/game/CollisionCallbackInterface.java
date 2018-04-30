package com.mygdx.game;

import com.mygdx.game.objects.Car;
import com.mygdx.game.objects.Checkpoint;
import com.mygdx.game.objects.Enemy;
import com.mygdx.game.objects.FinishLine;
import com.mygdx.game.objects.Flame;

public interface CollisionCallbackInterface {

	void collisionCarEnemy(final Car car, final Enemy enemy);

	void collisionCarCheckpoint(final Car car, final Checkpoint checkpoint);

	void collisionCarFinishLine(final Car car, final FinishLine finishLine);

	void collisionFlameEnemy(final Enemy enemy, final Flame flame);

}
