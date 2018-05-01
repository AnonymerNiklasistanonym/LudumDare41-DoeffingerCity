package com.mygdx.game.objects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.gamestate.states.PlayState;

public abstract class Tower implements Disposable {

	private static boolean soundOn;

	protected Sprite amunition;
	public Body body;
	private boolean buildingModeBlocked;
	protected Vector2 center;
	protected Color color;
	protected int cost = 10;
	protected float damage, soundVolumne;
	float delta = 0;
	protected Animation<TextureRegion> destroyAnimation;
	Array<Enemy> enemies;
	protected float firingLineTime = 0.1f;
	protected float firingSpriteTime = 0.3f;
	boolean healthBar;
	boolean isactive = false;
	private boolean isInBuildingMode;
	boolean isSoundPlaying = false;
	boolean justshot = false;
	protected float maxHealth;
	protected boolean permanentsound = false;
	protected float power;
	protected float range;
	private boolean rangeActivated = false;
	protected Vector2 shotposition;
	protected Sound soundShoot;
	protected float speed;
	protected Sprite spriteBody;
	protected Sprite spriteFiring;
	protected Sprite spriteUpperBody;
	protected Enemy target = null;
	protected float timesincelastshot;
	private boolean toremove;
	protected float turnspeed;

	protected Tower(final Vector2 position, final Texture spriteBody, final Texture spriteUpperBody,
			final Texture spriteFiring, final Array<Enemy> enemies, final World world, final int range,
			final Sound soundShoot) {
		this.soundShoot = soundShoot;
		this.enemies = enemies;
		this.range = range;
		this.spriteBody = new Sprite(spriteBody);
		this.spriteUpperBody = new Sprite(spriteUpperBody);
		this.spriteFiring = new Sprite(spriteFiring);
		this.spriteBody.setSize(spriteBody.getWidth() * PlayState.PIXEL_TO_METER,
				spriteBody.getHeight() * PlayState.PIXEL_TO_METER);
		this.spriteUpperBody.setSize(spriteUpperBody.getWidth() * PlayState.PIXEL_TO_METER,
				spriteUpperBody.getHeight() * PlayState.PIXEL_TO_METER);
		this.spriteFiring.setSize(spriteFiring.getWidth() * PlayState.PIXEL_TO_METER,
				spriteFiring.getHeight() * PlayState.PIXEL_TO_METER);
		this.spriteBody.setOriginCenter();
		this.spriteUpperBody.setOriginCenter();
		this.spriteFiring.setOriginCenter();

		timesincelastshot = 10;
		soundVolumne = 0.25f;
		healthBar = false;
		toremove = false;
		damage = 0;
		buildingModeBlocked = false;
		color = new Color(1, 0, 0, 0.3f);
		rangeActivated = false;

		// create box2D body and add it to the world
		final BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.KinematicBody;
		bodydef.position.set(position);
		body = world.createBody(bodydef);
		final PolygonShape towerBaseBox = new PolygonShape();
		towerBaseBox.setAsBox(spriteBody.getWidth() * 0.5f * PlayState.PIXEL_TO_METER,
				spriteBody.getHeight() * 0.5f * PlayState.PIXEL_TO_METER);
		final FixtureDef fdef = new FixtureDef();
		fdef.shape = towerBaseBox;
		fdef.isSensor = true;
		body.createFixture(fdef);
		body.setUserData(this);
	}

	public void activate() {
		isactive = true;
	}

	public void activateHealthBar(boolean activate) {
		healthBar = activate;
	}

	public void activateRange(boolean b) {
		this.rangeActivated = b;
	}

	public boolean buildingModeBlocked() {
		return this.buildingModeBlocked;
	}

	public boolean contains(float xPos, float yPos) {
		return (xPos >= this.spriteBody.getX() && xPos <= this.spriteBody.getX() + this.spriteBody.getWidth())
				&& (yPos >= this.spriteBody.getY() && yPos <= this.spriteBody.getY() + this.spriteBody.getHeight());
	}

	public void destroyAnimation() {
		// TODO
		System.out.println("Destroyed");
	}

	public void disposeMedia() {
		this.spriteBody.getTexture().dispose();
		this.spriteFiring.getTexture().dispose();
		this.spriteFiring.getTexture().dispose();
		this.soundShoot.dispose();
	}

