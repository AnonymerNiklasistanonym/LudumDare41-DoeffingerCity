package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.gamestate.states.PlayState;
import com.mygdx.game.unsorted.Node;

public abstract class Enemy implements Disposable {

	public static EnemyCallbackInterface callbackInterface;

	private static final float DAMAGE = 2;
	private static final float HEALTH = 10;
	private static final float MONEY = 1;
	private static final float SPEED = 80;
	private static final float SCORE = 10;
	private static final boolean HEALTH_BAR = true;

	private final Sprite sprite;
	private final Sprite spriteDamage;
	private final Texture textureDead;
	private final float time;

	protected float maxHealth;
	protected float health;
	protected float money;
	protected float score;
	protected float speed;
	protected float damage;

	private Body body;
	private Map map;
	private Array<Node> weg;
	private boolean justDied = false;
	private boolean delete;
	private boolean tot = false;
	private float distancetonode;
	private boolean activated;
	private float wasHitTime;
	private float hitRandomX;
	private float hitRandomY;
	private Color color;
	protected boolean healthBar;

	public Enemy(final Vector2 position, final World world, final Texture alive, final Texture deadsprite,
			final Texture damagesprite, final Map map, final float time) {

		textureDead = deadsprite;

		sprite = new Sprite(alive);
		sprite.setSize(sprite.getWidth() * PlayState.PIXEL_TO_METER, sprite.getHeight() * PlayState.PIXEL_TO_METER);
		sprite.setOriginCenter();

		spriteDamage = new Sprite(damagesprite);
		spriteDamage.setSize(spriteDamage.getWidth() * PlayState.PIXEL_TO_METER,
				spriteDamage.getHeight() * PlayState.PIXEL_TO_METER);
		spriteDamage.setOriginCenter();

		health = HEALTH;
		maxHealth = HEALTH;
		money = MONEY;
		score = SCORE;
		speed = SPEED;
		damage = DAMAGE;
		healthBar = HEALTH_BAR;

		// deactivate enemies on creation
		activated = false;

		// give them a time when the should spawn
		this.time = time;

		// create a random color for every enemy
		this.color = new Color(MathUtils.random(0f, 1f), MathUtils.random(0f, 1f), MathUtils.random(0f, 1f), 0.7f);

		// create body for box2D
		createBody(position, world);

		this.map = map;

		distancetonode = sprite.getWidth() * 4;
		findWay();
	}

	protected FixtureDef createFixture() {
		final CircleShape enemyCircle = new CircleShape();
		enemyCircle.setRadius(sprite.getHeight() * 0.35f);
		final FixtureDef fdef = new FixtureDef();
		fdef.shape = enemyCircle;
		fdef.density = 0.9f;
		return fdef;
	}

	private void createBody(final Vector2 position, World w) {
		final BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.DynamicBody;
		bodydef.position.set(position.x * PlayState.PIXEL_TO_METER, position.y * PlayState.PIXEL_TO_METER);

		this.body = w.createBody(bodydef);
		this.body.setActive(false);

		this.body.createFixture(createFixture());
		this.body.setUserData(this);
	}

	public float getTime() {
		return this.time;
	}

	public void activateEnemy() {
		this.activated = true;
		this.body.setActive(true);
	}

	public void steerLeft() {
		this.body.applyTorque(45, true);
	}

	public void steerRight() {
		this.body.applyTorque(45 * -1, true);
	}

	private void die() {
		// set dead
		this.setTot(true);
		// set position of dead sprite to the current one
		sprite.setTexture(textureDead);
		sprite.setSize(textureDead.getWidth() * PlayState.PIXEL_TO_METER,
				textureDead.getHeight() * PlayState.PIXEL_TO_METER);
		sprite.setRotation(MathUtils.random(360));
		// ???
		this.wasHitTime = 0;
		speed = 0;
		setJustDied(true);
	}

	public void takeDamage(float amount) {
		this.health -= amount;
		this.wasHitTime = 0.15f;
	}

	private void findWay() {
		// Wo bin ich wo will ich hin
		// Aus Map auslesen wo das Ziel ist
		// final PolygonShape ps = (PolygonShape)
		// map.getMapZielBody().getFixtureList().first().getShape();
		// final Vector2 vector = new Vector2();
		// ps.getVertex(0, vector);
		// weg = getPath(new Vector2(getBodyX() * PlayState.METER_TO_PIXEL,
		// this.getBodyY() * PlayState.METER_TO_PIXEL),
		// new Vector2(vector.x * PlayState.METER_TO_PIXEL, vector.y *
		// PlayState.METER_TO_PIXEL));
		weg = map.getRandomPath();
		if (weg.size < 1)
			System.out.println("Ich hab keinen gueltigen Weg bekommen :(");
	}

	public Array<Node> getWeg() {
		return weg;
	}

	public float getX() {
		return this.body.getPosition().x - sprite.getWidth() / 2;
	}

	public float getY() {
		return this.body.getPosition().y - sprite.getHeight() / 2;
	}

	public float getBodyX() {
		return this.body.getPosition().x;
	}

