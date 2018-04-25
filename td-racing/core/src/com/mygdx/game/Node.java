package com.mygdx.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Node {
	private final float x;
	private final float y;
	// Kosten von Start hierher
	private float g = 1;
	// Kosten bis zum Ziel
	private float h;
	// Erschwernis
	private float erschwernis;
	private final boolean noUse;

	private Node parent;

	private Array<Node> nachbarn;

	public Node(final float x, final float y) {
		this.noUse = false;
		this.x = x;
		this.y = y;
		this.erschwernis = MathUtils.random(1f, 3f);
		this.h = 99999;
		this.nachbarn = new Array<Node>();
	}

	public Node(final boolean noUse) {
		this.noUse = noUse;
		this.x = -1;
		this.y = -1;
	}

	public float getKosten() {
		return g + h * 7 * erschwernis;
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

	public Array<Node> getNachbarn() {
		return this.nachbarn;
	}

	public float getG() {
		return this.g;
	}

	public void setG(final float g) {
		this.g = g;
	}

	public void setParent(final Node node) {
		this.parent = node;
	}

	public void setKosten(float kosten) {
	}

	public float getErschwernis() {
		return this.erschwernis;
	}

	public Node getParent() {
		return this.parent;
	}

	public void setErschwernis(final float erschwernis) {
		this.erschwernis = erschwernis;
	}

	public void setH(final float h) {
		this.h = h;
	}
}
