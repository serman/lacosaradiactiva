package com.lacosaradioactiva.geiger;

import java.util.List;
import java.util.Vector;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;

import com.lacosaradioactiva.geiger.base.BaseActivity;
import com.lacosaradioactiva.geiger.processing.ProcessingSketch;

public class MainActivity extends BaseActivity {

	private static final String TAG = "MainActivity";

	private static final int FIRST_ITEM = 0;

	MenuAdapter cpa;
	ViewPager mViewPager;

	private ProcessingSketch proc;

	// View attributes

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate()");

		setContentView(R.layout.main);

		initialisePaging();

		// startActivity(new Intent(getApplicationContext(),
		// PreferenceActivity.class));

	}

	/**
	 * Initialise the fragments to be paged
	 */
	private void initialisePaging() {

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager_menu);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		List<Fragment> fragments = new Vector<Fragment>();
		fragments.add(Fragment.instantiate(this, BootScreenFragment.class.getName()));
		// fragments.add(Fragment.instantiate(this,
		// WebViewFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, CounterFragment.class.getName()));
		//fragments.add(Fragment.instantiate(this, EmptyFragment.class.getName()));
		// fragments.add( Fragment.instantiate(this,
		// ProcessingSketch.class.getName()));

		this.cpa = new MenuAdapter(super.getFragmentManager(), fragments);

		// cpa.setReferenceView(mViewPager);
		mViewPager.setAdapter(this.cpa);
		mViewPager.setCurrentItem(FIRST_ITEM);

		//addProcessingSketch(new CameraFragment(), R.id.f1);		
		// addProcessingSketch(new VideoPlayerFragment(), R.id.f1);
		// addProcessingSketch(new ProcessingSketch(), R.id.f1);
	
		//proc = new ProcessingSketch(); 
		//addProcessingSketchSide(proc, R.id.fragmentProcessing);

	}

	public void changePage() {
		mViewPager.setCurrentItem(1, true);
	}

	public void addProcessingSketch(Fragment processing, int fragmentPosition) {

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(fragmentPosition, processing);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

	}

	public void addProcessingSketchSide(Fragment fragment, int fragmentPosition) {

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		//ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
		ft.replace(fragmentPosition, fragment);
		ft.show(fragment); 

		
		//if (fragment.isHidden()) {
		//	ft.show(fragment);
		//} else {
		//	ft.hide(fragment);

		//}
		ft.commit();

	}

	@Override
	protected void onStart() {
		Log.d(TAG, "onStart()");
		super.onStart();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();

	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop()");
		super.onStop();
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause()");
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy()");
		super.onDestroy();
	}

	public ViewPager getViewPager() {
		return mViewPager;
	}
}