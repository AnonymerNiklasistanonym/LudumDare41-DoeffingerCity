package com.mygdx.game.objects.checkpoints;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.objects.Checkpoint;

public class NormalCheckpoint extends Checkpoint {

	public static Texture normalCheckPointActivated;
	public static Texture normalCheckPointDisabled;

	public NormalCheckpoint(final World world, final float xPosition, final float yPosition) {
		super(world, xPosition, yPosition, normalCheckPointDisabled, normalCheckPointActivated);
	}

}
