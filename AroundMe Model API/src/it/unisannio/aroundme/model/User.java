package it.unisannio.aroundme.model;


import java.util.Collection;
import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public abstract class User implements Model {
	private static final long serialVersionUID = 1L;
	
	/**
	 * <user id="123" name="Name">
	 * 	<position lat="0.0" lon="0.0" />
	 * 	<picture>http://url.com/123</picture>
	 *  <interests>
	 *  	<interest id="123" />
	 *  	<interest id="123" />
	 *  </interests>
	 * </user>
	 */
	public static final Serializer<User> SERIALIZER = new Serializer<User>() {

		@Override
		public User fromXML(Node xml) {
			if(!(xml instanceof Element))
				throw new IllegalArgumentException();
			
			Element user = (Element) xml;
			
			long id = Long.parseLong(user.getAttribute("id"));
			String name = user.getAttribute("name");
			
			Collection<Interest> interestCollection = new HashSet<Interest>();
			NodeList interestsList = user.getElementsByTagName("interests");
			if(interestsList.getLength() > 0) {
				Element interests = (Element) interestsList.item(0);
				
				NodeList interestList = interests.getElementsByTagName("interest");
				for(int i = 0, len = interestList.getLength(); i < len; ++i) {
					Interest interest = Interest.SERIALIZER.fromXML(interestList.item(i));
					interestCollection.add(interest);
				}
			}
			
			// FIXME la posizione non viene deserializzata
			
			return ModelFactory.getInstance().createUser(id, name, interestCollection);
				
		}

		@Override
		public Node toXML(User obj) {
			Document d = SerializerUtils.newDocument();
			Element user = d.createElement("user");
			user.setAttribute("id", String.valueOf(obj.getId()));
			user.setAttribute("name", obj.getName());
			
			Position p = obj.getPosition();
			if(p != null)
				user.appendChild(SerializerUtils.toXML(obj.getPosition()));
			
			Collection<Interest> iids = obj.getInterests();
			if(iids.size() > 0) {
				Element interests = d.createElement("interests");
				for(Interest i : iids) {
					interests.appendChild(SerializerUtils.toXML(i));
				}
				user.appendChild(interests);
			}
			
			// FIXME la posizione non viene serializzata
			
			return user;
		}
		
	};
	
	public abstract long getId();
	
	public abstract String getName();
	
	public abstract Position getPosition();
	
	public abstract void setPosition(Position p);
	
	public abstract Collection<Interest> getInterests();	
	
	/**
	 * Il rank &egrave; una misura di compatibilit&agrave tra due utenti, espresso con un numero decimale tra 0 (nessun interesse comune) e 1 (tutti gli interessi in comune).
	 * Notare che il rank non &egrave garantito essere simmetrico tra i due utenti.
	 * 
	 * @param u l'utente su cui viene fatto il confronto
	 * @return misura del rank, con 0 <= rank <= 1
	 * 
	 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
	 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
	 */
	public float getCompatibilityRank(User u) {
		return getCompatibilityRank(getInterests(), u.getInterests());		
	}
	
	protected <E> float getCompatibilityRank(Collection<E> myInterests, Collection<E> otherInterests) {
		if(myInterests.isEmpty())
			return 0;
		
		Collection<E> commonInterests = new HashSet<E>(myInterests);
		commonInterests.retainAll(otherInterests);
		
		return commonInterests.size() / myInterests.size();
	}
	
	/**
	 * Calcola la distanza tra i due utenti, espressa in metri.
	 * 
	 * @param u l'utente da cui calcolare la distanza
	 * @return la distanza in metri, -1 se non &egrave; possibile calcolarla
	 * 
	 * @see Position#getDistance(Position)
	 */
	public double getDistance(User u) {
		Position mine = getPosition();
		Position hers = u.getPosition();
		
		return (mine == null || hers == null) ? -1 : mine.getDistance(hers);
	}
}
