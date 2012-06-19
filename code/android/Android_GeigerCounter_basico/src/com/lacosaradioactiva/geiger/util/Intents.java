package com.lacosaradioactiva.geiger.util;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;

public class Intents {

	
	/**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 *
	 * @param context The application's environment.
	 * @param action The Intent action to check for availability. 
	 * such as  "com.google.zxing.client.android.SCAN" 
	 *
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	public static boolean isIntentAvailable(Context context, String action) {
	    final PackageManager packageManager = context.getPackageManager();
	    final Intent intent = new Intent(action);
	    List<ResolveInfo> list =
	            packageManager.queryIntentActivities(intent,
	                    PackageManager.MATCH_DEFAULT_ONLY);
	    return list.size() > 0;
	} 
	
	
	static public void openWeb(Context c, String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW); 
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		intent.setData(Uri.parse(url));
		c.startActivity(intent);

	}

	public static void webSearch(Context c, String text) {
		Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
		intent.setData(Uri.parse(text));
		c.startActivity(intent);
	} 

	static public void openMap(Context c, double longitude, double latitude) {

		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:" + longitude
				+ "," + latitude)); 
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		c.startActivity(intent);

	}

	static public void openSMSThread(Context c, String url) {

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.putExtra("address", "+34645865008");
		intent.setType("vnd.android-dir/mms-sms");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		c.startActivity(intent);

	}

	static public void sendEmail(Context c, String[] recipient, String subject, String msg) {
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("plain/text");
		intent.putExtra(android.content.Intent.EXTRA_EMAIL, recipient);
		intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(android.content.Intent.EXTRA_TEXT, msg);
		c.startActivity(Intent.createChooser(intent, "Send mail..."));

	}

	public static void dial(Context c) {
		Intent intent = new Intent(Intent.ACTION_DIAL);
		c.startActivity(intent);
	}

	public static void call(Context c, String number) {
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + number));
		c.startActivity(intent);
	}

	public static void recordAudio(Context c, int requestCode) { 

		Intent recordIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
		//c.startActivityForResult(recordIntent, requestCode);

	} 
	

}
