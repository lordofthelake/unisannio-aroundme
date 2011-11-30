package it.unisannio.aroundme.middleware;

import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public interface User extends Entity {
	
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
			User obj = Factory.getInstance().createUser();
			
			long id = Long.parseLong(user.getAttribute("id"));
			String name = user.getAttribute("name");
			obj.setId(id);
			obj.setName(name);
			
			NodeList pictureList = user.getElementsByTagName("picture");
			if(pictureList.getLength() > 0) {
				Element picture = (Element) pictureList.item(0);
				Picture<?> p = Picture.SERIALIZER.fromXML(picture);
				obj.setPicture(obj);
			}
			
			NodeList interestsList = user.getElementsByTagName("interests");
			if(interestsList.getLength() > 0) {
				Element interests = (Element) interestsList.item(0);
				
				NodeList interestList = interests.getElementsByTagName("interest");
				for(int i = 0, len = interestList.getLength(); i < len; ++i) {
					Interest interest = Interest.SERIALIZER.fromXML(interestList.item(i));
					obj.addInterest(interest);
				}
			}
			
			return obj;
				
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
			
			return user;
		}
		
	};
	
	long getId();
	
	void setName(String name);

	void setId(long id);

	void setPicture(User obj);

	void addInterest(Interest interest);

	String getName();
	
	Position getPosition();
	
	<U> Picture<U> getPicture();
	
	Collection<Interest> getInterests();	
}
