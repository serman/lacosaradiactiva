package com.lacosaradioactiva.geiger.data;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Geoposition {
	private double lon, lat = 0.0f;
	private boolean freshLocation = false;
	private Context mContext;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private TextView mtext;

	public Geoposition(Context mContext1) {
		this.mContext = mContext1;
		locationManager = (LocationManager) this.mContext.getSystemService(Context.LOCATION_SERVICE);
	}

	public void start() {
		Log.d("qq", "Starting gps ");
		// Acquire a reference to the system Location Manager

		// Define a listener that responds to location updates
		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location
				// provider.
				makeUseOfNewLocation(location);
			}

			public void onProviderEnabled(String provider) {
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			public void onProviderDisabled(String provider) {
			}
		};

		// Register the listener with the Location Manager to receive location
		// updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

	}

	public void setTextView(TextView mtext1) {

		this.mtext = mtext1;

	}

	public void stop() {
		// Remove the listener you previously added
		freshLocation = false;
		locationManager.removeUpdates(locationListener);

	}

	// Called when a new location is found by the network location provider.
	protected void makeUseOfNewLocation(Location location) {

		freshLocation = true;
		lon = location.getLongitude();
		lat = location.getLatitude();

		if (mtext != null) {
			this.mtext.setText("auto: " + lon);
			// Log.d("qq","new location: "+ lon +" // lat:" + lat);
		}
	}

	// Note user should check if the location is fresh or stinks.
	public double getLongitude() {
		if (!freshLocation) {
			Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (lastKnownLocation != null)
				return lastKnownLocation.getLongitude();
			else
				return 0;
		} else
			return lon;
	}

	public double getLatitude() {
		if (!freshLocation) {
			Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (lastKnownLocation != null)
				return lastKnownLocation.getLatitude();
			else
				return 0;
		} else
			return lat;
	}

	public boolean isFresh() {
		return freshLocation;
	}

}
