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
	private static final String LOCATIONS_PREF = "locationsPreference";
	private static final String NUM_LOCATIONS_PREF = ".num";
	private static final String LATITUDE_PREF = ".latitude@";
	private static final String LONGITUDE_PREF = ".longitude@";
	private static final String ADDRESS_PREF = ".address@";

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

	List<GeoAddress> getLocations() {
		ArrayList<GeoAddress> geoAddresses = new ArrayList<GeoAddress>();
		int size = sharedPreferences.getInt(LOCATIONS_PREF + NUM_LOCATIONS_PREF, 0);
		for (int i = 0; i < size; ++i) {
			GeoAddress geoAddress = new GeoAddress();
			float latitude = sharedPreferences.getFloat(LOCATIONS_PREF
					+ LATITUDE_PREF + i, 0.0f);
			float longitude = sharedPreferences.getFloat(LOCATIONS_PREF
					+ LONGITUDE_PREF + i, 0.0f);
			String address = sharedPreferences.getString(LOCATIONS_PREF
					+ ADDRESS_PREF + i, "");
			geoAddress.setLatitude(latitude);
			geoAddress.setLongitude(longitude);
			geoAddress.setAddress(address);

			geoAddresses.add(geoAddress);
		}
		return geoAddresses;
	}

	void setLocations(List<GeoAddress> locations) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(LOCATIONS_PREF + NUM_LOCATIONS_PREF, locations.size());

		int i = 0;
		for (GeoAddress geoAddress : locations) {
			editor.putFloat(LOCATIONS_PREF + LATITUDE_PREF + i,
					(float) geoAddress.getLatitude());
			editor.putFloat(LOCATIONS_PREF + LONGITUDE_PREF + i,
					(float) geoAddress.getLongitude());
			editor.putString(LOCATIONS_PREF + ADDRESS_PREF + i, geoAddress.getAddress());
			i++;
		}
		editor.commit();
	}
}