	public void draw(final SpriteBatch spriteBatch) {
		spriteBody.draw(spriteBatch);
	}

	public void drawLine(final ShapeRenderer shapeRenderer) {
		if (firingLineTime > timesincelastshot) {
			if (soundOn) {
				soundShoot.play(soundVolumne);
			}
			drawProjectileShape(shapeRenderer);
		} else {
			if (soundOn) {
				soundShoot.stop();
			}
		}
	}

	public void drawProjectile(final SpriteBatch spriteBatch) {

	}

	public abstract void drawProjectileShape(final ShapeRenderer shapeRenderer);

	public void drawRange(final ShapeRenderer shapeRenderer) {
		if (this.rangeActivated) {
			shapeRenderer.setColor(this.color);
			shapeRenderer.circle(this.spriteBody.getX() + this.spriteBody.getWidth() / 2,
					this.spriteBody.getY() + this.spriteBody.getHeight() / 2, this.range);
		}
	}

	public void drawRange(final ShapeRenderer shapeRenderer, final Color color) {
		if (this.rangeActivated) {
			shapeRenderer.setColor(color);
			shapeRenderer.circle(this.spriteBody.getX() + this.spriteBody.getWidth() / 2,
					this.spriteBody.getY() + this.spriteBody.getHeight() / 2, this.range);
		}
	}

	public void drawUpperBuddy(final SpriteBatch spriteBatch) {
		if (firingLineTime > timesincelastshot) {
			drawProjectile(spriteBatch);
			spriteFiring.draw(spriteBatch);
		} else {
			spriteUpperBody.draw(spriteBatch);
		}
	}

	public float getAngleToEnemy(Enemy e) {
		float angle = 0;
		Vector2 epos = new Vector2(center.x, center.y);
		Vector2 tpos = new Vector2(e.getBodyX(), e.getBodyY());

		angle = center.angle(epos);
		angle = (float) ((Math.atan2(epos.x - tpos.x, -(epos.y - tpos.y)) * 180.0d / Math.PI));
		return angle;

	}

	public Vector2 getCenter() {
		return center;
	}

	public float[][] getCornerPoints() {
		float[][] cornerPoints = new float[4][2];
		// left bottom
		cornerPoints[0][0] = this.spriteBody.getX();
		cornerPoints[0][1] = this.spriteBody.getY();
		// right top
		cornerPoints[1][0] = this.spriteBody.getX() + this.spriteBody.getHeight();
		cornerPoints[1][1] = this.spriteBody.getY() + this.spriteBody.getHeight();
		// left top
		cornerPoints[2][0] = this.spriteBody.getX();
		cornerPoints[2][1] = this.spriteBody.getY() + this.spriteBody.getHeight();
		// right bottom
		cornerPoints[3][0] = this.spriteBody.getX() + this.spriteBody.getHeight();
		cornerPoints[3][1] = this.spriteBody.getY();
		return cornerPoints;
	}

	public int getCost() {
		return this.cost;
	}

	public float getDegrees() {
		return this.spriteUpperBody.getRotation();
	}

	public float getRange() {
		return range;
	}

	public Sprite getSpriteBody() {
		return spriteBody;
	}

	public float getX() {
		return spriteBody.getX();
	}

	public float getY() {
		return spriteBody.getY();
	}

	public boolean isInBuildingMode() {
		return this.isInBuildingMode;
	}

	protected boolean isTargetInRange(Enemy e) {
		Vector2 epos = new Vector2(e.getBodyX(), e.getBodyY());
		Vector2 tpos = new Vector2(center.x, center.y);
		float dist = epos.dst(tpos);
		boolean inrange = false;
		if (dist < range)
			inrange = true;
		return inrange;
	}

	public boolean isToremove() {
		return toremove;
	}

	public boolean rangeIsActivated() {
		return this.rangeActivated;
	}

	public Array<Body> removeProjectiles() {
		return null;
	}

	public void rotate(float degrees) {
		spriteUpperBody.rotate(degrees);
		spriteFiring.rotate(degrees);
	}

	private void selectNewTarget() {

		Enemy best = null;
		for (Enemy e : enemies) {
			if (best == null)
				if (e.isTot() == false)
					if (isTargetInRange(e))
						best = e;
			if (best != null)
				if (e.getScore() < best.getScore() && e.isTot() == false)
					if (isTargetInRange(e))
						best = e;
		}
		target = best;
	}

