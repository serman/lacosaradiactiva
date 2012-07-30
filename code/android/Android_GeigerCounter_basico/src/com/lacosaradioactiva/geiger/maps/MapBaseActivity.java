package com.lacosaradioactiva.geiger.maps;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public abstract class MapBaseActivity extends MapActivity {
	private static final String TAG = "MAP";


	// location
	LocationManager locationManager;
	String bestProvider;
	LocationListener locationListener;
	public String locationInfo = "";
	public String speedInfo = "";

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		// entrar();


	}




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//MenuInflater inflater = getMenuInflater();
		//inflater.inflate(R.menu.menu, menu);
		// menu.getItem(R.id.stop).setVisible(false);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {

	


		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		return true;
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {

		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		Log.d("qq", "qq");
		return false;
	}
}
