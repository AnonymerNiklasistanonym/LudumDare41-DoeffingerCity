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
	Sprite sdamage;
	float score = 0;
	World world;
	ArrayList<Node> nodesList;
	MainMap map;
	boolean washit=false;
	LinkedList<Node> weg;

	public boolean tot = false;

	public Enemy(World w, Texture sprite, Texture deadsprite, Texture damagesprite, MainMap map) {
		final Sprite spriteSprite = new Sprite(sprite);
		spriteSprite.setSize(spriteSprite.getWidth() * PlayState.PIXEL_TO_METER, spriteSprite.getHeight() * PlayState.PIXEL_TO_METER);
		spriteSprite.setOriginCenter();
		
		final Sprite deadspriteSprite = new Sprite(deadsprite);
		deadspriteSprite.setSize(deadspriteSprite.getWidth() * PlayState.PIXEL_TO_METER, deadspriteSprite.getHeight() * PlayState.PIXEL_TO_METER);
		deadspriteSprite.setOriginCenter();
		
		final Sprite damageSprite = new Sprite(damagesprite);
		damageSprite.setSize(damageSprite.getWidth() * PlayState.PIXEL_TO_METER*0.5f, damageSprite.getHeight() * PlayState.PIXEL_TO_METER*0.5f);
		damageSprite.setOriginCenter();
		

		this.speed = 80;
		this.health = 10;
		this.saussehen = spriteSprite;
		this.stot = deadspriteSprite;
		this.sdamage=damageSprite;
		this.score = MathUtils.random(100);
		BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.DynamicBody;
		// bodydef.position.set(MathUtils.random(1280)*PlayState.PIXEL_TO_METER,
		// MathUtils.random(720)*PlayState.PIXEL_TO_METER);
		bodydef.position.set(300 * PlayState.PIXEL_TO_METER, 150 * PlayState.PIXEL_TO_METER);
		body = w.createBody(bodydef);
		CircleShape enemyCircle = new CircleShape();
		enemyCircle.setRadius(saussehen.getHeight() * 0.35f);
		
		FixtureDef fdef = new FixtureDef();
		fdef.shape = enemyCircle;
		fdef.density=0.1f;
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
		tot = true;
		speed = 0;
		saussehen = stot;

	}

	public void takeDamage(float amount) {
		health = health -amount;
		washit=true;

	}

	public void findWay() {
		// Wo bin ich wo will ich hin
		// Aus Map auslesen wo das Ziel ist
		PolygonShape ps=(PolygonShape)map.mapZiel.getFixtureList().first().getShape();
		Vector2 vector = new Vector2();
		ps.getVertex(0, vector);
		
		weg = getPath(this.getBodyX()*PlayState.METER_TO_PIXEL,this.getBodyY()*PlayState.METER_TO_PIXEL,vector.x*PlayState.METER_TO_PIXEL,vector.y*PlayState.METER_TO_PIXEL);
	}
	
	private LinkedList<Node> getPath(float startX,float startY,float zielX,float zielY){
		LinkedList<Node> openList = new LinkedList();
		LinkedList<Node> closedList = new LinkedList();
		Node[][] tempNodes2DList = map.nodes2DList;
		LinkedList<Node> tempweg = new LinkedList();
		Node aktuellerNode,tempNode;
		boolean isLower;
		float kostenAlt;
		
		boolean found = false;
		// Welcher Node ist der nächste?
		if(startX%10 < 5) {
			startX = startX - startX%10;
		}
		if(startX%10 >= 5) {
			startX = startX + (10-startX%10);
		}if(startY%10 < 5) {
			startY = startY - startY%10;
		}
		if(startY%10 >= 5) {
			startY = startY + (10-startY%10);
		}
		
		// Ende normalisiesrn?
		if(zielX%10 < 5) {
			zielX = zielX - zielX%10;
		}
		if(zielX%10 >= 5) {
			zielX = zielX + (10-zielX%10);
		}if(zielY%10 < 5) {
			zielY = zielY - zielY%10;
		}
		if(zielY%10 >= 5) {
			zielY = zielY + (10-zielY%10);
		}
		
		// Welcher Nachbar ist der beste
		float lowCost = 9999999;
		if(tempNodes2DList[(int)startX][(int)startY].noUse)
			System.out.println("Halt");
		
		if (tempNodes2DList[(int)startX][(int)startY].nachbarn != null)
		{
			for (Node node : tempNodes2DList[(int)startX][(int)startY].nachbarn) {
				if(!node.noUse) 
					if(lowCost > node.getKosten()) {
						node.g = 1;
						openList.add(node);
						lowCost = node.getKosten();
					}
			}
		}
		else {
			// Irgendwo im Nirgendwo... Raus da
			LinkedList<Node>blub = new LinkedList<Node>();
			blub.add(tempNodes2DList[(int)startX][(int)startY]);
			return blub;
		}
		

		aktuellerNode = openList.getFirst();
		int zaehler = 1000;
		while(!found) {
			
			zaehler--;
			if(zaehler < 0) {
				//Bisherige Liste zurückgeben
				
				break;
			}
			
			// NEU *********************************************
			lowCost = 999999;
			for (Node node : openList) {
				if(lowCost > node.getKosten())
					aktuellerNode = node;
					lowCost = node.getKosten();
			}
			
			openList.remove(openList.indexOf(aktuellerNode));
			closedList.add(aktuellerNode);
			
			for (Node node : aktuellerNode.nachbarn) {
				if(closedList.indexOf(node) == -1) {
					node.g = aktuellerNode.g+1;
					node.parent = aktuellerNode;
					if(openList.indexOf(node) == -1)
					{
						node.kosten = node.getKosten();
						openList.add(node);	
					}
					else {
						if(node.kosten > node.getKosten())
						{
							openList.remove(openList.indexOf(node));
							openList.add(node);							
						}
					}
				}
			}
			
			if(aktuellerNode.x == zielX && aktuellerNode.y == zielY) {
				System.out.println("Gefunden");
				break;
			}
			
			// NEU ENDE ***************************************
		

		}
		System.out.println("test");
		zaehler =1000;
		while(aktuellerNode != null) {
			zaehler--;
			if(zaehler < 0) {
				break;
			}
			tempweg.add(aktuellerNode);
			aktuellerNode = aktuellerNode.parent;
		}
		
		return tempweg;
	}
	
	public LinkedList<Node> getWeg(){
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

	public void update(float delta) {
		if (health < 0) {
			this.die();
		}
		body.applyForceToCenter(new Vector2(MathUtils.random(speed * -1, speed), MathUtils.random(speed * -1, speed)),
				true);
	}

	public void draw(SpriteBatch spriteBatch) {
		saussehen.setPosition(getX(), getY());
		saussehen.draw(spriteBatch);
		if(washit) {
			sdamage.setX(getX()+saussehen.getWidth()/2+MathUtils.random(-0.2f,0.2f));
			sdamage.setY(getY()+saussehen.getHeight()/2+MathUtils.random(-0.2f,0.2f));
			sdamage.draw(spriteBatch);
			washit=false;
		}
	}

	public float getScore() {
		return score;
	}
}
