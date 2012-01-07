package it.unisannio.aroundme.server.c2dm;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Indexed;

/**
 * Utilizzato per rendere persistenti nel database la chiave di autenticazione
 * fornitaci da Google e l'indirizzo del server C2DM
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
@Indexed
public class C2DMConfig {
	@Id
	private long key = 1;
	private String authKey;

	
	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	public long getKey() {
		return key;
	}

}
