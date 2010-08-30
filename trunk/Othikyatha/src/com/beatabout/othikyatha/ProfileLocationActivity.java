package com.beatabout.othikyatha;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class ProfileLocationActivity extends MapActivity {
	private MapView mapView;
	private LocationItemizedOverlay itemizedOverlay;
	private MyLocationOverlay myLocationOverlay;
	private Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.profilelocation);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		myLocationOverlay = new MyLocationOverlay(getApplicationContext(), mapView);
		myLocationOverlay.enableMyLocation();

		Drawable drawable = this.getResources().getDrawable(
				android.R.drawable.btn_radio);
		itemizedOverlay = new LocationItemizedOverlay(drawable, this);
	}

	@Override
	protected void onStart() {
		super.onStart();

		double longitude = -90.0;
		double latitude = -90.0;

		centerCurrentLocation(false);

		// Existing location
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			latitude = extras.getFloat("latitude", 100.0f);
			longitude = extras.getFloat("longitude", 100.0f);
			if (longitude != 100.0 || latitude != 100.0) {
				centerLocation(latitude, longitude);
				itemizedOverlay.setCurrentOverlayItem(latitude, longitude);
			}
		}

		List<Overlay> mapOverlays = mapView.getOverlays();
		mapOverlays.add(myLocationOverlay);
		mapOverlays.add(itemizedOverlay);
		
		showHelpTip(getString(R.string.mapview_tip_zooom_select_pt));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mapviewmenu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.location:
				centerCurrentLocation(true);
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void setSelectedGeoPoint(GeoPoint point) {
		mapView.getController().animateTo(point);
		if (mapView.getZoomLevel() == mapView.getMaxZoomLevel() - 1) {
			showHelpTip(getString(R.string.mapview_tip_select_pt));
		} else if (mapView.getZoomLevel() <= mapView.getMaxZoomLevel() - 1) {
			showHelpTip(getString(R.string.mapview_tip_zooom_select_pt));
		}

		boolean moreZoom = mapView.getController().zoomIn();
		if (!moreZoom) {
			showReverseGeoCoderMenu(point);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private void centerCurrentLocation(final boolean force) {
		myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				if (force || !itemizedOverlay.hasCurrentOverlayItem()) {
					Location location = myLocationOverlay.getLastFix();
					if (location != null) {
						centerLocation(location.getLatitude(), location.getLongitude());
					}
				}
			}
		});
	}

	private void centerLocation(double latitude, double longitude) {
		GeoPoint point = new GeoPoint((int) (latitude * 1E6),
				(int) (longitude * 1E6));
		mapView.getController().animateTo(point);
	}

	private void onSelectedPointAndAddress(GeoPoint point, CharSequence address) {
		if (point == null) {
			setResult(RESULT_CANCELED, getIntent());
		} else {
			float latitude = (float) (point.getLatitudeE6() / 1E6);
			float longitude = (float) (point.getLongitudeE6() / 1E6);
			getIntent().putExtra("newLatitude", latitude);
			getIntent().putExtra("newLongitude", longitude);
			getIntent().putExtra("newAddress", address);
			setResult(RESULT_OK, getIntent());
		}
		this.finish();
	}

	private void showReverseGeoCoderMenu(final GeoPoint point) {
		double latitude = (double) point.getLatitudeE6() / 1E6;
		double longitude = (double) point.getLongitudeE6() / 1E6;
		
		Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
		final List<Address> addresses;
		try {
			addresses = geocoder.getFromLocation(latitude, longitude, 2);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		final CharSequence[] items = new CharSequence[addresses.size() + 2];
		items[0] = "--Select another location--";
		items[1] = convertLatLongToString(latitude, longitude);
		int i = 2;
		for (Address address : addresses) {
			String addressLine = "";
			for (int j = 0; j < address.getMaxAddressLineIndex(); ++j) {
				addressLine += (j == 0 ? "" : ", ") + address.getAddressLine(j);
			}
			items[i++] = addressLine;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick a location name");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int index) {
				if (index != 0) {
					ProfileLocationActivity.this.onSelectedPointAndAddress(point,
							items[index]);
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void showHelpTip(String message) {
		if (toast == null) {
		  toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
		} else {
			toast.setText(message);
		}
		toast.show();
	}
	
	private String convertLatLongToString(double latitude, double longitude) {
		return convertLatOrLongToString(latitude) +
		       (latitude >= 0 ? "N" : "S") + ", " +
		       convertLatOrLongToString(longitude) +
		       (longitude >= 0 ? "E" : "W");
	}
	
	private String convertLatOrLongToString(double latorlong) {
		latorlong = Math.abs(latorlong);
		int degrees = (int) latorlong;
		int minutes = (int) ((latorlong - degrees) * 60);
		int seconds = (int) ((((latorlong - degrees) * 60) - minutes) * 60);
		// Example: 12°58′0″N 77°34′0″E
		return "" + degrees + "°" + minutes + "′" + seconds + "″";
	}
}
