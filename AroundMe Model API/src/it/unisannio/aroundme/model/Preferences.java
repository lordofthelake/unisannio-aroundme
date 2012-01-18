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
package it.unisannio.aroundme.model;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Modello di una mappa associativa chiave-valore per lo storage delle preferenze.
 * 
 * <p>Sei tipi di valori sono supportati: {@code String}, {@code int}, {@code float}, {@code double}, {@code long} e {@code boolean}.
 * Il tentativo di inserire valori diversi da quelli supportati o di ottenere il valore di una chiave con un tipo diverso da quello del 
 * valore memorizzato generalmente risulta sollevare una {@code ClassCastException}.</p>
 * 
 * <p>L'uso di chiavi contenenti spazi o altri caratteri non consentiti come in XML come nome di tag risulteranno in errori in fase di 
 * serializzazione/deserializzazione e va pertanto evitato.</p>
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public abstract class Preferences implements Model {
	private static final long serialVersionUID = 1L;

	/**
	 * Serializzatore di questo modello.
	 * 
	 * Il formato utilizzato nella codifica &egrave;:
	 * <pre><code>
	 * &lt;preferences&gt;
	 *   &lt;keyName type="string|float|double|int|long|boolean"&gt;value&lt;/keyName&gt;
	 *   &lt;!-- ... --&gt;
	 * &lt;/preferences&gt;
	 * </code></pre>
	 * 
	 * @see Serializer
	 */
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
				Object value = e.getValue();
				if(value == null)
					continue;
				
				entry.setTextContent(value.toString());
				Class<?> clazz = value.getClass();
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

	/**
	 * Restituisce una mappa contenente tutte le coppie chiave-valore contenute nell'istanza.
	 * 
	 * @return una mappa che riflette lo stato attuale dell'oggetto
	 */
	public abstract Map<String, Object> getAll();
	
	/**
	 * Controlla se l'istanza contiene la chiave specificata-
	 * 
	 * @param key la chiave da controllare
	 * @return {@code true} se esiste una preferenza con la chiave specificata, {@code false} altrimenti
	 */
	public abstract boolean contains(String key);
	
	/**
	 * Restituisce il valore associato alla chiave indicata.
	 * 
	 * @param key la chiave di cui si desidera conoscere il valore associato
	 * @return il valore memorizzato nelle preferenze, o {@code null} se non presente
	 */
	protected abstract Object getObject(String key);
	
	/**
	 * Inserisce nella mappa la coppia chiave-valore.
	 * 
	 * Se essa &egrave; gi&agrave; presente nelle preferenze, viene sovrascritta. 
	 * 
	 * @param key la chiave da salvare
	 * @param val il valore da associare alla chiave
	 */
	protected abstract void putObject(String key, Object val);
	
	/**
	 * Restituisce il valore booleano memorizzato nelle preferenze.
	 * 
	 * @param key la chiave su cui effettuare la ricerca
	 * @param def il valore di default da restituire nel caso in cui la chiave non sia presente
	 * @return il valore memorizzato nelle preferenze, o quello di default se non presente
	 * @throws ClassCastException se alla chiave &egrave; associato un valore non booleano
	 */
	public boolean get(String key, boolean def) {
		return contains(key) ? ((Boolean) getObject(key)) : def;
	}
	
	/**
	 * Restituisce il valore numerico memorizzato nelle preferenze, come float.
	 * 
	 * @param key la chiave su cui effettuare la ricerca
	 * @param def il valore di default da restituire nel caso in cui la chiave non sia presente
	 * @return il valore memorizzato nelle preferenze, o quello di default se non presente
	 * @throws ClassCastException se alla chiave &egrave; associato un valore non numerico
	 */
	public float get(String key, float def) {
		return contains(key) ? ((Number) getObject(key)).floatValue() : def;
	}
	
	/**
	 * Restituisce il valore numerico memorizzato nelle preferenze, come int.
	 * 
	 * @param key la chiave su cui effettuare la ricerca
	 * @param def il valore di default da restituire nel caso in cui la chiave non sia presente
	 * @return il valore memorizzato nelle preferenze, o quello di default se non presente
	 * @throws ClassCastException se alla chiave &egrave; associato un valore non numerico
	 */
	public int get(String key, int def) {
		return contains(key) ? ((Number) getObject(key)).intValue() : def;
	}
	
	/**
	 * Restituisce il valore numerico memorizzato nelle preferenze, come long.
	 * 
	 * @param key la chiave su cui effettuare la ricerca
	 * @param def il valore di default da restituire nel caso in cui la chiave non sia presente
	 * @return il valore memorizzato nelle preferenze, o quello di default se non presente
	 * @throws ClassCastException se alla chiave &egrave; associato un valore non numerico
	 */
	public long get(String key, long def) {
		return contains(key) ? ((Number) getObject(key)).longValue() : def;
	}
	
	/**
	 * Restituisce il valore numerico memorizzato nelle preferenze, come double.
	 * 
	 * @param key la chiave su cui effettuare la ricerca
	 * @param def il valore di default da restituire nel caso in cui la chiave non sia presente
	 * @return il valore memorizzato nelle preferenze, o quello di default se non presente
	 * @throws ClassCastException se alla chiave &egrave; associato un valore non numerico
	 */
	public double get(String key, double def) {
		return contains(key) ? ((Number) getObject(key)).doubleValue() : def;
	}
	
	/**
	 * Restituisce il valore stringa memorizzato nelle preferenze.
	 * 
	 * @param key la chiave su cui effettuare la ricerca
	 * @param def il valore di default da restituire nel caso in cui la chiave non sia presente
	 * @return il valore memorizzato nelle preferenze, o quello di default se non presente
	 * @throws ClassCastException se alla chiave &egrave; associato un valore che non sia una stringa
	 */
	public String get(String key, String def) {
		return contains(key) ? ((String) getObject(key)) : def;
	}
	
	/**
	 * Inserisce un valore booleano nelle preferenze.
	 * 
	 * @param key la chiave con cui la preferenza verr&agrave; salvata
	 * @param val il valore da salvare
	 */
	public void put(String key, boolean val) {
		putObject(key, val);
	}
	
	/**
	 * Inserisce un valore numerico nelle preferenze, come float.
	 * 
	 * @param key la chiave con cui la preferenza verr&agrave; salvata
	 * @param val il valore da salvare
	 */
	public void put(String key, float val) {
		putObject(key, val);
	}
	
	/**
	 * Inserisce un valore numerico nelle preferenze, come int.
	 * 
	 * @param key la chiave con cui la preferenza verr&agrave; salvata
	 * @param val il valore da salvare
	 */
	public void put(String key, int val) {
		putObject(key, val);
	}
	
	/**
	 * Inserisce un valore numerico nelle preferenze, come long.
	 * 
	 * @param key la chiave con cui la preferenza verr&agrave; salvata
	 * @param val il valore da salvare
	 */
	public void put(String key, long val) {
		putObject(key, val);
	}
	
	/**
	 * Inserisce un valore numerico nelle preferenze, come double.
	 * 
	 * @param key la chiave con cui la preferenza verr&agrave; salvata
	 * @param val il valore da salvare
	 */
	public void put(String key, double val) {
		putObject(key, val);
	}
	
	/**
	 * Inserisce una stringa nelle preferenze.
	 * 
	 * @param key la chiave con cui la preferenza verr&agrave; salvata
	 * @param val il valore da salvare
	 */
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
	
	/**
	 * Include tutte le entry dalla mappa speficata
	 *  
	 * @param map la mappa da cui includere le entry 
	 * @throws ClassCastException se i valori sono di un tipo diverso da quello supportato
	 */
	public void putAll(Map<String, ?> map){
		Iterator<String> keyIterator = map.keySet().iterator();
		while(keyIterator.hasNext()){
			String key = keyIterator.next();
			
			Object value = map.get(key);
			if(!Arrays.<Class<?>>asList(String.class, Integer.class, Long.class, Boolean.class, Float.class, Double.class).contains(value.getClass()))
				throw new ClassCastException("Cannot store a value of type " +value.getClass().getName() + " for the preference '" + key + "'");
			putObject(key, value);
		}
	}
	
	
	
}
