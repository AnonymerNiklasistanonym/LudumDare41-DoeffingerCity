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
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.gamestate.state.PlayState;



public class MainMap {
	Sprite sMap;
	Texture tMap;
	Body mapModel;
	Body mapZiel;
	Sprite debug;
	ArrayList<Node> nodesList;
	
	public MainMap (String mapName, World world, float resolution, float pixel_to_meter) {
		
		createSolidMap(mapName, world, resolution, pixel_to_meter);
		createAStarArray();
		
	}
	
	public void createSolidMap(String mapName, World world, float resolution, float pixel_to_meter) {
        // The following line would throw ExceptionInInitializerError
		tMap = new Texture("maps/Test.png");
		sMap = new Sprite(tMap);
		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.local("maps/test.json"));
		
		debug = new Sprite(new Texture("maps/Test.png"));
		
		// 1. Create a BodyDef, as usual.
		BodyDef bd = new BodyDef();
		bd.type = BodyType.StaticBody;
		
		BodyDef ziel = new BodyDef();
		ziel.type = BodyType.StaticBody;

		// 2. Create a FixtureDef, as usual.
		FixtureDef fd = new FixtureDef();
		fd.density = 1;
		fd.friction = 0.5f;
		fd.restitution = 0.3f;

		// 3. Create a Body, as usual.
		mapModel = world.createBody(bd);
		mapZiel = world.createBody(ziel);
//
//		// 4. Create the body fixture automatically by using the loader.
		loader.attachFixture(mapModel, "Name", fd, resolution*pixel_to_meter);
		loader.attachFixture(mapZiel, "Ziel", fd, resolution*pixel_to_meter);
	}
	
	public void createAStarArray() {
		// Nodes erstellen
		for( int i = 1; i <= PlayState.RESOLUTION_WIDTH ; i =+ 10) {
			for( int j = 1; j <= PlayState.RESOLUTION_HEIGHT ; j =+ 10) {
				nodesList.add(new Node((float)i,(float)j,mapZiel.getLocalCenter().x,mapZiel.getLocalCenter().y));
			}
		}		
	}
	
}
