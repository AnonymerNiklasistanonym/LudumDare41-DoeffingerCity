package com.mygdx.game.objects;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.MainMap;
import com.mygdx.game.Node;
import com.mygdx.game.gamestate.state.PlayState;

public abstract class Enemy implements Disposable {

	public static EnemyCallbackInterface callbackInterface;

	private static final float DAMAGE = 2;
	private static final float HEALTH = 10;
	private static final float MONEY = 1;
	private static final float SPEED = 80;
	private static final float SCORE = 10;

	private final Sprite sprite;
	private final Sprite spriteDamage;
	private final Texture textureDead;
	private final float time;

	protected float health;
	protected float money;
	protected float score;
	protected float speed;
	protected float damage;

	private Body body;
	private MainMap map;
	private LinkedList<Node> weg;
	private boolean justDied = false;
	private boolean delete;
	private boolean tot = false;
	private final float distancetonode;
	private boolean activated;
	private float wasHitTime;
	private float hitRandomX;
	private float hitRandomY;
	private Color color;

	public Enemy(final float xPosition, final float yPosition, final World world, final Texture alive,
			final Texture deadsprite, final Texture damagesprite, final MainMap map, final float time) {

		textureDead = deadsprite;

		sprite = new Sprite(alive);
		sprite.setSize(sprite.getWidth() * PlayState.PIXEL_TO_METER, sprite.getHeight() * PlayState.PIXEL_TO_METER);
		sprite.setOriginCenter();

		spriteDamage = new Sprite(damagesprite);
		spriteDamage.setSize(spriteDamage.getWidth() * PlayState.PIXEL_TO_METER,
				spriteDamage.getHeight() * PlayState.PIXEL_TO_METER);
		spriteDamage.setOriginCenter();

		health = HEALTH;
		money = MONEY;
		score = SCORE;
		speed = SPEED;
		damage = DAMAGE;

		// deactivate enemies on creation
		activated = false;

		// give them a time when the should spawn
		this.time = time;

		System.out.println("Enemy time: " + time);

		// create a random color for every enemy
		this.color = new Color(MathUtils.random(0f, 1f), MathUtils.random(0f, 1f), MathUtils.random(0f, 1f), 0.7f);

		// create body for box2D
		createBody(xPosition, yPosition, world);

		this.map = map;

		distancetonode = sprite.getWidth() * 4;
		findWay();
	}

	protected FixtureDef createFixture() {
		final CircleShape enemyCircle = new CircleShape();
		enemyCircle.setRadius(sprite.getHeight() * 0.35f);
		final FixtureDef fdef = new FixtureDef();
		fdef.shape = enemyCircle;
		fdef.density = 0.6f;
		return fdef;
	}

	private void createBody(float x, float y, World w) {
		final BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.DynamicBody;
		bodydef.position.set(x * PlayState.PIXEL_TO_METER, y * PlayState.PIXEL_TO_METER);

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
		final PolygonShape ps = (PolygonShape) map.getMapZielBody().getFixtureList().first().getShape();
		final Vector2 vector = new Vector2();
		ps.getVertex(0, vector);
		weg = getPath(this.getBodyX() * PlayState.METER_TO_PIXEL, this.getBodyY() * PlayState.METER_TO_PIXEL,
				vector.x * PlayState.METER_TO_PIXEL, vector.y * PlayState.METER_TO_PIXEL);
	}

