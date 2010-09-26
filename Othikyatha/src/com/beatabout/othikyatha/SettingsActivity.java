package com.beatabout.othikyatha;

import android.content.ContextWrapper;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
  private DataManager dataManager;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    dataManager = new DataManager(new ContextWrapper(getApplicationContext()));
    getPreferenceManager().setSharedPreferencesName(getString(R.string.settings));
    addPreferencesFromResource(R.xml.settings);
  }

  @Override
  public void onDestroy() {
    dataManager.onChangeSettings();
    super.onDestroy();
  }
  
  @Override
  public void onStop() {
    dataManager.onChangeSettings();
    super.onStop();
  }
}
