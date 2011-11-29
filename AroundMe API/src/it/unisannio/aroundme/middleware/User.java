package it.unisannio.aroundme.middleware;

import java.util.Collection;

import org.w3c.dom.Node;

public interface User extends Entity {
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
