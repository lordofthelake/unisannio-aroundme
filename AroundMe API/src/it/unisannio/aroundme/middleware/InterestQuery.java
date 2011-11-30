package it.unisannio.aroundme.middleware;

import java.util.Collection;

import org.w3c.dom.Node;

public abstract class InterestQuery implements Query<Interest>, Entity {
	
	/**
	 * <query type="interest">
	 * 	<interest id="123" />
	 * 	<interest id="123" />
	 * </query>
	 */
	public static final Serializer<InterestQuery> SERIALIZER = new Serializer<InterestQuery>() {

		@Override
		public <U extends InterestQuery> U fromXML(Node xml, U obj) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Node toXML(InterestQuery obj) {
			return null;
			// TODO Auto-generated method stub
			
		}
		
	};
	
	public void addInterestId(long id) {
		
	}
	
	public Collection<Long> getInterestIds() {
		return null;
	}
}
