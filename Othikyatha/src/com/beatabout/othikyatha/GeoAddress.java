package com.beatabout.othikyatha;

public class GeoAddress {
	private double latitude;
	private double longitude;
	private String address;

	public GeoAddress() {
		this(0.0, 0.0);
	}
	
	public GeoAddress(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public String getAddress() {
		return address == null ? "" : address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
}