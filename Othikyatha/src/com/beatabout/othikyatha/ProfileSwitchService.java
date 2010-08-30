package com.beatabout.othikyatha;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContextWrapper;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;

public class ProfileSwitchService extends IntentService {
	public static final String PROFILE_SWITCH_INTENT =
		  "com.beatabout.othikyatha.SWITCH_PROFILE";
	public static int PROFILE_SWITCH_NOTIFY_ID = 0;

	private AudioManager audioManager;
	private DataManager dataManager;
	private NotificationManager notificationManager;

	public ProfileSwitchService() {
		super("Profile Switch");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		notificationManager =
			  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		ContextWrapper contextWrapper =
			  new ContextWrapper(getApplicationContext());
		dataManager = new DataManager(contextWrapper);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		onHandleIntent(intent);
		return START_STICKY;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		int profileId = extras.getInt(ProximityAlertService.PROFILE_ID);

		boolean entering = extras.getBoolean(
				LocationManager.KEY_PROXIMITY_ENTERING, true);

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
