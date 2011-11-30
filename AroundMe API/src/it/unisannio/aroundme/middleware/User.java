package it.unisannio.aroundme.middleware;

import java.util.Collection;

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
		public <U extends User> U fromXML(Node xml, U obj) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Node toXML(User obj) {
			return null;
			// TODO Auto-generated method stub
			
		}
		
	};
	
	String getName();
	
	Position getPosition();
	
	<U> Picture<U> getPicture();
	
	Collection<Long> getInterestIds();
	
	void loadInterests(DataListener<Collection<Interest>> l);
	
}
