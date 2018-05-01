package com.mygdx.game.listener.collisions;

import com.mygdx.game.objects.Car;
import com.mygdx.game.objects.Checkpoint;
import com.mygdx.game.objects.Enemy;
import com.mygdx.game.objects.FinishLine;
import com.mygdx.game.objects.Flame;

public interface CollisionCallbackInterface {

	void collisionCallbackCarEnemy(final Car car, final Enemy enemy);

	void collisionCallbackCarCheckpoint(final Car car, final Checkpoint checkpoint);

	void collisionCallbackCarFinishLine(final Car car, final FinishLine finishLine);

	void collisionCallbackFlameEnemy(final Enemy enemy, final Flame flame);

}
