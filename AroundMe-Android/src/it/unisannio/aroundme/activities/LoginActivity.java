/* AroundMe - Social Network mobile basato sulla geolocalizzazione
 * Copyright (C) 2012 AroundMe Working Group
 *   
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unisannio.aroundme.activities;

import java.util.NoSuchElementException;

import it.unisannio.aroundme.C2DMReceiver;
import it.unisannio.aroundme.R;
import it.unisannio.aroundme.Setup;
import it.unisannio.aroundme.async.AsyncQueue;
import it.unisannio.aroundme.async.FutureListener;
import it.unisannio.aroundme.client.HttpStatusException;
import it.unisannio.aroundme.client.Identity;
import it.unisannio.aroundme.client.Registration;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.services.PositionTrackingService;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import android.app.AlertDialog;

import android.app.Dialog;	
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.*;

/**
 * Activity che viene avviata all'apertura dell'applicazione; gestisce il login e la registrazione degli utenti.
 * 
 * <p>Il processo di login procede secondo il seguente algoritmo:
 * <ol>
 * 		<li>Si controlla se esiste un Access Token in memoria. Se esiste, si passa al punto 3</li>
 * 		<li>Si richiede all'utente di autorizzare l'applicazione su Facebook</li>
 * 		<li>Si tenta di recuperare il profilo dal server di backend. Se la richiesta ha successo, si passa al punto 6.</li>
 * 		<li>Si importano i dati da Facebook. All'utente viene richiesto quali interessi includere nel proprio profilo.</li>
 * 		<li>Viene creato un profilo sul server di backend e viene impostato come identit&agrave; dell'utente</li>
 * 		<li>Si controlla se l'utente ha una posizione gi&agrave; conosciuta. Se esiste gi&agrave;, si passa al punto 8</li>
 * 		<li>Si tenta di avere l'ultima posizione memorizzata nel dispositivo. Se non ne esiste una, si mette in attesa l'utente
 * 			finch&eacute; non ne viene rilevata una. Nel caso in cui non sia attivo alcun provider per la posizione, si chiede all'utente
 * 			di attivarne uno dalle impostazioni.</li>
 * 		<li>Viene avviata la {@link ListViewActivity} in modalit&agrave; interattiva.</li>
 * </ol>
 * </p>
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */ 
public class LoginActivity extends FragmentActivity 
	implements FutureListener<Identity>, DialogListener, LocationListener {
	private static final int ACTIVITY_LOCATION_SETTINGS_REQUEST = 0;

	private Facebook facebook;
	
	private SharedPreferences preferences;
	private AsyncQueue async;
	private TextView txtLoading;
	
	private LocationManager locationManager;
	private boolean otherLocationProviderExists = true;
	private AlertDialog locationDialog = null;

	private SharedPreferences loginPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		loginPreferences = getPreferences(MODE_PRIVATE);
		facebook = new Facebook(Setup.FACEBOOK_APP_ID);
		
		this.async = new AsyncQueue();

		setContentView(R.layout.login);
		txtLoading= (TextView) findViewById(R.id.txtLoginWait);
		final String accessToken = loginPreferences.getString("access_token", null);
		long expires = loginPreferences.getLong("access_expires", 0);

		if(accessToken != null) {
			facebook.setAccessToken(accessToken);
		}
		if(expires != 0) {
			facebook.setAccessExpires(expires);
		}

		if(facebook.isSessionValid()) {
			Log.d("LoginActivity", "Session valid");
			// L'utente ha gia' effettuato un accesso su questo dispositivo
			async.exec(Identity.login(facebook), this);
		} else {
			// Chiediamo l'autorizzazione
			startAuthorizationProcess();
		}
	}
	
	private void startAuthorizationProcess() {
		SharedPreferences.Editor editor = preferences.edit();
		editor.remove("access_token");
		editor.remove("access_expires");
		editor.commit();

		facebook.authorize(LoginActivity.this, new String[] { "offline_access", "user_likes" }, this);
	}
	
	private void showErrorDialog(String message) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle(R.string.login_auth_error);
		if(message != null)
			b.setMessage(message);
		b.setPositiveButton(R.string.dialog_retry, new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				startAuthorizationProcess();
			}
		});
		
		b.setNegativeButton(R.string.dialog_exit, new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
			
		});
		
		AlertDialog dialog = b.create();
		dialog.show();
	}

	@Override
	public void onComplete(Bundle values) {
		txtLoading.setText(R.string.login_loggingin);
		async.exec(Identity.login(facebook), this);
	}

	@Override
	public void onFacebookError(FacebookError e) {
		showErrorDialog(e.getMessage());
	}

	@Override
	public void onError(DialogError e) {
		showErrorDialog(e.getMessage());
	}

	@Override
	public void onCancel() {
		finish();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == ACTIVITY_LOCATION_SETTINGS_REQUEST)
			requestLocation();
		else
			facebook.authorizeCallback(requestCode, resultCode, data);
	}


	@Override
	public void onSuccess(Identity me) {
		SharedPreferences.Editor editor = loginPreferences.edit();

		editor.putString("access_token", facebook.getAccessToken());
		editor.putLong("access_expires", facebook.getAccessExpires());
		editor.commit();

		txtLoading.setText(String.format(getString(R.string.login_welcome_message), me.getName()));
		requestLocation();		
	}
	
	private void requestLocation() {
		if(locationDialog != null)
			locationDialog.dismiss();
		
		Identity me = Identity.get();
		
		if(me.getPosition() == null) {
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			Location networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			
			Location lastFix = PositionTrackingService.isBetterLocation(networkLocation, gpsLocation) ? networkLocation : gpsLocation;
			if(lastFix != null) {
				Log.d("LoginActivity", "Using last known position");
				onLocationChanged(lastFix);
			} else {
				txtLoading.setText(R.string.login_waiting_position);
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
			} 
		} else {
			Log.d("LoginActivity", "Using in-memory position");
			startApplication();
		}
	}


	@Override
	public void onError(Throwable e) {
		if((e instanceof NoSuchElementException) || 
				(e instanceof HttpStatusException 
						&& ((HttpStatusException) e).getStatusCode() == 403)) {
			txtLoading.setText(R.string.login_facebook_import);
			async.exec(Registration.create(facebook), new FutureListener<Registration>() {

				@Override
				public void onSuccess(final Registration object) {
					object.createInterestEditorDialog(LoginActivity.this, new Dialog.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							async.exec(object, LoginActivity.this);
						}
					}).show();
				}

				@Override
				public void onError(Throwable e) {
					Log.w("LoginActivity", "Registration", e);
					showErrorDialog(null);
				}
				
			});
			
		} else {
			Log.w("LoginActivity", "Login", e);
			showErrorDialog(null);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		async.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		async.resume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		async.shutdown();
	}

	@Override
	public void onLocationChanged(Location location) {
		locationManager.removeUpdates(this);
		Identity me = Identity.get();
		me.setPosition(ModelFactory.getInstance().createPosition(location.getLatitude(), location.getLongitude()));
		startApplication();
	}
	
	private void startApplication() {
		Log.i("LoginActivity", "Login ok! User #" + Identity.get().getId() + " in " + Identity.get().getPosition());
		
		String c2dmRegistrationId = preferences.getString(Setup.C2DM_REGISTRATIONID_KEY, null);
		if (c2dmRegistrationId == null){
			C2DMReceiver.register(getApplicationContext());
		}
		
		startActivity(new Intent(this, ListViewActivity.class));
		if(preferences.getBoolean("tracking.enabled", true)) {
			startService(new Intent(this, PositionTrackingService.class));
		}
		finish();
	}

	@Override
	public void onProviderDisabled(String provider) {
		if(!otherLocationProviderExists) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle(R.string.dialog_missingprovider_title);
			b.setMessage(R.string.dialog_missingprovider_message);
			b.setPositiveButton(R.string.dialog_settings, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);	
				}
			});
			
			b.setNegativeButton(R.string.dialog_retry, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					requestLocation();
				}
			});
			
			AlertDialog dialog = b.create();
			dialog.show();
			locationDialog = dialog;
		}
			
		otherLocationProviderExists = false;
	}

	@Override
	public void onProviderEnabled(String provider) {
		otherLocationProviderExists = true;		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
	
}
