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
	private long key;
	private String authKey;
	private String c2dmUrl;
	
	public static final String DEFAULT_C2DM_URL = "https://android.clients.google.com/c2dm/send";
	public static final String DEFAULT_AUTH_TOKEN = "Il notro token C2DM";
	
	public String getAuthKey() {
		if (authKey == null)
			return DEFAULT_AUTH_TOKEN;
		return authKey;
	}
	
	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}
	
	public String getC2dmUrl() {
		if(c2dmUrl == null)
			return DEFAULT_C2DM_URL;
		return c2dmUrl;
	}
	
	public void setC2dmUrl(String c2dmUrl) {
		this.c2dmUrl = c2dmUrl;
	}

	public long getKey() {
		return key;
	}

	
}
