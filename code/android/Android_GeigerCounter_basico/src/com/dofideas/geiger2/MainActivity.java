package com.dofideas.geiger2;
///mnt/sdcard/Android/data/com.dofideas.geiger2/files
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements Runnable,Observer {
	
	private static final String TAG = "MAIN";
	
	private static final String ACTION_USB_PERMISSION = "com.dofideas.geiger.USB_PERMISSION";


	
	// View attributes
	private TextView currentUsvDisplay;
	private TextView currentCpmDisplay;
	private TextView seqDisplay;
	private TextView averageUsvDisplay;
	
	private TextView lonDisplay;
	// Reader thread and run() function
	private final Handler handler = new Handler();
	private int sequenceNumber = 0;
	
	// USB management
	private UsbManager usbManager;
	private UsbAccessory usbAccessory;
	private ParcelFileDescriptor fileDescriptor;
	private FileInputStream inputStream;
	
	//DataRecorder
	private DataRecorder mRecorder;
	private Geoposition mGeopos;
	private Button rec;
	private boolean state_recording=false;
	private boolean state_locationEnabled=false;
	
	//pachube & Red
	private pachubeUpdate mPachube;
	
	// Broadcast Receiver
	private final BroadcastReceiver bcastReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			//Log.d(TAG,"onReceive() : context "+context+" intent "+intent);
			
			if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				UsbAccessory accessory = UsbManager.getAccessory(intent);
			//	Log.d(TAG,"onReceive() : Accessory dettached : "+accessory);
				if (accessory != null && accessory.equals(usbAccessory)) {
				//	Log.d(TAG,"onReceive() : About to close accessory : "+usbAccessory);
					closeAccessory();
				}
			}
		}
	};
	
	// Model
	private final GeigerModel model = new GeigerModel();

	private Button pachubeButton;

	private boolean pachubeEnabled;

	

	 
	public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add(0, 1, 0, "Enable GPS"); 
		return true;

	} 
	
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 1:  //GPS ON off
			if(state_locationEnabled==true){
				item.setTitle("Enable GPS");
				mGeopos.stop();
				state_locationEnabled=false;
			}
			else{
				state_locationEnabled=true;
				item.setTitle("Disable GPS");
				mGeopos.start();
			}
			return true;		
		}
		return false;
	}
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate()");
        setContentView(R.layout.main);
        
        // Get View handlers
        currentUsvDisplay = (TextView) findViewById(R.id.current_usv_display);
        currentCpmDisplay = (TextView) findViewById(R.id.current_cpm_display);
        seqDisplay = (TextView) findViewById(R.id.seq_num);


        lonDisplay = (TextView) findViewById(R.id.gps_lon);


        averageUsvDisplay = (TextView) findViewById(R.id.average_usv_display);
        
