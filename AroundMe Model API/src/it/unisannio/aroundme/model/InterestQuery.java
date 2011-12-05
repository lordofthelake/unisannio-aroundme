package it.unisannio.aroundme.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public abstract class InterestQuery implements Query<Interest>, Model {
	private static final long serialVersionUID = 1L;

	/**
	 * <query type="interest">
	 * 	<id>123</id>
	 * 	<id>123</id>
	 * </query>
	 */
	public static final Serializer<InterestQuery> SERIALIZER = new Serializer<InterestQuery>() {

		@Override
		public InterestQuery fromXML(Node xml) {
			InterestQuery obj = ModelFactory.getInstance().createInterestQuery();
			if(!(xml instanceof Element))
				throw new IllegalArgumentException();
			
			Element query = (Element) xml;
			
			if(!query.getTagName().equals("query") || !"interest".equals(query.getAttribute("type"))) 
				throw new IllegalArgumentException();
			
			NodeList list = query.getElementsByTagName("id");
			
			for(int i = 0, len = list.getLength(); i < len; ++i) {
				Element interest = (Element) list.item(i);
				long id = Long.parseLong(interest.getTextContent());
				obj.addInterestId(id);
			}
			
			return obj;
		}

		@Override
		public Node toXML(InterestQuery obj) {
			Document d = SerializerUtils.newDocument();
			Element container = d.createElement("query");
			container.setAttribute("type", "interest");
			for(long l : obj.getInterestIds()) {
				Element e = d.createElement("id");
				e.setTextContent(String.valueOf(l));
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
