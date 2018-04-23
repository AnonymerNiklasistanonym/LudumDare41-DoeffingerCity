package com.mygdx.game;

import java.util.ArrayList;
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
import com.mygdx.game.gamestate.state.PlayState;

public abstract class Enemy extends BodyDef {
	public Body body;
	float speed = 80;
	float health = 0;
	Texture taussehen;
	Sprite saussehen;
	Sprite stot;
	float score = 10;
	float money = 0;
	Sprite sdamage;
	World world;
	ArrayList<Node> nodesList;
	MainMap map;
	boolean washit = false;
	LinkedList<Node> weg;
	public boolean justDied = false;

	public boolean tot = false;
	float distancetonode = 50f;

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
		this.saussehen = spriteSprite;
		this.stot = deadspriteSprite;
		this.sdamage = damageSprite;
		this.score = MathUtils.random(100);
		BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.DynamicBody;
		// bodydef.position.set(MathUtils.random(1280)*PlayState.PIXEL_TO_METER,
		// MathUtils.random(720)*PlayState.PIXEL_TO_METER);
		bodydef.position.set(x * PlayState.PIXEL_TO_METER, y * PlayState.PIXEL_TO_METER);
		body = w.createBody(bodydef);
		CircleShape enemyCircle = new CircleShape();
		enemyCircle.setRadius(saussehen.getHeight() * 0.35f);

		FixtureDef fdef = new FixtureDef();
		fdef.shape = enemyCircle;
		fdef.density = 1f;
		fdef.filter.categoryBits = PlayState.ENEMY_BOX;
		fdef.filter.categoryBits = PlayState.PLAYER_BOX;

