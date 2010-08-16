package com.beatabout.othikyatha;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

public class ProfileSwitchActivity extends Activity {
	public static int PROFILE_SWITCH_NOTIFY_ID = 0;
	private NotificationManager nm;
	private DataManager dataManager;
	private ProximityAlertManager proximityAlertManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.moveTaskToBack(true);

		super.onCreate(savedInstanceState);
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		ContextWrapper contextWrapper =
		  new ContextWrapper(getApplicationContext());
		dataManager = new DataManager(contextWrapper);
		LocationManager locationManager = (LocationManager)
    getSystemService(Context.LOCATION_SERVICE);
    proximityAlertManager = new ProximityAlertManager(getApplicationContext(), locationManager);
	}

	@Override
	protected void onStart() {
		this.moveTaskToBack(true);
		
		super.onStart();
		Bundle extras = getIntent().getExtras();
		int profileId = extras.getInt(ProximityAlertManager.PROFILE_ID);
		
		if (profileId < 0) {
			for (Profile profile : dataManager.getAllActiveProfiles()) {
				for (Location location : profile.getLocations()) {
				  proximityAlertManager.addProximityAlertForProfile(location, profile.getProfileId());
				}			
			}
			return;
		}
		
		boolean entering = extras
				.getBoolean(LocationManager.KEY_PROXIMITY_ENTERING);

		int icon = R.drawable.icon;
		Profile profile = dataManager.getProfile(profileId);
		CharSequence tickerText = "Profile changed" + 
		    (entering ? " to " : " from ") + profile.getName() + "(" + profileId + ")";
		Notification notification = new Notification(icon, tickerText,
				System.currentTimeMillis() + 5000);
		Intent intent = new Intent(this, ProfileListActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, intent, 0);
		notification.setLatestEventInfo(getApplicationContext(), "Profile Changed",
				tickerText, contentIntent);
		nm.notify(PROFILE_SWITCH_NOTIFY_ID + profileId * 2 + (entering ? 1 : 0),
				notification);

		// this.finish();
	}

}
