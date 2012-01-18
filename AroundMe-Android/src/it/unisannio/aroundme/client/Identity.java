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
package it.unisannio.aroundme.client;

import java.util.Collection;
import java.util.concurrent.Callable;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.facebook.android.Facebook;

import it.unisannio.aroundme.model.*;

/**
 * Classe che memorizza l'identit&agrave; dell'utente corrente sul dispositivo.
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class Identity extends User {
	private static final long serialVersionUID = 1L;
	
	private static Identity instance = null;
	
	/**
	 * Restituisce un task che effettua il login utilizzando la sessione di Facebook.
	 * 
	 * In caso di esito positivo l'identit&agrave; corrente viene settata a quella dell'utente che ha
	 * effettuato il login su Facebook. I dati dell'utente devono essere stati precedentemente importati
	 * affinch&eacute; il login possa avvenire con successo.
	 * 
	 * @param fb un client Facebook con le autorizzazioni necessarie
	 * @return un task in grado di effettuare il login dell'utente
	 * 
	 * @see Registration#create(Facebook)
	 * @see #set(Identity)
	 */
	public static Callable<Identity> login(final Facebook fb) {
		return new Callable<Identity>() {

			@Override
			public Identity call() throws Exception {
				JSONObject me = (JSONObject) new JSONTokener(fb.request("me")).nextValue();
				long id = me.getLong("id");
				String accessToken = fb.getAccessToken();
				synchronized(Identity.class) {
					instance = new Identity(null, accessToken); // Settiamo l'AccessToken
					try {
						instance = new Identity(UserQuery.single(id).call(), accessToken);
					} catch (Exception e) {
						instance = null;
						throw e;
					}
				}
				
				return instance;
			}
			
		};
	}

	/**
	 * Restituisce l'identit&agrave; attualmente in uso.
	 * 
	 * @return l'identit&agrave; attualmente in uso o {@code null} se non impostata.
	 */
	public static synchronized Identity get() {
		return instance;
	}

	/**
	 * Imposta l'identit&agrave; attualmente in uso.
	 * 
	 * @param identity l'identit&agrave; da usare
	 */
	public synchronized static void set(Identity identity) {
		instance = identity;
	}

	
	private final User self;
	private final String accessToken;
	
	/**
	 * Crea una nuova identit&agrave; utilizzando l'utente e la chiave d'accesso forniti.
	 * 
	 * @param self l'utente di cui si vogliono utilizzare le credenziali
	 * @param accessToken il token d'accesso
	 * 
	 * @see Facebook#getAccessToken()
	 */
	protected Identity(User self, String accessToken) {
		this.self = self;
		this.accessToken = accessToken;
	}
	
	/**
	 * Restituisce il token d'accesso di questo utente.
	 * 
	 * Questo viene utilizzato dal server per autenticare le richieste provenienti dai client.
	 * 
	 * @return il token d'accesso dell'utente
	 * @see Facebook#getAccessToken()
	 */
	public String getAccessToken() {
		return accessToken;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getId() {
		return self.getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return self.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Position getPosition() {
		return self.getPosition();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPosition(Position p) {
		self.setPosition(p);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Interest> getInterests() {
		return self.getInterests();
	}
	
	@Override
	public boolean equals(Object obj) {
		return self.equals(obj);
	}

}
