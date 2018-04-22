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

	protected float turnspeed;
	protected float maxHealth;
	protected float damage;
	protected float speed;
	protected float power;
	protected float range;
	protected float firingSpriteTime=0.3f;
	protected float firingLineTime=0.1f;
	protected Vector2 center;
	protected Sprite amunition;
	protected Animation<TextureRegion> destroyAnimation;
	protected Sprite spriteBody;
	protected Sprite spriteUpperBody;
	protected Sprite spriteFiring;
	protected float timesincelastshot;
	boolean healthBar;
	boolean justshot=false;
	Sound soundShoot;
	Enemy target = null;
	Array<Enemy> enemies;
	Vector2 shotposition;
	protected ShapeRenderer sRender;
	float delta=0;
	public Body body;
	boolean isactive=false;
	
	

	
	public void draw(final SpriteBatch spriteBatch) {
		if(firingLineTime>timesincelastshot)
		{
			drawLine(spriteBatch);
		}
		
		spriteBody.draw(spriteBatch);
		if(firingSpriteTime>timesincelastshot)
			spriteFiring.draw(spriteBatch);
		else
			spriteUpperBody.draw(spriteBatch);
		
	
		if (healthBar)
			drawHealthBar();
	}
	
	public void drawLine(final SpriteBatch spriteBatch) {
		spriteBatch.end();
		sRender.setProjectionMatrix(spriteBatch.getProjectionMatrix());
		sRender.begin(ShapeType.Filled);
		sRender.setColor(Color.YELLOW);
		sRender.rectLine(center, shotposition,0.2f);
		sRender.end();
		spriteBatch.begin();
	}

	protected Tower(final float xPosition, final float yPosition, final Texture spriteBody,
			final Texture spriteUpperBody, final Texture spriteFiring, Array<Enemy> enemies, final Sound soundShoot, World w) {
		
		
		
		
		
		
		this.timesincelastshot = 10;
		this.enemies = enemies;
		this.soundShoot = soundShoot;
		this.sRender=new ShapeRenderer();
		this.spriteBody = new Sprite(spriteBody);
		this.spriteBody.setSize(spriteBody.getWidth() * PlayState.PIXEL_TO_METER,
				spriteBody.getHeight() * PlayState.PIXEL_TO_METER);
		this.spriteBody.setOriginCenter();
		
		this.spriteUpperBody = new Sprite(spriteUpperBody);
		this.spriteUpperBody.setSize(spriteUpperBody.getWidth() * PlayState.PIXEL_TO_METER,
				spriteUpperBody.getHeight() * PlayState.PIXEL_TO_METER);
		this.spriteUpperBody.setOriginCenter();
		
		this.spriteFiring = new Sprite(spriteFiring);
		this.spriteFiring.setSize(spriteFiring.getWidth() * PlayState.PIXEL_TO_METER,
				spriteFiring.getHeight() * PlayState.PIXEL_TO_METER);
		this.spriteFiring.setOriginCenter();
		
		final float middleOfSpriteBody = spriteBody.getWidth() / 2 * PlayState.PIXEL_TO_METER;
		final float widthOfUpperBody = spriteUpperBody.getHeight() / 2 * PlayState.PIXEL_TO_METER;
		final float widthOfFiringBody = spriteFiring.getHeight() / 2 * PlayState.PIXEL_TO_METER;
		center = new Vector2(xPosition + middleOfSpriteBody, yPosition + middleOfSpriteBody);
		shotposition = new Vector2(xPosition + middleOfSpriteBody, yPosition + middleOfSpriteBody);
		this.spriteBody.setPosition(xPosition, yPosition);
		this.spriteUpperBody.setPosition(xPosition + middleOfSpriteBody - widthOfUpperBody,
				yPosition + middleOfSpriteBody - widthOfUpperBody);
		this.spriteFiring.setPosition(xPosition + middleOfSpriteBody - widthOfFiringBody,
				yPosition + middleOfSpriteBody - widthOfFiringBody);
		this.healthBar = false;
		this.damage = 0;
		
		BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.KinematicBody;
		bodydef.position.set(xPosition+middleOfSpriteBody, yPosition+middleOfSpriteBody);
		body = w.createBody(bodydef);
		PolygonShape towerBaseBox = new PolygonShape();
		towerBaseBox.setAsBox(spriteBody.getWidth() * 0.5f* PlayState.PIXEL_TO_METER, spriteBody.getHeight() * 0.5f* PlayState.PIXEL_TO_METER);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = towerBaseBox;
		fdef.isSensor=true;
		body.createFixture(fdef);
		body.setUserData(this);
		
	}

	public void tryshoot(Enemy e) {
		if (getAngleToEnemy(e) > getDegrees()) {
			setDegrees(getDegrees() + turnspeed*delta);
		} else {
			setDegrees(getDegrees() - turnspeed*delta);
		}
		if (Math.abs(getDegrees() - getAngleToEnemy(e)) < 1) {
			if (timesincelastshot > speed)
				shoot(e);
		}

		if (e.tot)
			target = null;

	}

	public void shoot(Enemy e) {
		e.takeDamage(power);
		if(PlayState.soundon)
		soundShoot.play();
		timesincelastshot = 0;
		shotposition.x=e.getX()+10*PlayState.PIXEL_TO_METER;
		shotposition.y=e.getY()+10*PlayState.PIXEL_TO_METER;
		//TODO: Versatz Dynamisch machen!
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

	public void update(float delta) {
		this.delta=delta;
		if(isactive) {
		timesincelastshot = timesincelastshot + delta;
		if (target == null)
			selectNewTarget();
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
		isactive=true;
	}

}
