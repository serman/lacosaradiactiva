package com.lacosaradioactiva.geiger;

import java.io.File;

import android.os.Environment;

import com.lacosaradioactiva.geiger.base.BaseNotification;
import com.lacosaradioactiva.geiger.base.MyApplication;

public class MainApp extends MyApplication {

	private static boolean isRunning = false;
	public static boolean DEVELOPER_MODE = false;

	BaseNotification myNotification;

	public static final String ROOT_FOLDER = Environment.getExternalStorageDirectory() + File.separator
			+ "GeigerCounter" + File.separator;
	
	public  static final int PHONE = 0;
	public static final int TABLET = 1;
	//public static final int MODE = TABLET; 
	public static final int MODE = PHONE; 
	
	@Override
	public void onCreate() {
		super.onCreate();

		TAG = getString(R.string.app_name);
		NAME = getString(R.string.app_name);		

		// load preferences and settings
		// PreferencesConstants.loadSettingPreferences(getApplicationContext());
		// PreferencesConstants.loadCustomPreferences(this);
		myNotification = new BaseNotification(this);

	}

	@Override
	public void onTerminate() {
		super.onTerminate();

		myNotification.hide();

	}

	public static boolean isRunning() {

		return isRunning;
	}

	public static void setRunning(boolean b) {
		isRunning = true;

	}

}
