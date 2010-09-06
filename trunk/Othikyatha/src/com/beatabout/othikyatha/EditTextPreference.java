package com.beatabout.othikyatha;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class EditTextPreference extends Preference implements TextWatcher {
	private EditText name;
	private boolean enabled = true;

	public EditTextPreference(Context context) {
		super(context);
	}

	public EditTextPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EditTextPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		name = (EditText) view.findViewById(R.id.name);
		name.setText(getPersistedString("Default"));
		name.setEnabled(enabled);
		name.addTextChangedListener(this);
		name.requestFocus();
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setText(getTitle());
	}

	public void afterTextChanged(Editable s) {
	}

	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		SharedPreferences.Editor editor = getEditor();
		editor.putString(getKey(), s.toString());
		editor.commit();
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
  }
}
