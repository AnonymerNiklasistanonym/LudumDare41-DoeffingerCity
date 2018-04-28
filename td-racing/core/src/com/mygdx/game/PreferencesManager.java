package com.mygdx.game;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class PreferencesManager {

	public class HighscoreEntry {
		public final int score;

		public int getScore() {
			return score;
		}

		public String getName() {
			return name;
		}

		public final String name;

		public String toString() {
			return score + " by " + name;
		}

		HighscoreEntry(final int score, final String name) {
			this.score = score;
			this.name = name;
		}
	}

	private static final String SOUND_EFFECTS = "SOUND_EFFECTS";
	private static final String MUSIC = "MUSIC";
	private static final String HIGHSCORE_NAME = "HIGHSCORE_NAME";
	private static final String HIGHSCORE_SCORE = "HIGHSCORE_SCORE";
	private static final String PREFERENCES_NAME = "My Preferences";
	
	private static final String LAST_NAME = "LAST_NAME";


	private final Preferences prefs;

	public PreferencesManager() {
		if (Gdx.app.getType() != ApplicationType.WebGL) {
			this.prefs = Gdx.app.getPreferences(PREFERENCES_NAME);
		} else {
			this.prefs = null;
		}
	}
	
	public void saveName(final String name) {
		prefs.putString(LAST_NAME, name);
		prefs.flush();
	}
	
	public char[] getName() {
		final String lastName = prefs.getString(LAST_NAME);
		return lastName.toCharArray();
	}

	public void checkHighscore() {
		if (Gdx.app.getType() != ApplicationType.WebGL) {
			final HighscoreEntry[] entries = retrieveHighscore();
			for (int i = 0; i < entries.length; i++) {
				if (entries[i].getName() == null || entries[i].getName().equals(""))
					prefs.putString(HIGHSCORE_NAME + i, "NOBODY");
				if (entries[i].getScore() < 0)
					prefs.putInteger(HIGHSCORE_SCORE + i, 0);
			}
			prefs.flush();
		}
	}

	public void clearHighscore() {
		if (Gdx.app.getType() != ApplicationType.WebGL) {
			for (int i = 0; i < 10; i++) {
				prefs.putString(HIGHSCORE_NAME + i, "NOBODY");
				prefs.putInteger(HIGHSCORE_SCORE + i, 0);
			}
			prefs.flush();
		}
	}

	public void saveHighscore(String[] names, int[] scores) {
		if (Gdx.app.getType() != ApplicationType.WebGL) {
			for (int i = 0; 0 < 10; i++) {
				prefs.putString(HIGHSCORE_NAME + i, names[i]);
				prefs.putInteger(HIGHSCORE_SCORE + i, scores[i]);
				prefs.flush();
			}
		}
	}

	public HighscoreEntry[] retrieveHighscore() {
		if (Gdx.app.getType() != ApplicationType.WebGL) {
			final HighscoreEntry[] entries = new HighscoreEntry[10];
			for (int i = 0; i < entries.length; i++) {
				entries[i] = new HighscoreEntry(prefs.getInteger(HIGHSCORE_SCORE + i),
						prefs.getString(HIGHSCORE_NAME + i));
				System.out.println(entries[i].toString());
			}
			return entries;
		} else {
			return null;
		}
	}

	public void saveSoundEffects(final boolean soundEffectsOn) {
		if (Gdx.app.getType() != ApplicationType.WebGL) {
			prefs.putBoolean(SOUND_EFFECTS, soundEffectsOn).flush();
		}
	}

	public void saveMusic(final boolean musicOn) {
		if (Gdx.app.getType() != ApplicationType.WebGL) {
			prefs.putBoolean(MUSIC, musicOn).flush();
		}
	}

	public boolean retrieveSoundEffects() {
		if (Gdx.app.getType() != ApplicationType.WebGL) {
			return prefs.getBoolean(SOUND_EFFECTS);
		} else {
			return false;
		}
	}

	public boolean retrieveMusic() {
		if (Gdx.app.getType() != ApplicationType.WebGL) {
			return prefs.getBoolean(MUSIC);
		} else {
			return false;
		}
	}

	public void saveHighscore(String name, int score) {
		saveName(name);
		if (Gdx.app.getType() != ApplicationType.WebGL) {
			final HighscoreEntry[] entries = retrieveHighscore();
			for (int i = 0; i < entries.length; i++) {
				if (entries[i].getScore() < score) {
					prefs.putString(HIGHSCORE_NAME + i, name);
					prefs.putInteger(HIGHSCORE_SCORE + i, score);
					for (int j = i + 1; j < entries.length; j++) {
						prefs.putString(HIGHSCORE_NAME + j, entries[j - 1].getName());
						prefs.putInteger(HIGHSCORE_SCORE + j, entries[j - 1].getScore());
					}
					prefs.flush();
					return;
				}
			}
		}
	}

	public boolean scoreIsInTop10(final int score) {
		for (final HighscoreEntry entry : retrieveHighscore()) {
			if (entry.getScore() < score)
				return true;
		}
		return false;
	}

}
