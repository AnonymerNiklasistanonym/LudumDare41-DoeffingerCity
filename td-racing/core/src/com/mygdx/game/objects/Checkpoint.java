package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.gamestate.state.PlayState;

public abstract class Checkpoint extends BodyDef {

	private boolean activated;
	protected final Body body;
	protected final Sprite sprite;
	protected final Texture activatedTexture;
	protected final Texture disabledTexture;

	public Checkpoint(final World world, float xPosition, float yPosition, final Texture disabled, final Texture activated) {
		this.type = BodyType.StaticBody;
		this.position.set(xPosition, yPosition);
		this.body = world.createBody(this);
		this.sprite = new Sprite(disabled);
		this.sprite.setSize(disabled.getWidth() * PlayState.PIXEL_TO_METER,
				disabled.getHeight() * PlayState.PIXEL_TO_METER);
		this.sprite.setPosition(xPosition, yPosition);

		final CircleShape circleShape = new CircleShape();
		circleShape.setRadius(this.sprite.getHeight() * 1f);
		final FixtureDef fdef = new FixtureDef();
		fdef.shape = circleShape;
		fdef.isSensor = true;
		this.body.createFixture(fdef);
		this.body.setUserData(this);
		this.activatedTexture = activated;
		this.disabledTexture = disabled;
	}

	public void setActivated(final boolean activated) {
		this.activated = activated;
		this.sprite.setTexture(this.activated ? activatedTexture : disabledTexture);
	}

	public boolean isActivated() {
		return activated;
	}

	private float getX() {
		return this.body.getPosition().x - this.sprite.getWidth() / 2;
	}

	private float getY() {
		return this.body.getPosition().y - this.sprite.getWidth() / 2;
	}

	public void draw(final SpriteBatch spriteBatch) {
		this.sprite.setPosition(getX(), getY());
		this.sprite.draw(spriteBatch);
	}

}
