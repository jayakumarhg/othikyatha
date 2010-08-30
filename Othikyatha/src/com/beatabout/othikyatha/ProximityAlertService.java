package com.beatabout.othikyatha;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;

public class ProximityAlertService extends IntentService {
	public static final int PROXIMITY_EXPIRATION = -1;
	public static final int PROXIMITY_RADIUS_IN_METERS = 42;

	public static final String PROFILE_ID = "profileId";
	public static final String PROXIMITY_ALERT_INTENT = "com.beatabout.othikyatha.PROXIMITY_ALERT";
	
	public static final String REQUEST_TYPE = "requestType";
	public static final int REQUEST_ADD = 1;
	public static final int REQUEST_REMOVE = 2;
	public static final int REQUEST_UPDATE = 3;

	private LocationManager locationManager;
	private DataManager dataManager;

	private AtomicInteger requestCode;
	private Map<String, PendingIntent> pendingIntentsMap;

	public ProximityAlertService() {
		super("Proximity Alert");
	}
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	onHandleIntent(intent);
        return START_STICKY;
    }
	
	@Override
	public void onCreate() {
		super.onCreate();
		ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
		dataManager = new DataManager(contextWrapper);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		pendingIntentsMap = Collections.synchronizedMap(new HashMap<String, PendingIntent>());
		requestCode = new AtomicInteger(0);
	}

	private void addProximityAlertForProfile(GeoAddress location, int profileId) {
		PendingIntent pendingIntent = getProfileSwitchPendingIntent(profileId);
		requestCode.incrementAndGet();
		String locationAndProfileKey = getLocationAndProfileKey(location, profileId);
		pendingIntentsMap.put(locationAndProfileKey, pendingIntent);
		locationManager.addProximityAlert(location.getLatitude(),
				location.getLongitude(), PROXIMITY_RADIUS_IN_METERS,
				PROXIMITY_EXPIRATION, pendingIntent);
	}

	private void removeAllProximityAlerts() {
		for (PendingIntent pendingIntent : pendingIntentsMap.values()) {
			locationManager.removeProximityAlert(pendingIntent);
		}
		pendingIntentsMap.clear();
	}
	
	private void addAllProximityAlerts() {
		for (Profile profile : dataManager.getAllProfiles()) {
			for (GeoAddress location : profile.getLocations()) {
				addProximityAlertForProfile(location, profile.getProfileId());
			}
		}
	}

	private PendingIntent getProfileSwitchPendingIntent(int profileId) {
		Intent intent = new Intent(ProfileSwitchService.PROFILE_SWITCH_INTENT);
		intent.putExtra(PROFILE_ID, profileId);

		PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),
				requestCode.get(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}

	private String getLocationAndProfileKey(GeoAddress location, int profileId) {
		String key = "key:" + location.getLatitude() + ":"
				+ location.getLongitude() + ":" + profileId;
		return key;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		int request = extras.getInt(REQUEST_TYPE);
		if (request == REQUEST_ADD) {
			addAllProximityAlerts();
		} else if (request == REQUEST_REMOVE) {
			removeAllProximityAlerts();
		} else if (request == REQUEST_UPDATE) {
			removeAllProximityAlerts();
			addAllProximityAlerts();
		}
	}
}