	public float getBodyY() {
		return this.body.getPosition().y;
	}

	public void drawHealthBar(final ShapeRenderer shapeRenderer) {
		if (tot || !activated || !healthBar || health == maxHealth || health <= 0)
			return;
		shapeRenderer.setColor(new Color(1, 0, 0, 1));
		shapeRenderer.rect(getBodyX(), getBodyY() + sprite.getHeight() / 2, 50 * PlayState.PIXEL_TO_METER,
				3 * PlayState.PIXEL_TO_METER);
		shapeRenderer.setColor(new Color(0, 1, 0, 1));
		shapeRenderer.rect(getBodyX(), getBodyY() + sprite.getHeight() / 2,
				50 * PlayState.PIXEL_TO_METER * (health / maxHealth), 3 * PlayState.PIXEL_TO_METER);
	}

	public void update(final float deltaTime) {
		if (this.isTot() || !this.activated)
			return;

		if (this.wasHitTime > 0) {
			this.wasHitTime -= deltaTime;
			this.hitRandomX = MathUtils.random(-this.sprite.getWidth() / 4, this.sprite.getWidth() / 4);
			this.hitRandomY = MathUtils.random(-this.sprite.getHeight() / 4, this.sprite.getHeight() / 4);
			this.spriteDamage.setPosition(
					getX() + this.sprite.getWidth() / 2 - this.spriteDamage.getWidth() / 2 + hitRandomX,
					getY() + this.sprite.getHeight() / 2 - this.spriteDamage.getHeight() / 2 + hitRandomY);
		}

		if (getHealth() <= 0)
			this.die();

		if (weg.size > 0) {
			final float angle = (float) ((Math.atan2(
					weg.get(weg.size - 1).getPosition().x * PlayState.PIXEL_TO_METER - getBodyX(),
					-(weg.get(weg.size - 1).getPosition().y * PlayState.PIXEL_TO_METER - getBodyY())) * 180.0d
					/ Math.PI));
			this.body.setTransform(this.body.getPosition(), (float) Math.toRadians(angle - 90));
			final Vector2 velo = new Vector2(speed, 0);
			velo.rotateRad(this.body.getAngle());

			body.applyForceToCenter(velo, true);
			reduceToMaxSpeed(speed);
			killLateral(0.1f);

			if (weg.size == 1)
				distancetonode = sprite.getWidth() * 2;

			if (this.body.getPosition().dst(weg.get(weg.size - 1).getPosition().x * PlayState.PIXEL_TO_METER,
					weg.get(weg.size - 1).getPosition().y * PlayState.PIXEL_TO_METER) < distancetonode)
				weg.removeIndex(weg.size - 1);

			if (weg.size > 0)
				score = weg.get(weg.size - 1).getH();

		} else {
			callbackInterface.enemyHitsHomeCallback(this);
			this.setDelete(true);
			this.setTot(true);
		}

		sprite.setPosition(getX(), getY());
		sprite.setRotation(MathUtils.radDeg * this.body.getAngle());
	}

	private void killLateral(float drift) {
		float lat = getVelocityVector().dot(getOrthogonal());
		body.applyLinearImpulse(getOrthogonal().scl(drift).scl(lat).scl(-1), body.getPosition(), true);
	}

	private Vector2 getVelocityVector() {
		return body.getLinearVelocity();
	}

	private Vector2 getOrthogonal() {
		final Vector2 ort = new Vector2(1, 0);
		ort.rotateRad(body.getAngle());
		ort.rotate90(1);
		return ort;
	}

	private void reduceToMaxSpeed(float maxspeed) {
		float speed = getForwardVelocity().x;
		if (speed < maxspeed * -1)
			speed = maxspeed * -1;
		if (speed > maxspeed)
			speed = maxspeed;

		final Vector2 newSpeed = new Vector2(speed, getForwardVelocity().y);
		newSpeed.rotateRad(body.getAngle());
		body.setLinearVelocity(newSpeed);
	}

	private Vector2 getForwardVelocity() {
		final Vector2 velo = getVelocityVector();
		velo.rotateRad(body.getAngle() * -1);
		return velo;
	}

	public void draw(final SpriteBatch spriteBatch) {
		if (activated)
			sprite.draw(spriteBatch);
		if (!this.isTot() && this.wasHitTime > 0)
			spriteDamage.draw(spriteBatch);
	}

	public float getScore() {
		return score;
	}

	public float getMoney() {
		return money;
	}

	public Body getBody() {
		return body;
	}

	public float getHealth() {
		return health;
	}

	public float getDamadge() {
		return damage;
	}

	public void setHealth(float health) {
		this.health = health;
	}

	public boolean isJustDied() {
		return justDied;
	}

	public void setJustDied(boolean justDied) {
		this.justDied = justDied;
	}

	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public boolean isTot() {
		return tot;
	}

	public void setTot(boolean tot) {
		this.tot = tot;
	}

	public boolean isActivated() {
		return this.activated;
	}

	public void disposeMedia() {
		sprite.getTexture().dispose();
		spriteDamage.getTexture().dispose();
	}

	public Color getColor() {
		return this.color;
	}
}
