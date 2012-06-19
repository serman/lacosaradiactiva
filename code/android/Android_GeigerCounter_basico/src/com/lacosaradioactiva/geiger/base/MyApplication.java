package com.lacosaradioactiva.geiger.base;


import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

	public static String TAG; 
	public static String NAME;
	private static Context c;
	private static String myState;
	private MyPreferences myPref; 
	public static Activity activityON; 


	@Override
	public void onCreate() {
		super.onCreate();

		c = getApplicationContext();
	
		myPref = new MyPreferences(this);
		myPref.readPreferences();
	

	}

	public final static String getState() {
		return myState;
	}

	public final void setState(String s) {
		myState = s;
	}

	public final static String getName() {

		return NAME;
	}

	/**
	 * @return the app
	 */
	public final static Context getContext() {
		return c;
	}

}
