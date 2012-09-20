package com.lacosaradioactiva.geiger;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class BootScreenFragment extends Fragment {

	protected int _splashTime = 2250;
	protected Handler _exitHandler = null;
	protected Runnable _exitRunnable = null;
	private View v;

	/**
	 * Called when the activity is first created.
	 * 
	 * @return
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		v = inflater.inflate(R.layout.bootscreen, container, false);
		return v;

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Runnable exiting the splash screen and launching the menu

		_exitRunnable = new Runnable() {
			public void run() {
				exitSplash();
			}
		};

		// Run the exitRunnable in in _splashTime ms
		_exitHandler = new Handler();

		if (MainApp.isRunning() == false) {
			_exitHandler.postDelayed(_exitRunnable, _splashTime);
			MainApp.setRunning(true); 
		} 
		

	}

	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// Remove the exitRunnable callback from the handler queue
			_exitHandler.removeCallbacks(_exitRunnable);
			// Run the exit code manually
			exitSplash();
		}
		return true;
	}

	private void exitSplash() {
		if (MainApp.isRunning() == false) {
			((MainActivity) getActivity()).changePage();
		}
	}
}
