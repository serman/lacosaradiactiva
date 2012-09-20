package com.lacosaradioactiva.geiger;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class EmptyFragment extends Fragment {

	private View v;

	/**
	 * Called when the activity is first created.
	 * 
	 * @return
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		v = inflater.inflate(R.layout.emptylayout, container, false);
		return v;

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);



	}

	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
		

		}
		return true;
	}

	
}
