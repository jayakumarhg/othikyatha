package com.beatabout.othikyatha;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.location.Location;

public class Profile {
	/**
	 * I have to duplicate the string declarations as the STUPID ANDROID do not
	 * provide any API to get the string resources without passing around the
	 * Context.
	 */
	private static final String NAME_PREF = "namePreference";
	private static final String RING_VOLUME_PREF = "ringVolumePreference";
	private static final String VOICE_VOLUME_PREF = "voiceVolumePreference";
	private static final String MEDIA_VOLUME_PREF = "mediaVolumePreference";
	private static final String SILENT_PREF = "silentPreference";
	private static final String VIBRATE_PREF = "vibratePreference";
	private static final String RINGTONE_PREF = "ringtonePreference";
	private static final String LOCATIONS_PREF = "locsPref";
	private static final String NUM_LOCATIONS_PREF = ".num";
	private static final String LATITUDE_PREF = ".latitude@";
	private static final String LONGITUDE_PREF = ".longitude@";

	private int profileId;
	private SharedPreferences sharedPreferences;

	Profile(int profileId, SharedPreferences sharedPreferences) {
		this.profileId = profileId;
		this.sharedPreferences = sharedPreferences;
	}

	int getProfileId() {
		return profileId;
	}

	private void setVolume(String key, int volume) {
		sharedPreferences.edit().putInt(key, volume).commit();
	}

	int getRingVolume() {
		return sharedPreferences.getInt(RING_VOLUME_PREF, 50);
	}

	void setRingVolume(int volume) {
		setVolume(RING_VOLUME_PREF, volume);
	}

	int getVoiceVolume() {
		return sharedPreferences.getInt(VOICE_VOLUME_PREF, 50);
	}

	void setVoiceVolume(int volume) {
		setVolume(VOICE_VOLUME_PREF, volume);
	}

	int getMediaVolume() {
		return sharedPreferences.getInt(MEDIA_VOLUME_PREF, 50);
	}

	void setMediaVolume(int volume) {
		setVolume(MEDIA_VOLUME_PREF, volume);
	}

	String getName() {
		return sharedPreferences.getString(NAME_PREF, "New Profile");
	}

	void setName(String name) {
		sharedPreferences.edit().putString(NAME_PREF, name).commit();
	}

	boolean getSilent() {
		return sharedPreferences.getBoolean(SILENT_PREF, false);
	}

	void setSilent(boolean bool) {
		sharedPreferences.edit().putBoolean(SILENT_PREF, bool).commit();
	}

	boolean getVibrate() {
		return sharedPreferences.getBoolean(VIBRATE_PREF, false);
	}

	void setVibrate(boolean bool) {
		sharedPreferences.edit().putBoolean(VIBRATE_PREF, bool).commit();
	}

	String getRingTone() {
		return sharedPreferences.getString(RINGTONE_PREF, "DEFAULT_RINGTONE_URI");
	}

	void setRingTone(String uri) {
		sharedPreferences.edit().putString(RINGTONE_PREF, uri).commit();
	}

	public String toString() {
		return getName();
	}

	List<Location> getLocations() {
		ArrayList<Location> locations = new ArrayList<Location>();
		int size = sharedPreferences.getInt(LOCATIONS_PREF + NUM_LOCATIONS_PREF, 0);
		for (int i = 0; i < size; ++i) {
			Location location = new Location("");
			float latitude = sharedPreferences.getFloat(LOCATIONS_PREF
					+ LATITUDE_PREF + i, 0.0f);
			float longitude = sharedPreferences.getFloat(LOCATIONS_PREF
					+ LONGITUDE_PREF + i, 0.0f);
			location.setLatitude(latitude);
			location.setLongitude(longitude);

			locations.add(location);
		}
		return locations;
	}

	void setLocations(List<Location> locations) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(LOCATIONS_PREF + NUM_LOCATIONS_PREF, locations.size());

		int i = 0;
		for (Location location : locations) {
			editor.putFloat(LOCATIONS_PREF + LATITUDE_PREF + i,
					(float) location.getLatitude());
			editor.putFloat(LOCATIONS_PREF + LONGITUDE_PREF + i,
					(float) location.getLongitude());
			i++;
		}
		editor.commit();
	}
}