		body.createFixture(fdef);
		body.setUserData(this);
		this.world = w;
		this.map = map;
		this.findWay();
	}

	public void startMove() {
		body.applyForceToCenter(new Vector2(MathUtils.random(speed * -1, speed), MathUtils.random(speed * -1, speed)),
				true);
	}

	public void endMove() {
		body.applyForceToCenter(new Vector2(speed * -1, 0), true);
	}

	public void steerLeft() {
		body.applyTorque(45, true);
	}

	public void steerRight() {
		body.applyTorque(45 * -1, true);
	}

	public void die() {
		System.out.println("Enemy died");
		tot = true;
		speed = 0;
		saussehen = stot;
		justDied = true;
	}

	public void takeDamage(float amount) {
		health = health - amount;
		washit = true;

	}

	public void findWay() {
		// Wo bin ich wo will ich hin
		// Aus Map auslesen wo das Ziel ist
		PolygonShape ps = (PolygonShape) map.mapZiel.getFixtureList().first().getShape();
		Vector2 vector = new Vector2();
		ps.getVertex(0, vector);

		weg = getPath(this.getBodyX() * PlayState.METER_TO_PIXEL, this.getBodyY() * PlayState.METER_TO_PIXEL,
				vector.x * PlayState.METER_TO_PIXEL, vector.y * PlayState.METER_TO_PIXEL);
	}

	private LinkedList<Node> getPath(float startX, float startY, float zielX, float zielY) {
		LinkedList<Node> openList = new LinkedList();
		LinkedList<Node> closedList = new LinkedList();
		Node[][] tempNodes2DList = map.nodes2DList;
		LinkedList<Node> tempweg = new LinkedList();
		Node aktuellerNode, tempNode;

		boolean found = false;
		// Welcher Node ist der nächste?
		if (startX % 10 < 5) {
			startX = startX - startX % 10;
		}
		if (startX % 10 >= 5) {
			startX = startX + (10 - startX % 10);
		}
		if (startY % 10 < 5) {
			startY = startY - startY % 10;
		}
		if (startY % 10 >= 5) {
			startY = startY + (10 - startY % 10);
		}

		// Ende normalisiesrn?
		if (zielX % 10 < 5) {
			zielX = zielX - zielX % 10;
		}
		if (zielX % 10 >= 5) {
			zielX = zielX + (10 - zielX % 10);
		}
		if (zielY % 10 < 5) {
			zielY = zielY - zielY % 10;
		}
		if (zielY % 10 >= 5) {
			zielY = zielY + (10 - zielY % 10);
		}

		// Welcher Nachbar ist der beste
		float lowCost = 9999999;
		if (tempNodes2DList[(int) startX][(int) startY].noUse)
			System.out.println("Halt");

		if (tempNodes2DList[(int) startX][(int) startY].nachbarn != null) {
			for (Node node : tempNodes2DList[(int) startX][(int) startY].nachbarn) {
				if (!node.noUse)
					if (lowCost > node.getKosten()) {
						node.g = 1;
						openList.add(node);
						lowCost = node.getKosten();
					}
			}
		} else {
			// Irgendwo im Nirgendwo... Raus da
			LinkedList<Node> blub = new LinkedList<Node>();
			blub.add(tempNodes2DList[(int) startX][(int) startY]);
			return blub;
		}

		aktuellerNode = openList.getFirst();
		int zaehler = 10000;
		while (!found) {

			zaehler--;
			if (zaehler < 0) {
				// Bisherige Liste zurückgeben

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
				System.out.println("aktueller Node ist 0");
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
					if (closedList.indexOf(node) > 1 && node.getKosten() > (aktuellerNode.g + 1) * node.h) {
						closedList.remove(closedList.indexOf(node));
						openList.add(node);
					}
				}

			}

			if (aktuellerNode.x == zielX && aktuellerNode.y == zielY) {
				System.out.println("Gefunden");
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
			// Für alle Wege die benutzt werden ein Erschwernis eintragen

			map.nodes2DList[(int) aktuellerNode.x][(int) aktuellerNode.y].erschwernis = MathUtils.random(10);

			// Hinzufügen
			tempweg.add(aktuellerNode);
			aktuellerNode = aktuellerNode.parent;
		}

		return tempweg;
	}

	public LinkedList<Node> getWeg() {
		return weg;
	}

	public float getX() {
		float carx = body.getPosition().x;
		carx = carx - saussehen.getWidth() / 2;
		return carx;
	}

	public float getY() {
		float cary = body.getPosition().y;
		cary = cary - saussehen.getWidth() / 2;
		return cary;
	}

	public float getBodyX() {
		return body.getPosition().x;
	}

	public float getBodyY() {
		return body.getPosition().y;
	}

	public void killLateral(float drift) {
		float lat = getVelocityVector().dot(getOrthogonal());
		Vector2 vlat = getOrthogonal();
		vlat.scl(drift);
		vlat.scl(lat);
		// vlat.scl(body.getFixtureList().first().getDensity());
		// vlat=vlat.scl(-1);
		body.applyLinearImpulse(vlat, body.getPosition(), true);
	}

	public Vector2 getForwardVelocity() {
		Vector2 velo = getVelocityVector();
		velo.rotateRad(body.getAngle() * -1);
		return velo;
	}

	public Vector2 getVelocityVector() {
		return body.getLinearVelocity();
	}

	public Vector2 getOrthogonal() {
		Vector2 ort = new Vector2(1, 0);
		ort.rotateRad(body.getAngle());
		ort.rotate90(1);
		return ort;
	}

	public void reduceToMaxSpeed(float maxspeed) {
		float speed = getForwardVelocity().x;
		if (speed < maxspeed * -1)
			speed = maxspeed * -1;
		if (speed > maxspeed)
			speed = maxspeed;

		Vector2 newSpeed = new Vector2(speed, getForwardVelocity().y);
		newSpeed.rotateRad(body.getAngle());
		body.setLinearVelocity(newSpeed);
	}

	public void update(float delta) {
		float angle = 0;
		if (weg.getLast() != null)
			if (!this.tot) {
				if (health < 0) {
					this.die();
				}

				float testX, testY, bodX, bodY, getLastX, getLastY, getFirstX, getFirstY;
				bodX = getBodyX();
				bodY = getBodyY();
				getLastX = weg.getLast().x * PlayState.PIXEL_TO_METER;
				getLastY = weg.getLast().y * PlayState.PIXEL_TO_METER;
				getFirstX = weg.getFirst().x;
				getFirstY = weg.getFirst().y;
				testX = getBodyX() - weg.getLast().x;
				testY = getBodyY() - weg.getLast().y;

				angle = (float) ((Math.atan2(weg.getLast().x * PlayState.PIXEL_TO_METER - getBodyX(),
						-(weg.getLast().y * PlayState.PIXEL_TO_METER - getBodyY())) * 180.0d / Math.PI));
				body.setTransform(body.getPosition(), (float) Math.toRadians(angle - 90));
				Vector2 velo = new Vector2(speed, 0);
				velo.rotateRad(body.getAngle());
				body.setLinearVelocity(velo);
				// body.applyForceToCenter(velo,true);
				// reduceToMaxSpeed(speed);
				// killLateral(1f);
				distancetonode = saussehen.getWidth();

				if (body.getPosition().dst(weg.getLast().x * PlayState.PIXEL_TO_METER,
						weg.getLast().y * PlayState.PIXEL_TO_METER) < distancetonode)
					weg.remove(weg.indexOf(weg.getLast()));
				if (weg != null)
					score = weg.getLast().h;

			}

	}

	

	public void draw(SpriteBatch spriteBatch) {
		saussehen.setPosition(getX(), getY());
		saussehen.setRotation(MathUtils.radDeg * body.getAngle());
		stot.setRotation(MathUtils.radDeg * body.getAngle());
		saussehen.draw(spriteBatch);
		if (washit) {
			sdamage.setX(getX() + saussehen.getWidth() / 2 + MathUtils.random(-0.2f, 0.2f));
			sdamage.setY(getY() + saussehen.getHeight() / 2 + MathUtils.random(-0.2f, 0.2f));
			sdamage.draw(spriteBatch);
			washit = false;
		}

		sdamage.setX(weg.getLast().x * PlayState.PIXEL_TO_METER);
		sdamage.setY(weg.getLast().y * PlayState.PIXEL_TO_METER);
		sdamage.draw(spriteBatch);
	}

	public float getScore() {
		return score;
	}

	public float getMoney() {
		return money;
	}
}
