package com.lacosaradioactiva.geiger.data;

import java.util.LinkedList;
import java.util.Observable;

import android.util.Log;


public class GeigerModel extends Observable  {
	//TODO: improve precision of calculations? (cpm should be calculated then in float, not int)
	//TODO: improve: do not show average value until 10 min has passed?
	
	class Q extends LinkedList<Integer>{
		
		private int size = 0;
		private final int maxSize;
		private float average =0.0f;
		private float longtermAverage=0.0f;
		
		private Q(int maxSize){
			super();
			this.maxSize = maxSize;
		}
		
		public boolean add(Integer i){
			/** Returns true if Q is full */
			if ( size < maxSize ){
				// Queue is not full
				super.add(i);	// Add to the end of the queue
				size ++;	
				average = ( i + average*(size-1) ) / size; // Update average value
				//Log.d(TAG,"add() to queue: "+i+" Size : "+size+" Average: "+average);

				return false;   // False when queue is not full
			} else {
				//Queue already full
				// Update average value
				average = ( i + average*(maxSize) - super.getFirst() ) / maxSize;
				super.remove();
				super.add(i);
				//Log.d(TAG,"add() to queue: "+i+" Size : "+size+" Average: "+average);
				return true;
			}
		}
		
		public float getAverage(){
			return average;
		}
	}
	
	static final float CONVERSION_FACTOR = (float) 0.00812037037037;		// Geiger Tube Conversion Factor. Hardcoded: TODO: make configurable?
	// Conversion factor from manufacturer http://www.cooking-hacks.com/index.php/documentation/tutorials/geiger-counter-arduino-radiation-sensor-board
	// J305beta Geiger Tube - North Optic
	private static final String TAG = "MODEL";




	private int cpm1min = 0;		 // Current measure in cpm, averaged 1 min
	private float usv1min = 0.0f;    // Current measure in uSv/h, averaged 1 min
	private int cpm10min = 0;
	private float usv10min = 0.0f;
	private int   seqNum = 0;
	private float usv60min=0.0f;
	private int counter = 0;		// Counter to trigger secondary queue


	

	static final float SAMPLE_INTERVAL = 0.500f;    // (secs) Hardcoded. TODO: make configurable?
	private static final int INTEGRATION_INTERVAL = 60; // In seconds
	//private static final int MAX_QUEUE_SIZE = (int) (60 / MainActivity.SAMPLE_INTERVAL); // Correspond to 1 min
	private static final int MAX_QUEUE_SIZE = (int) (INTEGRATION_INTERVAL / SAMPLE_INTERVAL);  // Primary queue 
	private static final int MAX_QUEUE_SIZE_2 = 10;   // Num of intervals averaged, for secondary queue
	


	private final Q q_long_term = new Q(60); 
	private LinkedList<Integer> q1;			// FIFO to hold cpm measures got from Arduino
	private final Q q = new Q(MAX_QUEUE_SIZE);  // Main queue for holding primary measured raw values
	private final Q q2 = new Q(MAX_QUEUE_SIZE_2);  // Secondary queue for calculating average (N measures)
	
	//osc 
	public OSC osc;

	
	public GeigerModel(){
		super();
		
		osc = new OSC(); 
		//setChanged();	//TODO: This is temporal
	}
	
	// Getters
	public float getUsv1min(){
		return usv1min;
	}	
	public int getCpm1min(){
		return cpm1min;
	}
	public float getUsv10min(){
		return usv10min;
	}
	public int getCpm10min(){
		return cpm10min;
	}
	public int getSeqNum(){
		return seqNum;
	}

	// Setters
	public void setIntervalCount(int c){
		q.add(c);
		calculateModel();
	}
	public void setSeqNum(int s){
		seqNum = s;
		// TODO: calculateModel();
	}
	
	private void calculateModel(){

		float averageCount = q.getAverage();

		cpm1min = (int) (60 * averageCount / SAMPLE_INTERVAL);
		usv1min = cpm1min * CONVERSION_FACTOR;
		
		if(seqNum % (60/SAMPLE_INTERVAL) ==0){ //long term value each minute
			q_long_term.add(cpm1min);
			usv60min = q_long_term.getAverage() * CONVERSION_FACTOR; 
			Log.d("qq", "media 60 min"+ usv60min);			
		}

		if ( counter >= MAX_QUEUE_SIZE) {
			counter = 0;	
			q2.add(cpm1min);
			cpm10min = (int) q2.getAverage();

			usv10min = cpm10min * CONVERSION_FACTOR;
		} else {
			counter++;
		}

		
		setChanged(); 
		
		if (osc.isEnabled()) {			
			osc.sendParam(cpm1min, cpm10min, usv1min, usv10min); 
		} 
		
		notifyObservers();
	}
	


	

}
