package it.unisannio.aroundme.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public abstract class Preferences implements Model {
	private static final long serialVersionUID = 1L;

	public static final Serializer<Preferences> SERIALIZER = new Serializer<Preferences>() {

		@Override
		public Preferences fromXML(Node xml) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Node toXML(Preferences obj) {
			// TODO Auto-generated method stub
			return null;
		}
		
	};

	public abstract long getUserId();
	
	public abstract Map<String, ?> getAll();
	
	public abstract boolean contains(String key);
	
	protected abstract Object get(String key);
	
	protected abstract void put(String key, Object val);
	
	public boolean get(String key, boolean def) {
		return contains(key) ? ((Boolean) get(key)) : def;
	}
	
	public float get(String key, float def) {
		return contains(key) ? ((Number) get(key)).floatValue() : def;
	}
	
	public int get(String key, int def) {
		return contains(key) ? ((Number) get(key)).intValue() : def;
	}
	
	public long get(String key, long def) {
		return contains(key) ? ((Number) get(key)).longValue() : def;
	}
	
	public double get(String key, double def) {
		return contains(key) ? ((Number) get(key)).doubleValue() : def;
	}
	
	public String get(String key, String def) {
		return contains(key) ? ((String) get(key)) : def;
	}
	
	public void put(String key, boolean val) {
		put(key, val);
	}
	
	public void put(String key, float val) {
		put(key, val);
	}
	
	public void put(String key, int val) {
		put(key, val);
	}
	
	public void put(String key, long val) {
		put(key, val);
	}
	
	public void put(String key, double val) {
		put(key, val);
	}
	
	public void put(String key, String val) {
		put(key, val);
	}
}
