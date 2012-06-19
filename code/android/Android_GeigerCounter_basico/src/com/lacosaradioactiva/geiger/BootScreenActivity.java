package com.lacosaradioactiva.geiger;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

import com.lacosaradioactiva.geiger.R;
import com.lacosaradioactiva.geiger.base.BaseActivity;

public class BootScreenActivity extends BaseActivity { 

	protected int _splashTime = 1250;
	protected Handler _exitHandler = null;
	protected Runnable _exitRunnable = null;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.bootscreen);
		// Runnable exiting the splash screen and launching the menu
		
		
		_exitRunnable = new Runnable() {
			public void run() {
				exitSplash(); 
			}
		};

		// Run the exitRunnable in in _splashTime ms
		_exitHandler = new Handler();
		_exitHandler.postDelayed(_exitRunnable, _splashTime);
	}

	@Override
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
		finish(); 
		startActivity(new Intent(getApplicationContext(), com.lacosaradioactiva.geiger.MainActivity.class));  
	} 
} 
