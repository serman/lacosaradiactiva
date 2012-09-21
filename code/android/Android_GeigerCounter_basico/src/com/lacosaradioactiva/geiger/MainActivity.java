package com.lacosaradioactiva.geiger;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;

import com.lacosaradioactiva.geiger.base.BaseActivity;
import com.lacosaradioactiva.geiger.data.DataRecorder;
import com.lacosaradioactiva.geiger.data.GeigerModel;
import com.lacosaradioactiva.geiger.data.Geoposition;
import com.lacosaradioactiva.geiger.data.PachubeUpdate;
import com.lacosaradioactiva.geiger.data.RandomGenerator;
import com.lacosaradioactiva.geiger.processing.ProcessingSketch;

public class MainActivity extends BaseActivity implements Runnable, Observer {

	private static final String TAG = "MainActivity";

	private static final int FIRST_ITEM = 0;

	private Activity mContext;
	MenuAdapter cpa;
	ViewPager mViewPager;

	private ProcessingSketch proc;

	private PendingIntent mPermissionIntent;

	// DataRecorder
	public DataRecorder mRecorder;
	public Geoposition mGeopos;

	// Model
	public final GeigerModel model = new GeigerModel();

	// Reader thread and run() function
	private final Handler handler = new Handler();
	private int sequenceNumber = 0;

	public boolean state_recording = false;
	public boolean state_locationEnabled = false;

	String remoteIP = "192.168.1.10";
	int remotePort = 12002;
	boolean connected = false;

	// pachube & Red
	private PachubeUpdate mPachube;
	public boolean pachubeEnabled; 
	


	// View attributes

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate()");

		setContentView(R.layout.main);

		initialisePaging();

		// startActivity(new Intent(getApplicationContext(),
		// PreferenceActivity.class));

		mContext = this;

		// USB management
		musbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
		mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		mContext.registerReceiver(mUsbReceiver, filter);

		// Register this in model
		model.addObserver(this);
		mRecorder = new DataRecorder(mContext);
		mGeopos = new Geoposition(mContext);
		mPachube = new PachubeUpdate(mContext, "key");

	}

	/**
	 * Initialise the fragments to be paged
	 */
	private void initialisePaging() {

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager_menu);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		List<Fragment> fragments = new Vector<Fragment>();
		fragments.add(Fragment.instantiate(this, BootScreenFragment.class.getName()));
		// fragments.add(Fragment.instantiate(this,
		// WebViewFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, CounterFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, SettingsFragment.class.getName()));
		//fragments.add(Fragment.instantiate(this, EmptyFragment.class.getName()));
		//fragments.add(Fragment.instantiate(this, ProcessingSketch.class.getName()));

		this.cpa = new MenuAdapter(super.getFragmentManager(), fragments);

		// cpa.setReferenceView(mViewPager);
		mViewPager.setAdapter(this.cpa);
		// mViewPager.setCurrentItem(FIRST_ITEM);

		// addProcessingSketch(new CameraFragment(), R.id.f1);
		// addProcessingSketch(new VideoPlayerFragment(), R.id.f1);
		// addProcessingSketch(new ProcessingSketch(), R.id.f1);

		// proc = new ProcessingSketch();
		// addProcessingSketchSide(proc, R.id.fragmentProcessing);

	}

	public void changePage() {
		mViewPager.setCurrentItem(1, true);
	}

	public void addProcessingSketch(Fragment processing, int fragmentPosition) {

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(fragmentPosition, processing);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

	}

	public void addProcessingSketchSide(Fragment fragment, int fragmentPosition) {

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
		ft.replace(fragmentPosition, fragment);
		ft.show(fragment);

		// if (fragment.isHidden()) {
		// ft.show(fragment);
		// } else {
		// ft.hide(fragment);

		// }
		ft.commit();

	}

	@Override
	protected void onStart() {
		Log.d(TAG, "onStart()");
		super.onStart();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();

		// If file descriptor to read from Arduino is already set, skip the
		// remainder of onResume
		if (inputStream != null) {
			return;
		}
		Log.d(TAG, "onResume() : justo antes : " + musbManager.toString());

		UsbAccessory[] accessories = musbManager.getAccessoryList();
		musbAccessory = (accessories == null ? null : accessories[0]);
		Log.d(TAG, "onResume() : accessory got from USB enumeration : " + musbAccessory);
		if (musbAccessory != null) {
			if (musbManager.hasPermission(musbAccessory)) {
				Log.d(TAG, "onResume() : We HAVE permission for accessory : " + musbAccessory);
				openAccessory();
			} else {
				Log.d(TAG, "onResume() : We NEED permission for accessory : " + musbAccessory);
			}
		}

	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop()");
		super.onStop();
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause()");
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		Log.d(TAG, "onDestroy()");
		mContext.unregisterReceiver(mUsbReceiver);
		closeAccessory();
		mRecorder.closeFile();
	}

	public ViewPager getViewPager() {
		return mViewPager;
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
		Log.d(TAG, "run()");
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
		// seqDisplay.setText("" + sequenceNumber);
	}

	public void update(Observable observable, Object data) {
		// Log.d(TAG,"update() , called from model");

		// Call updateViews from Main thread
		handler.post(new Runnable() {
			public void run() {
				if (listener != null) {
					listener.onUpdate();

				}

				if (state_recording)
					recordValues();
			}
		});

	}

	public UpdateViews listener;

	public void setListener(UpdateViews listener) {
		this.listener = listener;

	}

	private void recordValues() {
		// if(model.getSeqNum())
		if (state_locationEnabled && mGeopos.isFresh()) {
			mRecorder.addData(model.getCpm1min(), model.getSeqNum(), model.getUsv1min(), mGeopos.getLongitude(),
					mGeopos.getLatitude());
		} else {
			mRecorder.addData(model.getCpm1min(), model.getSeqNum(), model.getUsv1min());
		}

	}

	public void sendToPachube() {
		if (pachubeEnabled) {
			Log.d("qq", "Sending message to pachube() pachubeEnagled");
			// mPachube.execute(Integer.toString(model.getCpm1min()),
			// Double.toString(mGeopos.getLongitude()),Double.toString(mGeopos.getLatitude()));
			mPachube = new PachubeUpdate(mContext, "key");
			if (state_locationEnabled)
				mPachube.execute(Integer.toString(model.getCpm10min()), Double.toString(mGeopos.getLongitude()),
						Double.toString(mGeopos.getLatitude()));
			else {
				mPachube.execute(Integer.toString(model.getCpm10min()), "0", "0");
				Log.d("qq", "Sending message to pachube() with position");
			}
		}

	}

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
					UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						if (accessory != null) {
							// call method to set up accessory communication
							closeAccessory();
						}
					} else {
						Log.d(TAG, "permission denied for accessory " + accessory);
					}
				}
			}

		}
	};

	private void listAccesories() {

		UsbManager manager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
		UsbAccessory[] accessoryList = manager.getAccessoryList();
	}

	private void openAccessory() {
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

}