	public void setBlockBuildingMode(final boolean b) {
		this.buildingModeBlocked = b;

		if (this.buildingModeBlocked) {
			spriteBody.setColor(1, 0, 0, 0.5f);
			spriteUpperBody.setColor(1, 0, 0, 0.5f);
			spriteFiring.setColor(1, 0, 0, 0.5f);
		} else {
			spriteBody.setColor(1, 1, 1, 1);
			spriteUpperBody.setColor(1, 1, 1, 1);
			spriteFiring.setColor(1, 1, 1, 1);
		}
	}

	public void setBuildingMode(final boolean buildingMode) {
		this.isInBuildingMode = buildingMode;

		if (this.isInBuildingMode) {
			spriteBody.setColor(1, 1, 1, 0.5f);
			spriteUpperBody.setColor(1, 1, 1, 0.5f);
			spriteFiring.setColor(1, 1, 1, 0.5f);
		} else {
			spriteBody.setColor(1, 1, 1, 1);
			spriteUpperBody.setColor(1, 1, 1, 1);
			spriteFiring.setColor(1, 1, 1, 1);
		}
	}

	public void setCenter(Vector2 center) {
		this.center = center;
	}

	public void setDegrees(float degrees) {
		this.spriteUpperBody.setRotation(degrees);
		this.spriteFiring.setRotation(degrees);
	}

	public void setToremove(boolean toremove) {
		this.toremove = toremove;
	}



	public void shoot(Enemy e) {
		if (isTargetInRange(e)) {

			e.takeDamage(power);

			timesincelastshot = 0;
			shotposition.x = e.getX() + 10 * PlayState.PIXEL_TO_METER;
			shotposition.y = e.getY() + 10 * PlayState.PIXEL_TO_METER;
			// TODO: Versatz Dynamisch machen!
		} else {
			target = null;
		}
	}

	public void takeDamage(float amount) {
		damage += amount;
		if (damage >= maxHealth) {
			this.destroyAnimation();
		}
	}

	public void tryshoot(Enemy e) {
		if (getAngleToEnemy(e) > getDegrees()) {
			setDegrees(getDegrees() + turnspeed * delta);
		} else {
			setDegrees(getDegrees() - turnspeed * delta);
		}
		if (Math.abs(getDegrees() - getAngleToEnemy(e)) < turnspeed * delta) {
			setDegrees(getAngleToEnemy(e));
			if (timesincelastshot > speed)
				shoot(e);
		}

		if (e.isTot())
			target = null;

	}

	public void update(final float timeDelta, final Vector3 mousePos) {

		if (this.isInBuildingMode)
			this.updateSprites(new Vector2(mousePos.x, mousePos.y));

		this.delta = timeDelta;
		if (isactive) {
			timesincelastshot = timesincelastshot + timeDelta;
			if (target == null) {
				selectNewTarget();
				soundShoot.stop();
				isSoundPlaying = false;
			} else
				tryshoot(target);
			updateProjectiles(timeDelta);
		}

	}

	public void updateProjectiles(float delta) {

	}

	public void updateSprites(final Vector2 position) {

		// set body
		this.body.setTransform(position, this.body.getAngle());
		position.add(new Vector2(-spriteBody.getWidth() / 2, -spriteBody.getWidth() / 2));

		// set body to new position
		this.spriteBody.setPosition(position.x, position.y);
		// set upper body to new position
		this.spriteUpperBody.setPosition(position.x + this.spriteBody.getWidth() / 2 - spriteUpperBody.getHeight() / 2,
				position.y + this.spriteBody.getWidth() / 2 - spriteUpperBody.getHeight() / 2);
		// fire position to new position
		this.spriteFiring.setPosition(position.x + spriteBody.getWidth() / 2 - spriteFiring.getHeight() / 2,
				position.y + spriteBody.getWidth() / 2 - spriteFiring.getHeight() / 2);

		// shot position to new position
		this.shotposition = new Vector2(position.x + spriteBody.getWidth() / 2, position.y + spriteBody.getWidth() / 2);
		this.center = new Vector2(position.x + spriteBody.getWidth() / 2, position.y + spriteBody.getWidth() / 2);
	}

	public static void setSoundOn(boolean soundOn) {
		Tower.soundOn = soundOn;
	}

	public void updateSound() {
		if (!soundOn)
			soundShoot.stop();
	}

}
