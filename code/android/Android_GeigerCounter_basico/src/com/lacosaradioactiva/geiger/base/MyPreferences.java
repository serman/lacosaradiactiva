package com.lacosaradioactiva.geiger.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class MyPreferences {

	Context _c;

	final static String WELCOME_MESSAGE = "welcome_message";
	final static String TIMES_USED = "times_used";
	final static String ALLOW_USE = "allow_use";
	final static String NEW_UPDATE = "new_update";
	final static String HAS_CRASHED = "has_crashed";

	private String _pWelcomeMessage; 
	private int _pTimesUsed; 
	private int _pAllow; 
	private int _pNewUpdate; 
	private int _pHasCrashed; 

	SharedPreferences _settings; 

	public MyPreferences(Context c) {
		this._c = c;

		_settings = _c.getSharedPreferences("preferences", _c.MODE_PRIVATE);
	}

	public void readPreferences() {

		// leo las preferencias
		_pWelcomeMessage = _settings.getString(WELCOME_MESSAGE, "Welcome"); 
		_pTimesUsed = _settings.getInt(TIMES_USED, 1);
		_pAllow = _settings.getInt(ALLOW_USE, 1); 
		_pNewUpdate = _settings.getInt(NEW_UPDATE, 0); 
		_pHasCrashed = _settings.getInt(HAS_CRASHED, 0); 

		Log.d("prefs", _pWelcomeMessage + 
				" " + _pTimesUsed + 
				" " + _pAllow +
				" " + _pNewUpdate + 
				" " + _pHasCrashed + "" 
				); 

	}

	public void savePreferences() {

		SharedPreferences.Editor editor = _settings.edit();

		editor.putString(WELCOME_MESSAGE, _pWelcomeMessage); 
		editor.putInt(TIMES_USED, _pTimesUsed + 1);
		editor.putInt(ALLOW_USE, _pAllow);
		editor.putInt(ALLOW_USE, _pNewUpdate);
		editor.putInt(ALLOW_USE, _pHasCrashed); 
		editor.commit(); 

	}  
	
	
}
