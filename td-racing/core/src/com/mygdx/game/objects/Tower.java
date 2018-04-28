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
import com.mygdx.game.gamestate.state.PlayState;

public abstract class Tower implements Disposable {

	private boolean rangeActivated = false;
	protected float turnspeed;
	protected float maxHealth;
	protected float damage;
	protected float speed;
	protected float power;
	protected float range;
	protected float firingSpriteTime = 0.3f;
	protected float firingLineTime = 0.1f;
	protected Vector2 center;
	protected Sprite amunition;
	protected Animation<TextureRegion> destroyAnimation;
	protected Sprite spriteBody;
	protected Sprite spriteUpperBody;
	protected Sprite spriteFiring;
	protected float timesincelastshot;
	boolean healthBar;
	boolean justshot = false;
	protected boolean permanentsound = false;
	protected Sound soundShoot;
	protected Enemy target = null;
	Array<Enemy> enemies;
	protected Vector2 shotposition;
	float delta = 0;
	public Body body;
	boolean isactive = false;
	private boolean isInBuildingMode;
	boolean isSoundPlaying = false;
	protected int cost = 10;
	private boolean buildingModeBlocked;
	protected Color color;

	public int getCost() {
		return this.cost;
	}

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

	public void draw(final SpriteBatch spriteBatch) {
		spriteBody.draw(spriteBatch);
	}

	public void drawUpperBuddy(final SpriteBatch spriteBatch) {
		if (firingLineTime > timesincelastshot) {
			drawProjectile(spriteBatch);
			spriteFiring.draw(spriteBatch);
		} else {
			spriteUpperBody.draw(spriteBatch);
		}
	}

	public void drawProjectile(final SpriteBatch spriteBatch) {

	}

	public Array<Body> removeProjectiles() {
		return null;
	}

	public void drawLine(final ShapeRenderer shapeRenderer) {
		if (firingLineTime > timesincelastshot) {
			drawProjectileShape(shapeRenderer);
		}
	}

	public abstract void drawProjectileShape(final ShapeRenderer shapeRenderer);

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

	public boolean buildingModeBlocked() {
		return this.buildingModeBlocked;
	}

	public boolean isInBuildingMode() {
		return this.isInBuildingMode;
	}

