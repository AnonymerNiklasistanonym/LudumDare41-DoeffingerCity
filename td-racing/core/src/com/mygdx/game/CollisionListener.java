package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.objects.Checkpoint;
import com.mygdx.game.objects.FinishLine;

public class CollisionListener implements ContactListener {

	final CollisionCallbackInterface collisionCallbackInterface;

	public CollisionListener(CollisionCallbackInterface collisionCallbackInterface) {
		this.collisionCallbackInterface = collisionCallbackInterface;
	}

	@Override
	public void beginContact(Contact contact) {
		final Object a = contact.getFixtureA().getBody().getUserData();
		final Object b = contact.getFixtureB().getBody().getUserData();

		// if one of the objects is a car
		if (a instanceof Car || b instanceof Car) {

			// and the other object is an Enemy
			if (a instanceof Enemy || b instanceof Enemy) {
				
				if (a instanceof Enemy)
					this.collisionCallbackInterface.collisionCarEnemy((Car) b, (Enemy) a);
				else
					this.collisionCallbackInterface.collisionCarEnemy((Car) a, (Enemy) b);
			}

			// and the other object is a Checkpoint
			if (a instanceof Checkpoint || b instanceof Checkpoint) {
				
				if (a instanceof Checkpoint)
					this.collisionCallbackInterface.collisionCarCheckpoint((Car) b, (Checkpoint) a);
				else
					this.collisionCallbackInterface.collisionCarCheckpoint((Car) a, (Checkpoint) b);
			}
			
			// and the other object is a Checkpoint
			if (a instanceof FinishLine || b instanceof FinishLine) {
				
				if (a instanceof FinishLine)
					this.collisionCallbackInterface.collisionCarFinishLine((Car) b, (FinishLine) a);
				else
					this.collisionCallbackInterface.collisionCarFinishLine((Car) a, (FinishLine) b);
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
	}

}
