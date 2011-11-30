package it.unisannio.aroundme.middleware;

import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public interface User extends Entity {
	
	/**
	 * <user id="123">
	 * 	<name>Name</name>
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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Node toXML(User obj) {
			Document d = SerializerUtils.newDocument();
			Element user = d.createElement("user");
			user.setAttribute("id", String.valueOf(obj.getId()));
			
			Element name = d.createElement("name");
			name.appendChild(d.createTextNode(obj.getName()));
			user.appendChild(name);
			
			Position p = obj.getPosition();
			if(p != null)
				user.appendChild(SerializerUtils.toXML(obj.getPosition()));
			
			Collection<Long> iids = obj.getInterestIds();
			if(iids.size() > 0) {
				Element interests = d.createElement("interests");
				for(long l : iids) {
					Element interest = d.createElement("interest");
					interest.setAttribute("id", String.valueOf(l));
					interests.appendChild(interest);
				}
				user.appendChild(interests);
			}
			
			return user;
		}
		
	};
	
	long getId();
	
	String getName();
	
	Position getPosition();
	
	<U> Picture<U> getPicture();
	
	Collection<Long> getInterestIds();
	
	void getInterests(DataListener<Collection<Interest>> l);
	
}
