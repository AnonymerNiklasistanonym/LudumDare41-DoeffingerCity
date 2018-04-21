package com.mygdx.game.objects.checkpoints;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.objects.Checkpoint;

public class NormalCheckpoint extends Checkpoint {

	private static Texture normalCheckPointActivated = new Texture("checkpoints/checkpoint_normal_activated.png");
	private static Texture normalCheckPointDisabled = new Texture("checkpoints/checkpoint_normal_disabled.png");

	public NormalCheckpoint(World world, float xPosition, float yPosition) {
		super(world, xPosition, yPosition, normalCheckPointDisabled, normalCheckPointActivated);
	}

}
