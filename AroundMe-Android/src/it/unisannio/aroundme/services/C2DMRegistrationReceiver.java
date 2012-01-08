package it.unisannio.aroundme.services;

import it.unisannio.aroundme.Setup;
import it.unisannio.aroundme.client.HttpTask;
import it.unisannio.aroundme.client.Identity;
import it.unisannio.aroundme.model.Preferences;

import java.io.InputStream;
import java.io.OutputStream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * 
 * @author Giuseppe Fusco <gfeldiablo@gmail.com>
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class C2DMRegistrationReceiver extends BroadcastReceiver {

	public final static String C2DM_REGISTRATIONID = "c2dmRegistrationId";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.w("C2DM", "Registration Receiver called");
		if ("com.google.android.c2dm.intent.REGISTRATION".equals(action)) {
			Log.w("C2DM", "Received registration ID");
			handleRegistration(context, intent);
		}
	}
	
	private void handleRegistration(Context context, Intent intent){
		String registrationId = intent.getStringExtra("registration_id"); 
		String error = intent.getStringExtra("error");
	    if ( error!= null) {
	        // Registration failed, should try again later.
	    	Log.e(error, String.format("Received error: %s\n", error));
	        if (error.equals("ACCOUNT MISSING")) {
	           Toast.makeText(context, "Please add a google account to your device.", Toast.LENGTH_SHORT).show();
	         } else if(error.endsWith("AUTHENTICATION_FAILED")){
	        	 Toast.makeText(context, "Please add a google account to your device.", Toast.LENGTH_SHORT).show();
	         }else if(error.equals("INVALID_SENDER")){
	        	 Toast.makeText(context, "The sender account is not recognized.", Toast.LENGTH_SHORT).show();
	         }
	         else if(error.equals("SERVICE_NOT_AVAILABLE")){
	        	 Toast.makeText(context, "The device can't read the response, \nor there was a 500/503 from the server that can be retried later.", Toast.LENGTH_SHORT).show();
	         }
	         else if(error.equals("PHONE_REGISTRATION_ERROR")){
	        	 Toast.makeText(context, "Incorrect phone registration with Google. This phone doesn't currently support C2DM.", Toast.LENGTH_SHORT).show();
	         }else{
	        	 Toast.makeText(context, "Registration Error: " + error, Toast.LENGTH_SHORT).show();
	         }
	    } else if (registrationId != null) {
	    	String deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	    	
	    	SharedPreferences prefs = PreferenceManager	.getDefaultSharedPreferences(context);
			Editor edit = prefs.edit();
			edit.putString(C2DM_REGISTRATIONID, registrationId);
			edit.putString("deviceId", deviceId);
			edit.commit();
			
			//TODO Passare da SharedPreferences a it.unisannio.model.Preferences
	    	final Preferences preferences = null;
	    	
			try {
				(new HttpTask<Void>("POST", Setup.BACKEND_POSITION_PATH, Identity.get().getId()) {
	    } else if (regId != null) {
	    	String deviceId = Secure.getString(context.getContentResolver(),
					Secure.ANDROID_ID);
			createNotification(context, regId);
			sendRegistrationIdToServer(deviceId, regId);
			saveRegistrationId(context, regId);
	    }
	}
	
	private void saveRegistrationId(Context context, String registrationId) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor edit = prefs.edit();
		edit.putString(AUTH, registrationId);
		edit.commit();
	}
	
	private void createNotification(Context context, String registrationId) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.alert_light_frame,//FIXME ic_launcher
				"Registration successful", System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

					@Override
					protected Void read(InputStream in) throws Exception {
						return null;
					}

					@Override
					protected void write(OutputStream out) throws Exception {
						Preferences.SERIALIZER.write(preferences, out);
					}
					
				}).call();
				
			} catch (Exception e) {
				Log.d("C2DMRegistrationReceiver", "Http Error", e);
			}
	    }
	}
}
