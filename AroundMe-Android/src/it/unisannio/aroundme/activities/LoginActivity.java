package it.unisannio.aroundme.activities;

import java.util.NoSuchElementException;

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
import android.app.PendingIntent;
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
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */ 

// FIXME Externalize strings
// FIXME Lamentarsi se non si è connessi
public class LoginActivity extends FragmentActivity 
	implements FutureListener<Identity>, DialogListener, LocationListener {
	private static final int ACTIVITY_LOCATION_SETTINGS_REQUEST = 0;

	private Facebook facebook;
	
	// FIXME Sposta in Setup
	String FILENAME = "AroundMe_AuthData";
	private SharedPreferences mPrefs;
	private AsyncQueue async;
	private TextView txtLoading;
	
	LocationManager locationManager;
	private boolean otherLocationProviderExists = true;
	private AlertDialog locationDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		facebook = new Facebook(Setup.FACEBOOK_APP_ID);
		
		// FIXME Controlla se funziona
//		Identity identity = Identity.get();
//		if(identity != null) {
//			onSuccess(identity);
//			return;
//		}
		
		
		this.async = new AsyncQueue();

		setContentView(R.layout.login);
		txtLoading=(TextView) findViewById(R.id.txtLoginWait);
		mPrefs = getPreferences(MODE_PRIVATE);
		final String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);

		if(access_token != null) {
			facebook.setAccessToken(access_token);
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
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.remove("access_token");
		editor.remove("access_expires");
		editor.commit();

		facebook.authorize(LoginActivity.this, new String[] { "offline_access", "user_likes" }, this);
	}
	
	private void showErrorDialog(String message) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Errore di autenticazione");
		if(message != null)
			b.setMessage(message);
		b.setPositiveButton("Riprova", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				startAuthorizationProcess();
			}
		});
		
		b.setNegativeButton("Esci", new Dialog.OnClickListener() {

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
		txtLoading.setText("Login in corso");
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
		SharedPreferences.Editor editor = mPrefs.edit();

		editor.putString("access_token", facebook.getAccessToken());
		editor.putLong("access_expires", facebook.getAccessExpires());
		editor.commit();

		txtLoading.setText("Ciao " + me.getName() + "!");
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
				txtLoading.setText("In attesa di rilevare la posizione");
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
			txtLoading.setText("Importazione dati da Facebook");
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
		Log.d("LoginActivity", "Ready to start application. User in " + Identity.get().getPosition());
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String c2dmRegistrationId = prefs.getString(Setup.C2DM_REGISTRATIONID, null);
		if (c2dmRegistrationId == null){
			Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
			registrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
			registrationIntent.putExtra("sender", Setup.SENDER_ID);
			startService(registrationIntent);
		}
		
		startActivity(new Intent(this, ListViewActivity.class));
		// FIXME Il servizio deve essere avviato solo se e' impostato nelle preferenze
		startService(new Intent(this, PositionTrackingService.class));
		finish();
	}

	@Override
	public void onProviderDisabled(String provider) {
		if(!otherLocationProviderExists) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Rilevamento posizione");
			b.setMessage("Non � disponibile un provider per la posizione. Per favore attivane uno dalle impostazioni.");
			b.setPositiveButton("Impostazioni", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);	
				}
			});
			
			b.setNegativeButton("Riprova", new OnClickListener() {
				
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
