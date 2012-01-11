package it.unisannio.aroundme;

import android.graphics.Bitmap;
import android.location.LocationManager;
import it.unisannio.aroundme.activities.LoginActivity;
import it.unisannio.aroundme.async.AsyncQueue;
import it.unisannio.aroundme.client.HttpTask;
import it.unisannio.aroundme.client.Identity;
import it.unisannio.aroundme.client.Picture;
import it.unisannio.aroundme.model.Preferences;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;
import it.unisannio.aroundme.services.PositionTrackingService;

/**
 * Costanti di configurazione utilizzate all'interno dell'applicazione.
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public interface Setup {
	/**
	 * Id dell'applicazione Facebook.
	 * 
	 * @see LoginActivity
	 */
	final String FACEBOOK_APP_ID = "298477283507880";
	
	/**
	 * URL per le immagini.
	 * 
	 * @see Picture
	 */
	final String PICTURE_URL = "http://graph.facebook.com/%d/picture";
	
	/**
	 * Numero di thread contemporanei che vengono usati per caricare le immagini.
	 * 
	 * @see AsyncQueue
	 */
	final int PICTURE_CONCURRENCY = 4;
	
	/**
	 * Tempo di Keep-Alive per i thread che caricano le immagini.
	 * 
	 * @see AsyncQueue
	 */
	final int PICTURE_KEEPALIVE = 10;
	
	/**
	 * Dimensione della cache delle immagini (in byte).
	 * 
	 * @see Picture
	 */
	final int PICTURE_CACHE_SIZE = 4 * 1024 * 1024; // 4MiB
	
	/**
	 * Dimensione delle immagini formato 50x50 px fornite da Facebook, in formato bitmap
	 * (in byte).
	 * 
	 * Viene utilizzata come misura approssimata per il numero di entry che possono entrare nella
	 * cache, per versioni di Android inferiori alla 3.1 (API Level 12).
	 * 
	 * @see Picture#getCachedBitmap()
	 * @see Bitmap#getByteCount()
	 */
	final int PICTURE_AVERAGE_SIZE = 7654; // 7.7KiB
	
	/**
	 * Tempo di timeout dopo il quale una connessione HTTP viene terminata (in millisecondi).
	 * 
	 * @see HttpTask
	 */
	final int NETWORK_TIMEOUT = 30 * 1000; // 30 sec.
	
	/**
	 * Dimensione per la cache HTTP su disco (in byte).
	 * 
	 * La cache su disco &egrave; disponibile solo per Android 3.2+ (API Level 13).
	 * 
	 * @see android.net.http.HttpResponseCache
	 */
	final int NETWORK_CACHE_SIZE = 10 * 1024 * 1024; // 10 MiB
	
	/**
	 * Dimensione dei chunk per le richieste HTTP.
	 * 
	 * @see HttpTask
	 */
	final int NETWORK_CHUNCK_SIZE = 1024; // 1 KiB
	/**
	 * Indirizzo del server backend.
	 * 
	 * @see #BACKEND_USER_URL	
	 * @see #BACKEND_POSITION_URL
	 */
	final String BACKEND_HOST = "https://aroundme-backend.appspot.com";
	//final String BACKEND_HOST = "http://localhost:8888";
	
	/**
	 * Path della risorsa "User"
	 * 
	 * @see User
	 */
	final String BACKEND_USER_PATH = "/user/";
	
	/**
	 * Path della risorsa "Position"
	 * 
	 * @see Position
	 */
	final String BACKEND_POSITION_PATH = "/position/%d";
	
	/**
	 * Path della risorsa "Preferences"
	 * 
	 * @see Preferences
	 */
	final String BACKEND_PREFERENCES_PATH = "/preferences/%d";
	
	/**
	 * URL completo per il backend degli utenti.
	 * 
	 * @see #BACKEND_HOST
	 * @see #BACKEND_USER_PATH
	 */
	final String BACKEND_USER_URL = BACKEND_HOST + BACKEND_USER_PATH;

	/**
	 * URL completo per il backend delle posizioni.
	 * 
	 * @see #BACKEND_HOST
	 * @see #BACKEND_POSITION_PATH
	 */
	final String BACKEND_POSITION_URL = BACKEND_HOST + BACKEND_POSITION_PATH;
	
	/**
	 * URL completo per il backend delle preferenze
	 * 
	 * @see #BACKEND_HOST
	 * @see #BACKEND_PREFERENCES_PATH
	 */
	final String BACKEND_PREFERENCES_URL = BACKEND_HOST + BACKEND_PREFERENCES_PATH;
	
	/**
	 * Nome dell'header che viene usato per autenticarsi con il server.
	 * 
	 * @see Identity#getAccessToken()
	 */
	final String BACKEND_AUTH_HEADER = "X-AccessToken";
	
	/**
	 * Dimensione della cache degli utenti caricati, in numero di entry.
	 * 
	 * @see UserQuery#byId(long...)
	 * @see UserQuery#single(long)
	 */
	final int USER_CACHE_SIZE = 20;
	
	/**
	 * Intervallo di tempo dopo cui una rilevazione di posizione viene automaticamente considerata
	 * migliore della precedente (in millisecondi).
	 * 
	 * @see PositionTrackingService#isBetterLocation(android.location.Location, android.location.Location)
	 */
	final long TRACKING_TIME_WINDOW = 1000 * 60 * 2; // 2 min.
	

	/**
	 * Intervallo minimo di tempo tra le rilevazioni della posizione (in millisecondi).
	 * 
	 * Gli sviluppatori Android sconsigliano intervalli inferiori a 60 sec. per questioni di 
	 * risparmio energetico.
	 * 
	 * @see PositionTrackingService
	 * @see LocationManager#requestLocationUpdates(String, long, float, android.location.LocationListener)
	 */
	final long TRACKING_MIN_TIME = 60 * 1000; // 60 sec
	
	/**
	 * Distanza minima per una nuova rilevazione della posizione (in metri).
	 * 
	 * @see LocationManager#requestLocationUpdates(String, long, float, android.location.LocationListener)
	 */
	final float TRACKING_MIN_DISTANCE = 5;

	/**
	 * Valore di default per il filtro di compatibilita'.
	 */
	final float FILTERS_DEFAULT_RANK = 0.6f;

	/**
	 * Raggio di default per il filtro di distanza (in metri).
	 */
	final int FILTERS_DEFAULT_RADIUS = 500;
	
	/**
	 * L'Id per il C2DM con quale possiamo ricevere notifiche 
	 */
	final String SENDER_ID = "aroundmeproject@gmail.com";
	
	/**
	 * Il nome della SharedPreference contenente il registrationId per il C2DM
	 */
	final String C2DM_REGISTRATIONID = "c2dmRegistrationId";
}
