package com.lacosaradioactiva.geiger.processing;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.lacosaradioactiva.geiger.R;

public class ProcessingActivity extends FragmentActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainforfragments);

		addProcessingSketch(new ProcessingSketch(), R.id.f1); 

	} 

	public void addProcessingSketch(Fragment processing, int fragmentPosition) {

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(fragmentPosition, processing);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

	}  
	
}
