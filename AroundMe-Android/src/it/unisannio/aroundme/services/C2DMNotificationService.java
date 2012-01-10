package it.unisannio.aroundme.services;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.activities.ProfileActivity;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;
import android.app.IntentService;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * {@link IntentService} che si occupa di le mostrare notifiche riguardando la presenza
 * di Utenti compatibili nei dintorni
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 * @author Giuseppe Fusco <gfeldiablo@gmail.com>
 */
public class C2DMNotificationService extends IntentService{
	
	private final int NOTIFICATION_ID = 1;
	private static int unreadNotifications;
 
	
	public C2DMNotificationService() {
		super("C2DMNotificationService");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	
	@Override
	protected void onHandleIntent(Intent intent) {
			try {
				User user = UserQuery.single(intent.getLongExtra("userId", 0)).call();
				Context context = getApplicationContext();
				Intent profileActivityIntent = new Intent(context, ProfileActivity.class);
				profileActivityIntent.putExtra("userId", user.getId());
				Intent listActivityIntent = new Intent(context, ListActivity.class);
				NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				String tickerTitle, contentTitle, contentText;
				PendingIntent pendingIntent = PendingIntent.getActivity(context, 5,	profileActivityIntent, PendingIntent.FLAG_NO_CREATE);
				if(pendingIntent==null){ //FIXME Gestione della notifica in base al numero di non lette
					tickerTitle = "C'è gente intorno a te!"; //FIXME Esternalizzare le stringhe
					contentTitle = "Una nuova persona da conoscere!";
					contentText = user.getName();
					pendingIntent = PendingIntent.getActivity(context, 5, profileActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					unreadNotifications  = 1;
				}else{
					unreadNotifications++;
					tickerTitle = "C'è gente intorno a te!";
					contentTitle = "Nuove persone da conoscere!";
					contentText = unreadNotifications+" nuove persone nelle vicinanze!";
					notificationManager.cancelAll();
//					PendingIntent.getActivity(context, 5, profileActivityIntent, PendingIntent.FLAG_CANCEL_CURRENT).cancel();
					pendingIntent = PendingIntent.getActivity(context, 5, listActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					
				}
				notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				Notification notification = new Notification(R.drawable.ic_notification, tickerTitle, System.currentTimeMillis());

				// Nasconde la notifica dopo che è stata selezionata
				notification.flags |= Notification.FLAG_AUTO_CANCEL;

				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
				if(prefs.getBoolean("notification.active", true)){
					notification.defaults |= Notification.DEFAULT_LIGHTS;
					notification.defaults |= Notification.DEFAULT_SOUND; //FIXME Prendere i valori dalle preferenze
//					notification.sound = Uri.parse(prefs.getString("notification.sound", "DEFAULT_RINGTONE_URI"));
//					notification.sound = Uri.parse("DEFAULT_RINGTONE_URI");
					if(prefs.getBoolean("notification.vibrate", true));
					notification.defaults |= Notification.DEFAULT_VIBRATE;
				}
				notification.setLatestEventInfo(context, contentTitle, contentText, pendingIntent);
				notificationManager.notify(NOTIFICATION_ID, notification);
				
			} catch (Exception e) {
				Log.w("C2DMNotificationService", e);
			}	
		}
		

	

}
