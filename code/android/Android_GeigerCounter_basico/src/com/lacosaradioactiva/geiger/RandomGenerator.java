package com.lacosaradioactiva.geiger;

import android.util.Log;
import java.util.Random;

public class RandomGenerator implements Runnable {
	
	private static final String TAG = "RANDOM";
	private final Random random = new Random();
	
	GeigerModel model;
	
	public RandomGenerator(GeigerModel model){
		this.model = model;
	}

	public void run() {
		Log.d(TAG,"run()");
		int count=0;
		int seq=0;;
		while (true){
			// Set "faked" values
			count = random.nextInt(3);
			Log.d(TAG,"Random cpm = "+count);
			seq ++;
			Log.d(TAG,"seq = "+seq);
			
			// Push values into model
			model.setIntervalCount(count);
			model.setSeqNum(seq);
			// Wait for 500 ms
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
