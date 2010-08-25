package com.beatabout.othikyatha;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;

public class ProfileSwitchService extends IntentService {
	public static int PROFILE_SWITCH_NOTIFY_ID = 0;
	private NotificationManager nm;
	private DataManager dataManager;
	private ProximityAlertManager proximityAlertManager;

	public ProfileSwitchService() {
		super("Profile Switch");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
		dataManager = new DataManager(contextWrapper);
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		proximityAlertManager = new ProximityAlertManager(getApplicationContext(),
				locationManager);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		int profileId = extras.getInt(ProximityAlertManager.PROFILE_ID);

		if (profileId < 0) {
			for (Profile profile : dataManager.getAllProfiles()) {
				for (GeoAddress location : profile.getLocations()) {
					proximityAlertManager.addProximityAlertForProfile(location,
							profile.getProfileId());
				}
			}
			return;
		}

		boolean entering = extras
				.getBoolean(LocationManager.KEY_PROXIMITY_ENTERING, true);

		int icon = R.drawable.icon;
		Profile profile = dataManager.getProfile(profileId);
		CharSequence tickerText = "Profile changed"
				+ (entering ? " to " : " from ") + profile.getName();
		Notification notification = new Notification(icon, tickerText,
				System.currentTimeMillis() + 5000);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		Intent intent1 = new Intent(this, ProfileListActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, intent1, 0);
		notification.setLatestEventInfo(getApplicationContext(), "Profile Changed",
				tickerText, contentIntent);
		nm.notify(PROFILE_SWITCH_NOTIFY_ID + profileId * 2 + (entering ? 1 : 0),
				notification);
		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		ProfileManager.applyProfile(profile, audioManager, getContentResolver());
	}
}
