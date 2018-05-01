package com.mygdx.game.listener.collisions;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.objects.Car;
import com.mygdx.game.objects.Checkpoint;
import com.mygdx.game.objects.Enemy;
import com.mygdx.game.objects.FinishLine;
import com.mygdx.game.objects.Flame;

public class CollisionListener implements ContactListener {

	final CollisionCallbackInterface collisionCallbackInterface;

	public CollisionListener(CollisionCallbackInterface collisionCallbackInterface) {
		this.collisionCallbackInterface = collisionCallbackInterface;
	}

	@Override
	public void beginContact(final Contact contact) {
		final Object a = contact.getFixtureA().getBody().getUserData();
		final Object b = contact.getFixtureB().getBody().getUserData();

		// if one of the objects is a car
		if (a instanceof Car || b instanceof Car) {

			// and the other object is an Enemy
			if (a instanceof Enemy || b instanceof Enemy) {

				if (a instanceof Enemy)
					this.collisionCallbackInterface.collisionCallbackCarEnemy((Car) b, (Enemy) a);
				else
					this.collisionCallbackInterface.collisionCallbackCarEnemy((Car) a, (Enemy) b);
			}

			// and the other object is a Checkpoint
			if (a instanceof Checkpoint || b instanceof Checkpoint) {

				if (a instanceof Checkpoint)
					this.collisionCallbackInterface.collisionCallbackCarCheckpoint((Car) b, (Checkpoint) a);
				else
					this.collisionCallbackInterface.collisionCallbackCarCheckpoint((Car) a, (Checkpoint) b);
			}

			// and the other object is a Checkpoint
			if (a instanceof FinishLine || b instanceof FinishLine) {

				if (a instanceof FinishLine)
					this.collisionCallbackInterface.collisionCallbackCarFinishLine((Car) b, (FinishLine) a);
				else
					this.collisionCallbackInterface.collisionCallbackCarFinishLine((Car) a, (FinishLine) b);
			}
		}

		if (a instanceof Enemy || b instanceof Enemy) {

			// and the other object is an Enemy
			if (a instanceof Flame || b instanceof Flame) {

				if (a instanceof Enemy)
					this.collisionCallbackInterface.collisionCallbackFlameEnemy((Enemy) a, (Flame) b);
				else
					this.collisionCallbackInterface.collisionCallbackFlameEnemy((Enemy) b, (Flame) a);
			}
		}
	}

	@Override
	public void endContact(final Contact contact) {
		// TODO Auto-generated method stub
	}

	@Override
	public void preSolve(final Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
	}

	@Override
	public void postSolve(final Contact contact, final ContactImpulse impulse) {
		// TODO Auto-generated method stub
	}

}
