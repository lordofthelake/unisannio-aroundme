package it.unisannio.aroundme.middleware;

import java.util.Collection;

import org.w3c.dom.Node;

public abstract class UserQuery implements Query<User>, Entity {
	public static final Serializer<UserQuery> SERIALIZER = new Serializer<UserQuery>() {

		@Override
		public <U extends UserQuery> U fromXML(Node xml, U obj) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Node toXML(UserQuery obj) {
			return null;
			// TODO Auto-generated method stub
			
		}
		
	};
	
	public Neighbourhood getNeighbourhood() {
		return null;
	}
	
	public void setNeighbourhood(Neighbourhood n) {
		
	}
	
	public void addInterestId(long id) {
		
	}
	
	public Collection<Long> getInterestIds() {
		return null;
	}
	
	public void setCompatibility(Compatibility c) {
		
	}
	
	public Compatibility getCompatibility() {
		return null;
	}
	
	
}
