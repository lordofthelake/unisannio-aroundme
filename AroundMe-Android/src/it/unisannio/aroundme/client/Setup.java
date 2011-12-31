package it.unisannio.aroundme.client;

import it.unisannio.aroundme.client.async.AsyncQueue;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;

/**
 * Costanti di configurazione utilizzate all'interno dell'applicazione.
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public interface Setup {
	/**
	 * Id dell'applicazione Facebook.
	 */
	final String FACEBOOK_APP_ID = "298477283507880";
	
	/**
	 * URL per le immagini.
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
	 * N. di secondi dopo il quale una connessione HTTP viene terminata
	 * 
	 * @see HttpTask
	 */
	final int NETWORK_TIMEOUT = 30;
	
	/**
	 * Indirizzo del server backend.	
	 */
	final String BACKEND_HOST = "https://aroundme-backend.appspot.com";
	
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
	final String BACKEND_POSITION_PATH = "/user/%d/position";
	
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
	

	
}
