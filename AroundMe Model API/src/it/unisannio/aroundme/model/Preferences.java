package it.unisannio.aroundme.model;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public abstract class Preferences implements Model {
	private static final long serialVersionUID = 1L;

	public static final Serializer<Preferences> SERIALIZER = new Serializer<Preferences>() {

		@Override
		public Preferences fromXML(Element node) {
			validateTagName(node, "preferences");
			
			Preferences obj = ModelFactory.getInstance().createPreferences();
			
			NodeList entries = node.getChildNodes();
			for(int i = 0; i < entries.getLength(); i++) {
				if(!(entries.item(i) instanceof Element)) continue;
				
				Element entry = (Element) entries.item(i);
				String key = entry.getTagName();
				String content = entry.getTextContent();
				String type = entry.getAttribute("type");
				
				if(type.isEmpty()) type = "string";
				
				if(type.equals("string")) obj.put(key, (String) content);
				else if(type.equals("float")) obj.put(key, (float) Float.valueOf(content));
				else if(type.equals("double")) obj.put(key, (double) Double.valueOf(content));
				else if(type.equals("int")) obj.put(key, (int) Integer.valueOf(content));
				else if(type.equals("long")) obj.put(key, (long) Long.valueOf(content));
				else if(type.equals("boolean")) obj.put(key, (boolean) Boolean.valueOf(content));
				else throw new ClassCastException();
			}
			
			return obj;
		}

		@Override
		public Element toXML(Preferences obj) {
			Map<String, ?> map = obj.getAll();
			Document document = getDocumentBuilder().newDocument();
			
			Element preferences = document.createElement("preferences");
			for(Map.Entry<String, ?> e : map.entrySet()) {
				Element entry = document.createElement(e.getKey());
				entry.setTextContent(e.getValue().toString());
				Class<?> clazz = e.getValue().getClass();
				String type = null;
				
				if(clazz.equals(String.class)) type = "string";
				else if(clazz.equals(Float.class)) type = "float";
				else if(clazz.equals(Double.class)) type = "double";
				else if(clazz.equals(Integer.class)) type = "int";
				else if(clazz.equals(Long.class)) type = "long";
				else if(clazz.equals(Boolean.class)) type = "boolean";
				else throw new ClassCastException();
				
				entry.setAttribute("type", type);
				preferences.appendChild(entry);
			}
			
			return preferences;
		}
		
	};

	public abstract Map<String, Object> getAll();
	
	public abstract boolean contains(String key);
	
	protected abstract Object getObject(String key);
	
	protected abstract void putObject(String key, Object val);
	
	public boolean get(String key, boolean def) {
		return contains(key) ? ((Boolean) getObject(key)) : def;
	}
	
	public float get(String key, float def) {
		return contains(key) ? ((Number) getObject(key)).floatValue() : def;
	}
	
	public int get(String key, int def) {
		return contains(key) ? ((Number) getObject(key)).intValue() : def;
	}
	
	public long get(String key, long def) {
		return contains(key) ? ((Number) getObject(key)).longValue() : def;
	}
	
	public double get(String key, double def) {
		return contains(key) ? ((Number) getObject(key)).doubleValue() : def;
	}
	
	public String get(String key, String def) {
		return contains(key) ? ((String) getObject(key)) : def;
	}
	
	public void put(String key, boolean val) {
		putObject(key, val);
	}
	
	public void put(String key, float val) {
		putObject(key, val);
	}
	
	public void put(String key, int val) {
		putObject(key, val);
	}
	
	public void put(String key, long val) {
		putObject(key, val);
	}
	
	public void put(String key, double val) {
		putObject(key, val);
	}
	
	public void put(String key, String val) {
		putObject(key, val);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof Preferences))
			return false;
		
		Preferences other = (Preferences) obj;
		return other.getAll().equals(getAll());
	}
}
