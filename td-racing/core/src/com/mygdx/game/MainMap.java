package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import aurelienribon.bodyeditor.BodyEditorLoader;


public class MainMap {
	Sprite sMap;
	Texture tMap;
	
	
	public MainMap (String mapName, World world) {
		
		createSolidMap(mapName, world);
		
	}
	
	public void createSolidMap(String mapName, World world) {
		try {
            // The following line would throw ExceptionInInitializerError
			BodyEditorLoader solidMap = new BodyEditorLoader(Gdx.files.local("maps/test.json"));
        } catch (Throwable t) {
            System.out.println(t);
        }
		
		// 1. Create a BodyDef, as usual.
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DynamicBody;

		// 2. Create a FixtureDef, as usual.
		FixtureDef fd = new FixtureDef();
		fd.density = 1;
		fd.friction = 0.5f;
		fd.restitution = 0.3f;

		// 3. Create a Body, as usual.
//		bottleModel = world.createBody(bd);
//
//		// 4. Create the body fixture automatically by using the loader.
//		loader.attachFixture(bottleModel, "test01", fd, BOTTLE_WIDTH);
//		bottleModelOrigin = loader.getOrigin("test01", BOTTLE_WIDTH).cpy();
	}
	
}
