package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.gamestate.state.PlayState;
import com.mygdx.game.level.Level;

public class MainMap {

	private final Array<Node> nodesList;

	private Body mapModel;
	private Body mapZiel;
	private Body finishLine;
	private Body mapZombieWay;
	private Node[][] nodes2DList;
	private Vector2 spawn;
	private Sprite map;
	private Vector2 zielpos;
	private final Array<Array<Node>> paths;

	public MainMap(final Level levelinfo, final World world, final Body finishLine) {
		nodesList = new Array<Node>();
		createSolidMap(levelinfo.getMapName(), world);
		this.finishLine = finishLine;
		createAStarArray();
		tempNodes2DList = getNodesList();
		paths = creatZombieWays(levelinfo.getSpawnPoint(), 200);
	}
	
	private Array<Array<Node>> creatZombieWays(final Vector2 spawnPoint, final int numberOfWaysToCreate) {
		final Array<Array<Node>> paths = new Array<Array<Node>>(numberOfWaysToCreate);
		final PolygonShape ps = (PolygonShape) getMapZielBody().getFixtureList().first().getShape();
		final Vector2 vector = new Vector2();
		ps.getVertex(0, vector);		
		final Vector2 targetPosition = new Vector2(vector.x * PlayState.METER_TO_PIXEL, vector.y * PlayState.METER_TO_PIXEL);
		
		spawnPoint.set(getNextNode(spawnPoint));
		targetPosition.set(getNextNode(targetPosition));
		
		for (int i = 0; i < numberOfWaysToCreate; i++)
			paths.add(getPath(spawnPoint,targetPosition));
		return paths;
	}
	
	private Vector2 getNextNode(final Vector2 startPosition) {

		// Welcher Node ist der naechste?
		if (startPosition.x % 10 < 5)
			startPosition.x = startPosition.x - startPosition.x % 10;
		else
			startPosition.x = startPosition.x + (10 - startPosition.x % 10);
		if (startPosition.y % 10 < 5)
			startPosition.y = startPosition.y - startPosition.y % 10;
		else
			startPosition.y = startPosition.y + (10 - startPosition.y % 10);
		
		return startPosition;		
	}

	public Body getMapZielBody() {
		return mapZiel;
	}

	public Node[][] getNodesList() {
		return nodes2DList;
	}

	public void createSolidMap(String mapName, final World world) {
		// The following line would throw ExceptionInInitializerError

		map = new Sprite(new Texture(Gdx.files.internal("maps/" + mapName + ".png")));
		map.setSize(map.getWidth() * PlayState.PIXEL_TO_METER, map.getHeight() * PlayState.PIXEL_TO_METER);

		final BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("maps/" + mapName + "solid.json"));
		final BodyEditorLoader loaderZiel = new BodyEditorLoader(Gdx.files.internal("maps/" + mapName + "ziel.json"));
		final BodyEditorLoader loaderZombieWay = new BodyEditorLoader(
				Gdx.files.internal("maps/" + mapName + "zombieway.json"));

		// 1. Create a BodyDef, as usual.
		final BodyDef bd = new BodyDef();
		bd.type = BodyType.StaticBody;

		final BodyDef ziel = new BodyDef();
		ziel.type = BodyType.StaticBody;

		final BodyDef zombieway = new BodyDef();
		zombieway.type = BodyType.StaticBody;

		// 2. Create a FixtureDef, as usual.
		final FixtureDef solid = new FixtureDef();
		solid.density = 1;
		solid.friction = 0.5f;
		solid.restitution = 0.3f;

		final FixtureDef nonSolid = new FixtureDef();
		nonSolid.density = 1;
		nonSolid.friction = 0.5f;
		nonSolid.restitution = 0.3f;
		nonSolid.isSensor = true;

		// 3. Create a Body, as usual.
		mapModel = world.createBody(bd);
		mapZiel = world.createBody(ziel);
		mapZombieWay = world.createBody(ziel);

