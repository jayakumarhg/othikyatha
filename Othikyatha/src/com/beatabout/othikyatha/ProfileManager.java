package com.beatabout.othikyatha;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.util.Log;

public class ProfileManager {
  private static final String TAG = "ProfileManager";
  
  private static final String DISABLE_SUFFIX = "Othikyatha";
  private static final Uri APN_TABLE_URI = Uri.parse("content://telephony/carriers");

  private AudioManager audioMgr;
  private WifiManager wifiMgr;
  private ConnectivityManager connectivityMgr;

  public ProfileManager(AudioManager audioManager,
                        WifiManager wifiManager,
                        ConnectivityManager connectivityManager) {
    audioMgr = audioManager;
    wifiMgr = wifiManager;
    connectivityMgr = connectivityManager;
  }
  
	public void applyProfile(Profile profile,
	                         ContentResolver contentResolver) {
		setStreamVolume(audioMgr, AudioManager.STREAM_RING,	profile.getRingVolume());
		setStreamVolume(audioMgr, AudioManager.STREAM_ALARM, profile.getRingVolume());
		setStreamVolume(audioMgr, AudioManager.STREAM_NOTIFICATION, profile.getRingVolume());
		setStreamVolume(audioMgr, AudioManager.STREAM_SYSTEM,	profile.getRingVolume());
		setStreamVolume(audioMgr, AudioManager.STREAM_MUSIC, profile.getMediaVolume());
		setStreamVolume(audioMgr, AudioManager.STREAM_VOICE_CALL, profile.getVoiceVolume());

		if (profile.getSilent()) {
			audioMgr.setRingerMode(profile.getVibrate() ?
					AudioManager.RINGER_MODE_VIBRATE : AudioManager.RINGER_MODE_SILENT);
		} else {
			audioMgr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		}

		int setting = profile.getVibrate() ? AudioManager.VIBRATE_SETTING_ON
				: AudioManager.VIBRATE_SETTING_OFF;
		audioMgr.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, setting);
		audioMgr.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,
				setting);

		// TODO(sumit): We should not call this on every profile switch.
		// Call this when there is real change in state
		wifiMgr.setWifiEnabled(profile.getWifiState());
		
		// Data Access over Mobile Network
		// TODO(sumit): It seems there exists an internal API to turn on-off data over
		// Mobile Network. But don't know how to get it working. Once figured out, 
		// un-comment the following line and disable EnableData()
		//connectivityMgr.setMobileDataEnabled(profile.getDataState());
    // EnableData(contentResolver, profile.getDataState());
	}
	
	public static void readCurrentProfile(Profile profile, AudioManager audioManager,
	                                      WifiManager wifiManager) {
		profile.setRingVolume(getStreamVolume(audioManager, AudioManager.STREAM_RING));
		profile.setMediaVolume(getStreamVolume(audioManager, AudioManager.STREAM_MUSIC));
		profile.setVoiceVolume(getStreamVolume(audioManager, AudioManager.STREAM_VOICE_CALL));

		int ringerMode = audioManager.getRingerMode();
		profile.setSilent(ringerMode == AudioManager.RINGER_MODE_SILENT);
		profile.setVibrate(ringerMode == AudioManager.RINGER_MODE_VIBRATE);
		
		profile.setWifiState(wifiManager.isWifiEnabled());
	}

	private static void setStreamVolume(AudioManager audioManager, int stream,
			int volume) {
		float volumePerecent = (float) (volume / 100.0);
		int vol = (int) (audioManager.getStreamMaxVolume(stream) * volumePerecent);
		audioManager.setStreamVolume(stream, vol, AudioManager.FLAG_PLAY_SOUND);
	}
	
	private static int getStreamVolume(AudioManager audioManager, int stream) {
		int volume = audioManager.getStreamVolume(stream);
		int max	=	audioManager.getStreamMaxVolume(stream);
		return volume * 100 / max;
	}
	
	private static boolean EnableData(ContentResolver contentResolver, boolean enable) {
	  // First get the data out
	  String[] projection = new String[2];
	  projection[0] = "_id";
	  projection[1] = "apn";
	  Cursor cursor = contentResolver.query(APN_TABLE_URI, projection, null, null, null);
	  ArrayList<ContentValues> all_apns = new ArrayList<ContentValues>();
    cursor.moveToFirst();
    boolean ret = true;
    if (cursor != null) {
	    do {
	      if (cursor.getString(1).endsWith(DISABLE_SUFFIX)) {
	        // already disabled
	        if (enable) {
	          ContentValues entry = new ContentValues();
	          entry.put("_id", cursor.getString(0));
	          entry.put("apn", cursor.getString(1).substring(
	              0, cursor.getString(1).length() - DISABLE_SUFFIX.length()));
	          all_apns.add(entry);
	        }
	      } else {
	        // enabled
	        if (!enable) {
            ContentValues entry = new ContentValues();
            entry.put("_id", cursor.getString(0));
            entry.put("apn", cursor.getString(1) + DISABLE_SUFFIX);
            all_apns.add(entry);
	        }
	      }
	    } while (cursor.moveToNext());
	    for (int i = 0; i < all_apns.size(); ++i) {
	      ContentValues entry = all_apns.get(i);
	      try
        {
	        Log.i(TAG, "Updating " + entry);
            int count = contentResolver.update(APN_TABLE_URI, entry,
                "_id=" + entry.getAsString("_id"), null);
            if(count != 1)
            {
                ret &= false;
                Log.w(TAG, "Something went wrong while updating APN: " +
                      entry.getAsString("apn") + " ret_val: " + count);
            }
        }
        catch (SQLException e)
        {
            Log.d(TAG, e.getMessage());
        }
	    }
    }
    return ret;
	}
}
