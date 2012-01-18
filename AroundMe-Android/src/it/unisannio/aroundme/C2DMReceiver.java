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
package it.unisannio.aroundme;

import com.google.android.c2dm.C2DMBaseReceiver;

import it.unisannio.aroundme.services.C2DMNotificationService;
import it.unisannio.aroundme.services.PreferencesSyncService;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.provider.Settings;
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
        super(Setup.C2DM_SENDER_ID);
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
		edit.putString(Setup.C2DM_REGISTRATIONID_KEY, registrationId);
		edit.commit();

		startService(new Intent(this, PreferencesSyncService.class));

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
		edit.putString(Setup.C2DM_REGISTRATIONID_KEY, null);
		edit.commit();
		startService(new Intent(this, PreferencesSyncService.class));
    }


    /**
     *  Chiamato in caso di errori.
     * 
     * @param context il Context
     * @param errorId un messaggio di errore, definito in {@link C2DMBaseReceiver}
     */
    @Override
    public void onError(Context context, String errorId) {
    	if(errorId.equals("ACCOUNT_MISSING")){
//    		Toast.makeText(getApplicationContext(), R.string.error_GoogleAccountNeeded, Toast.LENGTH_LONG).show();
    		SharedPreferences prefs = PreferenceManager	.getDefaultSharedPreferences(context);
    		Editor edit = prefs.edit();
    		edit.putBoolean("notification.active", false);
    		edit.commit();
    		context.startActivity(new Intent(Settings.ACTION_ADD_ACCOUNT).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    	}
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
    
    /**
     * Registra il device per l'utilizzo del servizio C2DM
     * @param context il Context
     */
    public static void register(Context context){
    	Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
		registrationIntent.putExtra("app", PendingIntent.getBroadcast(context, 0, new Intent(), 0));
		registrationIntent.putExtra("sender", Setup.C2DM_SENDER_ID);
		context.startService(registrationIntent);
    }
    
    /**
     * Revoca la registrazione per l'utilizzo del servizio C2DM
     * 
     * @param context il Context
     */
    public static void unregister(Context context){
    	Intent unregIntent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
    	unregIntent.putExtra("app", PendingIntent.getBroadcast(context, 0, new Intent(), 0));
    	context.startService(unregIntent);
    }
    
}
