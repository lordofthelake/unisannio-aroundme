package it.unisannio.aroundme.services;

import java.util.ArrayList;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.activities.ListViewActivity;
import it.unisannio.aroundme.activities.ProfileActivity;
import it.unisannio.aroundme.model.UserQuery;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

	private final static int NOTIFICATION_ID = 1;
	private static ArrayList<Long> unreadNotifications = new ArrayList<Long>();

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
			long userId = intent.getLongExtra("userId", 0);
			unreadNotifications.add(userId);
			Context context = getApplicationContext();
			String contentTitle, contentText;
			PendingIntent pendingIntent;

			if(unreadNotifications.size()  == 1){
				contentTitle = getString(R.string.notif_contentTitle_sng);
				contentText = UserQuery.single(userId).call().getName();
				Intent profileIntent = new Intent(context, ProfileActivity.class);
				profileIntent.putExtra("userId", userId);
				profileIntent.putExtra("fromNotification", true);
				pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, profileIntent, 0);
			}else{
				contentTitle = getString(R.string.notif_contentTitle_plr);
				contentText = String.format(getString(R.string.notif_contentText), unreadNotifications.size());
				Intent listIntent = new Intent(context, ListViewActivity.class);

				long[] userIds = new long[unreadNotifications.size()];
				for(int i = 0; i<unreadNotifications.size(); i++)
					userIds[i] = unreadNotifications.get(i);
				listIntent.putExtra("userIds", userIds);
				
				pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, listIntent, 0);
			}

			
			Notification notification = new Notification(R.drawable.ic_notification, getString(R.string.notif_tickerTitle), System.currentTimeMillis());

			// Nasconde la notifica dopo che e' stata selezionata
			notification.flags |= Notification.FLAG_AUTO_CANCEL;

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			if(prefs.getBoolean("notification.active", true)){
				notification.defaults |= Notification.DEFAULT_LIGHTS;
				String soundPref = prefs.getString("notification.sound", "content://settings/system/notification_sound");
				notification.sound = Uri.parse(soundPref);
				if(prefs.getBoolean("notification.vibrate", true))
					notification.defaults |= Notification.DEFAULT_VIBRATE;
			}
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(NOTIFICATION_ID);
			notification.setLatestEventInfo(context, contentTitle, contentText, pendingIntent);
			notificationManager.notify(NOTIFICATION_ID, notification);

		} catch (Exception e) {
			Log.w("C2DMNotificationService", e);
		}	
	}

	/**
	 * Segna tutte le notifiche come gi&agrave; lette.
	 * Utilizzato, di solito, dopo aver visualizzato l'intera
	 * lista degli utenti nelle vicinanze.
	 */
	public static void markAllAsRead(Context context){
		unreadNotifications.clear();
		((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTIFICATION_ID);
	}

	/**
	 * Segna la notifica relativa ud un certo User come gi&agrave; letta
	 * @param userId L'id dell'User la cui notifica &egrave; stata letta
	 */
	public static void markAsRead(long userId){
		unreadNotifications.remove(new Long(userId));
	}



}
