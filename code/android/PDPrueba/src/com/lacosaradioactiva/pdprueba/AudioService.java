package com.lacosaradioactiva.pdprueba;

import java.io.File;
import java.io.IOException;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.service.PdService;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.PdListener;
import org.puredata.core.utils.IoUtils;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class AudioService {

	public static PdService pdService = null;

	public static final ServiceConnection pdConnection = new ServiceConnection() {
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder) service).getService();
			
			try {
				initPd();
				loadPatch();
			} catch (IOException e) { 
				finish(); 
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

	};

	private static PdUiDispatcher dispatcher;

	private static void initPd() throws IOException {

		// configure audio glue
		int sampleRate = AudioParameters.suggestSampleRate();
		pdService.initAudio(sampleRate, 1, 2, 10.0f); 
		start(); 
		
		// create and install the dispatcher
		dispatcher = new PdUiDispatcher();

		dispatcher.addListener("pitch", new PdListener.Adapter() {
			@Override
			public void receiveFloat(String source, float x) {
				Log.i("lalal", "pitch: " + x);

			}

		});

		PdBase.setReceiver(dispatcher);

	}

	static void start() {
		if (!pdService.isRunning()) {
			Intent intent = new Intent(pdService,
					MainActivity.class);
			pdService.startAudio(intent, R.drawable.icon,
					"GuitarTuner", "Return to GuitarTuner.");
		}
	}

	protected static void triggerNote(int value) {

		int m = (int) (Math.random() * 5); 
		Log.d("qq", "" + m); 
		PdBase.sendFloat("midinote", value); //m);
		PdBase.sendBang("trigger"); 


	} 
	
	protected static void changeSpeed(int value) { 
		PdBase.sendFloat("tempo", value); //m);		
		
	} 

	
	protected static void finish() {
	}

	private static void loadPatch() throws IOException {
		
		File dir = pdService.getFilesDir();
		IoUtils.extractZipResource(pdService.getResources().openRawResource(R.raw.tuner),
				dir, true);
		File patchFile = new File(dir, "tuner/sampleplay.pd");
		Log.d("qq", patchFile.getAbsolutePath()); 
		PdBase.openPatch(patchFile.getAbsolutePath());
	}

}
