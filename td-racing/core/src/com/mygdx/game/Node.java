package com.mygdx.game;

import java.util.ArrayList;

public class Node {
	float x, y;
	// Kosten von Start hierher
	float g;
	// Kosten bis zum Ziel
	float h;
	// Erschwernis
	float erschwernis;
	
	boolean noUse = false;
	
	Node parent;
	
	ArrayList<Node> nachbarn;

	public Node(float x, float y, float zielX, float zielY) {
		this.x = x;
		this.y = y;
		erschwernis = 1;
		h = (float) Math.sqrt((((x - zielX) * (x - zielX)) + ((y - zielY) * (y - zielY))));
		nachbarn = new ArrayList();
	}
	
	public Node(boolean noUse) {
		this.noUse = noUse;
	}
	
	public float getKosten() {
		return g * h * erschwernis;
	}
	
	
}
