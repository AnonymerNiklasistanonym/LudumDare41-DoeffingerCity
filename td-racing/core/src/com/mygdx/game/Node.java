package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.gamestate.state.PlayState;

public class Node {
	float x, y;
	// Kosten von Start hierher
	float g = 1;
	// Kosten bis zum Ziel
	float h;
	// Erschwernis
	float erschwernis;
	// Kosten
	float kosten;
	
	boolean noUse = false;
	
	Node parent;
	
	ArrayList<Node> nachbarn;

	public Node(float x, float y, float zielX, float zielY) {
		this.x = x;
		this.y = y;
		erschwernis = MathUtils.random(3f);
		h = 99999;
		nachbarn = new ArrayList();
	}
	
	public Node(boolean noUse) {
		this.noUse = noUse;
	}
	
	public float getKosten() {
		return 4*g + h * erschwernis;
	}
	
	public boolean getNoUse() {
		return noUse;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getH() {
		return h;
	}
}
