package com.lacosaradioactiva.geiger;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

import com.lacosaradioactiva.geiger.data.PachubeUpdate;
import com.lacosaradioactiva.geiger.processing.ProcessingActivity;

public class SettingsFragment extends Fragment {

	MainActivity mContext;

	private View v;
	private Switch recSwitch;
	private Switch cosmSwitch;
	private Switch gpsSwitch;
	private Button cosmButton;

	private Button reset;

	/**
	 * Called when the activity is first created.
	 * 
	 * @return
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		v = inflater.inflate(R.layout.settingslayout, container, false); 
		

		// TODO: temp test: trigger ACTION_USB_PERMISSION from buttom
		recSwitch = (Switch) v.findViewById(R.id.record_button);
		recSwitch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Intent mIntent = new Intent();
				// mIntent.setAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
				// sendBroadcast(mIntent);

				mContext.state_recording = !mContext.state_recording;

				if (mContext.state_recording) { // they clicked and now it is
												// recording
					if (mContext.mRecorder.open() == false) {
						mContext.state_recording = false;
					} else
						recSwitch.setText("Stop Record");
				} else {
					recSwitch.setText("Record");
					mContext.mRecorder.closeFile();
				}
			}
		});

		cosmSwitch = (Switch) v.findViewById(R.id.cosm_button);

		cosmSwitch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// mPachube.execute("30", "0.09","52");
				mContext.pachubeEnabled = !mContext.pachubeEnabled;
				if (mContext.pachubeEnabled) { // they clicked and now it is
												// recording
					cosmSwitch.setText("Stop Pachube");
				} else {
					cosmSwitch.setText("StartPachube");
				}
			}

		});

		cosmButton = (Button) v.findViewById(R.id.pachubeOneUpdate);

		cosmButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				PachubeUpdate mPachube;
				// mPachube.execute("30", "0.09","52");
				mPachube = new PachubeUpdate(mContext, "key");
				if (mContext.state_locationEnabled)
					mPachube.execute(Integer.toString(mContext.model.getCpm10min()), Double.toString(mContext.mGeopos.getLongitude()),
							Double.toString(mContext.mGeopos.getLatitude()));
				else {
					mPachube.execute(Integer.toString(mContext.model.getCpm10min()), "0", "0");
					Log.d("qq", "Sending message to pachube() with position");
				}
				
			}

		});

		reset = (Button) v.findViewById(R.id.reset);
		
		reset.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mContext.model.reset(); 

				
			}
			
		});
		

		gpsSwitch = (Switch) v.findViewById(R.id.GPS_button);
		gpsSwitch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mContext.state_locationEnabled == true) {
					gpsSwitch.setText("Enable GPS");
					mContext.mGeopos.stop();
					mContext.state_locationEnabled = false;
				} else {
					mContext.state_locationEnabled = true;
					gpsSwitch.setText("Disable GPS");
					mContext.mGeopos.start();
				}
			}
		}); 
		
		Button p5 = (Button) v.findViewById(R.id.launch_processing);

		p5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, ProcessingActivity.class));
			}
		});

		
		final TextView ipText = (TextView) v.findViewById(R.id.oscIPText); 
		
		Switch osc = (Switch) v.findViewById(R.id.osc_button); 
		osc.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				
				mContext.model.osc.connect(isChecked, (String) ipText.getText().toString()); 
			}
		}); 
		

		return v;

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mContext = (MainActivity) this.getActivity();


	}

	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
		

		}
		return true;
	}

	
}
