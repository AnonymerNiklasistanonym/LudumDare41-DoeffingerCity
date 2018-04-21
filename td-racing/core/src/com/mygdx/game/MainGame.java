package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public class MainGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture teststrecke;
	Texture maincar;
	World world;
	Car car;
	Body carbody;

	@Override
	public void create() {
		batch = new SpriteBatch();
		teststrecke = new Texture("maps/test.png");
		maincar = new Texture("cars/car_standard.png");

		world = new World(new Vector2(0, 0), true);
		BodyDef bodydef = new BodyDef();
		bodydef.type = BodyDef.BodyType.DynamicBody;
		carbody = world.createBody(bodydef);

	}

	public void getInput() {

	}

	public void updateGame() {

	}

	@Override
	public void render() {
		getInput();
		updateGame();
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(teststrecke, 0, 0);
		batch.draw(maincar, 500, 100);
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		teststrecke.dispose();
	}
}
