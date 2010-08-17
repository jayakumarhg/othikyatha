package com.beatabout.othikyatha;

import java.io.IOException;
import java.util.ArrayList;
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
import android.widget.Toast;

public class ProfileLocationActivity extends MapActivity {
	private LocationItemizedOverlay itemizedOverlay;
	private MapView mapView;
	private MyLocationOverlay myLocationOverlay;

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

		Location location = myLocationOverlay.getLastFix();
		if (location != null) {
			centerLocation(location.getLatitude(), location.getLongitude());
		}

		// Existing location
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			latitude = extras.getFloat("latitude", -90.0f);
			longitude = extras.getFloat("longitude", -90.0f);
			if (longitude != -90.0 || latitude != -90.0) {
				centerLocation(latitude, longitude);
				itemizedOverlay.setPreviousOverlayItem(latitude, longitude);
			}
		}

		List<Overlay> mapOverlays = mapView.getOverlays();
		mapOverlays.add(myLocationOverlay);
		mapOverlays.add(itemizedOverlay);
	}

	private void centerLocation(double latitude, double longitude) {
		GeoPoint point = new GeoPoint((int) (latitude * 1E6),
				(int) (longitude * 1E6));
		mapView.getController().setCenter(point);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void setSelectedGeoPoint(GeoPoint point) {
		mapView.getController().animateTo(point);
		boolean more = mapView.getController().zoomIn();
		if (!more) {
		  showReverseGeoCoderMenu(point);
		}
	}

	public void onSelectedPointAndAddress(GeoPoint point, Address address) {
		if (point == null) {
			setResult(RESULT_CANCELED, getIntent());
		} else {
			float latitude = (float) (point.getLatitudeE6() / 1E6);
			float longitude = (float) (point.getLongitudeE6() / 1E6);
			getIntent().putExtra("newLatitude", latitude);
			getIntent().putExtra("newLongitude", longitude);
			setResult(RESULT_OK, getIntent());
		}
		this.finish();
	}

	private void showReverseGeoCoderMenu(final GeoPoint point) {
		Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
		final List<Address> addresses;
		try {
			addresses = geocoder.getFromLocation(
					(double) point.getLatitudeE6() / 1E6,
					(double) point.getLongitudeE6() / 1E6, 3);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		final CharSequence[] items = new CharSequence[addresses.size() + 1];
		items[0] = "None - Select a point again";
		int i = 1;
		for (Address address : addresses) {
			items[i++] = address.toString();
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick a location name");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (item != 0) {
					ProfileLocationActivity.this.onSelectedPointAndAddress(point,
							addresses.get(item));
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
