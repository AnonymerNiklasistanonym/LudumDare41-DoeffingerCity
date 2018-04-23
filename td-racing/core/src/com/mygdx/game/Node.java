package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.math.MathUtils;

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
		erschwernis = MathUtils.random(1f,3f);
		h = 99999;
		nachbarn = new ArrayList();
	}
	
	public Node(boolean noUse) {
		this.noUse = noUse;
	}
	
	public float getKosten() {
		return g + h*7 * erschwernis;
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