	protected Tower(final float xPosition, final float yPosition, final Texture spriteBody,
			final Texture spriteUpperBody, final Texture spriteFiring, final Array<Enemy> enemies, final World world,
			final int range, final Sound soundShoot) {
		this.soundShoot = soundShoot;
		this.timesincelastshot = 10;
		this.enemies = enemies;
		this.healthBar = false;
		this.damage = 0;
		this.range = range;
		this.buildingModeBlocked = false;
		this.color = new Color(1, 0, 0, 0.3f);

		this.spriteBody = new Sprite(spriteBody);
		this.spriteUpperBody = new Sprite(spriteUpperBody);
		this.spriteFiring = new Sprite(spriteFiring);
		this.spriteBody.setSize(spriteBody.getWidth() * PlayState.PIXEL_TO_METER,
				spriteBody.getHeight() * PlayState.PIXEL_TO_METER);
		this.spriteUpperBody.setSize(spriteUpperBody.getWidth() * PlayState.PIXEL_TO_METER,
				spriteUpperBody.getHeight() * PlayState.PIXEL_TO_METER);
		this.spriteFiring.setSize(spriteFiring.getWidth() * PlayState.PIXEL_TO_METER,
				spriteFiring.getHeight() * PlayState.PIXEL_TO_METER);

		// shotposition = new Vector2(xPosition + middleOfSpriteBody, yPosition +
		// middleOfSpriteBody);
		// center = new Vector2(xPosition + middleOfSpriteBody, yPosition +
		// middleOfSpriteBody);

		boolean towerWasAddedToTheWorld = false;
		while (!towerWasAddedToTheWorld) {
			synchronized (world) {
				if (!world.isLocked()) {
					towerWasAddedToTheWorld = true;
					final BodyDef bodydef = new BodyDef();
					bodydef.type = BodyDef.BodyType.KinematicBody;
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

			}
		}

		this.spriteBody.setOriginCenter();
		this.spriteUpperBody.setOriginCenter();
		this.spriteFiring.setOriginCenter();

		this.updateSprites(xPosition, yPosition);

	}

	public void updateSprites(float xPosition, float yPosition) {

		// set body
		this.body.setTransform(new Vector2(xPosition, yPosition), this.body.getAngle());

		xPosition -= spriteBody.getWidth() / 2;
		yPosition -= spriteBody.getWidth() / 2;

		// set body to new position
		this.spriteBody.setPosition(xPosition, yPosition);
		// set upper body to new position
		this.spriteUpperBody.setPosition(xPosition + this.spriteBody.getWidth() / 2 - spriteUpperBody.getHeight() / 2,
				yPosition + this.spriteBody.getWidth() / 2 - spriteUpperBody.getHeight() / 2);
		// fire position to new position
		this.spriteFiring.setPosition(xPosition + spriteBody.getWidth() / 2 - spriteFiring.getHeight() / 2,
				yPosition + spriteBody.getWidth() / 2 - spriteFiring.getHeight() / 2);

		// shot position to new position
		this.shotposition = new Vector2(xPosition + spriteBody.getWidth() / 2, yPosition + spriteBody.getWidth() / 2);
		this.center = new Vector2(xPosition + spriteBody.getWidth() / 2, yPosition + spriteBody.getWidth() / 2);
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

	public void shoot(Enemy e) {
		if (isTargetInRange(e)) {

			e.takeDamage(power);
			if (PlayState.soundon)
				if (!isSoundPlaying) {
					soundShoot.loop(0.5f);
					isSoundPlaying = true;
				}
			timesincelastshot = 0;
			shotposition.x = e.getX() + 10 * PlayState.PIXEL_TO_METER;
			shotposition.y = e.getY() + 10 * PlayState.PIXEL_TO_METER;
			// TODO: Versatz Dynamisch machen!
		} else {
			target = null;
			if (isSoundPlaying) {
				soundShoot.stop();
				;
				isSoundPlaying = false;
			}
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

	public float getDegrees() {
		return this.spriteUpperBody.getRotation();
	}

	public void setDegrees(float degrees) {
		this.spriteUpperBody.setRotation(degrees);
		this.spriteFiring.setRotation(degrees);
	}

	public void rotate(float degrees) {
		spriteUpperBody.rotate(degrees);
		spriteFiring.rotate(degrees);
	}

	public void destroyAnimation() {
		// TODO
		System.out.println("Destroyed");
	}

	public void activateHealthBar(boolean activate) {
		healthBar = activate;
	}

	private void drawHealthBar() {
		// TODO
	}

	public void takeDamage(float amount) {
		damage += amount;
		if (damage >= maxHealth) {
			this.destroyAnimation();
		}
	}

	public void update(float delta, Vector3 mousepos) {

		if (this.isInBuildingMode) {
			this.updateSprites(mousepos.x, mousepos.y);
		}

		this.delta = delta;
		if (isactive) {
			timesincelastshot = timesincelastshot + delta;
			if (target == null) {
				selectNewTarget();
				soundShoot.stop();
				isSoundPlaying = false;
			} else
				tryshoot(target);
			updateProjectiles(delta);
		}

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

	protected boolean isTargetInRange(Enemy e) {
		Vector2 epos = new Vector2(e.getBodyX(), e.getBodyY());
		Vector2 tpos = new Vector2(center.x, center.y);
		float dist = epos.dst(tpos);
		boolean inrange = false;
		if (dist < range)
			inrange = true;
		return inrange;
	}

	public float getX() {
		return spriteBody.getX();
	}

	public float getY() {
		return spriteBody.getY();
	}

	public float getRange() {
		return range;
	}

	public Sprite getSpriteBody() {
		return spriteBody;
	}

	public Vector2 getCenter() {
		return center;
	}

	public void setCenter(Vector2 center) {
		this.center = center;
	}

	public void activate() {
		isactive = true;
	}

	public void activateRange(boolean b) {
		this.rangeActivated = b;
	}

	public boolean rangeIsActivated() {
		return this.rangeActivated;
	}

	public void updateProjectiles(float delta) {

	}

	public boolean contains(float xPos, float yPos) {
		return (xPos >= this.spriteBody.getX() && xPos <= this.spriteBody.getX() + this.spriteBody.getWidth())
				&& (yPos >= this.spriteBody.getY() && yPos <= this.spriteBody.getY() + this.spriteBody.getHeight());
	}

	public void disposeMedia() {
		this.spriteBody.getTexture().dispose();
		this.spriteFiring.getTexture().dispose();
		this.spriteFiring.getTexture().dispose();
		this.soundShoot.dispose();
	}

}
