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
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.gamestate.state.PlayState;

public class MainMap {

	Body mapModel;
	public Body mapZiel;
	Body finishLine;
	Body mapZombieWay;
	Sprite debug;
	ArrayList<Node> nodesList;
	public Node[][] nodes2DList;
	Array<EnemyWaveEntry> enemyWave;
	Vector2 spawn;
	private Sprite map;
	public Vector2 zielpos;

	public MainMap(String mapName, World world, Body finishLine) {

		nodesList = new ArrayList<Node>();
		createSolidMap(mapName, world);
		this.finishLine = finishLine;
		createAStarArray();

		this.enemyWave = setEnemyWave();
	}

	public Array<EnemyWaveEntry> setEnemyWave() {
		final Array<EnemyWaveEntry> enemyWavesToSet = new Array<EnemyWaveEntry>();
		// enemyWavesToSet.add(new EnemyWaveEntry(10, new Vector2(220, 20),
		// EnemyWaveEntry.ENEMY_BYCICLE));
		// enemyWavesToSet.addAll(EnemyWaveEntry.createEnemyEntries(new Vector2[] {new
		// Vector2(220, 20), new Vector2(220, 20)}, 15, null, 0, null, 0));
		// enemyWavesToSet.addAll(EnemyWaveEntry.createEnemyEntries(new Vector2(220,20),
		// 2, 10,0.1f, 20,1f, 10,0.01f));
		// enemyWavesToSet.addAll(EnemyWaveEntry.createEnemyEntries(new Vector2(220,20),
		// 2, 0,0.1f, 10,1f, 0,0.01f));
		// enemyWavesToSet.addAll(EnemyWaveEntry.createEnemyEntries(new Vector2(220,20),
		// 2, 0, 0, 10));
		return enemyWavesToSet;
	}

	public Array<EnemyWaveEntry> getEnemyWaves() {
		return this.enemyWave;
	}

	public Node[][] getNodesList() {
		return nodes2DList;
	}

	public void createSolidMap(String mapName, World world) {
		// The following line would throw ExceptionInInitializerError

		this.map = new Sprite(new Texture(Gdx.files.internal("maps/" + mapName + ".png")));
		this.map.setSize(this.map.getWidth() * PlayState.PIXEL_TO_METER,
				this.map.getHeight() * PlayState.PIXEL_TO_METER);

		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("maps/" + mapName + "solid.json"));
		BodyEditorLoader loaderZiel = new BodyEditorLoader(Gdx.files.internal("maps/" + mapName + "ziel.json"));
		BodyEditorLoader loaderZombieWay = new BodyEditorLoader(
				Gdx.files.internal("maps/" + mapName + "zombieway.json"));

		// 1. Create a BodyDef, as usual.
		BodyDef bd = new BodyDef();
		bd.type = BodyType.StaticBody;

		BodyDef ziel = new BodyDef();
		ziel.type = BodyType.StaticBody;

		BodyDef zombieway = new BodyDef();
		zombieway.type = BodyType.StaticBody;

		// 2. Create a FixtureDef, as usual.
		FixtureDef solid = new FixtureDef();
		solid.density = 1;
		solid.friction = 0.5f;
		solid.restitution = 0.3f;

		FixtureDef nonSolid = new FixtureDef();
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

	public void createAStarArray() {
		boolean befahrbar = true;
		final PolygonShape ps = (PolygonShape) mapZiel.getFixtureList().first().getShape();
		final Vector2 vector = new Vector2();
		ps.getVertex(0, vector);

		// Nodes erstellen
		for (int i = 0; i <= PlayState.RESOLUTION_WIDTH; i += 10) {
			for (int j = 0; j <= PlayState.RESOLUTION_HEIGHT; j += 10) {
				// Im befahrbaren Bereich?
				befahrbar = true;
				for (final Fixture f : mapZombieWay.getFixtureList()) {
					if (f.testPoint(i * PlayState.PIXEL_TO_METER, j * PlayState.PIXEL_TO_METER))
						befahrbar = false;
				}
				for (final Fixture f : finishLine.getFixtureList()) {
					if (f.testPoint(i * PlayState.PIXEL_TO_METER, j * PlayState.PIXEL_TO_METER))
						befahrbar = false;
				}
				if (befahrbar)
					nodesList.add(new Node((float) i, (float) j));
			}
		}
		// Alle Nachbarn in die Nodes eintragen
		for (final Node nodeMain : nodesList) {
			// Nachbar finde
			for (final Node nodeNachbar : nodesList) {
				if ((nodeMain.getX() + 10 == nodeNachbar.getX() && nodeMain.getY() == nodeNachbar.getY())
						|| (nodeMain.getX() == nodeNachbar.getX() && nodeMain.getY() + 10 == nodeNachbar.getY())
						|| (nodeMain.getX() - 10 == nodeNachbar.getX() && nodeMain.getY() == nodeNachbar.getY())
						|| (nodeMain.getX() == nodeNachbar.getX() && nodeMain.getY() - 10 == nodeNachbar.getY()))
					nodeMain.getNachbarn().add(nodeNachbar);
			}
		}

		// Vom Ziel ausgehend noch die Entfernung in jeden Node schreiben

		// Ende normalisiesrn?
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
				// set "Ziel" node
				werteSetzen(node);
				break;
			}
		}

		boolean isFound = false;

		this.nodes2DList = new Node[(int) PlayState.RESOLUTION_WIDTH][(int) PlayState.RESOLUTION_HEIGHT];
		// In 2d Array schreiben
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

	public void werteSetzen(final Node meinNode) {
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

}
