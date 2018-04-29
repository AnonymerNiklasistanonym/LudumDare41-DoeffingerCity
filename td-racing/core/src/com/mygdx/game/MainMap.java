package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.gamestate.state.PlayState;

public class MainMap {
	
	private final ArrayList<Node> nodesList;

	private Body mapModel;
	private Body mapZiel;
	private Body finishLine;
	private Body mapZombieWay;
	private Node[][] nodes2DList;
	private Vector2 spawn;
	private Sprite map;
	private Vector2 zielpos;

	public MainMap(final String mapName, final World world, final Body finishLine) {
		nodesList = new ArrayList<Node>();
		createSolidMap(mapName, world);
		this.finishLine = finishLine;
		createAStarArray();
	}
	
	public Body getMapZielBody() {
		return mapZiel;
	}

	public Node[][] getNodesList() {
		return nodes2DList;
	}

	public void createSolidMap(final String mapName, final World world) {
		// The following line would throw ExceptionInInitializerError

		this.map = new Sprite(new Texture(Gdx.files.internal("maps/" + mapName + ".png")));
		this.map.setSize(this.map.getWidth() * PlayState.PIXEL_TO_METER,
				this.map.getHeight() * PlayState.PIXEL_TO_METER);

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
		for (final Node nodeMain : nodesList) {
			// Find neighbor
			for (final Node nodeNachbar : nodesList) {
				if ((nodeMain.getX() + 10 == nodeNachbar.getX() && nodeMain.getY() == nodeNachbar.getY())
						|| (nodeMain.getX() == nodeNachbar.getX() && nodeMain.getY() + 10 == nodeNachbar.getY())
						|| (nodeMain.getX() - 10 == nodeNachbar.getX() && nodeMain.getY() == nodeNachbar.getY())
						|| (nodeMain.getX() == nodeNachbar.getX() && nodeMain.getY() - 10 == nodeNachbar.getY()))
					nodeMain.getNachbarn().add(nodeNachbar);
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
			if (node.getX() == zielX * PlayState.METER_TO_PIXEL && node.getY() == zielY * PlayState.METER_TO_PIXEL) {
				node.setH(1);
				// set "target" node
				werteSetzen(node);
				break;
			}
		}

		boolean isFound = false;

		this.nodes2DList = new Node[(int) PlayState.RESOLUTION_WIDTH][(int) PlayState.RESOLUTION_HEIGHT];
		// Write to 2D array
		for (int i = 0; i < (int) PlayState.RESOLUTION_WIDTH; i = i + 10) {
			for (int j = 0; j < (int) PlayState.RESOLUTION_HEIGHT; j = j + 10) {
				isFound = false;
				for (final Node node : nodesList) {
					if (node.getX() == i && node.getY() == j) {
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
		this.map.draw(spriteBatch);
	}

	public Vector2 getZielPos() {
		return zielpos;
	}

}
