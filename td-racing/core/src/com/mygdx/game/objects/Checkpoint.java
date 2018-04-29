package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.gamestate.state.PlayState;

public abstract class Checkpoint extends BodyDef {

	private final Body body;
	private final Sprite sprite;
	private static final int CHECKPOINT_WIDTH = 80;

	private boolean activated;

	public Checkpoint(final World world, float xPosition, float yPosition) {
		this.type = BodyType.StaticBody;
		this.position.set(xPosition, yPosition);
		this.body = world.createBody(this);
		this.sprite = new Sprite();
		this.sprite.setSize(CHECKPOINT_WIDTH * PlayState.PIXEL_TO_METER, CHECKPOINT_WIDTH * PlayState.PIXEL_TO_METER);
		this.sprite.setPosition(xPosition, yPosition);

		final PolygonShape circleShape = new PolygonShape();
		circleShape.setAsBox(this.sprite.getHeight(), this.sprite.getWidth());
		final FixtureDef fdef = new FixtureDef();
		fdef.shape = circleShape;
		fdef.isSensor = true;
		this.body.createFixture(fdef);
		this.body.setUserData(this);
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(final boolean activated) {
		this.activated = activated;
	}

}
