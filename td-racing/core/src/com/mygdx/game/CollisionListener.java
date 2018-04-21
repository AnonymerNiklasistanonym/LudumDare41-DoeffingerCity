package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class CollisionListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		Object a = contact.getFixtureA().getBody().getUserData();
		Object b = contact.getFixtureB().getBody().getUserData();
		System.out.println("userData a/b " + a + "/" + b);

		if (a instanceof Car || b instanceof Car) {
			System.out.println("ya");

			if (a instanceof Enemy || b instanceof Enemy) {
				System.out.println("yo");
				if (a instanceof Enemy) {
					Enemy e = (Enemy) a;
					e.takeDamage(20);
				}
				if (b instanceof Enemy) {
					Enemy e = (Enemy) b;
					e.die();
				}
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
