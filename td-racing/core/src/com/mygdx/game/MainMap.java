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
	Sprite debug;
	ArrayList<Node> nodesList;

	public MainMap(String mapName, World world, float resolution, float pixel_to_meter) {

		nodesList = new ArrayList<Node>();
		createSolidMap(mapName, world, resolution, pixel_to_meter);
		createAStarArray();

	}

	public void createSolidMap(String mapName, World world, float resolution, float pixel_to_meter) {
		// The following line would throw ExceptionInInitializerError
		tMap = new Texture("maps/test.png");
		sMap = new Sprite(tMap);
		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.local("maps/test.json"));
		BodyEditorLoader loaderZiel = new BodyEditorLoader(Gdx.files.local("maps/ziel.json"));

		debug = new Sprite(new Texture("maps/test.png"));

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
		loader.attachFixture(mapModel, "Name", solid, resolution * pixel_to_meter);
		loaderZiel.attachFixture(mapZiel, "Ziel", solid, resolution * pixel_to_meter);
		System.out.println();
	}

	public void createAStarArray() {
		boolean befahrbar = true;
		PolygonShape ps=(PolygonShape)mapZiel.getFixtureList().first().getShape();
		Vector2 vector = new Vector2();
		ps.getVertex(0, vector);

		// Nodes erstellen
		for (int i = 1; i <= PlayState.RESOLUTION_WIDTH; i += 10) {
			for (int j = 1; j <= PlayState.RESOLUTION_HEIGHT; j += 10) {
				// Im befahrbaren Bereich?
				befahrbar = true;
				for (Fixture f : mapModel.getFixtureList()) {
					if(f.testPoint(i, j)) {
						befahrbar = false;
					}				
				}
				if(befahrbar) nodesList.add(new Node((float) i, (float) j,vector.x, vector.y));
			}
		}		
	}

}