		// // 4. Create the body fixture automatically by using the loader.
		loader.attachFixture(mapModel, "Map", solid, PlayState.RESOLUTION_WIDTH * PlayState.PIXEL_TO_METER);
		loaderZiel.attachFixture(mapZiel, "Ziel", nonSolid, PlayState.RESOLUTION_WIDTH * PlayState.PIXEL_TO_METER);
		loaderZombieWay.attachFixture(mapZombieWay, "Zombieway", nonSolid,
				PlayState.RESOLUTION_WIDTH * PlayState.PIXEL_TO_METER);
	}

	public boolean isInBody(final float xPosition, final float yPosition) {
		for (final Fixture f : mapModel.getFixtureList()) {
			if (f.testPoint(xPosition, yPosition))
				return true;
		}
		return false;
	}

	private void createAStarArray() {
		boolean inEnemyMoveArea = true;
		final PolygonShape ps = (PolygonShape) mapZiel.getFixtureList().first().getShape();
		final Vector2 vector = new Vector2();
		ps.getVertex(0, vector);

		// Create nodes
		for (int i = 0; i <= PlayState.RESOLUTION_WIDTH; i += 10) {
			for (int j = 0; j <= PlayState.RESOLUTION_HEIGHT; j += 10) {
				// In enemy move area?
				inEnemyMoveArea = true;
				for (final Fixture f : mapZombieWay.getFixtureList()) {
					if (f.testPoint(i * PlayState.PIXEL_TO_METER, j * PlayState.PIXEL_TO_METER))
						inEnemyMoveArea = false;
				}
				for (final Fixture f : finishLine.getFixtureList()) {
					if (f.testPoint(i * PlayState.PIXEL_TO_METER, j * PlayState.PIXEL_TO_METER))
						inEnemyMoveArea = false;
				}
				if (inEnemyMoveArea)
					nodesList.add(new Node((float) i, (float) j));
			}
		}
		// Write all neighbors into the nodes
		for (int i = 0; i < nodesList.size; i++) {
			// find Neighbor
			for (int j = 0; j < nodesList.size; j++) {
				if ((nodesList.get(i).getPosition().x + 10 == nodesList.get(j).getPosition().x
						&& nodesList.get(i).getPosition().y == nodesList.get(j).getPosition().y)
						|| (nodesList.get(i).getPosition().x == nodesList.get(j).getPosition().x
								&& nodesList.get(i).getPosition().y + 10 == nodesList.get(j).getPosition().y)
						|| (nodesList.get(i).getPosition().x - 10 == nodesList.get(j).getPosition().x
								&& nodesList.get(i).getPosition().y == nodesList.get(j).getPosition().y)
						|| (nodesList.get(i).getPosition().x == nodesList.get(j).getPosition().x
								&& nodesList.get(i).getPosition().y - 10 == nodesList.get(j).getPosition().y))
					nodesList.get(i).getNachbarn().add(nodesList.get(j));
			}
			
		}

		// Write from the target the distance into every node

		// normalize end?
		float zielX = 1, zielY = 1;
		if (vector.x % 10 < 5)
			zielX = vector.x - vector.x % 10;
		if (vector.x % 10 >= 5)
			zielX = vector.x + (10 - vector.x % 10);
		if (vector.y % 10 < 5)
			zielY = vector.y - vector.y % 10;
		if (vector.y % 10 >= 5)
			zielY = vector.y + (10 - vector.y % 10);

		zielpos = new Vector2(zielX, zielY);

		for (final Node node : nodesList) {
			if (node.getPosition().x == zielX * PlayState.METER_TO_PIXEL
					&& node.getPosition().y == zielY * PlayState.METER_TO_PIXEL) {
				node.setH(1);
				// set "target" node
				werteSetzen(node);
				break;
			}
		}

		boolean isFound = false;

		this.nodes2DList = new Node[(int) PlayState.RESOLUTION_WIDTH][(int) PlayState.RESOLUTION_HEIGHT];
		// Write to 2D array
		for (int i = 0; i < nodes2DList.length; i = i + 10) {
			for (int j = 0; j < nodes2DList[i].length; j = j + 10) {
				isFound = false;
				for (final Node node : nodesList) {
					if (node.getPosition().x == i && node.getPosition().y == j) {
						isFound = true;
						nodes2DList[i][j] = node;
						break;
					}
				}
				if (!isFound)
					nodes2DList[i][j] = new Node(true);
			}
		}

	}

	private void werteSetzen(final Node meinNode) {
		if (meinNode != null && meinNode.getNachbarn() != null)
			for (final Node node : meinNode.getNachbarn()) {
				if (node.getH() > meinNode.getH() + 1) {
					node.setH(meinNode.getH() + 1);
					werteSetzen(node);
				}
			}
	}

	public Vector2 getSpawn() {
		return spawn;
	}

	public void setSpawn(final Vector2 spawn) {
		this.spawn = spawn;
	}

	public void draw(SpriteBatch spriteBatch) {
		map.draw(spriteBatch);
	}

	public Vector2 getZielPos() {
		return zielpos;
	}
	
	private final Array<Node> openList = new Array<Node>();
	private final Array<Node> closedList = new Array<Node>();
	private final Array<Node> tempweg = new Array<Node>();
	private final Node[][] tempNodes2DList;

	private Node aktuellerNode;

	private Array<Node> getPath(final Vector2 startPosition, final Vector2 targetPosition) {
		openList.clear();
		closedList.clear();
		tempweg.clear();



		boolean found = false;

	
		// Welcher Nachbar ist der beste
		float lowCost = 9999999;

		if (tempNodes2DList[(int) startPosition.x][(int) startPosition.y].getNoUse()) 
			System.out.println("Halt, Start Node ist ungueltig");
			
		if (tempNodes2DList[(int) targetPosition.x][(int) targetPosition.y].getNoUse()) 
			System.out.println("Halt, End Node ist ungueltig");

		openList.add(tempNodes2DList[(int) startPosition.x][(int) startPosition.y]);
		aktuellerNode = tempNodes2DList[(int) startPosition.x][(int) startPosition.y];
		lowCost = aktuellerNode.getCost();

		while (!found) {

			// NEU *********************************************
			lowCost = 999999999;

			for (Node node : openList) {
				if (lowCost > node.getCost()) {
					aktuellerNode = node;
					lowCost = node.getCost();

				}
			}

			if (openList.indexOf(aktuellerNode, true) < 0) {
				System.out.println("aktuellerNode ist auf openList nicht zu finden (MainMap)");
				System.out.println("openList size: "+openList.size);
				System.out.println("aktuellerNode size: "+aktuellerNode.getPosition().x+"/"+aktuellerNode.getPosition().y);
				System.out.println("tempweg size: "+openList.size);
				return tempweg;
			}

			if (openList.indexOf(aktuellerNode, true) != -1)
				openList.removeValue(aktuellerNode, true);
			else
				System.out.println("What the hell just happened???");

			closedList.add(aktuellerNode);

			// Das geht an dieser Stelle irgendwie nicht?
			// tempweg.add(aktuellerNode);
			// aktuellerNode.setErschwernis(MathUtils.random(1f, 3f));

			for (int i = 0; i < aktuellerNode.getNachbarn().size; i++) {
				final Node node = aktuellerNode.getNachbarn().get(i);
				if (closedList.indexOf(node, true) == -1) {
					node.setG(aktuellerNode.getG() + 1);
					node.setParent(aktuellerNode);
					if (openList.indexOf(node, true) == -1) {
						node.setCost(node.getCost());
						openList.add(node);
					}
				}

			}

			if (aktuellerNode.getPosition().x == targetPosition.x
					&& aktuellerNode.getPosition().y == targetPosition.y) {
				found = true;
				break;
			}

			// NEU ENDE ***************************************

		}

		while (aktuellerNode != null) {

			// Fuer alle Wege die benutzt werden ein Erschwernis eintragen

			getNodesList()[(int) aktuellerNode.getPosition().x][(int) aktuellerNode.getPosition().y]
					.setAdditionalDifficulty(MathUtils.random(1f, 3f));

			// Hinzufuegen
			tempweg.add(aktuellerNode);
			aktuellerNode = aktuellerNode.getParent();
		}
	
		return tempweg;
	}

	public Array<Node> getRandomPath() {
		Array<Node> rdm=paths.random();
		Array<Node> cpy=new Array<Node>();
		cpy.addAll(rdm);
		
		return cpy;
	}
}
