package com.beatabout.othikyatha;

import java.util.Vector;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class LocationsPreference extends Preference {
	private Button addButton;
	private ListView listView;
	private TextView textView;
	private OnClickListener addButtonListener;
	private OnClickListener listItemListener;

	public LocationsPreference(Context context) {
		super(context);
	}
	
	public LocationsPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public LocationsPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
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
		
		listView = (ListView) view.findViewById(R.id.locationlist);
		listView.setScrollContainer(false);
		populateLocations();
		view.requestLayout();
	}

	public void populateLocations() {
		int numLocations = getSharedPreferences().getInt(getKey() + ".num", 0);
		Vector<String> locationStrings = new Vector<String>();
		for (int i = 0; i < numLocations; ++i) {
			float latitude = getSharedPreferences().getFloat(getKey() + ".latitude@" + i, 0);
			float longitude = getSharedPreferences().getFloat(getKey() + ".longitude@" + i, 0);
			locationStrings.add("(" + latitude + ", " + longitude + ")");
		}
		
		LocationAdapter adapter = new LocationAdapter(getContext(), android.R.layout.simple_list_item_1, locationStrings);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}
	
	private class LocationAdapter extends ArrayAdapter<String> {
		private Vector<String> locations;
		private LayoutInflater inflater;
		
		public LocationAdapter(Context context,
				int textViewResourceId, Vector<String> profiles) {
			super(context, textViewResourceId, profiles);
			this.locations = profiles;
			this.inflater = LayoutInflater.from(context);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
      View v = convertView;
      if (v == null) {
      	v = inflater.inflate(android.R.layout.simple_list_item_1, null);
      }
      String location = locations.get(position);
      
      TextView txtView = (TextView) v;
      txtView.setText(location);
      
      v.setClickable(true);
    	v.setId(position);
    	if (listItemListener != null) {
      	v.setOnClickListener(listItemListener);
    	}
      return v;
		}
	}

	public void setAddLocationListener(OnClickListener onClickListener) {
		addButtonListener = onClickListener;
	}

	public void setListItemListener(OnClickListener onClickListener) {
		listItemListener = onClickListener;
	}
}
