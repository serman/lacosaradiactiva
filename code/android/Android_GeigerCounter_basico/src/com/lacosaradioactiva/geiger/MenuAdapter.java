package com.lacosaradioactiva.geiger;

import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Parcelable;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one
 * of the primary sections of the app.
 */
public class MenuAdapter extends FragmentPagerAdapter {

	private List<Fragment> fragments;
	
	public MenuAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm); 
		this.fragments = fragments; 
		
	}

	@Override
	public int getCount() {
		Log.d("el tama–o es: ", "" + fragments.size()); 
		return fragments.size();
	}

	@Override
	public Fragment getItem(int position) { 
		Fragment ft = (Fragment) fragments.get(position); 
	

		return ft; 
	} 
	
	public void addFragment(Fragment ft) {
		fragments.add(ft); 
		
	} 
	

	@Override
	public void destroyItem(View pager, int position, Object view) {
	}

	@Override
	public void startUpdate(View arg0) {

	}

	@Override
	public void finishUpdate(View arg0) {

	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {

	}

	@Override
	public Parcelable saveState() {
		return null;
	}


}