//>>>>>>> master
        // USB management
        usbManager = UsbManager.getInstance(this);
        //.d(TAG,"onCreate() : usbManager = "+ usbManager);
        
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        registerReceiver(bcastReceiver, filter);
        //Log.d(TAG,"onCreate() : bcastReceiver registered :"+bcastReceiver);
                        
        // TODO: temp test: trigger ACTION_USB_PERMISSION from buttom
        rec = (Button) findViewById(R.id.record_button);
        pachubeButton = (Button) findViewById(R.id.sendPachube);
        rec.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Log.d(TAG,"onClick()");
			//	Intent mIntent = new Intent();
			//	mIntent.setAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
			//	sendBroadcast(mIntent);
				
				state_recording=!state_recording;
				
				if(state_recording){ //they clicked and now it is recording
					if(mRecorder.open()==false){
						state_recording=false;
					}
					else
						rec.setText("Stop Record");									
				}
				else{
					rec.setText("Record");
					mRecorder.closeFile();
				}				
			}
        });
        pachubeButton.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		//mPachube.execute("30", "0.09","52");
        		pachubeEnabled=!pachubeEnabled;
        		if(pachubeEnabled){ //they clicked and now it is recording
        			pachubeButton.setText("Stop Pachube");									
				}
				else{
					pachubeButton.setText("StartPachube");
				}
        	}        	
        });
        
        // Register this in model
        model.addObserver(this);
        
        mRecorder = new DataRecorder(this);
        mGeopos = new Geoposition(this,lonDisplay);
        mPachube = new pachubeUpdate(this,"key");
        //mRecorder.open();       
        
    }
    

 
    @Override
    protected void onStart() {
    	Log.d(TAG,"onStart()");
    	super.onStart();
    }
    
    @Override
    protected void onResume() {
    	Log.d(TAG,"onResume()");
    	super.onResume();
        
    	// If file descriptor to read from Arduino is already set, skip the remainder of onResume
    	if ( inputStream != null){
    		return;
    	}
        UsbAccessory[] accessories = usbManager.getAccessoryList();
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		Log.d(TAG,"onResume() : accessory got from USB enumeration : "+accessory);
		if (accessory != null) {
			if (usbManager.hasPermission(accessory)) {
				Log.d(TAG,"onResume() : We HAVE permission for accessory : "+accessory);
				openAccessory(accessory);
			} else {
				Log.d(TAG,"onResume() : We NEED permission for accessory : "+accessory);
				// TODO:
			}
		} else {
			Log.d(TAG, "onResume() : accessory is null");
			//TODO: For testing purposes, we start here the random generator
			//startRandomGenerator();
		}
    }
    
    @Override
    protected void onStop() {
    	Log.d(TAG,"onStop()");
    	super.onStop();
    }
    
    @Override
    protected void onPause() {
    	Log.d(TAG,"onPause()");
    	super.onPause();
    	if(state_recording){
    		state_recording=false;
    		mRecorder.closeFile();
    		rec.setText("record");
    	}
    }
    
    @Override
    protected void onDestroy() {
    	Log.d(TAG,"onDestroy()");
    	unregisterReceiver(bcastReceiver);
    	Log.d(TAG,"onDestroy() : call closeAccessory()");
    	// TODO: VERY IMPORTANT: closeAccessory() is needed so that we can restart
    	//	the app from launcher without problems.
    	closeAccessory();
    	super.onDestroy();
    	mRecorder.closeFile();
    }
    
    private void openAccessory(UsbAccessory accessory){
    	Log.d(TAG,"openAccessory() : "+accessory);
    	usbAccessory = accessory;
		fileDescriptor = usbManager.openAccessory(accessory);
		Log.d(TAG,"openAccessory() : after opening accessory "+accessory+" : filedescriptor is "+fileDescriptor);
		if (fileDescriptor != null) {
			usbAccessory = accessory;
			FileDescriptor fd = fileDescriptor.getFileDescriptor();
			inputStream = new FileInputStream(fd);
			Log.d(TAG, "openAccessory() : accessory opened : inputStream "+inputStream);
			Thread thread = new Thread(null, this, "GeigerCounter");
			thread.start();
			Log.d(TAG, "openAccessory() : thread started : "+thread);
		} else {
			Log.d(TAG, "openAccessory() : accessory open fail");
		}
    }
    
    private void closeAccessory(){
    	Log.d(TAG,"closeAccessory()");
		try {
			if (fileDescriptor != null) {
				fileDescriptor.close();
			}
		} catch (IOException e) {
		} finally {
			fileDescriptor = null;
			usbAccessory = null;
		}
    }

    private void startRandomGenerator(){
    	/** TODO: This is a TEMPORARY ramdom generator to be able to test offline
    	  * only for testing purposes, should be deleted in final code 
    	  * */
    	Log.d(TAG,"startRandomGenerator()");
    	Runnable randomRunnable = new RandomGenerator(model);
    	Thread randomThread = new Thread(randomRunnable);
    	randomThread.start();
    }


	public void run() {
		//Log.d(TAG,"run()");
		while (fileDescriptor != null){
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
						    cpm |= buffer[i+1] & 0xFF;
						    cpm <<= 8;
						    cpm |= buffer[i+2] & 0xFF;
						    //Log.d(TAG,"run() : (0x1) measure read "+cpm);
						    model.setIntervalCount(cpm);
						}
						i += 3;
						break;
					case 0x2:
						if (len >= 3) {
							seq = 0;
						    seq |= buffer[i+1] & 0xFF;
						    seq <<= 8;
						    seq |= buffer[i+2] & 0xFF;
						    //Log.d(TAG,"run() : (0x2) seq num "+seq);
						    model.setSeqNum(seq);
						    /**TODO: improve this
						    sequenceNumber = seq;
							handler.post(new Runnable(){
								public void run(){
									displaySeq();
								}
							});*/
						    
						}
						i += 3;
						break;
						
					default:
						//Log.d(TAG, "run() : unknown msg: " + buffer[i]);
						i = len;
						break;
					}
				}

			}
		}
		Log.d(TAG,"run() : exit");
	}
	
	private void displaySeq(){
		seqDisplay.setText(""+sequenceNumber);
	}



	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub
		//Log.d(TAG,"update() , called from model");
		
		// Call updateViews from Main thread
		handler.post(new Runnable(){
			public void run() {
				updateViews();	
				if(state_recording)
					recordValues();
			}});
		
	}
	
	private void recordValues(){
		//if(model.getSeqNum())
			if (state_locationEnabled && mGeopos.isFresh() ){
				mRecorder.addData(model.getCpm1min(), model.getSeqNum(), model.getUsv1min(), mGeopos.getLongitude(), mGeopos.getLatitude() );
			}else{
				mRecorder.addData(model.getCpm1min(), model.getSeqNum(),model.getUsv1min());				
			}
			
	}
	
	private void sendToPachube(){
		if (pachubeEnabled ){
			Log.d("qq","Sending message to pachube() pachubeEnagled");
			//mPachube.execute(Integer.toString(model.getCpm1min()), Double.toString(mGeopos.getLongitude()),Double.toString(mGeopos.getLatitude()));			
			mPachube = new pachubeUpdate(this,"key");
			if(state_locationEnabled)
				mPachube.execute(Integer.toString(model.getCpm1min()), Double.toString(mGeopos.getLongitude()),Double.toString(mGeopos.getLatitude()));
			else{
				mPachube.execute(Integer.toString(model.getCpm1min()), "0","0");
				Log.d("qq","Sending message to pachube() with position");
				}	
		}
		
	}
	
	private void updateViews(){
		//Log.d(TAG,"updateViews()");
		// Get values from model
		int cpm1min = model.getCpm1min();
		int seqNum = model.getSeqNum();
		float usv1min = model.getUsv1min();
		float usv10min = model.getUsv10min();
		if(pachubeEnabled){
			if(seqNum%30==0)
				sendToPachube();
		}
		
		currentCpmDisplay.setText(""+cpm1min);		
		seqDisplay.setText(""+seqNum);
		lonDisplay.setText("lon:"+mGeopos.getLongitude());
		currentUsvDisplay.setText(String.format("%.2f",usv1min));
		averageUsvDisplay.setText(String.format("%.2f",usv10min));
	}
}