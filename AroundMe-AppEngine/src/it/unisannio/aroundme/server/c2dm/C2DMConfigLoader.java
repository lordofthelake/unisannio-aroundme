package it.unisannio.aroundme.server.c2dm;

import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

/**
 * Utilizzato per gestire i parametri di configurazione per l'invio di notifiche tramite C2DM.
 * Questi sono resi persistenti sul datastore tramite un oggetto di tipo
 * {@link C2DMConfig} che viene recuperato o aggiornato a seconda dei casi. 
 * 
 * @see C2DMConfig  
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class C2DMConfigLoader {
	private String authKey;
	private String c2dmUrl;


	//SingletonHolder pattern
	private static class C2DMConfigLoaderHolder { 
		public static final C2DMConfigLoader instance = new C2DMConfigLoader();
	}
	
	/**
	 * Singleton Pattern. Restituisce l'unica istanza di {@link C2DMConfigLoader}
	 * @return L'unica istanza di {@link C2DMConfigLoader}
	 */
	public static C2DMConfigLoader getInstance() {
        return C2DMConfigLoaderHolder.instance;
	}

	private C2DMConfigLoader(){}

	/**
	 * Restitusce la chiave necessaria per l'autenticazione sul server C2DM
	 * 
	 * @return La Stringa contenente chiave di autenticazione 
	 */
	public String getAuthKey() {
		if (authKey == null)
			authKey = retrieveConfig().getAuthKey();
		return authKey;
	}

	/**
	 * Imposta la chiave necessaria per l'autenticazione sul server C2DM rendendone
	 * persistene il valore sul Datastore.
	 *  
	 * @param authKey La nuova chiave di autenticazione 
	 */
	public void setAuthKey(String authKey) {
		this.authKey = authKey;
		C2DMConfig config = retrieveConfig();
		config.setAuthKey(authKey);
		Objectify ofy = ObjectifyService.begin();
		ofy.put(config);
	}

	/**
	 * Restitusce l'URL del server C2DM
	 * 
	 * @return La Stringa contenente l'URL del server C2DM 
	 */
	public String getC2dmUrl() {
		if(c2dmUrl == null)
			c2dmUrl = retrieveConfig().getC2dmUrl();
		return c2dmUrl;
	}

	/**
	 * Imposta l'URL del server C2DM rendendone persistene il valore sul Datastore.
	 *  
	 * @param authKey Il nuovo URL del server C2DM
	 */
	public void setC2dmUrl(String c2dmUrl) {
		this.c2dmUrl = c2dmUrl;
		C2DMConfig config = retrieveConfig();
		config.setC2dmUrl(c2dmUrl);
		Objectify ofy = ObjectifyService.begin();
		ofy.put(config);
	}
	
	/**
	 * Invalida il token di registrazione forzandone la lettura dal datastore.
	 * Utile quando il {@link C2DMConfigLoader} potrebbe non avere in memoria
	 * l'ultimo token di autenticazione fornito da Google
	 */
	public void invalidateAuthToken(){
		authKey = null;
	}

	/**
	 * Recupera dal Datastore {@link C2DMConfig} per poter accedere alle impostazioni
	 */
	private C2DMConfig retrieveConfig(){
		Objectify ofy = ObjectifyService.begin();
		
		C2DMConfig config;
		try{
			config =  ofy.get(C2DMConfig.class, 1);
		}catch (NotFoundException e) {
			config = new C2DMConfig();
			ofy.put(config);
		}
		return config;
	}

}
