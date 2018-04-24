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
import com.mygdx.game.MainMap;
import com.mygdx.game.Node;
import com.mygdx.game.gamestate.state.PlayState;

public abstract class Enemy extends BodyDef {

	private final Sprite spriteAlive;
	private final Sprite spriteDead;
	private final Sprite spriteDamadge;

	protected float health = 0;
	protected float money = 1;
	protected float score = 10;
	protected float speed = 80;
	protected float damage;

	private Body body;
	private MainMap map;
	private boolean washit = false;
	private LinkedList<Node> weg;
	private boolean justDied = false;
	private boolean delete;
	private boolean tot = false;
	private float distancetonode = 50f;

	public Enemy(float x, float y, World w, Texture sprite, Texture deadsprite, Texture damagesprite, MainMap map) {
		final Sprite spriteSprite = new Sprite(sprite);
		spriteSprite.setSize(spriteSprite.getWidth() * PlayState.PIXEL_TO_METER,
				spriteSprite.getHeight() * PlayState.PIXEL_TO_METER);
		spriteSprite.setOriginCenter();

		final Sprite deadspriteSprite = new Sprite(deadsprite);
		deadspriteSprite.setSize(deadspriteSprite.getWidth() * PlayState.PIXEL_TO_METER,
				deadspriteSprite.getHeight() * PlayState.PIXEL_TO_METER);
		deadspriteSprite.setOriginCenter();

		final Sprite damageSprite = new Sprite(damagesprite);
		damageSprite.setSize(damageSprite.getWidth() * PlayState.PIXEL_TO_METER * 0.5f,
				damageSprite.getHeight() * PlayState.PIXEL_TO_METER * 0.5f);
		damageSprite.setOriginCenter();

		this.speed = 80;
		this.health = 10;
		this.spriteAlive = spriteSprite;
		this.spriteDead = deadspriteSprite;
		this.spriteDamadge = damageSprite;
		this.score = MathUtils.random(100);

		final BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.DynamicBody;
		// bodydef.position.set(MathUtils.random(1280)*PlayState.PIXEL_TO_METER,
		// MathUtils.random(720)*PlayState.PIXEL_TO_METER);
		bodydef.position.set(x * PlayState.PIXEL_TO_METER, y * PlayState.PIXEL_TO_METER);
		this.body = w.createBody(bodydef);
		final CircleShape enemyCircle = new CircleShape();
		enemyCircle.setRadius(spriteAlive.getHeight() * 0.35f);

		final FixtureDef fdef = new FixtureDef();
		fdef.shape = enemyCircle;
		fdef.density = 1f;
		fdef.filter.categoryBits = PlayState.ENEMY_BOX;
		fdef.filter.categoryBits = PlayState.PLAYER_BOX;

		this.body.createFixture(fdef);
		this.body.setUserData(this);
		this.map = map;
		this.findWay();
	}

	public void startMove() {
		getBody().applyForceToCenter(
				new Vector2(MathUtils.random(speed * -1, speed), MathUtils.random(speed * -1, speed)), true);
	}

	public void endMove() {
		getBody().applyForceToCenter(new Vector2(speed * -1, 0), true);
	}

	public void steerLeft() {
		getBody().applyTorque(45, true);
	}

	public void steerRight() {
		getBody().applyTorque(45 * -1, true);
	}

	public void die() {
		// set dead
		this.setTot(true);
		// set position of dead sprite to the current one
		this.spriteDead.setPosition(spriteAlive.getX(), spriteAlive.getY());
		this.spriteDead.setRotation(MathUtils.radDeg * this.body.getAngle());

		// ???
		speed = 0;
		setJustDied(true);
	}

