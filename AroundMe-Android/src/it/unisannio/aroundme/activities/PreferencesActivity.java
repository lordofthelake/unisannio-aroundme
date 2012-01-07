package it.unisannio.aroundme.activities;

import it.unisannio.aroundme.R;
import android.os.Bundle;
import android.support.v4.app.SherlockPreferenceActivity;

public class PreferencesActivity extends SherlockPreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
