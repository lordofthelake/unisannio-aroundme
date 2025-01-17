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


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Modello per un utente iscritto al network.
 * 
 * <p>Ogni utente &egrave; provvisto di un ID univoco corrispondente a quello utilizzato per la stessa persona da Facebook, di un
 * nome (anch'esso creato utilizzando i dati di Facebook, nella forma di "Nome Cognome") e di un insieme di interessi.</p>
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 * 
 * @see https://developers.facebook.com/docs/reference/api/
 * @see UserQuery
 */
public abstract class User implements Model {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Serializzatore di questo modello.
	 * 
	 * <p>Il formato utilizzato nella codifica &egrave;:
	 * <pre><code>
	 * &lt;user id="123" name="Name"&gt;
	 * 	&lt;position lat="0.0" lon="0.0" /&gt;
	 *  &lt;interests&gt;
	 *  	&lt;!-- lista degli interessi --&gt;
	 *  &lt;/interests&gt;
	 * &lt;/user&gt;
	 * </code></pre>
	 * </p>
	 * 
	 * @see Serializer
	 */
	public static final Serializer<User> SERIALIZER = new Serializer<User>() {

		@Override
		public User fromXML(Element node) {
			validateTagName(node, "user");
			
			long id = Long.parseLong(getRequiredAttribute(node, "id"));
			String name = getRequiredAttribute(node, "name");
			
			Collection<Interest> interestCollection = new HashSet<Interest>();
			Element interests = getSingleElementByTagName(node, "interests");
			
			if(interests != null) {
				
				NodeList interestList = interests.getElementsByTagName("interest");
				for(int i = 0, len = interestList.getLength(); i < len; ++i) {
					interestCollection.add(Interest.SERIALIZER.fromXML((Element)interestList.item(i)));
				}
			}
			
			User user = ModelFactory.getInstance().createUser(id, name, interestCollection);
			
			Element position = getSingleElementByTagName(node, "position");
			if(position != null)
				user.setPosition(Position.SERIALIZER.fromXML(position));
			
			return user;
				
		}

		@Override
		public Element toXML(User obj) {
			Document document = getDocumentBuilder().newDocument();
			
			Element user = document.createElement("user");
			user.setAttribute("id", String.valueOf(obj.getId()));
			user.setAttribute("name", obj.getName());
			
			Position position = obj.getPosition();
			if(position != null)
				user.appendChild(document.adoptNode(Position.SERIALIZER.toXML(position)));
			
			Collection<Interest> userInterests = obj.getInterests();
			if(userInterests.size() > 0) {
				Element interests = document.createElement("interests");
				for(Interest i : userInterests) {
					if(i != null)
						interests.appendChild(document.adoptNode(Interest.SERIALIZER.toXML(i)));
				}
				user.appendChild(interests);
			}
			
			return user;
		}
		
	};
	
	/**
	 * Restituisce l'ID univoco di questo utente, corrispondente a quello assegnato da Facebook.
	 * 
	 * @return l'id dell'utente
	 */
	public abstract long getId();
	
	/**
	 * Imposta la posizione corrente dell'utente.
	 * 
	 * @param position la posizione in cui si trova l'utente
	 */
	public abstract void setPosition(Position position);

	/**
	 * Restituisce il nome dell'utente, tipicamente nella forma "Nome Cognome"
	 * 
	 * @return il nome dell'utente
	 */
	public abstract String getName();
	
	/**
	 * Restituisce la posizione corrente dell'utente.
	 * 
	 * @return la posizione dell'utente, o {@code null} se sconosciuta
	 */
	public abstract Position getPosition();
	
	/**
	 * Restituisce una collezione degli interessi dell'utente.
	 * 
	 * @return una collezione di interessi
	 */
	public abstract Collection<Interest> getInterests();	
	
	/**
	 * Restituisce il rank tra due utenti.
	 * 
	 * <p>Il rank &egrave; una misura di compatibilit&agrave tra due utenti, espresso con un numero decimale tra 0 (nessun interesse comune) e 1 (tutti gli interessi in comune).</p>
	 * 
	 * @param u l'utente su cui viene fatto il confronto
	 * @return misura del rank, con 0 <= rank <= 1, o -1 se entrambi i set sono vuoti
	 * 
	 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
	 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
	 */
	public float getCompatibilityRank(User u) {
		return getCompatibilityRank(getInterests(), u.getInterests());		
	}
	
	/**
	 * Effettua il calcolo del rank utilizzando come interessi insiemi generici ci oggetti.
	 * 
	 * @param myInterests primo insieme di interessi
	 * @param otherInterests secondo insieme di interessi
	 * @return il rank calcolato
	 * 
	 * @see #getCompatibilityRank(User)
	 */
	protected <E> float getCompatibilityRank(Collection<E> myInterests, Collection<E> otherInterests) {
		if(myInterests.isEmpty() && otherInterests.isEmpty())
			return -1;
		
		ArrayList<E> commonInterests=new ArrayList<E>();
		ArrayList<E> myInterestsList=new ArrayList<E>(myInterests);
		ArrayList<E> otherInterestsList=new ArrayList<E>(otherInterests);
		for (int i=0;i<myInterests.size();i++){
			if (otherInterestsList.contains(myInterestsList.get(i)))
				commonInterests.add(myInterestsList.get(i));
		}
		return 2.0f * commonInterests.size() / (myInterestsList.size() + otherInterestsList.size());
	}
	
	/**
	 * Calcola la distanza tra i due utenti, espressa in metri.
	 * 
	 * @param u l'utente da cui calcolare la distanza
	 * @return la distanza in metri, -1 se non &egrave; possibile calcolarla
	 * 
	 * @see Position#getDistance(Position)
	 */
	public int getDistance(User u) {
		Position mine = getPosition();
		Position hers = u.getPosition();
		
		return (mine == null || hers == null) ? -1 : mine.getDistance(hers);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof User))
			return false;
		
		User other = (User) obj;
		return getId() == other.getId() 
				&& getName().equals(other.getName())
				&& getInterests().containsAll(other.getInterests());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getName() + "(#" + getId() + ") " + getInterests();
	}
}
