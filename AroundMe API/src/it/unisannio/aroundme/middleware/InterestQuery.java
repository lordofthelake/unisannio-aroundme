package it.unisannio.aroundme.middleware;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
			Document d = SerializerUtils.newDocument();
			Element container = d.createElement("query");
			container.setAttribute("type", "interest");
			for(long l : obj.getInterestIds()) {
				Element e = d.createElement("interest");
				e.setAttribute("id", String.valueOf(l));
				container.appendChild(e);
			}
			
			return container;
		}
		
	};
	
	private Set<Long> interestIds = new HashSet<Long>();
	
	public void addInterestId(long id) {
		interestIds.add(id);
	}
	
	public Collection<Long> getInterestIds() {
		return interestIds;
	}
}
