package com.beatabout.othikyatha;

import java.util.List;

import android.content.ContextWrapper;
import android.content.Intent;
import android.location.Location;
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
    addPreferencesFromResource(R.xml.profile);
    
    dataManager = new DataManager(new ContextWrapper(getApplicationContext()));

    locationsPreference = (LocationsPreference) findPreference("locsPref");
    locationsPreference.setAddLocationListener(new AddButtonListener());
    locationsPreference.setListItemListener(new ListItemListener());
  }
	
	@Override
  protected void onStart() {
		super.onStart();
		
    Bundle extras = getIntent().getExtras();
    profileId = extras.getInt("profileId");
    
    String prefName = DataManager.getPreferenceName(profileId);
    getPreferenceManager().setSharedPreferencesName(prefName);
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
			Location location = profile.getLocations().get(id);
			editLocationIntent.putExtra("longitude", (float) location.getLongitude());
			editLocationIntent.putExtra("latitude", (float) location.getLatitude());
			startActivityForResult(editLocationIntent, 1);
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode,
      Intent data) {
		if (requestCode == 0 && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				float longitude = extras.getFloat("newLongitude");
				float latitude = extras.getFloat("newLatitude");
				
				Location newLocation = new Location("");
				newLocation.setLongitude(longitude);
				newLocation.setLatitude(latitude);
				
				Profile profile = dataManager.getProfile(profileId);
				List<Location> locations = profile.getLocations();
				locations.add(newLocation);
				profile.setLocations(locations);
			}
		}
		locationsPreference.populateLocations();
	}
}
