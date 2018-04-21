package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.gamestate.state.PlayState;
import com.mygdx.game.objects.Checkpoint;
import com.mygdx.game.objects.Tower;

public class CollisionListener implements ContactListener {
	PlayState playstate;

	public CollisionListener(PlayState playState) {
		this.playstate = playState;
	}

	@Override
	public void beginContact(Contact contact) {
		Object a = contact.getFixtureA().getBody().getUserData();
		Object b = contact.getFixtureB().getBody().getUserData();

		// if one of the objects is a car
		if (a instanceof Car || b instanceof Car) {

			// and the other object is an Enemy
			if (a instanceof Enemy || b instanceof Enemy) {
				if (a instanceof Enemy)
					this.playstate.collisionCarEnemy((Car) b, (Enemy) a);
				else
					this.playstate.collisionCarEnemy((Car) a, (Enemy) b);
				return;
			}

			// and the other object is a Checkpoint
			if (a instanceof Checkpoint || b instanceof Checkpoint) {
				if (a instanceof Checkpoint)
					this.playstate.collisionCarCheckpoint((Car) b, (Checkpoint) a);
				else
					this.playstate.collisionCarCheckpoint((Car) a, (Checkpoint) b);
				return;
			}

			// and the other object is a Checkpoint
			if (a instanceof Tower || b instanceof Tower) {
				if (a instanceof Tower)
					this.playstate.collisionCarTower((Car) b, (Tower) a);
				else
					this.playstate.collisionCarTower((Car) a, (Tower) b);
				return;
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
