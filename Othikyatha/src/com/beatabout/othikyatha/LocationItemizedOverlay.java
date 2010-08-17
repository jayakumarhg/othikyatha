package com.beatabout.othikyatha;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class LocationItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	OverlayItem prevOverlayItem;
	OverlayItem currOverlayItem;
	ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>();

	private ProfileLocationActivity profileLocationActivity;

	public LocationItemizedOverlay(Drawable defaultMarker,
			ProfileLocationActivity profileLocationActivity) {
		super(boundCenter(defaultMarker));
		this.profileLocationActivity = profileLocationActivity;
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return overlayItems.get(i);
	}

	public void setPreviousOverlayItem(double latitude, double longitude) {
		GeoPoint point = new GeoPoint((int) (latitude * 1E6),
				(int) (longitude * 1E6));
		setPreviousOverlayItem(point);
	}

	protected void setPreviousOverlayItem(GeoPoint point) {
		if (prevOverlayItem != null) {
			overlayItems.remove(prevOverlayItem);
		}
		
		prevOverlayItem = new OverlayItem(point, "Old selection", "");
		overlayItems.add(prevOverlayItem);
		populate();
	}

	protected void setCurrentOverlayItem(double longitude, double latitude) {
		GeoPoint point = new GeoPoint((int) (latitude * 1E6),
				(int) (longitude * 1E6));
		setCurrentOverlayItem(point);
	}

	protected void setCurrentOverlayItem(GeoPoint point) {
		if (currOverlayItem != null) {
			overlayItems.remove(currOverlayItem);
		}
		currOverlayItem = new OverlayItem(point, "New selection", "");
		overlayItems.add(currOverlayItem);
		populate();
	}

	@Override
	public int size() {
		return overlayItems.size();
	}

	@Override
	protected boolean onTap(int pIndex) {
		if (overlayItems.get(pIndex) != null) {
			profileLocationActivity.setSelectedGeoPoint(overlayItems.get(pIndex).getPoint());
			return true;
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (event.getEventTime() - event.getDownTime() < 200) {
				GeoPoint newPoint = mapView.getProjection().fromPixels(
						(int) event.getX(), (int) event.getY());
				setCurrentOverlayItem(newPoint);
			  profileLocationActivity.setSelectedGeoPoint(currOverlayItem.getPoint());
				return true;
			}
		}
		return false;
	}
}
