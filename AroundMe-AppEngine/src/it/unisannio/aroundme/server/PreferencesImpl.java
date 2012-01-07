package it.unisannio.aroundme.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.objectify.annotation.Serialized;
import com.googlecode.objectify.annotation.Unindexed;

import it.unisannio.aroundme.model.Preferences;

@Unindexed
public class PreferencesImpl extends Preferences{
	private static final long serialVersionUID = 1L;
	
	@Serialized
	private Map<String, Object> preferencesMap;

	public PreferencesImpl() {
		preferencesMap = new HashMap<String, Object>();	
	}
	
	@Override
	public Map<String, Object> getAll() {
		return Collections.unmodifiableMap(preferencesMap);
	}

	@Override
	public boolean contains(String key) {
		return preferencesMap.containsKey(key);
	}

	@Override
	protected Object getObject(String key) {
		return preferencesMap.get(key);
	}

	@Override
	protected void putObject(String key, Object value) {
		preferencesMap.put(key, value);
	}

}
