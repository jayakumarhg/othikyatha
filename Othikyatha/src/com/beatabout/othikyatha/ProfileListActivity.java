package com.beatabout.othikyatha;

import java.util.ArrayList;
import java.util.Vector;

import android.app.ListActivity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ProfileListActivity extends ListActivity {
	private DataManager dataManager;
	private ProximityAlertManager proximityAlertManager;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
		dataManager = new DataManager(contextWrapper);

		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		proximityAlertManager = new ProximityAlertManager(getApplicationContext(),
				locationManager);

		if (dataManager.hasNoProfiles()) {
			createDefaultProfiles();
		}
		registerForContextMenu(getListView());
	}

	public void onStart() {
		super.onStart();
		reloadProfileList();
		// Move this to code to proximityalertactivity
		for (Profile profile : dataManager.getAllProfiles()) {
			for (GeoAddress location : profile.getLocations()) {
				proximityAlertManager.addProximityAlertForProfile(location,
						profile.getProfileId());
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.listmenu, menu);
		return true;
	}

	@Override
	public boolean onPreparePanel(int featureId, View view, Menu menu) {
		MenuItem item = menu.findItem(R.id.manualMode);
		item.setIcon(
				dataManager.getManualMode()
						? android.R.drawable.checkbox_on_background
				    : android.R.drawable.checkbox_off_background);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.addProfile:
				int profileId = dataManager.addProfileEntry();
				startEditActivity(profileId);
				return true;

			case R.id.manualMode:
				dataManager.toggleManualMode();
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.profilelistmenu, menu);
		menu.setHeaderTitle("Profile Actions");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int profileId = (int) info.targetView.getId();
		switch (item.getItemId()) {
			case R.id.edit:
				startEditActivity(profileId);
				return true;

			case R.id.delete:
				dataManager.removeProfileEntry(profileId);
				reloadProfileList();
				return true;
				
			default:
				return true;
		}
	}
	

	private void createDefaultProfiles() {
		int profileId;
		Profile profile;
		GeoAddress location;
		ArrayList<GeoAddress> locations;

		profileId = dataManager.addProfileEntry();
		profile = dataManager.getProfile(profileId);
		profile.setRingVolume(10);
		profile.setName("Default");
		location = new GeoAddress(-90.0, -90.0);
		locations = new ArrayList<GeoAddress>();
		locations.add(location);
		profile.setLocations(locations);

		profileId = dataManager.addProfileEntry();
		profile = dataManager.getProfile(profileId);
		profile.setRingVolume(40);
		profile.setName("Home");
		location = new GeoAddress(42, 41);
		locations = new ArrayList<GeoAddress>();
		locations.add(location);
		profile.setLocations(locations);

		profileId = dataManager.addProfileEntry();
		profile = dataManager.getProfile(profileId);
		profile.setRingVolume(80);
		profile.setName("Work");
		location = new GeoAddress(80, 81);
		locations = new ArrayList<GeoAddress>();
		locations.add(location);
		profile.setLocations(locations);
	}

	private void reloadProfileList() {
		setListAdapter(newListAdapter());
	}

	private ListAdapter newListAdapter() {
		Vector<Profile> vprofiles = dataManager.getAllProfiles();
		Profile[] profiles = new Profile[vprofiles.size()];

		int i = 0;
		for (Profile profile : vprofiles) {
			profiles[i++] = profile;
		}

		ProfileAdapter adapter = new ProfileAdapter(this,
				android.R.layout.simple_list_item_single_choice, vprofiles);
		return adapter;
	}

	private void startEditActivity(int profileId) {
		Intent intent = new Intent("com.beatabout.othikyatha.EDIT_PROFILE");
		intent.putExtra("profileId", profileId);
		startActivity(intent);
	}

	private class ProfileAdapter extends ArrayAdapter<Profile> {
		private Vector<Profile> profiles;
		private LayoutInflater inflater;

		public ProfileAdapter(Context context, int textViewResourceId,
				Vector<Profile> profiles) {
			super(context, textViewResourceId, profiles);
			this.profiles = profiles;
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				v = inflater.inflate(R.layout.listitem, null);
			}
			Profile profile = profiles.get(position);

			TextView txtView = (TextView) v.findViewById(R.id.title);
			txtView.setText(profile.getName());
			txtView.setTextAppearance(getContext(),
					android.R.style.TextAppearance_Large);
			txtView.setMaxEms(9);
			txtView.setClickable(true);
			txtView.setId(profile.getProfileId());
			txtView.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (dataManager.getManualMode()) {
						int profileId = v.getId();
						Intent intent = new Intent(
								"com.beatabout.othikyatha.SWITCH_PROFILE");
						intent.putExtra("profileId", profileId);
						v.getContext().startService(intent);
					}
				}
			});
			txtView.setFocusable(true);
			txtView.setFocusableInTouchMode(true);
			txtView.setLongClickable(true);

			ImageView imageView = (ImageView) v.findViewById(R.id.image);
			imageView.setImageResource(android.R.drawable.ic_menu_edit);
			imageView.setClickable(true);
			imageView.setId(profile.getProfileId());
			imageView.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					int profileId = v.getId();
					startEditActivity(profileId);
				}
			});

			v.setId(profile.getProfileId());
			v.setFocusable(true);
			v.setFocusableInTouchMode(true);
			v.setClickable(true);
			v.setLongClickable(true);
			
			return v;
		}
	}
}