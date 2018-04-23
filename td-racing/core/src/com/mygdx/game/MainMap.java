package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.gamestate.state.PlayState;

public class MainMap {
	Sprite sMap;
	Texture tMap;
	Body mapModel;
	Body mapZiel;
	Body finishLine;
	Sprite debug;
	ArrayList<Node> nodesList;
	Node[][] nodes2DList;

	public MainMap(String mapName, World world, Body finishLine) {

		nodesList = new ArrayList<Node>();
		createSolidMap(mapName, world);
		this.finishLine=finishLine;
		createAStarArray();
	}
	
	public Node[][] getNodesList(){
		return nodes2DList;
	}
	
	public void createSolidMap(String mapName, World world) {
		// The following line would throw ExceptionInInitializerError
		tMap = new Texture(Gdx.files.internal("maps/test.png"));
		sMap = new Sprite(tMap);
		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("maps/test.json"));
		BodyEditorLoader loaderZiel = new BodyEditorLoader(Gdx.files.internal("maps/ziel.json"));

		debug = new Sprite(new Texture(Gdx.files.internal("maps/test.png")));

		// 1. Create a BodyDef, as usual.
		BodyDef bd = new BodyDef();
		bd.type = BodyType.StaticBody;

		BodyDef ziel = new BodyDef();
		ziel.type = BodyType.StaticBody;

		// 2. Create a FixtureDef, as usual.
		FixtureDef solid = new FixtureDef();
		solid.density = 1;
		solid.friction = 0.5f;
		solid.restitution = 0.3f;

		FixtureDef nonSolid = new FixtureDef();
		nonSolid.density = 1;
		nonSolid.friction = 0.5f;
		nonSolid.restitution = 0.3f;
		// nonSolid.isSensor = true;

		// 3. Create a Body, as usual.
		mapModel = world.createBody(bd);
		mapZiel = world.createBody(ziel);

		// // 4. Create the body fixture automatically by using the loader.
		loader.attachFixture(mapModel, "Name", solid, PlayState.RESOLUTION_WIDTH * PlayState.PIXEL_TO_METER);
		loaderZiel.attachFixture(mapZiel, "Ziel", solid, PlayState.RESOLUTION_WIDTH * PlayState.PIXEL_TO_METER);
		System.out.println();
	}
	
	public boolean isInBody(final float xPosition, final float yPosition) {
		for (int i = 0; i <= PlayState.RESOLUTION_WIDTH; i += 10) {
			for (int j = 0; j <= PlayState.RESOLUTION_HEIGHT; j += 10) {
				for (final Fixture f : mapModel.getFixtureList()) {
					if (f.testPoint(xPosition, yPosition)) return true;
				}
			}
		}
		return false;
	}

	public void createAStarArray() {
		boolean befahrbar = true;
		PolygonShape ps=(PolygonShape)mapZiel.getFixtureList().first().getShape();
		Vector2 vector = new Vector2();
		ps.getVertex(0, vector);

		// Nodes erstellen
		for (int i = 0; i <= PlayState.RESOLUTION_WIDTH; i += 10) {
			for (int j = 0; j <= PlayState.RESOLUTION_HEIGHT; j += 10) {
				// Im befahrbaren Bereich?
				befahrbar = true;
				for (Fixture f : mapModel.getFixtureList()) {
					if(f.testPoint(i*PlayState.PIXEL_TO_METER, j*PlayState.PIXEL_TO_METER)) {
						befahrbar = false;
					}				
				}
				for (Fixture f : finishLine.getFixtureList()) {
					if(f.testPoint(i*PlayState.PIXEL_TO_METER, j*PlayState.PIXEL_TO_METER)) {
						befahrbar = false;
					}	
				}
				if(befahrbar) nodesList.add(new Node((float) i, (float) j,vector.x, vector.y));
			}
		}
		// Alle Nachbarn in die Nodes eintragen
		for (Node nodeMain : nodesList) {
			// Nachbar finde
			for (Node nodeNachbar : nodesList) {
				if((nodeMain.x+10 == nodeNachbar.x && nodeMain.y == nodeNachbar.y) 
					|| (nodeMain.x == nodeNachbar.x && nodeMain.y+10 == nodeNachbar.y)
					|| (nodeMain.x-10 == nodeNachbar.x && nodeMain.y == nodeNachbar.y) 
					|| (nodeMain.x == nodeNachbar.x && nodeMain.y-10 == nodeNachbar.y))  nodeMain.nachbarn.add(nodeNachbar);
			}
		}
		
		// Vom Ziel ausgehend noch die Entfernung in jeden Node schreiben
		
		// Ende normalisiesrn?
		float zielX = 1,zielY = 1;
		if(vector.x%10 < 5) {
			zielX = vector.x - vector.x%10;
		}
		if(vector.x%10 >= 5) {
			zielX = vector.x + (10-vector.x%10);
		}if(vector.y%10 < 5) {
			zielY = vector.y - vector.y%10;
		}
		if(vector.y%10 >= 5) {
			zielY = vector.y + (10-vector.y%10);
		}
		
		Node zielNode = new Node(true);
		
		for (Node node : nodesList) {
			if(node.x == zielX && node.y == zielY) {
				node.h = 1;
				zielNode = node;
				break;
			}				
		}
		
		werteSetzen(zielNode);
				
		System.out.println();
		boolean isFound = false;
		Node uebergebNode = new Node(true);
		
		this.nodes2DList = new Node[(int)PlayState.RESOLUTION_WIDTH][(int)PlayState.RESOLUTION_HEIGHT];
		// In 2d Array schreiben
		for (int i = 0;i<(int)PlayState.RESOLUTION_WIDTH;i = i + 10) {
			for (int j = 0;j<(int)PlayState.RESOLUTION_HEIGHT;j = j + 10) {
				isFound = false;
				for (Node node : nodesList) {
					if(node.x == i && node.y == j) {
						isFound = true;
						uebergebNode = node;
						break;
					}
				}
				if(isFound) {
					nodes2DList[i][j] = uebergebNode;
				}
				else {
					nodes2DList[i][j] = new Node(true);
				}
			}
		}
		
		
	}
	
	public void werteSetzen(Node meinNode) {
		if(meinNode != null) 
			if(meinNode.nachbarn != null)
				for (Node node : meinNode.nachbarn) {
					if(node.h > meinNode.h+1)
						node.h = meinNode.h+1;
					werteSetzen(node);
				}
	}

} 
