package com.mygdx.game.objects;

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
import com.mygdx.game.MainGame;
import com.mygdx.game.gamestate.states.PlayState;
import com.mygdx.game.level.Level;
import com.mygdx.game.unsorted.BodyEditorLoader;
import com.mygdx.game.unsorted.Node;

public class Map {

	private final Array<Node> nodesList;
	private final Vector2 healthBarPosition;

	private Body mapModel, mapZiel, finishLine, mapZombieWay;
	private Vector2 spawnPosition, targetPosition;
	private Node[][] nodes2DList;
	private Sprite map;
	private Array<Array<Node>> paths;
	private float spawnheighty;

	public Map(final Level currentLevel, final World world, final Body finishLine, final float sizePitstop) {
		nodesList = new Array<Node>();
		createSolidMap(currentLevel.getMapName(), world);
		this.finishLine = finishLine;
		spawnPosition = new Vector2();
		targetPosition = new Vector2();
		createAStarArray();
		paths = new Array<Array<Node>>();
		healthBarPosition = currentLevel.getHealthBarPosition();
		spawnheighty = currentLevel.getPitStopPosition().y * PlayState.PIXEL_TO_METER + sizePitstop;

		// Create x calculated ways
		final PolygonShape ps = (PolygonShape) mapZiel.getFixtureList().first().getShape();
		final Vector2 vector = new Vector2();
		ps.getVertex(0, vector);

		for (int i = 0; i < 200; i++) {
			paths.add(getPath(new Vector2(currentLevel.getSpawnPoint().x, currentLevel.getSpawnPoint().y),
					new Vector2(vector.x * PlayState.METER_TO_PIXEL, vector.y * PlayState.METER_TO_PIXEL)));
		}
	}

	public Node[][] getNodesList() {
		return nodes2DList;
	}

