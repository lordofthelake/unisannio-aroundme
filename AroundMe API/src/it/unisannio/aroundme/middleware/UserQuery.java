package it.unisannio.aroundme.middleware;

import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
		public UserQuery fromXML(Node xml) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Node toXML(UserQuery obj) {
			Document d = SerializerUtils.newDocument();
			Element container = d.createElement("query");
			container.setAttribute("type", "user");
			
			Neighbourhood n = obj.getNeighbourhood();
			if(n != null)
				container.appendChild(SerializerUtils.toXML(n));
			
			Compatibility c = obj.getCompatibility();
			if(c != null)
				container.appendChild(SerializerUtils.toXML(c));
			
			Collection<Long> iids = obj.getInterestIds();
			if(iids.size() > 0) {
				Element interests = d.createElement("interests");
				
				for(long l : obj.getInterestIds()) {
					Element e = d.createElement("interest");
					e.setAttribute("id", String.valueOf(l));
					interests.appendChild(e);
				}
				
				container.appendChild(interests);
			}
			
			return container;
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
