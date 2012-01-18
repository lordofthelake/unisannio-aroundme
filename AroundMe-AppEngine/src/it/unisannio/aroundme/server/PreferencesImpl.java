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
package it.unisannio.aroundme.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.objectify.annotation.Serialized;
import com.googlecode.objectify.annotation.Unindexed;

import it.unisannio.aroundme.model.Preferences;

/**
 * Implementazione lato server di {@link Preferences}.
 * Utilizza le annotazioni necessarie per la persistenza sul Datastore
 * 
 * @see Preferences
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
@Unindexed
public class PreferencesImpl extends Preferences{
	private static final long serialVersionUID = 1L;
	
	@Serialized
	private Map<String, Object> preferencesMap;
	
	/**
	 * Crea un nuovo {@link User}.
	 * &Egrave dichiarato protected per evitare che
	 * non venga creato tramite {@link ModelFactory}. 
	 */
	protected PreferencesImpl() {
		preferencesMap = new HashMap<String, Object>();	
	}
	
	/**
	 * {@inheritDoc}	
	 */
	@Override
	public Map<String, Object> getAll() {
		return Collections.unmodifiableMap(preferencesMap);
	}

	/**
	 * {@inheritDoc}	
	 */
	@Override
	public boolean contains(String key) {
		return preferencesMap.containsKey(key);
	}

	/**
	 * {@inheritDoc}	
	 */
	@Override
	protected Object getObject(String key) {
		return preferencesMap.get(key);
	}

	/**
	 * {@inheritDoc}	
	 */
	@Override
	protected void putObject(String key, Object value) {
		preferencesMap.put(key, value);
	}

}
