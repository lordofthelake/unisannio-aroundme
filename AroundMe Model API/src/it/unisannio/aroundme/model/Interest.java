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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Modello per rappresentare un interesse di un utente iscritto al network.
 * 
 * Ogni interesse ha una corrispondenza univoca con una pagina di Facebook: ID, nome e categoria trovano una precisa corrispondenza con 
 * quelli restituiti dalle Graph API di Facebook.
 * 
 * @see https://developers.facebook.com/docs/reference/api/
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public abstract class Interest implements Model {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Serializzatore di questo modello.
	 * 
	 * Il formato utilizzato nella codifica &egrave;:
	 * <pre><code>
	 * &lt;interest id="123" name="Name" category="Category" /&gt;
	 * </code></pre>
	 * 
	 * @see Serializer
	 */
	public static final Serializer<Interest> SERIALIZER = new Serializer<Interest>() {

		@Override
		public Interest fromXML(Element node) {
			validateTagName(node, "interest");
			
			long id = Long.parseLong(node.getAttribute("id"));
			String name = node.getAttribute("name");
			String category = node.getAttribute("category");
			
			return ModelFactory.getInstance().createInterest(id, name, category);
		}

		@Override
		public Element toXML(Interest obj) {
			Document document = getDocumentBuilder().newDocument();
			Element interest = document.createElement("interest");
			
			interest.setAttribute("id", String.valueOf(obj.getId()));
			interest.setAttribute("name", obj.getName());
			interest.setAttribute("category", obj.getCategory());
			
			return interest;
		}
		
	};
	
	/**
	 * Restituisce il nome dell'interesse.
	 * 
	 * @return Il nome dell'interesse, coerentemente con i dati di Facebook
	 */
	public abstract String getName();
	
	/**
	 * Restituisce la categoria a cui l'interesse appartiene.
	 * @return Il nome della categoria, coerentemente con quella assegnata da Facebook
	 */
	public abstract String getCategory();
	
	/**
	 * Restituisce l'ID univoco dell'interesse.
	 * 
	 * @return L'ID interesse, corrispondente a quello utilizzato per identificare la pagina nelle Graph API
	 */
	public abstract long getId();
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof Interest))
			return false;
		
		Interest i = (Interest) obj;
		return getId() == i.getId() && getName().equals(i.getName()) && getCategory().equals(i.getCategory());
	}
	
	@Override
	public String toString() {
		return "\"" + getName() + "\" (#" + getId() + ")";
	}
}
