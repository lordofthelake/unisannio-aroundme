package it.unisannio.aroundme.activities;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.services.PositionTrackingService;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.support.v4.app.SherlockPreferenceActivity;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */

// FIXME Invia tutto al server quando l'Activity termina
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
		
		findPreference("delete").setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference arg0) {
				// FIXME Gestisci preferenza "elimina"
				return false;
			}
			
		});
	}
}
