package com.lacosaradioactiva.geiger.maps;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
import com.lacosaradioactiva.geiger.R;
import com.lacosaradioactiva.geiger.base.LocationNode;
import com.lacosaradioactiva.geiger.base.MapBaseActivity;
import com.lacosaradioactiva.geiger.base.MyOverlayItem;

public class MyMapActivity extends MapBaseActivity {

	private MapController mapController;
	private MapView mapView; 
	public Activity mActivity; 

	static ActionItem chart;  
	static ActionItem production;  

	List<Overlay> mapOverlays;
	MyItemizedOverlay itemizedoverlay_me;

	Drawable icon_park;
	Drawable icon_me;


	Handler handler = new Handler(); 
	private int mRadius = 50; 
	private boolean mFirstTime = true;
	private double mLongitude = -3.6934661865234375;
	private double mLatitude = 40.41065325368961;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.map); // bind the layout to the activity

		mActivity = MyMapActivity.this; 
		// create a map view
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(true);
		mapView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d("qq", "qq");
				return false;
			}
		});

		ImageButton btnToCenter = (ImageButton) findViewById(R.id.btnCenter);
		btnToCenter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				centerGPSInfo(mLatitude, mLongitude);

			}
		}); 
		
		final TextView lblProgress = (TextView) findViewById(R.id.lblDistance); 
		lblProgress.setText("" + mRadius + " km"); 

		SeekBar bar = (SeekBar) findViewById(R.id.barDistance); 
		bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
		
				mRadius = seekBar.getProgress(); 
				reloadMapData(); 
				Log.d("qq", "cambio el radio a " + mRadius); 
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { 
				lblProgress.setText("" + progress + " km"); 
			} 
		}); 
	


			
		mapController = mapView.getController();
		mapController.setZoom(8); // Zoon 1 is world view

		mapOverlays = mapView.getOverlays();

		icon_park = getResources().getDrawable(R.drawable.ic_launcher);
		icon_me = getResources().getDrawable(R.drawable.ic_launcher);
	
		itemizedoverlay_me = new MyItemizedOverlay(this, icon_me);

		double latitude = mLatitude;
		double longitude = mLongitude;

		moveTo(latitude, longitude);


	}
	



	void moveTo(double latitude, double longitude) {

		itemizedoverlay_me = new MyItemizedOverlay(this, icon_me);

		GeoPoint point = new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));
		OverlayItem overlayitem = new OverlayItem(point, "Medialab Prado",
				"Plaza de las letras, Madrid");
		

		mapController.animateTo(point);

		itemizedoverlay_me.addOverlay(overlayitem);
		mapOverlays.add(itemizedoverlay_me);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		boolean result = super.dispatchTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_UP) {
			reloadMapData(); // / call the first block of code here

		}

		return result;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void clearMap() { 
		itemizedoverlay_me.clear();
		mapOverlays.clear();
	}

	public void reloadMapData() {
		Projection projection = mapView.getProjection();
		int y = mapView.getHeight() / 2;
		int x = mapView.getWidth() / 2;

		GeoPoint geoPoint = projection.fromPixels(x, y);
		double latitude = (double) geoPoint.getLatitudeE6() / (double) 1E6;
		double longitude = (double) geoPoint.getLongitudeE6() / (double) 1E6;

		clearMap();
	}



	public void centerGPSInfo(double latitude, double longitude) {
		moveTo(latitude, longitude);
	}
}
