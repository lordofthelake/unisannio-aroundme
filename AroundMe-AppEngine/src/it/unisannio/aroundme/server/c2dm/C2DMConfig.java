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
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof C2DMConfig) {
			C2DMConfig c2dmConfig = (C2DMConfig) obj;
			return c2dmConfig.getAuthKey().equals(this.getAuthKey());
		}
		return false;
	}

}
