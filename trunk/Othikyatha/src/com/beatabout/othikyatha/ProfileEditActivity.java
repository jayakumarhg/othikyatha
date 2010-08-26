package com.beatabout.othikyatha;

import java.util.List;

import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.View.OnClickListener;

public class ProfileEditActivity extends PreferenceActivity {
	DataManager dataManager;
	private LocationsPreference locationsPreference;
	private int profileId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dataManager = new DataManager(new ContextWrapper(getApplicationContext()));
	}

	@Override
	protected void onStart() {
		super.onStart();

		Bundle extras = getIntent().getExtras();
		profileId = extras.getInt("profileId");

		String prefName = DataManager.getPreferenceName(profileId);
		getPreferenceManager().setSharedPreferencesName(prefName);
		addPreferencesFromResource(R.xml.profile);

		locationsPreference = (LocationsPreference) findPreference("locsPref");
		locationsPreference.setAddLocationListener(new AddButtonListener());
		locationsPreference.setListItemListener(new ListItemListener());
		locationsPreference.setListItemDeleteListener(new ListItemDeleteListener());
		setResult(RESULT_OK, getIntent());
	}

	private class AddButtonListener implements OnClickListener {
		public void onClick(View v) {
			Intent editLocationIntent = new Intent(ProfileEditActivity.this,
					ProfileLocationActivity.class);
			startActivityForResult(editLocationIntent, 0);
		}
	}

	private class ListItemListener implements OnClickListener {
		public void onClick(View v) {
			Intent editLocationIntent = new Intent(ProfileEditActivity.this,
					ProfileLocationActivity.class);
			int id = v.getId();
			Profile profile = dataManager.getProfile(profileId);
			GeoAddress location = profile.getLocations().get(id);
			editLocationIntent.putExtra("index", id);
			editLocationIntent.putExtra("longitude", (float) location.getLongitude());
			editLocationIntent.putExtra("latitude", (float) location.getLatitude());
			startActivityForResult(editLocationIntent, 1);
		}
	}

	private class ListItemDeleteListener implements OnClickListener {
		public void onClick(View v) {
			int index = v.getId();
			Profile profile = dataManager.getProfile(profileId);
			List<GeoAddress> locations = profile.getLocations();
			locations.remove(index);
			profile.setLocations(locations);
			ProfileEditActivity.this.locationsPreference.populateLocations();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				Profile profile = dataManager.getProfile(profileId);
				List<GeoAddress> locations = profile.getLocations();

				float longitude = extras.getFloat("newLongitude");
				float latitude = extras.getFloat("newLatitude");
				String address = extras.getString("newAddress");

				GeoAddress newLocation = null;
				if (requestCode == 0) {
					newLocation = new GeoAddress();
					locations.add(newLocation);
				} else {
					int index = extras.getInt("index");
					newLocation = locations.get(index);
				}
				newLocation.setLongitude(longitude);
				newLocation.setLatitude(latitude);
				newLocation.setAddress(address);
				profile.setLocations(locations);

				locationsPreference.populateLocations();
			}
		}
	}
}