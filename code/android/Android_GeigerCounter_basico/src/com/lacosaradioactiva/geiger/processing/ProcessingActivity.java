package com.lacosaradioactiva.geiger.processing;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.lacosaradioactiva.geiger.R;

public class ProcessingActivity extends FragmentActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainforfragments);

		addProcessingSketch(new ProcessingSketch(), R.id.f1); 

	} 

	public void addProcessingSketch(ProcessingSketch processing, int fragmentPosition) {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(fragmentPosition, processing);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

	}  
	
}
