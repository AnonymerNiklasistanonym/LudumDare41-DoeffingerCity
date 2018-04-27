package com.mygdx.game;

public class FPSCounter {

	float time = 0;
	int frames = 0;
	int lastframes = 0;
	int lastlastframes = 0;

	public FPSCounter() {

	}

	public void update(float delta) {
		time = time + delta;
		if (time > 1) {
			time = time - 1;
			lastframes = frames;
			if (lastlastframes == 0)
				lastlastframes = frames;
			else
				lastlastframes = lastframes;

			lastframes = frames;
			frames = 0;

		} else {
			frames++;
		}
	}

	public int getFrames() {
		return lastframes + 1;
	}
}
