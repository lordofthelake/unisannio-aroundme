package it.unisannio.aroundme.services;

import java.util.ArrayList;
import java.util.Collection;

import it.unisannio.aroundme.activities.ProfileActivity;
import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.User;
import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.c2dm.C2DMBaseReceiver;
import com.google.android.c2dm.C2DMBroadcastReceiver;

/**
 * @author Giuseppe Fusco <gfeldiablo@gmail.com>
 *
 */
public class C2DMMessageReceiver extends BroadcastReceiver {

	private User user;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.w("C2DM", "Message Receiver called");
		if ("com.google.android.c2dm.intent.RECEIVE".equals(action)) {
			Log.w("C2DM", "Received message");
			long userId = Long.parseLong(intent.getStringExtra("userId"));
			// Query per ottenere l'utente tramite il suo id
			
			createNotification(context, user.getName());

		}
	}

	public void createNotification(Context context, String userName) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_app_actionbar,
				"Amici nei dintorni", System.currentTimeMillis());
		// Nasconde la notifica dopo che � stata selezionata
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND;

		Intent intent = new Intent(context, ProfileActivity.class);
		intent.putExtra("userId", user.getId());
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		notification.setLatestEventInfo(context, user.getName(),
				"Around you", pendingIntent);
		notificationManager.notify(0, notification);

	}

}
