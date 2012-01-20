package com.dofideas.geiger2;

import java.util.LinkedList;
import java.util.Observable;

import android.util.Log;


public class GeigerModel extends Observable  {
	
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
				Log.d(TAG,"add() to queue: "+i+" Size : "+size+" Average: "+average);

				return false;   // False when queue is not full
			} else {
				//Queue already full
				// Update average value
				average = ( i + average*(maxSize) - super.getFirst() ) / maxSize;
				super.remove();
				super.add(i);
				Log.d(TAG,"add() to queue: "+i+" Size : "+size+" Average: "+average);
				return true;
			}
		}
		
		public float getAverage(){
			return average;
		}
	}
	
	private static final String TAG = "MODEL";
	private static final int MAX_QUEUE_SIZE = (int) (60 / MainActivity.SAMPLE_INTERVAL); // Correspond to 1 min

	private int cpm1min = 0;		 // Current measure in cpm, averaged 1 min
	private float usv1min = 0.0f;    // Current measure in uSv/h, averaged 1 min
	private int   seqNum = 0;
	private float usv60min=0.0f;
	
	private final Q q = new Q(MAX_QUEUE_SIZE); 
	
	private final Q q_long_term = new Q(60); 
	
	private LinkedList<Integer> q1;			// FIFO to hold cpm measures got from Arduino
	
	
	public GeigerModel(){
		super();
		//setChanged();	//TODO: This is temporal
	}
	
	// Getters
	public float getUsv1min(){
		return usv1min;
	}	

	public int getCpm1min(){
		return cpm1min;
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

		cpm1min = (int) (60 * averageCount / MainActivity.SAMPLE_INTERVAL);
		usv1min = cpm1min * MainActivity.CONVERSION_FACTOR;
		
		if(seqNum % (60/MainActivity.SAMPLE_INTERVAL) ==0){ //long term value each minute
			q_long_term.add(cpm1min);
			usv60min = q_long_term.getAverage() * MainActivity.CONVERSION_FACTOR; 
			Log.d("qq", "media 60 min"+ usv60min);			
		}
		
		
		
		setChanged();
		notifyObservers();
	}
	


	

}
