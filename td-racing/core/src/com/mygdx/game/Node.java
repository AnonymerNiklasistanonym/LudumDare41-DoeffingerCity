package com.mygdx.game;

import java.util.ArrayList;

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
		erschwernis = 1;
		h = (float) Math.sqrt((((x - zielX*PlayState.METER_TO_PIXEL) * (x - zielX*PlayState.METER_TO_PIXEL)) + ((y - zielY*PlayState.METER_TO_PIXEL) * (y - zielY*PlayState.METER_TO_PIXEL))));
		nachbarn = new ArrayList();
	}
	
	public Node(boolean noUse) {
		this.noUse = noUse;
	}
	
	public float getKosten() {
		return g * h * erschwernis;
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
}
