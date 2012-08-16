package com.lacosaradioactiva.pdprueba;

import org.puredata.android.service.PdService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.lacosaradioactiva.pdprueba.R.id;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		bindButton(R.id.e, 0); // 40);
		bindButton(R.id.a, 1); // 45);
		bindButton(R.id.d, 2); // 50);
		bindButton(R.id.g, 3); // 55);
		bindButton(R.id.b, 4); // 59);
		bindButton(R.id.ee, 5); // 64);

		SeekBar sk = (SeekBar) findViewById(id.seekBar1); 
		sk.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) { 
				AudioService.changeSpeed(progress); 
			}
		}); 
		
		bindService(new Intent(this, PdService.class),
				AudioService.pdConnection, BIND_AUTO_CREATE);
		initSystemServices();
	}

	public void bindButton(int resource, final int value) {
		Button btn = (Button) findViewById(resource);
		btn.setSoundEffectsEnabled(false);
		/* 
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// AudioService.triggerNote(value);
			}
		});
		*/ 

		btn.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
			
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Log.d("qq", "action_down"); 
					AudioService.triggerNote(value);

					// btn.setPressed(true);
					// Set whatever color you want to set

				} else {
					// btn.setPressed(false);
				}
				return false;
			}
		});

	}

	private void initSystemServices() {
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				if (AudioService.pdService == null)
					return;
				if (state == TelephonyManager.CALL_STATE_IDLE) {
					AudioService.start();
				} else {
					AudioService.pdService.stopAudio();
				}
			}
		}, PhoneStateListener.LISTEN_CALL_STATE);
	}

	//

	@Override
	protected void onResume() {
		super.onResume();
		// PdAudio.startAudio(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// PdAudio.stopAudio();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unbindService(AudioService.pdConnection);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
