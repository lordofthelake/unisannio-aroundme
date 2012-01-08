package it.unisannio.aroundme.model;


import java.util.Collection;
import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
	
	public abstract long getId();
	
	public abstract void setPosition(Position position);

	public abstract String getName();
	
	public abstract Position getPosition();
	
	public abstract Collection<Interest> getInterests();	
	
	/**
	 * Il rank &egrave; una misura di compatibilit&agrave tra due utenti, espresso con un numero decimale tra 0 (nessun interesse comune) e 1 (tutti gli interessi in comune).
	 * 
	 * @param u l'utente su cui viene fatto il confronto
	 * @return misura del rank, con 0 <= rank <= 1 o -1 se entrambi i set sono vuoti
	 * 
	 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
	 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
	 */
	public float getCompatibilityRank(User u) {
		return getCompatibilityRank(getInterests(), u.getInterests());		
	}
	
	protected <E> float getCompatibilityRank(Collection<E> myInterests, Collection<E> otherInterests) {
		if(myInterests.isEmpty() && otherInterests.isEmpty())
			return -1;
		
		Collection<E> commonInterests = new HashSet<E>(myInterests);
		commonInterests.retainAll(otherInterests);
		
		return 2.0f * commonInterests.size() / (myInterests.size() + otherInterests.size());
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
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof User))
			return false;
		
		User other = (User) obj;
		return getId() == other.getId() 
				&& getName().equals(other.getName())
				&& getInterests().equals(other.getInterests());
	}
	
	@Override
	public String toString() {
		return getName() + "(#" + getId() + ") " + getInterests();
	}
}
