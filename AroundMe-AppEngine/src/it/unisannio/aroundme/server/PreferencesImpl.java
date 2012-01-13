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
