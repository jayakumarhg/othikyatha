package com.beatabout.othikyatha;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SeekbarPreference extends Preference implements
		OnSeekBarChangeListener {
	public SeekbarPreference(Context context) {
		super(context);
	}

	public SeekbarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SeekbarPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		SeekBar bar = (SeekBar) view.findViewById(R.id.volume);
		bar.setProgress(getPersistedInt(0));
		bar.setOnSeekBarChangeListener(this);
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setText(getTitle());
	}

	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		SharedPreferences.Editor editor = getEditor();
		editor.putInt(getKey(), progress);
		editor.commit();
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
	}
}
