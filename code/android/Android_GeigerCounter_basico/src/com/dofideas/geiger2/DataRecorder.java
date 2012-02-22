package com.dofideas.geiger2;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import android.text.format.DateUtils;
import android.content.Context;
import java.text.SimpleDateFormat;

import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

public class DataRecorder {

	private Context contextRef;
	private DataOutputStream fos;
	DataRecorder(Context andContext){
		this.contextRef=andContext;		
	}
	boolean checkForCard(){
		//checking for media CArd
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		Log.d("qq","mExternalStorageWriteable: "+mExternalStorageWriteable);
		return mExternalStorageWriteable;
		
	}
	
	boolean open(){
		if(checkForCard()){
			if(openFile()){
				Log.d("qq","open: ");
				return true;
			}
		}
		return false;
	}
	boolean openFile(){
		File folder1=  contextRef.getExternalFilesDir(null);
		try {
			Log.d("qq","open file " + folder1.getCanonicalPath());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm"); 
		String dateStr = sdf.format(cal.getTime());		
		
		File file = new File(folder1, dateStr+".txt");
		
		try {			
			fos = new DataOutputStream( new FileOutputStream(file) );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	boolean closeFile(){
		Log.d("qq","closing file");
		try {
			fos.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	boolean addData (int cpm, int seq, float sieverts) {
		try {
			fos.writeUTF(seq + "," + cpm + ","+sieverts+",0,0"+ "\n" );
		} catch (IOException e) {
				Log.d("qq","error in addDAta: ");
				e.printStackTrace();
				return false;			
		}	
		return true;
	}
	boolean addData (int cpm, int seq, float sieverts,  double lon, double lat) {
		try {
			fos.writeUTF(seq + "," + cpm +","+sieverts+ ","+ lon+ "," +lat+ "\n" );
		} catch (IOException e) {
				Log.d("qq","error in addDAta: ");
				e.printStackTrace();
				return false;			
		}	
		return true;
	}
}
