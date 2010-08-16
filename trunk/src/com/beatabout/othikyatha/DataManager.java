package com.beatabout.othikyatha;

import java.util.Vector;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DataManager {
	public static final String globalPreferencesFile = "globalPrefsFile";
	public static final String nextId = "nextId";
	public static final String activeProfilesFile = "activeProfilesFile";
	public static final String manualMode = "manualMode";
	PreferenceManager manager;
	ContextWrapper contextWrapper;
	SharedPreferences profileIdPreferences;
	SharedPreferences activeProfilesPreferences;

	public DataManager(ContextWrapper contextWrapper) {
		this.contextWrapper = contextWrapper;
		profileIdPreferences = contextWrapper.getSharedPreferences(
				globalPreferencesFile, Context.MODE_WORLD_READABLE);
		activeProfilesPreferences = contextWrapper.getSharedPreferences(
				activeProfilesFile, Context.MODE_WORLD_READABLE);
	}

	public synchronized int getNextId() {
		int next = profileIdPreferences.getInt(nextId, 0);
		profileIdPreferences.edit().putInt(nextId, next + 1).commit();
		return next;
	}

	public synchronized boolean getManualMode() {
		return profileIdPreferences.getBoolean(manualMode, false);
	}

	public synchronized void setManualMode(boolean mode) {
		profileIdPreferences.edit().putBoolean(manualMode, mode).commit();
	}

	public synchronized void toggleManualMode() {
		setManualMode(!getManualMode());
	}

	public static String getPreferenceName(int id) {
		return "profile:id-" + Integer.toString(id);
	}

	public void removeProfileEntry(int id) {
		// Clears the profile data in preference file
		// manager.setSharedPreferencesName(getPreferenceName(id));
		// manager.getSharedPreferences().edit().clear();
		// manager.getSharedPreferences().edit().commit();

		// Removes the profile id in the active preferences file
		activeProfilesPreferences.edit().remove(getPreferenceName(id)).commit();
	}

	public void addProfileEntry(int id) {
		activeProfilesPreferences.edit().putInt(getPreferenceName(id), id)
				.commit();
	}

	public int addProfileEntry() {
		int id = getNextId();
		addProfileEntry(id);
		return id;
	}

	public Profile getProfile(int profileId) {
		SharedPreferences sharedPref = contextWrapper.getSharedPreferences(
				getPreferenceName(profileId), Context.MODE_WORLD_READABLE);
		return new Profile(profileId, sharedPref);
	}

	public Vector<Profile> getAllActiveProfiles() {
		Vector<Profile> list = new Vector<Profile>();
		for (Object value : activeProfilesPreferences.getAll().values()) {
			int profileId = (Integer) value;
			list.add(getProfile(profileId));
		}
		return list;
	}

	public boolean hasNoActiveProfiles() {
		return activeProfilesPreferences.getAll().isEmpty();
	}
}
