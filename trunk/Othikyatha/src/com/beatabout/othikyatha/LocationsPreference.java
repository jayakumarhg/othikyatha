package com.beatabout.othikyatha;

import java.util.List;

import android.content.Context;
import android.content.ContextWrapper;
import android.location.Location;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LocationsPreference extends Preference {
	DataManager dataManager;
	private Button addButton;
	private LinearLayout locationsList;
	private TextView textView;
	private OnClickListener addButtonListener;
	private OnClickListener listItemListener;
	private OnClickListener listItemDeleteListener;

	public LocationsPreference(Context context) {
		super(context);
		dataManager = new DataManager(new ContextWrapper(context));
	}

	public LocationsPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		dataManager = new DataManager(new ContextWrapper(context));
	}

	public LocationsPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		dataManager = new DataManager(new ContextWrapper(context));
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);

		textView = (TextView) view.findViewById(R.id.title);
		textView.setText(getTitle());
		addButton = (Button) view.findViewById(R.id.addlocation);
		if (addButtonListener != null) {
			addButton.setOnClickListener(addButtonListener);
		}

		locationsList = (LinearLayout) view.findViewById(R.id.locationlist);
		populateLocations();
	}

	public void populateLocations() {
		locationsList.removeAllViews();

		// Arbitrary profile id - 0 just to get profile data.
		Profile profile = new Profile(0, getPreferenceManager()
				.getSharedPreferences());

		// Add all the location items
		List<Location> locations = profile.getLocations();
		for (int i = 0; i < locations.size(); ++i) {
			float latitude = (float) locations.get(i).getLatitude();
			float longitude = (float) locations.get(i).getLongitude();
			String locationString = "(" + latitude + ", " + longitude + ")";

			View view = LayoutInflater.from(getContext()).inflate(R.layout.listitem,
					null);
			TextView txtView = (TextView) view.findViewById(R.id.title);
			txtView.setText(locationString);
			txtView.setTextAppearance(getContext(),
					android.R.attr.textAppearanceSmall);

			txtView.setClickable(true);
			txtView.setFocusable(true);
			txtView.setId(i);
			if (listItemListener != null) {
				txtView.setOnClickListener(listItemListener);
			}

			ImageView imageView = (ImageView) view.findViewById(R.id.delete);
			imageView.setImageResource(android.R.drawable.ic_delete);
			imageView.setId(i);
			imageView.setClickable(true);
			if (listItemDeleteListener != null) {
				imageView.setOnClickListener(listItemDeleteListener);
			}
			locationsList.addView(view);
		}
		locationsList.requestLayout();
	}

	public void setAddLocationListener(OnClickListener onClickListener) {
		addButtonListener = onClickListener;
	}

	public void setListItemListener(OnClickListener onClickListener) {
		listItemListener = onClickListener;
	}

	public void setListItemDeleteListener(OnClickListener onClickListener) {
		listItemDeleteListener = onClickListener;
	}
}
