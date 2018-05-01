package com.mygdx.game.objects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.gamestate.states.PlayState;

public abstract class Checkpoint extends BodyDef {

	private static final int CHECKPOINT_WIDTH = 80;

	private final Body body;

	private boolean activated;

	public Checkpoint(final World world, float xPosition, float yPosition) {
		type = BodyType.StaticBody;
		position.set(xPosition, yPosition);
		body = world.createBody(this);
		final PolygonShape circleShape = new PolygonShape();
		circleShape.setAsBox(CHECKPOINT_WIDTH * PlayState.PIXEL_TO_METER, CHECKPOINT_WIDTH * PlayState.PIXEL_TO_METER);
		final FixtureDef fdef = new FixtureDef();
		fdef.shape = circleShape;
		fdef.isSensor = true;
		body.createFixture(fdef);
		body.setUserData(this);
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(final boolean activated) {
		this.activated = activated;
	}

}
