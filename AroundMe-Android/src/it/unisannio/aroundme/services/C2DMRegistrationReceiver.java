package it.unisannio.aroundme.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
 * @author Giuseppe Fusco <gfeldiablo@gmail.com>
 *
 */
public class C2DMRegistrationReceiver extends BroadcastReceiver {

	public final static String AUTH = "authentication";
	
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
		String regId = intent.getStringExtra("registration_id"); 
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
		Notification notification = new Notification(R.drawable.ic_launcher,
				"Registration successful", System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		Intent intent = new Intent(context, C2DMRegistrationReceiver.class);
		intent.putExtra("registration_id", registrationId);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		notification.setLatestEventInfo(context, "Registration",
				"Successfully registered", pendingIntent);
		notificationManager.notify(0, notification);
	}

	private void sendRegistrationIdToServer(String deviceId,
			String registrationId) {
		Log.d("C2DM", "Sending registration ID to my application server");
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://google.com/register");
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("deviceid", deviceId));
			nameValuePairs.add(new BasicNameValuePair("registrationid",
					registrationId));

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(post);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				Log.e("HttpResponse", line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
