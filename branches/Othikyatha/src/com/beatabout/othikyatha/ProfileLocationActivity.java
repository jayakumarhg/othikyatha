package com.beatabout.othikyatha;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

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
    
    Drawable drawable = this.getResources().getDrawable(android.R.drawable.btn_radio);
    itemizedOverlay = new LocationItemizedOverlay(drawable, this);
	}
   
	@Override
  protected void onStart() {
		super.onStart();
		
		double longitude = 0.0;
		double latitude = 0.0;
		
		GeoPoint point = myLocationOverlay.getMyLocation();
		if (point != null) {
      mapView.getController().setCenter(point);
		}
		
    // Existing location
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			longitude = extras.getFloat("longitude");
			latitude = extras.getFloat("latitude");
			if (longitude != 0 || latitude != 0) {
				itemizedOverlay.setPreviousOverlayItem(longitude, latitude);
			}
			centerLocation(longitude, latitude);
		}

    List<Overlay> mapOverlays = mapView.getOverlays();
    mapOverlays.add(myLocationOverlay);
    mapOverlays.add(itemizedOverlay);
	}
	
	private void centerLocation(double longitude, double latitude) {
		GeoPoint point = new GeoPoint((int)(longitude * 1E6), (int)(latitude * 1E6));
    mapView.getController().setCenter(point);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	public void setSelectedGeoPoint(GeoPoint point) {
		if (point == null) {
			setResult(RESULT_CANCELED, getIntent());
		} else {
			float longitude = (float) (point.getLongitudeE6() / 1E6);
			float latitude = (float) (point.getLatitudeE6() / 1E6);
			getIntent().putExtra("newLongitude", longitude);
			getIntent().putExtra("newLatitude", latitude);
			setResult(RESULT_OK, getIntent());
		}
		this.finish();
	}
}
