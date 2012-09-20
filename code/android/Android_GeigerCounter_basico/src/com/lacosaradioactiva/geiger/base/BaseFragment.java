package com.lacosaradioactiva.geiger.base;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import com.lacosaradioactiva.geiger.MainApp;
import com.lacosaradioactiva.geiger.R;

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
	public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
		final int animatorId = (enter) ? R.anim.slide_in_right : R.anim.slide_in_right;
		final Animator anim = AnimatorInflater.loadAnimator(getActivity(), animatorId);
		anim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {

				setReady();
			}
		});

		return anim;
	}

	public void setReady() {
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