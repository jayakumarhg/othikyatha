package com.beatabout.othikyatha;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

public class DataManager {
	// The global preferences file stores following information:
	// 1.next available id
	// 2.active profile id
	// 3.default profile id
	// 4.manual mode settings.
	public static final String GLOBAL_PREFERENCES_FILE = "globalPrefsFile";
	// Enabled profiles preference file stores all ids of profiles that were
	// created
	// and not deleted. It stores them as profilePrefName -> profileId map.
	public static final String ENABLED_PROFILES_FILE = "enabledProfilesFile";

	public static final String NEXT_ID_KEY = "nextId";
	public static final String MANUAL_MODE_KEY = "manualMode";
	public static final String ACTIVE_PROFILE_KEY = "activeProfileId";
	public static final String DEFAULT_PROFILE_KEY = "defaultProfileId";

	private ContextWrapper contextWrapper;
	private SharedPreferences globalPreferences;
	private SharedPreferences enabledProfilesPreferences;

	public DataManager(ContextWrapper contextWrapper) {
		this.contextWrapper = contextWrapper;
		globalPreferences = contextWrapper.getSharedPreferences(
				GLOBAL_PREFERENCES_FILE, Context.MODE_WORLD_READABLE);
		enabledProfilesPreferences = contextWrapper.getSharedPreferences(
				ENABLED_PROFILES_FILE, Context.MODE_WORLD_READABLE);
	}

	public synchronized int getNextId() {
		int next = globalPreferences.getInt(NEXT_ID_KEY, 0);
		globalPreferences.edit().putInt(NEXT_ID_KEY, next + 1).commit();
		return next;
	}

	public synchronized boolean getManualMode() {
		return globalPreferences.getBoolean(MANUAL_MODE_KEY, false);
	}

	public synchronized void setManualMode(boolean mode) {
		globalPreferences.edit().putBoolean(MANUAL_MODE_KEY, mode).commit();
	}

	public synchronized void toggleManualMode() {
		setManualMode(!getManualMode());
	}

	/**
	 * Preference file name for a given profileId where that profiles data is
	 * stored.
	 */
	public static String getPreferenceNameForProfile(int profileId) {
		return "profile:id-" + Integer.toString(profileId);
	}

	public void removeProfileEntry(int id) {
		// Clears the profile data in preference file
		// manager.setSharedPreferencesName(getPreferenceName(id));
		// manager.getSharedPreferences().edit().clear();
		// manager.getSharedPreferences().edit().commit();

		// Removes the profile id in the enabled preferences file
		enabledProfilesPreferences.edit().remove(getPreferenceNameForProfile(id))
				.commit();
	}

	public void addProfileEntry(int id) {
		enabledProfilesPreferences.edit()
				.putInt(getPreferenceNameForProfile(id), id).commit();
	}

	public int addProfileEntry() {
		int id = getNextId();
		addProfileEntry(id);
		return id;
	}

	public Profile getProfile(int profileId) {
		SharedPreferences sharedPref = contextWrapper.getSharedPreferences(
				getPreferenceNameForProfile(profileId), Context.MODE_WORLD_READABLE);
		return new Profile(profileId, sharedPref);
	}

	public List<Profile> getAllProfiles() {
		List<Profile> list = new ArrayList<Profile>();
		for (Object value : enabledProfilesPreferences.getAll().values()) {
			int profileId = (Integer) value;
			list.add(getProfile(profileId));
		}
		Collections.sort(list, new Comparator<Profile>() {
			public int compare(Profile object1, Profile object2) {
				if (object1.getProfileId() == getDefaultProfileId()) {
					return -1;
				} else if (object2.getProfileId() == getDefaultProfileId()) {
					return 1;
				} else {
					return object1.getName().compareToIgnoreCase(object2.getName()); 
				}
			}
		});
		return list;
	}

	public boolean hasNoProfiles() {
		return enabledProfilesPreferences.getAll().isEmpty();
	}

	public void setActiveProfile(Profile profile) {
		setActiveProfileId(profile.getProfileId());
	}

	public void setActiveProfileId(int profileId) {
		globalPreferences.edit().putInt(ACTIVE_PROFILE_KEY, profileId).commit();
	}

	public Profile getActiveProfile() {
		int profileId = globalPreferences.getInt(ACTIVE_PROFILE_KEY, -1);
		return getProfile(profileId);
	}

	public int getActiveProfileId() {
		int profileId = globalPreferences.getInt(ACTIVE_PROFILE_KEY, -1);
		return profileId;
	}

	public void setDefaultProfile(Profile profile) {
		setDefaultProfileId(profile.getProfileId());
	}

	public void setDefaultProfileId(int profileId) {
		globalPreferences.edit().putInt(DEFAULT_PROFILE_KEY, profileId).commit();
	}

	public Profile getDefaultProfile() {
		int profileId = globalPreferences.getInt(DEFAULT_PROFILE_KEY, -1);
		return getProfile(profileId);
	}

	public int getDefaultProfileId() {
		int profileId = globalPreferences.getInt(DEFAULT_PROFILE_KEY, -1);
		return profileId;
	}
}