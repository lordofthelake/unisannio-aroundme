package it.unisannio.aroundme;

import it.unisannio.aroundme.services.NotificationService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author Giuseppe Fusco <gfeldiablo@gmail.com>
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class C2DMMessageReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.w("C2DM", "Message Receiver called");
		if ("com.google.android.c2dm.intent.RECEIVE".equals(action)) {
			Log.w("C2DM", "Received message");

			Intent notificationIntent = new Intent(context, NotificationService.class);
			notificationIntent.putExtra("userId", Long.parseLong(intent.getStringExtra("userId")));
			context.startService(notificationIntent);

		}
	}
}