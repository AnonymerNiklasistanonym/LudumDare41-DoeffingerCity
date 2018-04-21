package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.gamestate.state.PlayState;

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

			// and the other object is a Enemy
			if (a instanceof Enemy || b instanceof Enemy) {
				if (a instanceof Enemy)
					this.playstate.collisionCarEnemy((Car) b, (Enemy) a);
				else
					this.playstate.collisionCarEnemy((Car) a, (Enemy) b);
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
