package com.beatabout.othikyatha;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.location.Location;

public class Profile {
	private static final String VOLUME_PREF = "volumePref";
	private static final String NAME_PREF = "namePref";
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
	
	int getVolume() {
		return sharedPreferences.getInt(VOLUME_PREF, 50);
	}
	
	void setVolume(int volume) {
		sharedPreferences.edit()
			  .putInt(VOLUME_PREF, volume)
			  .commit();
	}
	
	String getName() {
		return sharedPreferences.getString(NAME_PREF, "New Profile");
	}
	
	void setName(String name) {
		sharedPreferences.edit()
		    .putString(NAME_PREF, name)
		    .commit();
	}
	
	public String toString() {
		return getName();
	}
	
	List<Location> getLocations() {
		ArrayList<Location> locations = new ArrayList<Location>();
		int size = sharedPreferences.getInt(LOCATIONS_PREF + NUM_LOCATIONS_PREF, 0);
		for (int i = 0; i < size; ++i) {
			Location location = new Location("");
			float latitude = sharedPreferences.getFloat(LOCATIONS_PREF + LATITUDE_PREF + i, 0.0f);
			float longitude = sharedPreferences.getFloat(LOCATIONS_PREF + LONGITUDE_PREF + i, 0.0f);
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
			editor.putFloat(LOCATIONS_PREF + LATITUDE_PREF + i, (float) location.getLatitude());
			editor.putFloat(LOCATIONS_PREF + LONGITUDE_PREF + i, (float) location.getLongitude());
			i++;
		}
		editor.commit();
	}
}
