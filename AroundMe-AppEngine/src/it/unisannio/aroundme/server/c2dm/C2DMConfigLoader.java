package it.unisannio.aroundme.server.c2dm;

import java.io.IOException;

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
	
	/**
	 * Costruttore privato per realizzare il SingletonPattern
	 */
	private C2DMConfigLoader(){}

	/**
	 * Restitusce la chiave necessaria per l'autenticazione sul server C2DM
	 * Se il Datastore restituisce null, questa viene richiesta al server 
	 * ClientLogin di Google tramite {@link C2DMAuthenticationUtil}
	 * 
	 * @return La Stringa contenente chiave di autenticazione 
	 * @throws IOException Se si verificano errori durante il recupero 
	 * del token dal Server ClientLogin di Google
	 */
	public String getAuthKey() throws IOException {
		if (authKey == null){
			authKey = retrieveConfig().getAuthKey();
			if (authKey == null){
				authKey = C2DMAuthenticationUtil.getToken();
				setAuthKey(authKey);
			}
		}
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
	 * Invalida il token di registrazione forzandone la lettura dal datastore.
	 * Utile quando il {@link C2DMConfigLoader} potrebbe non avere in memoria
	 * l'ultimo token di autenticazione fornito da Google
	 */
	public void invalidateAuthToken(){
		authKey = null;
	}
	
	/**
	 * Fa in modo che al prossimo getAuthToken(), venga richiesto 
	 * un nuovo token al server ClientLogin di Google 
	 */
	public void regenerateAuthToken(){
		setAuthKey(null);
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
