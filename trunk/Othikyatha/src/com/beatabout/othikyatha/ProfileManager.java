package com.beatabout.othikyatha;

import android.content.ContentResolver;
import android.media.AudioManager;
import android.net.wifi.WifiManager;

public class ProfileManager {

  private AudioManager audioMgr;
  private WifiManager wifiMgr;

  public ProfileManager(AudioManager audioManager,
                        WifiManager wifiManager) {
    audioMgr = audioManager;
    wifiMgr = wifiManager;
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
}
