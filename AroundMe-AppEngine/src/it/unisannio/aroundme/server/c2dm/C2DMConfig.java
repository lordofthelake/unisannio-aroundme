package it.unisannio.aroundme.server.c2dm;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Indexed;

/**
 * Utilizzato per rendere persistenti nel database la chiave di autenticazione
 * per il server C2DM fornita da Google.
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
@Indexed
public class C2DMConfig {
	@Id
	private long key = 1;
	private String authKey;

	/**
	 * Restituisce la chiave autenticazione per il server C2DM
	 * @return La chiave autenticazione per il server C2DM
	 */
	public String getAuthKey() {
		return authKey;
	}

	/**
	 * Imposta la chiave autenticazione per il server C2DM
	 * @param authKey la chiave autenticazione
	 */
	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}
	
	/**
	 * Restituisce la chiave per il Datastore con cui il {@link C2DMConfig} è reso persistente
	 * @return La chiave per il Datastore con cui il {@link C2DMConfig} è reso persistente
	 */
	public long getKey() {
		return key;
	}

}
