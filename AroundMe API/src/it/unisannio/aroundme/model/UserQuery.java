package it.unisannio.aroundme.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class UserQuery implements Query<User>, Model {
	
	/**
	 * <query type="user">
	 * 	<compatibility rank="0.0" userid="123" />
	 * 	<neighbourhood radius="0.0">
	 * 		<position lat="0.0" lon="0.0" />
	 * 	</neighbourhood>
	 * 	<interest-ids>
	 * 		<id>123</id>
	 * 		<id>123</id>
	 * 	</interest-ids>
	 * </query>
	 */
	public static final Serializer<UserQuery> SERIALIZER = new Serializer<UserQuery>() {

		@Override
		public UserQuery fromXML(Node xml) {
			if(!(xml instanceof Element))
				throw new IllegalArgumentException();
			
			UserQuery obj = ModelFactory.getInstance().createUserQuery();
			Element query = (Element) xml;
			
			NodeList compatibilityList = query.getElementsByTagName("compatibility");
			if(compatibilityList.getLength() > 0) {
				Compatibility c = Compatibility.SERIALIZER.fromXML(compatibilityList.item(0));
				obj.setCompatibility(c);
			}
			
			NodeList neighbourhoodList = query.getElementsByTagName("neighbourhood");
			if(neighbourhoodList.getLength() > 0) {
				Neighbourhood n = Neighbourhood.SERIALIZER.fromXML(neighbourhoodList.item(0));
				obj.setNeighbourhood(n);
			}
			
			NodeList interestsList = query.getElementsByTagName("interest-ids");
			if(interestsList.getLength() > 0) {
				Element interests = (Element) interestsList.item(0);
				NodeList ids = interests.getElementsByTagName("id");
				for(int i = 0, len = ids.getLength(); i < len; ++i) {
					Element id = (Element) ids.item(i);
					obj.addInterestId(Long.parseLong(id.getTextContent()));
				}
			}
			
			return obj;
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
				Element interests = d.createElement("interest-ids");
				
				for(long l : obj.getInterestIds()) {
					Element e = d.createElement("id");
					e.setTextContent(String.valueOf(l));
					interests.appendChild(e);
				}
				
				container.appendChild(interests);
			}
			
			return container;
		}
		
	};
	
	private Neighbourhood neighbourhood;
	private Set<Long> interestIds = new HashSet<Long>();
	private Compatibility compatibility;
	
	public Neighbourhood getNeighbourhood() {
		return neighbourhood;
	}
	
	public void setNeighbourhood(Neighbourhood n) {
		neighbourhood = n;
	}
	
	public void addInterestId(long id) {
		interestIds.add(id);
	}
	
	public Collection<Long> getInterestIds() {
		return interestIds;
	}
	
	public void setCompatibility(Compatibility c) {
		compatibility = c;
	}
	
	public Compatibility getCompatibility() {
		return compatibility;
	}
	
	
}