package com.lacosaradioactiva.geiger.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.lacosaradioactiva.geiger.MainApp;
import com.lacosaradioactiva.geiger.R;

public class BaseActivity extends FragmentActivity {

	final public String TAG = getClass().getSimpleName();
	public static String APPNAME;

	
	MainApp ap;
	private static Context c;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (MainApp.DEVELOPER_MODE) {
			//StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().build());

			// StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder() //
			// .detectLeakedSqlLiteObjects() .detectAll() .penaltyLog()
			// .penaltyDeath() .build());
		}

		ap = (MainApp) getApplication();
		c = getContext();

		getPreferences(); 
		
	
	}

	

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	void getPreferences() {

		//set fullscreen depending on the settings 
		setFullScreen(PreferencesConstants.fullscreen); 
		//setScreenLock(PreferencesConstants.lock); 
		setScreenRotation("1"); 
	
	} 
	

	private void setScreenRotation(String rotation) {
		
		
		if (rotation.equals("0")) {
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else if (rotation.equals("1")) {
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else if (rotation.equals("2")) {
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}

		
	}

	private void setScreenLock(boolean isScreenLocked) {

		if (isScreenLocked) {
			this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		}

				
	}

	private void setFullScreen(boolean isFullScreen) {

		if (isFullScreen) {
			// this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			// requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);

		} else {

			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);

		}
		
	}

	@Override
	protected void onPause() {
		super.onPause();



	}

	@Override
	protected void onResume() {
		super.onResume();

		getPreferences();


	}

	@Override
	protected void onDestroy() {
		super.onDestroy();


	}


	public static Context getContext() {
		return c;

	}

	public void changeFragment(Fragment newFragment, int fragmentNum, boolean addBackStack) {

		Log.d(MainApp.TAG + ":" + TAG, "cambiado");

		setContentView(R.layout.mainfragmentcontainer);
		int id = R.id.fragmentchange;

		FrameLayout _frameLayout = (FrameLayout) findViewById(id);
		_frameLayout.setVisibility(View.VISIBLE);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (addBackStack == false) {
			ft.add(id, newFragment);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			// ft.addToBackStack(null);
		} else {
			ft.replace(id, newFragment);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.addToBackStack(null);
		}
		ft.commit();

	}

	public void clearStack() {

		FragmentManager fm = getSupportFragmentManager();
		for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
			fm.popBackStack();
		}
	}
}