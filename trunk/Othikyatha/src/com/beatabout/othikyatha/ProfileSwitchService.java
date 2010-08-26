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
	public static final String PROFILE_SWITCH_INTENT = "com.beatabout.othikyatha.SWITCH_PROFILE";
	public static int PROFILE_SWITCH_NOTIFY_ID = 0;
	private NotificationManager nm;
	private DataManager dataManager;
	
	public ProfileSwitchService() {
		super("Profile Switch");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
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
		dataManager.setActiveProfile(profile);
	}
}
