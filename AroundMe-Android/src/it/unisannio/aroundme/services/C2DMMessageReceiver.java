package it.unisannio.aroundme.services;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.activities.ProfileActivity;
import it.unisannio.aroundme.model.User;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author Giuseppe Fusco <gfeldiablo@gmail.com>
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class C2DMMessageReceiver extends BroadcastReceiver {
	
	private final int NOTIFICATION_ID = 1;

	private User user;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.w("C2DM", "Message Receiver called");
		if ("com.google.android.c2dm.intent.RECEIVE".equals(action)) {
			Log.w("C2DM", "Received message");
			createNotification(context,  getUser(Long.parseLong(intent.getStringExtra("userId"))).getName());
		}
	}
	
	private void createNotification(Context context, String userName) {
		
		//FIXME Non è stato gestita una notifica diversa a seconda del numero di notifiche non lette
		// Ora la notifica viene semplicemente sostituita con la piu' nuova 
		NotificationManager notificationManager = (NotificationManager) context	.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_notification,"Qualcuno nei dintorni", System.currentTimeMillis());
		
		// Nasconde la notifica dopo che è stata selezionata
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if(prefs.getBoolean("notification.active", true)){
			notification.defaults |= Notification.DEFAULT_LIGHTS;
			notification.sound = Uri.parse(prefs.getString("notification.sound", "DEFAULT_RINGTONE_URI"));
			if(prefs.getBoolean("notification.vibrate", true));
				notification.defaults |= Notification.DEFAULT_VIBRATE;
		}
		Intent intent = new Intent(context, ProfileActivity.class);
		intent.putExtra("userId", user.getId());
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,	intent, 0);
		notification.setLatestEventInfo(context, user.getName(), "Around you", pendingIntent);
		notificationManager.notify(NOTIFICATION_ID, notification);
		
	}
	
	private User getUser(long id){
		User user = null;
		//TODO query al GAE per ottenere l'utente
		return user;
	}

}