	public void takeDamage(float amount) {
		setHealth(getHealth() - amount);
		washit = true;
		spriteDamadge.setPosition(getX() + spriteAlive.getWidth() / 2 + MathUtils.random(-0.2f, 0.2f),
				getY() + spriteAlive.getHeight() / 2 + MathUtils.random(-0.2f, 0.2f));
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
		final LinkedList<Node> openList = new LinkedList<Node>();
		final LinkedList<Node> closedList = new LinkedList<Node>();
		final Node[][] tempNodes2DList = map.nodes2DList;
		final LinkedList<Node> tempweg = new LinkedList<Node>();
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
		if (tempNodes2DList[(int) startX][(int) startY].noUse)
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
			lowCost = 999999;
			for (Node node : openList) {
				if (lowCost > node.getKosten())
					aktuellerNode = node;
				lowCost = node.getKosten();
			}
			if (aktuellerNode == null)
				System.out.println("aktueller Node ist null");
			if (openList.indexOf(aktuellerNode) < 0)
				System.out.println("aktueller Node ist 0");
			// if(openList.indexOf(aktuellerNode) > 0)
			openList.remove(openList.indexOf(aktuellerNode));

			closedList.add(aktuellerNode);

			for (Node node : aktuellerNode.nachbarn) {
				if (closedList.indexOf(node) == -1) {
					node.g = aktuellerNode.g + 1;
					node.parent = aktuellerNode;
					if (openList.indexOf(node) == -1) {
						node.kosten = node.getKosten();
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

			if (aktuellerNode.x == zielX && aktuellerNode.y == zielY) {
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

			map.nodes2DList[(int) aktuellerNode.x][(int) aktuellerNode.y].erschwernis = MathUtils.random(1f, 3f);

			// Hinzufuegen
			tempweg.add(aktuellerNode);
			aktuellerNode = aktuellerNode.parent;
		}

		return tempweg;
	}

	public LinkedList<Node> getWeg() {
		return weg;
	}

	public float getX() {
		return getBody().getPosition().x - spriteAlive.getWidth() / 2;
	}

	public float getY() {
		return getBody().getPosition().y - spriteAlive.getHeight() / 2;
	}

	public float getBodyX() {
		return getBody().getPosition().x;
	}

	public float getBodyY() {
		return getBody().getPosition().y;
	}

	public void killLateral(final float drift) {
		final float lat = getVelocityVector().dot(getOrthogonal());
		final Vector2 vlat = getOrthogonal();
		vlat.scl(drift);
		vlat.scl(lat);
		// vlat.scl(body.getFixtureList().first().getDensity());
		// vlat=vlat.scl(-1);
		getBody().applyLinearImpulse(vlat, getBody().getPosition(), true);
	}

	public Vector2 getForwardVelocity() {
		return getVelocityVector().rotateRad(getBody().getAngle() * -1);
	}

	public Vector2 getVelocityVector() {
		return getBody().getLinearVelocity();
	}

	public Vector2 getOrthogonal() {
		final Vector2 ort = new Vector2(1, 0);
		ort.rotateRad(getBody().getAngle());
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
		newSpeed.rotateRad(getBody().getAngle());
		getBody().setLinearVelocity(newSpeed);
	}

	public void update(float delta) {
		if (this.isTot())
			return;

		if (getHealth() < 0)
			this.die();

		if (weg.size() > 0) {
			final float angle = (float) ((Math.atan2(weg.getLast().x * PlayState.PIXEL_TO_METER - getBodyX(),
					-(weg.getLast().y * PlayState.PIXEL_TO_METER - getBodyY())) * 180.0d / Math.PI));
			getBody().setTransform(getBody().getPosition(), (float) Math.toRadians(angle - 90));
			final Vector2 velo = new Vector2(speed, 0);
			velo.rotateRad(this.body.getAngle());
			this.body.setLinearVelocity(velo);
			// body.applyForceToCenter(velo,true);
			// reduceToMaxSpeed(speed);
			// killLateral(1f);
			distancetonode = spriteAlive.getWidth() * 4;

			if (this.body.getPosition().dst(weg.getLast().x * PlayState.PIXEL_TO_METER,
					weg.getLast().y * PlayState.PIXEL_TO_METER) < distancetonode)
				weg.remove(weg.indexOf(weg.getLast()));
			if (weg.size() > 0)
				score = weg.getLast().h;
		} else {
			PlayState.scoreBoard.reduceLife(damage);
			this.setDelete(true);
			this.setTot(true);
		}

		spriteAlive.setPosition(getX(), getY());
		spriteAlive.setRotation(MathUtils.radDeg * getBody().getAngle());
	}

	public void draw(SpriteBatch spriteBatch) {
		if (this.isTot())
			spriteDead.draw(spriteBatch);
		else {
			spriteAlive.draw(spriteBatch);
			if (washit) {
				spriteDamadge.draw(spriteBatch);
				washit = false;
			}
		}
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

	public void setBody(Body body) {
		this.body = body;
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
}
