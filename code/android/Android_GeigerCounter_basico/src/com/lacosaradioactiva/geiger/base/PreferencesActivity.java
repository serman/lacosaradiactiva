/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lacosaradioactiva.geiger.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.lacosaradioactiva.geiger.MainApp;
import com.lacosaradioactiva.geiger.R;

public class PreferencesActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	final static String TAG = "Preferences";
	protected static final int ABOUT_DIALOG_ID = 0;
	protected static final int ABOUT_DIALOG_ID_1 = 1;
	MainApp ap;
	Context c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
		ap = (MainApp) getApplication();
		c = getApplicationContext();

		stop();

		Preference aboutPreference = (Preference) findPreference("cAbout");
		aboutPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {

				showDialog(ABOUT_DIALOG_ID_1);

				return true;
			}
		});

		PackageManager pm = getPackageManager();
		boolean hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
		boolean hasLocation = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION);
		boolean hasCompass = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
		boolean hasMicrophone = pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
		boolean hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
		boolean hasLightSensor = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_LIGHT);
		boolean hasProximitySensor = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_PROXIMITY);
	

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		PreferencesConstants.loadSettingPreferences(this);

		// ap.start();

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	private void stop() {

		if (MainApp.isRunning() == true) {
		

		}

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {

		Log.d(MainApp.TAG + TAG, "" + arg1);
		// ap.getPreferences();

	}

	

}
