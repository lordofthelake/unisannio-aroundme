package it.unisannio.aroundme.activities;

import java.io.InputStream;

import it.unisannio.aroundme.Application;
import it.unisannio.aroundme.C2DMReceiver;
import it.unisannio.aroundme.R;
import it.unisannio.aroundme.Setup;
import it.unisannio.aroundme.async.AsyncQueue;
import it.unisannio.aroundme.async.FutureListener;
import it.unisannio.aroundme.client.HttpTask;
import it.unisannio.aroundme.client.Identity;
import it.unisannio.aroundme.services.PositionTrackingService;
import it.unisannio.aroundme.services.PreferencesSyncService;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.support.v4.app.SherlockPreferenceActivity;
import android.util.Log;
import android.widget.Toast;

/**
 * Activity che permette di cambiare le preferenze dell'applicazione.
 * 
 * Al termine della sua esecuzione, avvia il {@link PreferencesSyncService} per sincronizzare le modifiche effettuate con il server
 * di backend.
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class PreferencesActivity extends SherlockPreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		findPreference("tracking.enabled").setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if((Boolean) newValue) {
					startService(new Intent(PreferencesActivity.this, PositionTrackingService.class));
				} else {
					stopService(new Intent(PreferencesActivity.this, PositionTrackingService.class));
				}
				
				return true;
			}});
		
		findPreference("notification.active").setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				if((Boolean) newValue) {
					C2DMReceiver.register(PreferencesActivity.this);
				} else {
					C2DMReceiver.unregister(PreferencesActivity.this);
				}
				return true;
			}
			
		});
		
		findPreference("logout").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				C2DMReceiver.unregister(PreferencesActivity.this);
				SharedPreferences prefs = getSharedPreferences("activities.LoginActivity", MODE_PRIVATE);
				prefs.edit().clear().commit();				
				((Application) getApplication()).terminate();
				Identity.set(null);
				finish();
				
				return false;
			}
		});
		
		findPreference("delete").setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference arg0) {
				AlertDialog.Builder b = new AlertDialog.Builder(PreferencesActivity.this);
				b.setTitle(R.string.dialog_delete_title);
				b.setMessage(R.string.dialog_delete_message);
				b.setPositiveButton(R.string.dialog_continue, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						final ProgressDialog progress = new ProgressDialog(PreferencesActivity.this);
						progress.setMessage(getString(R.string.progress_deleting));
						progress.show();
						final AsyncQueue async = new AsyncQueue();
						async.exec(new HttpTask<Void>("DELETE", Setup.BACKEND_USER_URL_PARAMETRIC, Identity.get().getId()) {

							@Override
							protected Void read(InputStream in)
									throws Exception {
								return null;
							}
						}, new FutureListener<Void>() {

							@Override
							public void onSuccess(Void object) {
								dismiss();
								SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(PreferencesActivity.this);
								prefs.edit().clear().commit();
								Toast.makeText(PreferencesActivity.this, R.string.toast_account_deleted, Toast.LENGTH_LONG).show();
								((Application) getApplication()).terminate();
								Identity.set(null);
								finish();
							}

							@Override
							public void onError(Throwable e) {
								dismiss();
								Toast.makeText(PreferencesActivity.this, R.string.toast_deletion_error, Toast.LENGTH_LONG).show();
								Log.w("PreferencesActivity", "Error removing account", e);
								
							}
							
							private void dismiss() {
								progress.dismiss();
								async.shutdown();
							}
						});
						
					}});
				b.setNegativeButton(R.string.dialog_cancel, null);
				b.create().show();
				
				return false;
			}
			
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		startService(new Intent(this, PreferencesSyncService.class));
	}
}
