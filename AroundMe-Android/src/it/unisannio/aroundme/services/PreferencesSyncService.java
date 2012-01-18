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
package it.unisannio.aroundme.services;

import java.io.InputStream;

import it.unisannio.aroundme.Setup;
import it.unisannio.aroundme.activities.PreferencesActivity;
import it.unisannio.aroundme.client.HttpTask;
import it.unisannio.aroundme.client.Identity;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Preferences;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Servizio che si occupa di mandare una copia delle preferenze del client al server di backend.
 * 
 * Viene richiamato tipicamente dopo ogni modifica delle preferenze che riguardano anche il lato server (es. impostazioni delle notifiche).
 * 
 * @see Preferences
 * @see PreferencesActivity
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class PreferencesSyncService extends IntentService {

	public PreferencesSyncService() {
		super("PreferencesSyncService");
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		if(Identity.get() == null)
			return;
		
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
