package com.mygdx.game.unsorted;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Node {

	private final Vector2 position;
	private final boolean noUse;

	/**
	 * Cost from start to this node
	 */
	private float g;
	/**
	 * Cost from this node to the target
	 */
	private float h;
	private float additionalDifficulty;
	private Node parentNode;
	private Array<Node> nachbarn;

	public Node(final float x, final float y) {
		g = 1;
		noUse = false;
		position = new Vector2(x, y);
		additionalDifficulty = 0;
		h = 99999;
		nachbarn = new Array<Node>();
	}

	public Node(final boolean noUse) {
		this.noUse = noUse;
		position = null;
	}

	public float getCost() {
		return g + h * 7 * additionalDifficulty;
	}

	public boolean getNoUse() {
		return noUse;
	}

	public Vector2 getPosition() {
		return position;
	}

	public float getH() {
		return h;
	}

	public Array<Node> getNachbarn() {
		return nachbarn;
	}

	public float getG() {
		return g;
	}

	public void setG(final float g) {
		this.g = g;
	}

	public void setParent(final Node node) {
		parentNode = node;
	}

	public float getAdditionalDifficulty() {
		return additionalDifficulty;
	}

	public Node getParent() {
		return parentNode;
	}

	public void setAdditionalDifficulty(final float aD) {
		this.additionalDifficulty = this.additionalDifficulty+aD;
	}

	public void setH(final float h) {
		this.h = h;
	}

	public void setCost(final float cost) {
		// TODO What happened with this method
	}
}
