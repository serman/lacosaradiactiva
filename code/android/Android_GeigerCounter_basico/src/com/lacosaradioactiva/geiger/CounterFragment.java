package com.lacosaradioactiva.geiger;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lacosaradioactiva.geiger.data.DataRecorder;
import com.lacosaradioactiva.geiger.data.GeigerModel;
import com.lacosaradioactiva.geiger.data.Geoposition;
import com.lacosaradioactiva.geiger.data.PachubeUpdate;
import com.lacosaradioactiva.geiger.data.RandomGenerator;
import com.lacosaradioactiva.geiger.processing.ProcessingActivity;


/* 
 * NOTAS: he quitado seqDisplay porque no sab’a lo que era 
 * 
 * 
 */ 

public class CounterFragment extends Fragment implements Runnable, Observer {

	private static final String TAG = "MAIN";

	Context mContext;

	// View attributes
	private TextView currentUsvDisplay;
	private TextView currentCpmDisplay;
	//private TextView seqDisplay;
	private TextView averageUsvDisplay;
	private TextView lonDisplay;

	// Reader thread and run() function
	private final Handler handler = new Handler();
	private int sequenceNumber = 0;

	// DataRecorder
	private DataRecorder mRecorder;
	private Geoposition mGeopos;
	private Button rec;
	private boolean state_recording = false;
	private boolean state_locationEnabled = false;

	// pachube & Red
	private PachubeUpdate mPachube;

