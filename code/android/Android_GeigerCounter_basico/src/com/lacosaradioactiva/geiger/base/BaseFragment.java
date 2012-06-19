package com.lacosaradioactiva.geiger.base;

import com.lacosaradioactiva.geiger.MainApp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment { 

	final public String TAG = getClass().getSimpleName();

	public static Activity ac; 
	public static MainApp ap; 
	public static Context c; 
	public static String APPNAME;


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	} 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ac = getActivity(); 
		ap = (MainApp) getActivity().getApplication(); 
		c = getActivity().getBaseContext(); 
		
	}

	

	@Override
	public void onPause() {
		super.onPause();



	};

	@Override
	public void onResume() {
		super.onResume();


	};

	@Override
	public void onDestroy() {
		super.onDestroy();

	}


}