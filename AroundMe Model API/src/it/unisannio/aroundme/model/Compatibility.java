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
 * Modello che incapsula il grado di compatibilit&agrave; richiesto tra un utente e altri contenuti in un insieme.
 * 
 * Utilizzato tipicamente per comporre criteri di interrogazione, come ad esempio in una {@link UserQuery}.
 * 
 * @see UserQuery#setCompatibility(Compatibility)
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class Compatibility implements Model {
	/** 
	 * Serializzatore di questo modello.
	 * 
	 * <p>Il formato utilizzato nella codifica &egrave;:
	 * <pre><code>
	 * &lt;compatibility rank="0.0" userid="123" /&gt;
	 * </code></pre>
	 * </p>
	 * 
	 * @see Serializer
	 */
	public static final Serializer<Compatibility> SERIALIZER = new Serializer<Compatibility>() {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Compatibility fromXML(Element node) {
			validateTagName(node, "compatibility");
			
			float rank = Float.parseFloat(getRequiredAttribute(node, "rank"));
			long userId = Long.parseLong(getRequiredAttribute(node, "userid"));
			
			Compatibility obj = new Compatibility(userId, rank);
			
			return obj;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Element toXML(Compatibility obj) {
			Document document = getDocumentBuilder().newDocument();
			
			Element e = document.createElement("compatibility");
			e.setAttribute("rank", String.valueOf(obj.getRank()));
			e.setAttribute("userid", String.valueOf(obj.getUserId()));
			
			return e;
		}
		
	};
	
	private static final long serialVersionUID = 1L;
	
	private final long userId;
	private final float rank;
	
	/**
	 * Inizializza un'istanza di Compatibility con i parametri forniti.
	 * 
	 * @param userId l'ID dell'utente che si desidera usare come termine di confronto
	 * @param rank Il grado di compatibilit&agrave; richiesto con l'utente selezionato
	 */
	public Compatibility(long userId, float rank) {
		this.userId = userId;
		this.rank = rank;
	}
	
	/**
	 * Restituisce l'ID dell'utente che viene usato come termine di confronto per l'interrogazione.
	 * 
	 * @return l'ID utente
	 * @see User#getId()
	 */
	public long getUserId() {
		return userId;
	}
	
	/**
	 * Restituisce il rank di compatibilit&agrave; richiesto da questa interrogazione.
	 * 
	 * @return il rank di compatibilit&agrave;
	 * @see User#getCompatibilityRank(User)
	 */
	public float getRank() {
		return rank;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof Compatibility))
			return false;
		
		Compatibility c = (Compatibility) obj;
		return getUserId() == c.getUserId() && getRank() == c.getRank();
	}
	
	@Override
	public String toString() {
		return "{" + getRank() * 100 + "% like #" + getUserId() + "}";
	}
}
