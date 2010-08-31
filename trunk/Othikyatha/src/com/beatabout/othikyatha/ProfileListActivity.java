package com.beatabout.othikyatha;

import java.util.List;
import android.app.ListActivity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ProfileListActivity extends ListActivity {
	private DataManager dataManager;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
		dataManager = new DataManager(contextWrapper);

		if (dataManager.hasNoProfiles()) {
			createDefaultProfiles();
		}
		registerForContextMenu(getListView());
		if (!dataManager.getManualMode()) {
			addAllProximityAlerts();
		}
	}
	
	private void addAllProximityAlerts() {
		Intent intent = new Intent(BackgroundService.BACKGROUND_SERVICE_INTENT);
		intent.putExtra(BackgroundService.REQUEST_TYPE, BackgroundService.REQUEST_ADD);
		getApplicationContext().startService(intent);
	}
	
	private void removeAllProximityAlerts() {
		Intent intent = new Intent(BackgroundService.BACKGROUND_SERVICE_INTENT);
		intent.putExtra(BackgroundService.REQUEST_TYPE, BackgroundService.REQUEST_REMOVE);
		getApplicationContext().startService(intent);
	}

	public void onStart() {
		super.onStart();
		reloadProfileList();
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
				if (dataManager.getManualMode()) {
					removeAllProximityAlerts();
				} else {
					addAllProximityAlerts();
				}
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
		this.onPrepareOptionsMenu(menu);
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
				deleteProfile(profileId);
				return true;
				
			default:
				return true;
		}
	}

	private void deleteProfile(int profileId) {
		if (profileId != dataManager.getDefaultProfileId()) {
		  dataManager.removeProfileEntry(profileId);
		  reloadProfileList();
		} else {
			Toast.makeText(getApplicationContext(),
					getString(R.string.default_no_delete), Toast.LENGTH_SHORT).show();
		}
	}

	private void createDefaultProfiles() {
		int profileId;
		Profile profile;
		
		AudioManager audioManager =
			  (AudioManager) getSystemService(AUDIO_SERVICE);

		profileId = dataManager.addProfileEntry();
		profile = dataManager.getProfile(profileId);
		profile.setName("Default");
		ProfileManager.readCurrentProfile(profile, audioManager);
		dataManager.setActiveProfile(profile);
		dataManager.setDefaultProfile(profile);

		profileId = dataManager.addProfileEntry();
		profile = dataManager.getProfile(profileId);
		ProfileManager.readCurrentProfile(profile, audioManager);
		profile.setVibrate(false);
		profile.setMediaVolume(Math.min(100, 2 * profile.getMediaVolume()));
		profile.setName("Home");

		profileId = dataManager.addProfileEntry();
		profile = dataManager.getProfile(profileId);
		ProfileManager.readCurrentProfile(profile, audioManager);
		profile.setVibrate(true);
		profile.setMediaVolume(profile.getMediaVolume() / 2);
		profile.setRingVolume(profile.getRingVolume() / 2);
		profile.setName("Work");
	}

	private void reloadProfileList() {
		setListAdapter(newListAdapter());
	}

	private ListAdapter newListAdapter() {
		List<Profile> vprofiles = dataManager.getAllProfiles();
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
		private List<Profile> profiles;
		private LayoutInflater inflater;

		public ProfileAdapter(Context context, int textViewResourceId,
				List<Profile> vprofiles) {
			super(context, textViewResourceId, vprofiles);
			this.profiles = vprofiles;
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
			if (profile.getProfileId() == dataManager.getActiveProfileId()) {
				txtView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
				txtView.setTextColor(Color.WHITE);
			} else {
				txtView.setTextColor(Color.LTGRAY);
			}
			txtView.setMaxEms(9);
			txtView.setId(profile.getProfileId());
			v.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (dataManager.getManualMode()) {
						int profileId = v.getId();
						Intent intent = new Intent(BackgroundService.BACKGROUND_SERVICE_INTENT);
						intent.putExtra(BackgroundService.REQUEST_TYPE, BackgroundService.REQUEST_SWITCH);
						intent.putExtra("profileId", profileId);
						v.getContext().startService(intent);
						dataManager.setActiveProfile(dataManager.getProfile(profileId));
						reloadProfileList();
					}
				}
			});

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
			v.setClickable(true);
			v.setLongClickable(true);
			
			return v;
		}
	}
}