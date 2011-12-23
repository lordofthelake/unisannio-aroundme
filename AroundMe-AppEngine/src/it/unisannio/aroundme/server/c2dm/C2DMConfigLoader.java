package it.unisannio.aroundme.server.c2dm;

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
	
	/**
	 * Crea un {@link C2DMConfigLoader} caricando dal Datastore l'oggetto di tipo {@link C2DMConfig}.
	 * Se questo non è presente nel Datastore, ne viene creato uno nuovo (contentente i valori di default)
	 * che viene salvato sul Datastore  
	 */
	public C2DMConfigLoader(){
		Objectify ofy = ObjectifyService.begin();
		C2DMConfig config =  ofy.get(C2DMConfig.class, 1);
		if(config == null){
			config = new C2DMConfig();
			ofy.put(config);
		}
		authKey = config.getAuthKey();
		c2dmUrl = config.getC2dmUrl();
	}
	
	/**
	 * Restitusce la chiave necessaria per l'autenticazione sul server C2DM
	 * 
	 * @return La Stringa contenente chiave di autenticazione 
	 */
	public String getAuthKey() {
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
		Objectify ofy = ObjectifyService.begin();
		C2DMConfig config =  ofy.get(C2DMConfig.class, 1);
		config.setAuthKey(authKey);
		ofy.put(config);
	}
	
	/**
	 * Restitusce l'URL del server C2DM
	 * 
	 * @return La Stringa contenente l'URL del server C2DM 
	 */
	public String getC2dmUrl() {
		return c2dmUrl;
	}
	
	/**
	 * Imposta l'URL del server C2DM rendendone persistene il valore sul Datastore.
	 *  
	 * @param authKey Il nuovo URL del server C2DM
	 */
	public void setC2dmUrl(String c2dmUrl) {
		Objectify ofy = ObjectifyService.begin();
		C2DMConfig config =  ofy.get(C2DMConfig.class, 1);
		config.setAuthKey(authKey);
		ofy.put(config);
	}
	
}
