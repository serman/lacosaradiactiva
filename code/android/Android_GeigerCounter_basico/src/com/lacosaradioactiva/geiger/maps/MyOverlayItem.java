package com.lacosaradioactiva.geiger.maps;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MyOverlayItem extends OverlayItem {

	String nid; 
	
	
	public MyOverlayItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet); 
		// TODO Auto-generated constructor stub 
	} 
	
	public void addNid(String nid) { 
		this.nid = nid; 
	} 
	
	public String getNid() { 
		
		return nid; 
	}

}
