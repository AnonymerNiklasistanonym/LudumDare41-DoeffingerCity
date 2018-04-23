package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class PreferencesManager {
	
	private static final String HIGHSCORE = "HIGHSCORE";
	private static final String SOUND_EFFECTS = "SOUND_EFFECTS";
	private static final String MUSIC = "MUSIC";
	private static final String HIGHSCORE_NAME = "HIGHSCORE_NAME";
	private static final String HIGHSCORE_SCORE = "HIGHSCORE_SCORE";


	private static final String PREFERENCES_NAME = "My Preferences";
	
	private final Preferences prefs;

		
	public PreferencesManager() {
		this.prefs = Gdx.app.getPreferences(PREFERENCES_NAME);
		// 		Gdx.input.getTextInput(null, "Dialog Title", "Initial Textfield Value", "Hint Value");
	}
	
	public void saveHighscore(String[] names, int[] scores) {
		for (int i = 0; 0 < 10; i++) {
			prefs.putString(HIGHSCORE_NAME + i, names[i]);
			prefs.putInteger(HIGHSCORE_SCORE + i, scores[i]);
			prefs.flush();
		}
		
	}
	
	public void retrieveHighscore() {
		String[] names = new String[10];
		int[] scores = new int[10];
		for (int i = 0; 0 < 10; i++) {
			names[i] = prefs.getString(HIGHSCORE_NAME + i);
			scores[i] = prefs.getInteger(HIGHSCORE_SCORE + i);
		}
		// return // TODO
	}
	
	public void saveSoundEffects(final boolean soundEffectsOn) {
		prefs.putBoolean(SOUND_EFFECTS, soundEffectsOn).flush();
	}
	
	public void saveMusic(final boolean musicOn) {
		prefs.putBoolean(MUSIC, musicOn).flush();
	}
	
	public boolean retrieveSoundEffects() {
		return prefs.getBoolean(SOUND_EFFECTS);
	}
	
	public boolean retrieveMusic() {
		return prefs.getBoolean(MUSIC);
	}

}