	public void createSolidMap(String mapName, final World world) {

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
		loader.attachFixture(mapModel, "Map", solid, MainGame.GAME_WIDTH * PlayState.PIXEL_TO_METER);
		loaderZiel.attachFixture(mapZiel, "Ziel", nonSolid, MainGame.GAME_WIDTH * PlayState.PIXEL_TO_METER);
		loaderZombieWay.attachFixture(mapZombieWay, "Zombieway", nonSolid,
				MainGame.GAME_WIDTH * PlayState.PIXEL_TO_METER);
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
		for (int i = 0; i <= MainGame.GAME_WIDTH; i += 10) {
			for (int j = 0; j <= MainGame.GAME_HEIGHT; j += 10) {
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
		final Vector2[] iterationHelper = new Vector2[] { new Vector2(10, 0), new Vector2(0, 10), new Vector2(-10, 0),
				new Vector2(0, -10) };
		for (int i = 0; i < nodesList.size; i++) {
			final Node nodeMain = nodesList.get(i);
			for (int j = 0; j < nodesList.size; j++) {
				final Node nodeNeighbor = nodesList.get(j);
				for (int k = 0; k < iterationHelper.length; k++) {
					if (((int) (nodeMain.getPosition().x + iterationHelper[k].x) == (int) (nodeNeighbor.getPosition().x)
							&& (int) (nodeMain.getPosition().y
									+ iterationHelper[k].y) == (int) (nodeNeighbor.getPosition().y))) {
						nodeMain.getNachbarn().add(nodeNeighbor);
						break;
					}
				}
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

		targetPosition.set(zielX, zielY);

		for (int i = 0; i < nodesList.size; i++) {
			final Node node = nodesList.get(i);
			if ((int) node.getPosition().x == (int) (zielX * PlayState.METER_TO_PIXEL)
					&& (int) node.getPosition().y == (int) (zielY * PlayState.METER_TO_PIXEL)) {
				node.setH(1);
				// set "target" node
				werteSetzen(node);
				break;
			}
		}

		boolean isFound = false;
		nodes2DList = new Node[MainGame.GAME_WIDTH][MainGame.GAME_HEIGHT];
		// Write to 2D array
		for (int i = 0; i < MainGame.GAME_WIDTH; i = i + 10) {
			for (int j = 0; j < MainGame.GAME_HEIGHT; j = j + 10) {
				isFound = false;
				for (int k = 0; k < nodesList.size; k++) {
					final Node node = nodesList.get(k);
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

	public Vector2 getSpawnPosition() {
		return spawnPosition;
	}

	public void setSpawnPosition(final Vector2 spawn) {
		this.spawnPosition = spawn;
	}

	public void draw(SpriteBatch spriteBatch) {
		map.draw(spriteBatch);
	}

	public Vector2 getTargetPosition() {
		return targetPosition;
	}

	private Vector2 normalizeVectorForGrid(final Vector2 vector) {
		if (vector.x % 10 < 5)
			vector.x = vector.x - vector.x % 10;
		else
			vector.x = vector.x + (10 - vector.x % 10);
		if (vector.y % 10 < 5)
			vector.y = vector.y - vector.y % 10;
		else
			vector.y = vector.y + (10 - vector.y % 10);
		return vector;
	}

	private Array<Node> getPath(final Vector2 startPosition, final Vector2 targetPosition) {
		Array<Node> openList = new Array<Node>();
		Array<Node> closedList = new Array<Node>();
		Node[][] tempNodes2DList = getNodesList();
		Array<Node> tempweg = new Array<Node>();
		Node aktuellerNode;

		// was the way found
		boolean foundWay = false;

		// Which node is the next
		startPosition.set(normalizeVectorForGrid(startPosition));
		targetPosition.set(normalizeVectorForGrid(targetPosition));

		// Which neighbor is the best
		if (tempNodes2DList[(int) startPosition.x][(int) startPosition.y].getNoUse())
			System.out.println("Halt, Start Node ist ungueltig");

		if (tempNodes2DList[(int) targetPosition.x][(int) targetPosition.y].getNoUse())
			System.out.println("Halt, End Node ist ungueltig");

		openList.add(tempNodes2DList[(int) startPosition.x][(int) startPosition.y]);
		aktuellerNode = tempNodes2DList[(int) startPosition.x][(int) startPosition.y];

		float lowCost = aktuellerNode.getCost();

		while (!foundWay) {
			lowCost = 999999999;

			for (int i = 0; i < openList.size; i++) {
				final Node node = openList.get(i);
				if (lowCost > node.getCost()) {
					aktuellerNode = node;
					lowCost = node.getCost();
				}
			}

			if (openList.indexOf(aktuellerNode, true) < 0) {
				System.out.println("aktuellerNode ist auf openList nicht zu finden (MainMap)\n" + "openList size: "
						+ openList.size + "\n" + "aktuellerNode size: " + aktuellerNode.getPosition().toString() + "\n"
						+ "tempweg size: " + openList.size);
				return tempweg;
			}

			if (openList.indexOf(aktuellerNode, true) != -1)
				openList.removeIndex(openList.indexOf(aktuellerNode, true));

			closedList.add(aktuellerNode);

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

			if (aktuellerNode.getPosition().equals(targetPosition)) {
				foundWay = true;
				break;
			}
		}

		while (aktuellerNode != null) {
			// Add for every way that is used an additional difficulty
			getNodesList()[(int) aktuellerNode.getPosition().x][(int) aktuellerNode.getPosition().y]
					.setAdditionalDifficulty(MathUtils.random(1f, 3f));
			// Add node to way
			tempweg.add(aktuellerNode);
			// do it until there is no "parent" any more
			aktuellerNode = aktuellerNode.getParent();
		}

		return tempweg;
	}

	public Array<Node> getRandomPath() {
		return new Array<Node>(paths.random());
	}

	public Vector2 getHealthBarPos() {
		return healthBarPosition;
	}

	public float getSpawnheighty() {
		return spawnheighty;
	}

}
