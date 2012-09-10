package com.lacosaradioactiva.geiger.base;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;

import com.lacosaradioactiva.geiger.R;

public class PreferencesConstants {

	//
	public static String TIMES = "ctimes";
	public static String URL = "url";
	public static String UPDATED = "cupdated";

	//
	public static String ROTATE = "cRotateOption";
	public static String FULLSCREEN = "cFullscreen";
	public static String LOCK = "cLock";

	// cosas
	public static int times;
	public static int updated;
	public static String url;

	// settings
	public static int currenttime;
	

	public static boolean fullscreen;
	public static boolean lock;
	public static String rotate;
	

	public static int version = 1;

	public static void loadSettingPreferences(Context c) {

		PreferenceManager.setDefaultValues(c, R.xml.preferences, false);
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(c);
		p.edit().clear();
		p.edit().commit();

		PreferencesConstants.fullscreen = p.getBoolean(PreferencesConstants.FULLSCREEN, true);
		PreferencesConstants.lock = p.getBoolean(PreferencesConstants.LOCK, true);
		PreferencesConstants.rotate = p.getString(PreferencesConstants.ROTATE, "2");
		

	}

	public static void loadCustomPreferences(Context c) {

		SharedPreferences settings = c.getSharedPreferences("preferen", c.MODE_PRIVATE); 
		PreferencesConstants.times = settings.getInt(PreferencesConstants.TIMES, 1);

		try {
			PreferencesConstants.version = c.getPackageManager().getPackageInfo(
					"com.lacosaradiactiva.geiger", 0).versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PreferencesConstants.updated = settings.getInt(PreferencesConstants.UPDATED, 1);
		SharedPreferences.Editor editor = settings.edit();
		PreferencesConstants.times += 1;
		editor.putInt(PreferencesConstants.TIMES, PreferencesConstants.times);
		editor.commit(); 

	}

	public static void saveCustomPreferences(Context c) {

		SharedPreferences settings = c.getSharedPreferences("preferen", c.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();

		editor.putInt(PreferencesConstants.UPDATED, PreferencesConstants.version);
		editor.commit();

	} 


	public void clear(Context c) { 
		Editor editor = c.getSharedPreferences("preferen", Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit(); 
	} 
	

}