	private LinkedList<Node> getPath(float startX, float startY, float zielX, float zielY) {
		LinkedList<Node> openList = new LinkedList<Node>();
		LinkedList<Node> closedList = new LinkedList<Node>();
		Node[][] tempNodes2DList = map.getNodesList();
		LinkedList<Node> tempweg = new LinkedList<Node>();
		Node aktuellerNode;

		boolean found = false;
		// Welcher Node ist der naechste?
		if (startX % 10 < 5)
			startX = startX - startX % 10;
		else
			startX = startX + (10 - startX % 10);
		if (startY % 10 < 5)
			startY = startY - startY % 10;
		else
			startY = startY + (10 - startY % 10);

		// Ende normalisiesrn?
		if (zielX % 10 < 5)
			zielX = zielX - zielX % 10;
		else
			zielX = zielX + (10 - zielX % 10);
		if (zielY % 10 < 5)
			zielY = zielY - zielY % 10;
		else
			zielY = zielY + (10 - zielY % 10);

		// Welcher Nachbar ist der beste
		float lowCost = 9999999;

		if (tempNodes2DList[(int) startX][(int) startY].getNoUse())
			System.out.println("Halt, Start Node ist ungueltig");

		openList.add(tempNodes2DList[(int) startX][(int) startY]);
		aktuellerNode = tempNodes2DList[(int) startX][(int) startY];
		lowCost = aktuellerNode.getKosten();

		while (!found) {

			// NEU *********************************************
			lowCost = 999999999;

			for (Node node : openList) {
				if (lowCost > node.getKosten()) {
					aktuellerNode = node;
					lowCost = node.getKosten();

				}
			}

			if (openList.indexOf(aktuellerNode) < 0) {
				System.out.println("aktueller Node ist 0");
				return tempweg;
			}

			if (openList.indexOf(aktuellerNode) != -1)
				openList.remove(openList.indexOf(aktuellerNode));
			else
				System.out.println("What the hell just happened???");

			closedList.add(aktuellerNode);

			// Das geht an dieser Stelle irgendwie nicht?
			// tempweg.add(aktuellerNode);
			// aktuellerNode.setErschwernis(MathUtils.random(1f, 3f));

			for (int i = 0; i < aktuellerNode.getNachbarn().size; i++) {
				final Node node = aktuellerNode.getNachbarn().get(i);
				if (closedList.indexOf(node) == -1) {
					node.setG(aktuellerNode.getG() + 1);
					node.setParent(aktuellerNode);
					if (openList.indexOf(node) == -1) {
						node.setKosten(node.getKosten());
						openList.add(node);
					}
				}

			}

			if (aktuellerNode.getX() == zielX && aktuellerNode.getY() == zielY) {
				found = true;
				break;
			}

			// NEU ENDE ***************************************

		}

		while (aktuellerNode != null) {

			// Fuer alle Wege die benutzt werden ein Erschwernis eintragen

			map.getNodesList()[(int) aktuellerNode.getX()][(int) aktuellerNode.getY()]
					.setErschwernis(MathUtils.random(1f, 3f));

			// Hinzufuegen
			tempweg.add(aktuellerNode);
			aktuellerNode = aktuellerNode.getParent();
		}

		return tempweg;
	}

	public LinkedList<Node> getWeg() {
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

		if (weg.size() > 0) {
			final float angle = (float) ((Math.atan2(weg.getLast().getX() * PlayState.PIXEL_TO_METER - getBodyX(),
					-(weg.getLast().getY() * PlayState.PIXEL_TO_METER - getBodyY())) * 180.0d / Math.PI));
			this.body.setTransform(this.body.getPosition(), (float) Math.toRadians(angle - 90));
			final Vector2 velo = new Vector2(speed, 0);
			velo.rotateRad(this.body.getAngle());
			this.body.setLinearVelocity(velo);
			// body.applyForceToCenter(velo,true);
			// reduceToMaxSpeed(speed);
			// killLateral(1f);

			if (this.body.getPosition().dst(weg.getLast().getX() * PlayState.PIXEL_TO_METER,
					weg.getLast().getY() * PlayState.PIXEL_TO_METER) < distancetonode)
				weg.remove(weg.indexOf(weg.getLast()));
			if (weg.size() > 0)
				score = weg.getLast().getH();
		} else {
			callbackInterface.enemyHitsHomeCallback(this);
			this.setDelete(true);
			this.setTot(true);
		}

		sprite.setPosition(getX(), getY());
		sprite.setRotation(MathUtils.radDeg * this.body.getAngle());
	}

	public void draw(final SpriteBatch spriteBatch) {
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
