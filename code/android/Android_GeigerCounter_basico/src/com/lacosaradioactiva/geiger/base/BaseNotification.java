package com.lacosaradioactiva.geiger.base;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class BaseNotification {

	public static int HELLO_ID = 1;

	Context c;
	NotificationManager mNotificationManager;

	public BaseNotification(Context context) {
		c = context;

		String ns = c.NOTIFICATION_SERVICE;
		mNotificationManager = (NotificationManager) c.getSystemService(ns);

	}

	// this part is in the onUpdate part of the widget
	public void show(Class<?> cls, int icon, String text, String title) {
		CharSequence tickerText = "OSCdroid is started"; // this text appears in
															// the

		// notification bar with the icon, when the notification launches
		long when = System.currentTimeMillis(); // you can display a
		// notification now, or set a different time in the future
		Notification notification = new Notification(icon, tickerText, when);
		long[] vibrate = { 0, 100, 200, 300 };
		notification.vibrate = vibrate;

		// Flashing the LED If we want to flash the LED then we can use the
		// defaults flag to DEFAULT_LIGHTS.
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		// notification.ledARGB = Color.RED;
		// notification.ledOffMS = 300;
		// notification.ledOnMS = 300;

		notification.defaults |= Notification.DEFAULT_SOUND;
		// To use a different sound with your notifications, pass a Uri
		// reference to the sound field.
		// notification.sound =
		// Uri.parse("file:///sdcard/notification/ringer.mp3");
		// In the next example, the audio file is chosen from the internal
		// notification.sound =
		// Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6");

		CharSequence contentTitle = title;
		CharSequence contentText = text;

		Intent notificationIntent = new Intent(c, cls);
		PendingIntent contentIntent = PendingIntent.getActivity(c, 0, notificationIntent, 0);
		notification.setLatestEventInfo(c, contentTitle, contentText, contentIntent);

		mNotificationManager.notify(HELLO_ID, notification);
		// mNotificationManager.cancel(null, HELLO_ID);

	}

	public void hide() {
		mNotificationManager.cancel(null, HELLO_ID);

	}

}
