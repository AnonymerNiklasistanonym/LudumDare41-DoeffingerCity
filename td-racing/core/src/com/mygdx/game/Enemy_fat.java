package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;

	public class Enemy_fat extends Enemy {
		
		public static Texture normalTexture;
		public static Texture deadTexture;
		public static Texture damageTexture;

		public Enemy_fat(float x, float y,World w, MainMap map) {
			super(x,y,w, normalTexture, deadTexture,damageTexture,map);
			this.speed = 0.5f;
			this.health = 100;
		}
}
