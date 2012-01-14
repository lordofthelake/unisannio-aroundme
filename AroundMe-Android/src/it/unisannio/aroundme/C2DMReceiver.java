package it.unisannio.aroundme;

import com.google.android.c2dm.C2DMBaseReceiver;

import it.unisannio.aroundme.client.HttpTask;
import it.unisannio.aroundme.client.Identity;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Preferences;
import it.unisannio.aroundme.services.C2DMNotificationService;

import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Riceve un messaggio push message dal Cloud to Device Messaging (C2DM) service.
 * Gestisce sia la registazione del dispositivo per l'utilizzo del servizio,
 * sia la ricecione dei messaggi.
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 * @author Giuseppe Fusco <gfeldiablo@gmail.com>
 * 
 */
public class C2DMReceiver extends C2DMBaseReceiver {

    public C2DMReceiver() {
        super("aroundmeproject@gmail.com");
    }

    /**
     * Chiamato quando viene ricevuto l'id di registrazione all'utilizzo
     * del C2DM. Si occupa di salvare l'id di registrazione ll'interno
     * delle SharedPreferences e vengo dunque inviate al server
     * per poter permettere l'invio di messaggi.
     * 
     * @param context il Context
     * @param registrationId il registrationid per il  C2DM
     * 
     * @author Danilo Iannelli <daniloiannelli6@gmail.com>
     */
    @Override
    public void onRegistered(Context context, String registrationId) {
    	Log.i("C2DMRegistrationReceiver", "Received c2dmRegistrationId "+ registrationId);
		SharedPreferences prefs = PreferenceManager	.getDefaultSharedPreferences(context);
		Editor edit = prefs.edit();
		edit.putString(Setup.C2DM_REGISTRATIONID, registrationId);
		edit.commit();

		final Preferences preferences = ModelFactory.getInstance().createPreferences();
		preferences.putAll(prefs.getAll());
		
		sendC2DMPreferenceToServer(preferences);

    }

    /**
     * Chiamato quando viene revocata la registrazione al device.
     * 
     * @param context the Context
     * 
     * @author Danilo Iannelli <daniloiannelli6@gmail.com>
     */
    @Override
    public void onUnregistered(Context context) {
    	SharedPreferences prefs = PreferenceManager	.getDefaultSharedPreferences(context);
		Editor edit = prefs.edit();
		edit.putString(Setup.C2DM_REGISTRATIONID, null);
		edit.commit();

		final Preferences preferences = ModelFactory.getInstance().createPreferences();
		preferences.putAll(prefs.getAll());
		
		sendC2DMPreferenceToServer(preferences);
    }

    /**
     * Called on registration error. This is called in the context of a Service
     * - no dialog or UI.
     * 
     * @param context the Context
     * @param errorId an error message, defined in {@link C2DMBaseReceiver}
     */
    @Override
    public void onError(Context context, String errorId) {
//        context.sendBroadcast(new Intent(Util.UPDATE_UI_INTENT));
    }

    /**
     * Chiamato alla ricezione di un messaggio dal  server C2DM
     */
    @Override
    public void onMessage(Context context, Intent intent) {
    	long userId = Long.parseLong(intent.getStringExtra("userId"));
    	Log.i("C2DMReceiver", "Arrived message cointaining "+userId);
    	Intent notificationIntent = new Intent(context, C2DMNotificationService.class);
		notificationIntent.putExtra("userId", userId);
		context.startService(notificationIntent);
    }
    
    
    private void sendC2DMPreferenceToServer(final Preferences preferences){
    	try {
			(new HttpTask<Void>("POST", Setup.BACKEND_PREFERENCES_URL, Identity.get().getId()) {

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
			Log.d("C2DMPreferenceSender", "Http Error", e);
		}
    }
}
