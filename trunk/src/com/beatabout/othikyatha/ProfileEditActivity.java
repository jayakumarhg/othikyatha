package com.beatabout.othikyatha;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ProfileEditActivity extends PreferenceActivity {
	@Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    Bundle extras = getIntent().getExtras();
    int profileId = extras.getInt("profileId");
    String prefName = DataManager.getPreferenceName(profileId);
    getPreferenceManager().setSharedPreferencesName(prefName);
    getIntent().putExtra("pref_file", prefName);
    addPreferencesFromResource(R.xml.profile);
    setResult(RESULT_OK, getIntent());
  }
}
