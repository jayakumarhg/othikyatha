package com.beatabout.othikyatha;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;

public class BackgroundService extends IntentService {
	public static final int PROXIMITY_EXPIRATION = -1;
	public static final int PROXIMITY_RADIUS_IN_METERS = 42;

	public static final String PROFILE_ID = "profileId";
	public static final String BACKGROUND_SERVICE_INTENT = "com.beatabout.othikyatha.BACKGROUND_SERVICE";

	public static final String REQUEST_TYPE = "requestType";
	public static final int REQUEST_ADD = 1;
	public static final int REQUEST_REMOVE = 2;
	public static final int REQUEST_UPDATE = 3;
	public static final int REQUEST_SWITCH = 4;
	
	public static int PROFILE_SWITCH_NOTIFY_ID = 0;

	private AudioManager audioManager;
	private NotificationManager notificationManager;


	private LocationManager locationManager;
	private DataManager dataManager;

	private AtomicInteger requestCode;
	private Map<String, PendingIntent> pendingIntentsMap;

	public BackgroundService() {
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
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		notificationManager =
			  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
		dataManager = new DataManager(contextWrapper);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		pendingIntentsMap = Collections
				.synchronizedMap(new HashMap<String, PendingIntent>());
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
		if (dataManager.getManualMode()) {
			return;
		}

		for (Profile profile : dataManager.getAllProfiles()) {
			for (GeoAddress location : profile.getLocations()) {
				addProximityAlertForProfile(location, profile.getProfileId());
			}
		}
	}

	private PendingIntent getProfileSwitchPendingIntent(int profileId) {
		Intent intent = new Intent(BackgroundService.BACKGROUND_SERVICE_INTENT);
		intent.putExtra(REQUEST_TYPE, REQUEST_SWITCH);
		intent.putExtra(PROFILE_ID, profileId);

		PendingIntent pendingIntent = PendingIntent.getService(
				getApplicationContext(), requestCode.get(), intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
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
		} else if (request == REQUEST_SWITCH) {
			int profileId = extras.getInt(PROFILE_ID);
			boolean entering = extras.getBoolean(
					LocationManager.KEY_PROXIMITY_ENTERING, true);

			switchProfile(profileId, entering);
		}
	}

	private void switchProfile(int profileId, boolean entering) {
		// If we are leaving the current proximity we need to apply default profile
		if (!entering) {
			profileId = dataManager.getDefaultProfileId();
		}
		Profile profile = dataManager.getProfile(profileId);
		ProfileManager.applyProfile(profile, audioManager, getContentResolver());

		// Notify only if we are really changing from the active profile.
		if (profileId != dataManager.getActiveProfileId()) {
			int icon = R.drawable.icon;
			CharSequence tickerText = "Profile changed to " + profile.getName();
			Notification notification = new Notification(icon, tickerText,
					System.currentTimeMillis() + 5000);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			Intent notificationIntent = new Intent(this, ProfileListActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(
					getApplicationContext(), 0, notificationIntent, 0);
			notification.setLatestEventInfo(getApplicationContext(),
					"Profile changed", tickerText, contentIntent);
			notificationManager.notify(PROFILE_SWITCH_NOTIFY_ID + profileId,
					notification);
			dataManager.setActiveProfile(profile);
		}
	}
}
