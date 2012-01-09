package it.unisannio.aroundme.activities;

import java.util.NoSuchElementException;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.Setup;
import it.unisannio.aroundme.async.AsyncQueue;
import it.unisannio.aroundme.async.FutureListener;
import it.unisannio.aroundme.client.HttpStatusException;
import it.unisannio.aroundme.client.Identity;
import it.unisannio.aroundme.client.Registration;
import it.unisannio.aroundme.services.PositionTrackingService;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.*;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */ 
public class LoginActivity extends FragmentActivity 
	implements FutureListener<Identity>, DialogListener {

	private Facebook facebook;
	String FILENAME = "AroundMe_AuthData";
	private SharedPreferences mPrefs;
	private AsyncQueue async;
	private TextView txtLoading;
	/**
	 * Lo scopo di questa activity � quello di ottenere un accesso a facebook ed ottenere tutte le informazioni che occorrono
	 *  
	 *  La prima cosa da fare � ottenere le seguenti cose:
	 *  
	 *  - Oggetto Identity contenente tutte le informazioni dell' utente che sta utilizzando
	 *  
	 * */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		facebook = new Facebook(Setup.FACEBOOK_APP_ID);
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
			// L'utente ha già effettuato un accesso su questo dispositivo
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

		facebook.authorizeCallback(requestCode, resultCode, data);
	}


	@Override
	public void onSuccess(Identity object) {
		SharedPreferences.Editor editor = mPrefs.edit();

		editor.putString("access_token", facebook.getAccessToken());
		editor.putLong("access_expires", facebook.getAccessExpires());
		editor.commit();

		txtLoading.setText("Ciao " + object.getName() + "!");
		
		if(object.getPosition() == null) {
			object.setPosition(PositionTrackingService.getLastKnownPosition(this));
		}
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String c2dmRegistrationId = prefs.getString("c2dmRegistrationId", null);
		if (c2dmRegistrationId == null){
			Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
			registrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
			registrationIntent.putExtra("sender", "aroundmeproject@gmail.com"); //FIXME Metterle e-mail e nome pref  c2dm nel setup
			startService(registrationIntent);
		}
		
		//if(!getIntent().getAction().equals("relogin"))
			startActivity(new Intent(this, ListViewActivity.class));
		
		startService(new Intent(this, PositionTrackingService.class));
		finish();
		
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
}