	// *************************************** USB management
	private UsbManager musbManager;
	private UsbAccessory musbAccessory;
	private ParcelFileDescriptor fileDescriptor;
	private FileInputStream inputStream;
	private FileOutputStream outputStream;

	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	// Broadcast Receiver

	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbAccessory accessory = (UsbAccessory) intent
							.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						if (accessory != null) {
							// call method to set up accessory communication
							closeAccessory();
						}
					} else {
						Log.d(TAG, "permission denied for accessory "
								+ accessory);
					}
				}
			}

		}
	};

	private void listAccesories() {

		UsbManager manager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
		UsbAccessory[] accessoryList = manager.getAccessoryList();
	}

	private void openAccessory( ){
		Log.d(TAG, "openAccessory() starting into : " + musbAccessory);
		
		UsbManager musbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
		fileDescriptor = musbManager.openAccessory(musbAccessory);

		if (fileDescriptor != null) {

			FileDescriptor fd = fileDescriptor.getFileDescriptor();
			inputStream = new FileInputStream(fd);
			outputStream = new FileOutputStream(fd);
			Thread thread = new Thread(null, this, "GeigerCounter");
			thread.start();
			Log.d(TAG, "openAccessory() : thread started : " + thread);
		} else {
			Log.d(TAG, "openAccessory() : accessory open fail");
		}

	}

	private void closeAccessory() {

		Log.d(TAG, "closeAccessory()");
		try {
			if (fileDescriptor != null) {
				fileDescriptor.close();
			}
		} catch (IOException e) {
		} finally {
			fileDescriptor = null;
			musbAccessory = null;
		}

	}

	// ****************************** USB

	
	
	// Model
	private final GeigerModel model = new GeigerModel();
	private Button pachubeButton;
	private boolean pachubeEnabled;

	private PendingIntent mPermissionIntent;

	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, 1, 0, "Enable GPS");
		return true;

	}

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 1: // GPS ON off
			if (state_locationEnabled == true) {
				item.setTitle("Enable GPS");
				mGeopos.stop();
				state_locationEnabled = false;
			} else {
				state_locationEnabled = true;
				item.setTitle("Disable GPS");
				mGeopos.start();
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		super.onCreateView(inflater, container, savedInstanceState);
		Log.d(TAG, "onCreateView()");
		View v = inflater.inflate(R.layout.main_g, null);

		// Get View handlers
		currentUsvDisplay = (TextView) v.findViewById(R.id.current_usv_display);
		currentCpmDisplay = (TextView) v.findViewById(R.id.current_cpm_display);
		//seqDisplay = (TextView) v.findViewById(R.id.seq_num);
		lonDisplay = (TextView) v.findViewById(R.id.gps_lon);
		averageUsvDisplay = (TextView) v.findViewById(R.id.average_usv_display);

		
		// TODO: temp test: trigger ACTION_USB_PERMISSION from buttom
		rec = (Button) v.findViewById(R.id.record_button);
		rec.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Intent mIntent = new Intent();
				// mIntent.setAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
				// sendBroadcast(mIntent);

				state_recording = !state_recording;

				if (state_recording) { // they clicked and now it is recording
					if (mRecorder.open() == false) {
						state_recording = false;
					} else
						rec.setText("Stop Record");
				} else {
					rec.setText("Record");
					mRecorder.closeFile();
				}
			}
		});
	
		pachubeButton = (Button) v.findViewById(R.id.sendPachube);
		pachubeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// mPachube.execute("30", "0.09","52");
				pachubeEnabled = !pachubeEnabled;
				if (pachubeEnabled) { // they clicked and now it is recording
					pachubeButton.setText("Stop Pachube");
				} else {
					pachubeButton.setText("StartPachube");
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

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mContext = (Activity) this.getActivity();

		// USB management
		musbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
		mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		mContext.registerReceiver(mUsbReceiver, filter);

		// Register this in model
		model.addObserver(this);
		mRecorder = new DataRecorder(mContext);
		mGeopos = new Geoposition(mContext, lonDisplay);
		mPachube = new PachubeUpdate(mContext, "key");
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
		// If file descriptor to read from Arduino is already set, skip the
		// remainder of onResume
		if (inputStream != null) {
			return;
		}
		Log.d(TAG,"onResume() : justo antes : " + musbManager.toString());
		
		  UsbAccessory[] accessories = musbManager.getAccessoryList();
		  musbAccessory = (accessories == null ? null : accessories[0]);
		  Log.d(TAG,"onResume() : accessory got from USB enumeration : " +musbAccessory); 
		  if (musbAccessory != null) { 
			  if(musbManager.hasPermission(musbAccessory)) {
				  	Log.d(TAG,"onResume() : We HAVE permission for accessory : " +musbAccessory); 
				  	openAccessory(	); 
			} else {
				Log.d(TAG,"onResume() : We NEED permission for accessory : "+musbAccessory); 
		   }
		  }
		

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
		if (state_recording) {
			state_recording = false;
			mRecorder.closeFile();
			rec.setText("record");
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy()");
		mContext.unregisterReceiver(mUsbReceiver);
		closeAccessory();
		mRecorder.closeFile();
	}

	private void startRandomGenerator() {
		/**
		 * TODO: This is a TEMPORARY ramdom generator to be able to test offline
		 * only for testing purposes, should be deleted in final code
		 * */
		Log.d(TAG, "startRandomGenerator()");
		Runnable randomRunnable = new RandomGenerator(model);
		Thread randomThread = new Thread(randomRunnable);
		randomThread.start();
	}

	public void run() {
		Log.d(TAG,"run()");
		while (fileDescriptor != null) {
			int ret = 0;
			byte[] buffer = new byte[16384];
			int i;
			int cpm = 0;
			int seq = 0;

			while (ret >= 0) {
				try {
					ret = inputStream.read(buffer);
				} catch (IOException e) {
					break;
				}

				i = 0;
				while (i < ret) {
					int len = ret - i;

					switch (buffer[i]) {
					case 0x1:
						if (len >= 3) {
							cpm = 0;
							cpm |= buffer[i + 1] & 0xFF;
							cpm <<= 8;
							cpm |= buffer[i + 2] & 0xFF;
							// Log.d(TAG,"run() : (0x1) measure read "+cpm);
							model.setIntervalCount(cpm);
						}
						i += 3;
						break;
					case 0x2:
						if (len >= 3) {
							seq = 0;
							seq |= buffer[i + 1] & 0xFF;
							seq <<= 8;
							seq |= buffer[i + 2] & 0xFF;
							// Log.d(TAG,"run() : (0x2) seq num "+seq);
							model.setSeqNum(seq);
							/**
							 * TODO: improve this sequenceNumber = seq;
							 * handler.post(new Runnable(){ public void run(){
							 * displaySeq(); } });
							 */

						}
						i += 3;
						break;

					default:
						// Log.d(TAG, "run() : unknown msg: " + buffer[i]);
						i = len;
						break;
					}
				}

			}
		}
		Log.d(TAG, "run() : exit");
	}

	private void displaySeq() {
		//seqDisplay.setText("" + sequenceNumber);
	}

	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub
		// Log.d(TAG,"update() , called from model");

		// Call updateViews from Main thread
		handler.post(new Runnable() {
			public void run() {
				updateViews();
				if (state_recording)
					recordValues();
			}
		});

	}

	private void recordValues() {
		// if(model.getSeqNum())
		if (state_locationEnabled && mGeopos.isFresh()) {
			mRecorder.addData(model.getCpm1min(), model.getSeqNum(),
					model.getUsv1min(), mGeopos.getLongitude(),
					mGeopos.getLatitude());
		} else {
			mRecorder.addData(model.getCpm1min(), model.getSeqNum(),
					model.getUsv1min());
		}

	}

	private void sendToPachube() {
		if (pachubeEnabled) {
			Log.d("qq", "Sending message to pachube() pachubeEnagled");
			// mPachube.execute(Integer.toString(model.getCpm1min()),
			// Double.toString(mGeopos.getLongitude()),Double.toString(mGeopos.getLatitude()));
			mPachube = new PachubeUpdate(mContext, "key");
			if (state_locationEnabled)
				mPachube.execute(Integer.toString(model.getCpm1min()),
						Double.toString(mGeopos.getLongitude()),
						Double.toString(mGeopos.getLatitude()));
			else {
				mPachube.execute(Integer.toString(model.getCpm1min()), "0", "0");
				Log.d("qq", "Sending message to pachube() with position");
			}
		}

	}

	private void updateViews() {
		// Log.d(TAG,"updateViews()");
		// Get values from model
		int cpm1min = model.getCpm1min();
		int seqNum = model.getSeqNum();
		float usv1min = model.getUsv1min();
		float usv10min = model.getUsv10min();
		if (pachubeEnabled) {
			if (seqNum % 30 == 0)
				sendToPachube();
		}

		currentCpmDisplay.setText("" + cpm1min);
		//seqDisplay.setText("" + seqNum);
		lonDisplay.setText("lon:" + mGeopos.getLongitude());
		currentUsvDisplay.setText(String.format("%.2f", usv1min));
		averageUsvDisplay.setText(String.format("%.2f", usv10min));
	}
}