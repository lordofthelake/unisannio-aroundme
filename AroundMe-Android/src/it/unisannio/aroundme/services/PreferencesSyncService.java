package it.unisannio.aroundme.services;

import java.io.InputStream;

import it.unisannio.aroundme.Setup;
import it.unisannio.aroundme.client.HttpTask;
import it.unisannio.aroundme.client.Identity;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Preferences;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class PreferencesSyncService extends IntentService {

	public PreferencesSyncService() {
		super("PreferencesSyncService");
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		try {
			Log.i("PreferencesSyncService", "Service started");
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			final Preferences userPreferences = ModelFactory.getInstance().createPreferences();
			userPreferences.putAll(pref.getAll());
			
			Identity me = Identity.get();
			(new HttpTask<Void>("POST", Setup.BACKEND_PREFERENCES_URL, me.getId()) {

				@Override
				protected Void read(InputStream in) throws Exception {
					return null;
				}
				
				protected void write(java.io.OutputStream out) throws Exception {
					Preferences.SERIALIZER.write(userPreferences, out);
				};
			}).call();
			Log.i("PreferencesSyncService", "Sync completed");
		} catch (Exception e) {
			Log.w("PreferencesSyncService", "Error sending preferences", e);
		}
		
	}

}
