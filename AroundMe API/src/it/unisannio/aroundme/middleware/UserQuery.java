package it.unisannio.aroundme.middleware;

import java.util.Collection;

import org.w3c.dom.Node;

public abstract class UserQuery implements Query<User>, Entity {
	
	/**
	 * <query type="user">
	 * 	<compatibility rank="0.0" userid="123" />
	 * 	<neighbourhood radius="0.0">
	 * 		<position lat="0.0" lon="0.0" />
	 * 	</neighbourhood>
	 * 	<interests>
	 * 		<interest id="123" />
	 * 		<interest id="123" />
	 * 	</interests>
	 * </query>
	 */
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
