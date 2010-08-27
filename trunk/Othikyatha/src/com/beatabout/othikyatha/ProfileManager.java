package com.beatabout.othikyatha;

import android.content.ContentResolver;
import android.media.AudioManager;

public class ProfileManager {
	
	public static void applyProfile(Profile profile, AudioManager audioManager,
			ContentResolver contentResolver) {
		setStreamVolume(audioManager, AudioManager.STREAM_RING,
				profile.getRingVolume());
		setStreamVolume(audioManager, AudioManager.STREAM_ALARM,
				profile.getRingVolume());
		setStreamVolume(audioManager, AudioManager.STREAM_NOTIFICATION,
				profile.getRingVolume());
		setStreamVolume(audioManager, AudioManager.STREAM_SYSTEM,
				profile.getRingVolume());
		setStreamVolume(audioManager, AudioManager.STREAM_MUSIC,
				profile.getMediaVolume());
		setStreamVolume(audioManager, AudioManager.STREAM_VOICE_CALL,
				profile.getVoiceVolume());

		// Settings.System.putString(context, Settings.System.RINGTONE,
		//		profile.getRingTone());

		if (profile.getSilent()) {
			audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		} else {
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		}

		int setting = profile.getVibrate() ? AudioManager.VIBRATE_SETTING_ON
				: AudioManager.VIBRATE_SETTING_OFF;
		audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, setting);
		audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,
				setting);
	}
	
	public static void readCurrentProfile(Profile profile, AudioManager audioManager) {
		profile.setRingVolume(getStreamVolume(audioManager, AudioManager.STREAM_RING));
		profile.setMediaVolume(getStreamVolume(audioManager, AudioManager.STREAM_MUSIC));
		profile.setVoiceVolume(getStreamVolume(audioManager, AudioManager.STREAM_VOICE_CALL));

		// profile.Settings.System.putString(context, Settings.System.RINGTONE,
		//		profile.getRingTone());

		int ringerMode = audioManager.getRingerMode();
		profile.setSilent(ringerMode == AudioManager.RINGER_MODE_SILENT);
		profile.setVibrate(ringerMode == AudioManager.RINGER_MODE_VIBRATE);
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
