package com.mygdx.game.objects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Enemy;
import com.mygdx.game.gamestate.state.PlayState;

public abstract class Tower {

	public static Texture circleTexture;
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
	protected Sprite spriteRange;
	protected float timesincelastshot;
	boolean healthBar;
	boolean justshot = false;
	protected boolean permanentsound=false;
	Sound soundShoot;
	Enemy target = null;
	Array<Enemy> enemies;
	protected Vector2 shotposition;
	protected ShapeRenderer sRender;
	float delta = 0;
	public Body body;
	boolean isactive = false;
	private boolean isInBuildingMode;
	boolean isSoundPlaying=false;
	
	public void draw(final SpriteBatch spriteBatch) {

		spriteBody.draw(spriteBatch);
		
		if (firingSpriteTime > timesincelastshot)
			spriteFiring.draw(spriteBatch);
		else
			spriteUpperBody.draw(spriteBatch);
		
		if (this.rangeActivated) {
			spriteRange.draw(spriteBatch);
		}
		
		if (firingLineTime > timesincelastshot) {
			spriteBatch.end();
			sRender.setProjectionMatrix(spriteBatch.getProjectionMatrix());
			sRender.begin(ShapeType.Filled);
			sRender.setColor(Color.YELLOW);
			sRender.rectLine(center, shotposition, 0.2f);
			sRender.end();
			spriteBatch.begin();
		}

		if (healthBar)
			drawHealthBar();
	}

	public void drawLine(final SpriteBatch spriteBatch) {
		sRender.setProjectionMatrix(spriteBatch.getProjectionMatrix());
		sRender.begin(ShapeType.Filled);
		sRender.setColor(Color.YELLOW);
		sRender.rectLine(center, shotposition, 0.2f);
		sRender.end();
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
	
	public boolean isInBuildingMode() {
		return this.isInBuildingMode;
	}

	protected Tower(final float xPosition, final float yPosition, final Texture spriteBody,
			final Texture spriteUpperBody, final Texture spriteFiring, Array<Enemy> enemies, final Sound soundShoot,
			World w, int range) {

		this.timesincelastshot = 10;
		this.enemies = enemies;
		this.soundShoot = soundShoot;
		this.healthBar = false;
		this.damage = 0;
		this.sRender = new ShapeRenderer();
		this.range = range;
		
		spriteRange = new Sprite(circleTexture);
		spriteRange.setSize(this.range * 2, this.range * 2);
		spriteRange.setOriginCenter();
		
		this.spriteBody = new Sprite(spriteBody);
		this.spriteUpperBody = new Sprite(spriteUpperBody);
		this.spriteFiring = new Sprite(spriteFiring);
		this.spriteBody.setSize(spriteBody.getWidth() * PlayState.PIXEL_TO_METER,
				spriteBody.getHeight() * PlayState.PIXEL_TO_METER);
		this.spriteUpperBody.setSize(spriteUpperBody.getWidth() * PlayState.PIXEL_TO_METER,
				spriteUpperBody.getHeight() * PlayState.PIXEL_TO_METER);
		this.spriteFiring.setSize(spriteFiring.getWidth() * PlayState.PIXEL_TO_METER,
				spriteFiring.getHeight() * PlayState.PIXEL_TO_METER);
	
		
		// shotposition = new Vector2(xPosition + middleOfSpriteBody, yPosition + middleOfSpriteBody);
		// center = new Vector2(xPosition + middleOfSpriteBody, yPosition + middleOfSpriteBody);
		
		BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.KinematicBody;
		body = w.createBody(bodydef);
		PolygonShape towerBaseBox = new PolygonShape();
		towerBaseBox.setAsBox(spriteBody.getWidth() * 0.5f * PlayState.PIXEL_TO_METER,
				spriteBody.getHeight() * 0.5f * PlayState.PIXEL_TO_METER);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = towerBaseBox;
		fdef.isSensor = true;
		body.createFixture(fdef);
		body.setUserData(this);
		
		this.spriteBody.setOriginCenter();
		this.spriteUpperBody.setOriginCenter();
		this.spriteFiring.setOriginCenter();	
		
		this.updateSprites(xPosition, yPosition);

		
	}

	public void updateSprites(float xPosition, float yPosition) {
		System.out.println("Update sprite");
		
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
		// range position to new position
		this.spriteRange.setOriginBasedPosition(xPosition +  this.spriteBody.getWidth() / 2,
				yPosition +  this.spriteBody.getWidth() / 2);
		
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
		if (Math.abs(getDegrees() - getAngleToEnemy(e)) < turnspeed*delta) {
			setDegrees(getAngleToEnemy(e));
			if (timesincelastshot > speed)
				shoot(e);
		}

		if (e.tot)
			target = null;

	}

	public void shoot(Enemy e) {
		if(isTargetInRange(e)) {
			
		
		e.takeDamage(power);
		if (PlayState.soundon)
			if(permanentsound)
				if(!isSoundPlaying) {
					soundShoot.loop();
					System.out.println("loop");
					isSoundPlaying=true;
				}
			
			else
				soundShoot.play();
		timesincelastshot = 0;
		shotposition.x = e.getX() + 10 * PlayState.PIXEL_TO_METER;
		shotposition.y = e.getY() + 10 * PlayState.PIXEL_TO_METER;
		// TODO: Versatz Dynamisch machen!
		}
		else
		{
			target=null;
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

	public void dispose() {
		spriteBody.getTexture().dispose();
		spriteUpperBody.getTexture().dispose();
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

	public void update(float delta, Vector2 mousepos) {
		
		if (this.isInBuildingMode) {
			this.updateSprites(mousepos.x,mousepos.y);
		}
		
		this.delta = delta;
		if (isactive) {
			timesincelastshot = timesincelastshot + delta;
			if (target == null) {
				selectNewTarget();
				soundShoot.stop();
				isSoundPlaying=false;
			}
			else
				tryshoot(target);
		}
		
	}

	private void selectNewTarget() {

		Enemy best = null;
		for (Enemy e : enemies) {
			if (best == null)
				if (e.tot == false)
					if (isTargetInRange(e))
						best = e;
			if (best != null)
				if (e.getScore() > best.getScore() && e.tot == false)
					if (isTargetInRange(e))
						best = e;
		}
		target = best;
	}

	private boolean isTargetInRange(Enemy e) {
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

}
