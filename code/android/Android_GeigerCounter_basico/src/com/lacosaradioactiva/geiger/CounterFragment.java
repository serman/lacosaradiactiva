package com.lacosaradioactiva.geiger;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lacosaradioactiva.geiger.views.GraphView;

public class CounterFragment extends Fragment {

	private static final String TAG = "MAIN";

	MainActivity mContext;

	// View attributes
	private TextView currentUsvDisplay;
	private TextView currentCpmDisplay;
	// private TextView seqDisplay;
	private TextView averageUsvDisplay;
	private TextView lonDisplay;
	public GraphView graphView;

	private Button gpsButton;

	private Button rec;

	private Button pachubeButton;

	private Button cosmButton;

	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, 1, 0, "Enable GPS");
		return true;

	}

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 1: // GPS ON off
			if (mContext.state_locationEnabled == true) {
				item.setTitle("Enable GPS");
				mContext.mGeopos.stop();
				mContext.state_locationEnabled = false;
			} else {
				mContext.state_locationEnabled = true;
				item.setTitle("Disable GPS");
				mContext.mGeopos.start();
			}
			return true;
		}
		return false;
	}

	/**
	 * Called when the activity is first created.
	 * 
	 * @return
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
		Log.d(TAG, "onCreateView()");
		View v = inflater.inflate(R.layout.main_g, null);

		// Get View handlers
		currentUsvDisplay = (TextView) v.findViewById(R.id.current_usv_display);
		currentCpmDisplay = (TextView) v.findViewById(R.id.current_cpm_display);
		// seqDisplay = (TextView) v.findViewById(R.id.seq_num);
		lonDisplay = (TextView) v.findViewById(R.id.gps_lon);
		averageUsvDisplay = (TextView) v.findViewById(R.id.average_usv_display);
		graphView = (GraphView) v.findViewById(R.id.graphview);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mContext = (MainActivity) this.getActivity();

		mContext.setListener(new UpdateViews() {

			@Override
			public void onUpdate() {

				// Log.d(TAG,"updateViews()");
				// Get values from model
				int cpm1min = mContext.model.getCpm1min();
				int seqNum = mContext.model.getSeqNum();
				float usv1min = mContext.model.getUsv1min();
				float usv10min = mContext.model.getUsv10min();
				/*if (mContext.pachubeEnabled) {
					if (seqNum % 30 == 0)
						mContext.sendToPachube();
				}*/

				if (seqNum % 3 == 0) {
					graphView.setVal(cpm1min, seqNum, usv1min, usv10min);
				}

				currentCpmDisplay.setText("" + cpm1min);
				// seqDisplay.setText("" + seqNum);
				lonDisplay.setText("lon:" + mContext.mGeopos.getLongitude());
				currentUsvDisplay.setText(String.format("%.2f", usv1min));
				averageUsvDisplay.setText(String.format("%.2f", usv10min));

			}
		});

		// mRecorder.open();

	}

	@Override
	public void onStart() {
		Log.d(TAG, "onStart()");
		super.onStart();
	}

	@Override
	public void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();

	}

	@Override
	public void onStop() {
		Log.d(TAG, "onStop()");
		super.onStop();
	}

	@Override
	public void onPause() {
		Log.d(TAG, "onPause()");
		super.onPause();
		if (mContext.state_recording) {
			mContext.state_recording = false;
			mContext.mRecorder.closeFile();
			rec.setText("record");
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy()");

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

	}

}