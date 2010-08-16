package com.beatabout.othikyatha;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

public class ProximityAlertManager {
	public static final int PROXIMITY_RADIUS_IN_METERS = 30;
	public static final int PROXIMITY_EXPIRATION = -1;
	public static final String PROFILE_ID = "com.beatabout.othikyatha:"
			+ "profileId";
	public static final String PROFILE_SWITCH_INTENT = "com.beatabout.othikyatha.SWITCH_PROFILE";

	private Context context;
	private LocationManager locationManager;

	private AtomicInteger requestCode;
	private Map<String, PendingIntent> pendingIntentsMap;

	public ProximityAlertManager(Context context, LocationManager locationManager) {
		this.context = context;
		this.locationManager = locationManager;
		this.requestCode = new AtomicInteger(0);
		this.pendingIntentsMap = Collections
				.synchronizedMap(new HashMap<String, PendingIntent>());
	}

	public void addProximityAlertForProfile(Location location, int profileId) {
		PendingIntent pendingIntent = getProfileSwitchPendingIntent(profileId);
		requestCode.incrementAndGet();
		String locationAndProfileKey = getLocationAndProfileKey(location, profileId);
		pendingIntentsMap.put(locationAndProfileKey, pendingIntent);
		locationManager.addProximityAlert(location.getLatitude(),
				location.getLongitude(), PROXIMITY_RADIUS_IN_METERS,
				PROXIMITY_EXPIRATION, pendingIntent);
	}

	public void removeProximityAlertForProfile(Location location, int profileId) {
		String locationAndProfileKey = getLocationAndProfileKey(location, profileId);
		PendingIntent pendingIntent = pendingIntentsMap.get(locationAndProfileKey);
		locationManager.removeProximityAlert(pendingIntent);
		pendingIntentsMap.remove(locationAndProfileKey);
	}
	
	public void removeAllProximityAlerts() {
		for (PendingIntent pendingIntent : pendingIntentsMap.values()) {
			locationManager.removeProximityAlert(pendingIntent);
		}
	}
	
	public void addReIncarnationHook() {
		PendingIntent intent = getProfileSwitchPendingIntent(-1);
		int minTime = 4 * 60 * 1000;
		int minDistance = PROXIMITY_RADIUS_IN_METERS;
		locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, minTime, minDistance, intent);
	}

	private PendingIntent getProfileSwitchPendingIntent(int profileId) {
		Intent intent = new Intent(PROFILE_SWITCH_INTENT);
		intent.putExtra(PROFILE_ID, profileId);

		PendingIntent pendingIntent = PendingIntent.getActivity(context,
				requestCode.get(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}

	private String getLocationAndProfileKey(Location location, int profileId) {
		String key = "key:" + location.getLatitude() + ":"
				+ location.getLongitude() + ":" + profileId;
		return key;
	}
}
