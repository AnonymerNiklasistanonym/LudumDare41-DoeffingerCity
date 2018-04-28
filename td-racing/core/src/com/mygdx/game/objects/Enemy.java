package com.mygdx.game.objects;

import java.util.LinkedList;

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

	private final Sprite spriteAlive;
	private final Texture textureDead;

	private final float time;

	protected float health = 0;
	protected float money = 1;
	protected float score = 10;
	protected float speed = 80;
	protected float damage;

	private Body body;
	private MainMap map;
	private LinkedList<Node> weg;
	private boolean justDied = false;
	private boolean delete;
	private boolean tot = false;
	private float distancetonode = 50f;
	private boolean activated;
	private final Sprite spriteDamadge;
	private float wasHitTime;
	private float hitRandomX;
	private float hitRandomY;

	public static boolean worldIsLocked;

	public Enemy(float x, float y, World w, Texture sprite, Texture deadsprite, Texture damagesprite, MainMap map,
			final float time) {
		final Sprite spriteSprite = new Sprite(sprite);
		spriteSprite.setSize(spriteSprite.getWidth() * PlayState.PIXEL_TO_METER,
				spriteSprite.getHeight() * PlayState.PIXEL_TO_METER);
		spriteSprite.setOriginCenter();

		final Sprite damageSprite = new Sprite(damagesprite);
		damageSprite.setSize(damagesprite.getWidth() * PlayState.PIXEL_TO_METER,
				damagesprite.getHeight() * PlayState.PIXEL_TO_METER);
		damageSprite.setOriginCenter();

		this.textureDead = deadsprite;

		this.speed = 80;
		this.health = 10;
		this.spriteAlive = spriteSprite;
		this.spriteDamadge = damageSprite;
		this.score = MathUtils.random(100);
		this.activated = false;
		this.time = time;

		final BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.DynamicBody;
		// bodydef.position.set(MathUtils.random(1280)*PlayState.PIXEL_TO_METER,
		// MathUtils.random(720)*PlayState.PIXEL_TO_METER);
		bodydef.position.set(x * PlayState.PIXEL_TO_METER, y * PlayState.PIXEL_TO_METER);
		boolean enemyCreated = false;
		while (!enemyCreated) {
			synchronized (w) {
				if (!worldIsLocked) {
					this.body = w.createBody(bodydef);
					this.body.setActive(false);
					enemyCreated = true;
				}
			}
		}
		final CircleShape enemyCircle = new CircleShape();
		enemyCircle.setRadius(spriteAlive.getHeight() * 0.35f);

		final FixtureDef fdef = new FixtureDef();
		fdef.shape = enemyCircle;
		fdef.density = 1f;
		// fdef.isSensor=true;
		fdef.filter.categoryBits = PlayState.ENEMY_BOX;
		fdef.filter.categoryBits = PlayState.PLAYER_BOX;

		this.body.createFixture(fdef);
		this.body.setUserData(this);
		this.map = map;
		this.findWay();
	}

	public float getTime() {
		return this.time;
	}

	public void activateEnemy() {
		this.activated = true;
		this.body.setActive(true);
	}

	public void startMove() {
		this.body.applyForceToCenter(
				new Vector2(MathUtils.random(speed * -1, speed), MathUtils.random(speed * -1, speed)), true);
	}

	public void endMove() {
		this.body.applyForceToCenter(new Vector2(speed * -1, 0), true);
	}

	public void steerLeft() {
		this.body.applyTorque(45, true);
	}

	public void steerRight() {
		this.body.applyTorque(45 * -1, true);
	}

	public void die() {
		// set dead
		this.setTot(true);
		// set position of dead sprite to the current one
		this.spriteAlive.setTexture(textureDead);
		this.spriteAlive.setRotation(MathUtils.radDeg * this.body.getAngle());

		// ???
		this.wasHitTime = 0;
		speed = 0;
		setJustDied(true);
	}

	public void takeDamage(float amount) {
		this.health -= amount;
		this.wasHitTime = 1f;
		this.hitRandomX = MathUtils.random(-this.spriteAlive.getWidth() / 4, this.spriteAlive.getWidth() / 4);
		this.hitRandomY = MathUtils.random(-this.spriteAlive.getHeight() / 4, this.spriteAlive.getHeight() / 4);
	}

	public void findWay() {
		// Wo bin ich wo will ich hin
		// Aus Map auslesen wo das Ziel ist
		final PolygonShape ps = (PolygonShape) map.mapZiel.getFixtureList().first().getShape();
		final Vector2 vector = new Vector2();
		ps.getVertex(0, vector);
		weg = getPath(this.getBodyX() * PlayState.METER_TO_PIXEL, this.getBodyY() * PlayState.METER_TO_PIXEL,
				vector.x * PlayState.METER_TO_PIXEL, vector.y * PlayState.METER_TO_PIXEL);
	}

	private LinkedList<Node> getPath(float startX, float startY, float zielX, float zielY) {
		LinkedList<Node> openList = new LinkedList<Node>();
		LinkedList<Node> closedList = new LinkedList<Node>();
		Node[][] tempNodes2DList = map.nodes2DList;
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
			System.out.println("Halt");

		// if (tempNodes2DList[(int) startX][(int) startY].nachbarn != null) {
		// for (Node node : tempNodes2DList[(int) startX][(int) startY].nachbarn) {
		// if (!node.noUse)
		// if (lowCost > node.getKosten()) {
		// node.g = 1;
		// openList.add(node);
		// lowCost = node.getKosten();
		// }
		// }
		// } else {
		// // Irgendwo im Nirgendwo... Raus da
		// LinkedList<Node> blub = new LinkedList<Node>();
		// blub.add(tempNodes2DList[(int) startX][(int) startY]);
		// return blub;
		// }

		openList.add(tempNodes2DList[(int) startX][(int) startY]);
		aktuellerNode = tempNodes2DList[(int) startX][(int) startY];
		int zaehler = 10000;
		while (!found) {

			zaehler--;
			if (zaehler < 0) {
				// Bisherige Liste zurueckgeben

				break;
			}

			// NEU *********************************************
			lowCost = 999999999;
			for (Node node : openList) {
				if (lowCost > node.getKosten())
					aktuellerNode = node;
				lowCost = node.getKosten();
			}
			if (aktuellerNode == null) {
				System.out.println("aktueller Node ist null");
				return tempweg;
			}
			if (openList.indexOf(aktuellerNode) < 0)
				System.out.println("aktueller Node ist 0");
			// if(openList.indexOf(aktuellerNode) > 0)
			if (openList.indexOf(aktuellerNode) != -1)
				openList.remove(openList.indexOf(aktuellerNode));
			else
				System.out.println("What the hell just happened???");

			closedList.add(aktuellerNode);

			for (Node node : aktuellerNode.getNachbarn()) {
				if (closedList.indexOf(node) == -1) {
					node.setG(aktuellerNode.getG() + 1);
					node.setParent(aktuellerNode);
					if (openList.indexOf(node) == -1) {
						node.setKosten(node.getKosten());
						openList.add(node);
					} else {
						// if(node.kosten > node.getKosten())
						// {
						// openList.remove(openList.indexOf(node));
						// openList.add(node);
						// }
					}
				} else {
					// if (closedList.indexOf(node) > 1 && node.getKosten() > (aktuellerNode.g + 1)
					// * node.h) {
					// closedList.remove(closedList.indexOf(node));
					// openList.add(node);
					// }
				}

			}

			if (aktuellerNode.getX() == zielX && aktuellerNode.getY() == zielY) {
				// System.out.println("Gefunden");
				break;
			}

			// NEU ENDE ***************************************

		}
		zaehler = 1000;
		while (aktuellerNode != null) {
			zaehler--;
			if (zaehler < 0) {
				break;
			}
			// Fuer alle Wege die benutzt werden ein Erschwernis eintragen

			map.nodes2DList[(int) aktuellerNode.getX()][(int) aktuellerNode.getY()]
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
		return this.body.getPosition().x - spriteAlive.getWidth() / 2;
	}

	public float getY() {
		return this.body.getPosition().y - spriteAlive.getHeight() / 2;
	}

	public float getBodyX() {
		return this.body.getPosition().x;
	}

	public float getBodyY() {
		return this.body.getPosition().y;
	}

	public void killLateral(final float drift) {
		final float lat = getVelocityVector().dot(getOrthogonal());
		final Vector2 vlat = getOrthogonal();
		vlat.scl(drift);
		vlat.scl(lat);
		// vlat.scl(body.getFixtureList().first().getDensity());
		// vlat=vlat.scl(-1);
		this.body.applyLinearImpulse(vlat, this.body.getPosition(), true);
	}

	public Vector2 getForwardVelocity() {
		return getVelocityVector().rotateRad(this.body.getAngle() * -1);
	}

	public Vector2 getVelocityVector() {
		return this.body.getLinearVelocity();
	}

	public Vector2 getOrthogonal() {
		final Vector2 ort = new Vector2(1, 0);
		ort.rotateRad(this.body.getAngle());
		ort.rotate90(1);
		return ort;
	}

	public void reduceToMaxSpeed(final float maxspeed) {
		float speed = getForwardVelocity().x;
		if (speed < maxspeed * -1)
			speed = maxspeed * -1;
		if (speed > maxspeed)
			speed = maxspeed;

		final Vector2 newSpeed = new Vector2(speed, getForwardVelocity().y);
		newSpeed.rotateRad(this.body.getAngle());
		this.body.setLinearVelocity(newSpeed);
	}

	public void update(final float deltaTime) {
		if (this.isTot() || !this.activated)
			return;

		if (this.wasHitTime > 0) {
			this.wasHitTime -= deltaTime;
			this.spriteDamadge.setPosition(
					getX() + this.spriteAlive.getWidth() / 2 - this.spriteDamadge.getWidth() / 2 + hitRandomX,
					getY() + this.spriteAlive.getHeight() / 2 - this.spriteDamadge.getHeight() / 2 + hitRandomY);
		}

		if (getHealth() <= 0)
			this.die();

		if (weg.size() >= 0) {
			final float angle = (float) ((Math.atan2(weg.getLast().getX() * PlayState.PIXEL_TO_METER - getBodyX(),
					-(weg.getLast().getY() * PlayState.PIXEL_TO_METER - getBodyY())) * 180.0d / Math.PI));
			this.body.setTransform(this.body.getPosition(), (float) Math.toRadians(angle - 90));
			final Vector2 velo = new Vector2(speed, 0);
			velo.rotateRad(this.body.getAngle());
			this.body.setLinearVelocity(velo);
			// body.applyForceToCenter(velo,true);
			// reduceToMaxSpeed(speed);
			// killLateral(1f);
			distancetonode = spriteAlive.getWidth() * 4;

			if (this.body.getPosition().dst(weg.getLast().getX() * PlayState.PIXEL_TO_METER,
					weg.getLast().getY() * PlayState.PIXEL_TO_METER) < distancetonode)
				weg.remove(weg.indexOf(weg.getLast()));
			if (weg.size() > 0)
				score = weg.getLast().getH();
		} else {
			PlayState.enemyHitsYourHome(damage);
			this.setDelete(true);
			this.setTot(true);
		}

		spriteAlive.setPosition(getX(), getY());
		spriteAlive.setRotation(MathUtils.radDeg * this.body.getAngle());
	}

	public void draw(final SpriteBatch spriteBatch) {
		spriteAlive.draw(spriteBatch);
		if (!this.isTot() && this.wasHitTime > 0)
			spriteDamadge.draw(spriteBatch);
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
		spriteAlive.getTexture().dispose();
		textureDead.dispose();
		spriteDamadge.getTexture().dispose();
	}
}
