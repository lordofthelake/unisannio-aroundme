package it.unisannio.aroundme.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public abstract class UserQuery implements Callable<Collection<User>>, Model {
	private static final long serialVersionUID = 1L;

	/**
	 * <query>
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
		public UserQuery fromXML(Element node) {
			validateTagName(node, "query");
			
			UserQuery obj = ModelFactory.getInstance().createUserQuery();

			Element compatibility = getSingleElementByTagName(node, "compatibility");
			if(compatibility != null) 
				obj.setCompatibility(Compatibility.SERIALIZER.fromXML(compatibility));
			
			Element neighbourhood = getSingleElementByTagName(node, "neighbourhood");
			if(neighbourhood != null) 
				obj.setNeighbourhood(Neighbourhood.SERIALIZER.fromXML(neighbourhood));
			
			Element interestIds = getSingleElementByTagName(node, "interest-ids");
			if(interestIds != null) {
				NodeList ids = interestIds.getElementsByTagName("id");
				for(int i = 0, len = ids.getLength(); i < len; ++i) {
					Element id = (Element) ids.item(i);
					obj.addInterestId(Long.parseLong(id.getTextContent()));
				}
			}
			
			Element userIds = getSingleElementByTagName(node, "ids");
			if(userIds != null) {
				NodeList ids = userIds.getElementsByTagName("id");
				for(int i = 0, len = ids.getLength(); i < len; ++i) {
					Element id = (Element) ids.item(i);
					obj.addId(Long.parseLong(id.getTextContent()));
				}
			}
			
			return obj;
		}

		@Override
		public Element toXML(UserQuery obj) {
			Document document = getDocumentBuilder().newDocument();
			
			Element container = document.createElement("query");
			
			Neighbourhood n = obj.getNeighbourhood();
			if(n != null)
				container.appendChild(document.importNode(Neighbourhood.SERIALIZER.toXML(n), true));
			
			Compatibility c = obj.getCompatibility();
			if(c != null)
				container.appendChild(document.importNode(Compatibility.SERIALIZER.toXML(c), true));
			
			Collection<Long> iids = obj.getInterestIds();
			if(iids.size() > 0) {
				Element interests = document.createElement("interest-ids");
				
				for(long l : obj.getInterestIds()) {
					Element e = document.createElement("id");
					e.setTextContent(String.valueOf(l));
					interests.appendChild(e);
				}
				
				container.appendChild(interests);
			}
			
			Collection<Long> ids = obj.getIds();
			if(ids.size() > 0) {
				Element ids1 = document.createElement("ids");
				
				for(long l : ids) {
					Element e = document.createElement("id");
					e.setTextContent(String.valueOf(l));
					ids1.appendChild(e);
				}
				
				container.appendChild(ids1);
			}
			
			return container;
		}
		
	};
	
	public static UserQuery byId(long... ids) {
		return ModelFactory.getInstance().createUserQuery().addId(ids);
	}
	
	public static UserQuery byId(Collection<Long> ids) {
		return ModelFactory.getInstance().createUserQuery().addId(ids);
	}
	
	public static Callable<User> single(final long id) {
		return new Callable<User>() {

			@Override
			public User call() throws Exception {
				Collection<? extends User> c = byId(id).call();
				User[] u = c.toArray(new User[0]);
				
				return u.length == 0 ? null : u[0];
			}
			
		};
	}
	
	private Neighbourhood neighbourhood;
	private Set<Long> interestIds = new HashSet<Long>();
	private Compatibility compatibility;
	private Set<Long> ids = new HashSet<Long>();
	
	public Neighbourhood getNeighbourhood() {
		return neighbourhood;
	}
	
	public UserQuery setNeighbourhood(Neighbourhood n) {
		neighbourhood = n;
		
		return this;
	}
	
	public UserQuery addId(long... id) {
		for(long i : id)
			ids.add(i);
		
		return this;
	}
	
	public UserQuery addId(Collection<Long> id) {
		ids.addAll(id);
		
		return this;
	}
	
	public Collection<Long> getIds() {
		return ids;
	}
	
	public UserQuery addInterestId(long... id) {
		for(long i : id)
			interestIds.add(i);
		
		return this;
	}
	
	public UserQuery addInterestId(Collection<Long> id) {
		interestIds.addAll(id);
		
		return this;
	}
	
	public Collection<Long> getInterestIds() {
		return interestIds;
	}
	
	public UserQuery setCompatibility(Compatibility c) {
		compatibility = c;
		
		return this;
	}
	
	public Compatibility getCompatibility() {
		return compatibility;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null && !(obj instanceof UserQuery))
			return false;
		
		UserQuery other = (UserQuery) obj;
		
		Compatibility myCompatibility = getCompatibility();
		Compatibility otherCompatibility = other.getCompatibility();
		
		boolean compatibilityEquals = 
				(myCompatibility == null && otherCompatibility == null)
				|| (myCompatibility != null && myCompatibility.equals(otherCompatibility));
		
		Neighbourhood myNeighbourhood = getNeighbourhood();
		Neighbourhood otherNeighbourhood = other.getNeighbourhood();

		boolean neighbourhoodEquals = 
				(myNeighbourhood == null && otherNeighbourhood == null)
				|| (myNeighbourhood != null && myNeighbourhood.equals(otherNeighbourhood));
		
		Collection<Long> myInterestIds = getInterestIds();
		Collection<Long> otherInterestIds = other.getInterestIds();
		
		boolean interestIdsEquals = 
				(myInterestIds == null && otherInterestIds == null)
				|| (myInterestIds != null && myInterestIds.equals(otherInterestIds));
		
		Collection<Long> myIds = getIds();
		Collection<Long> otherIds = other.getIds();

		boolean idsEquals = 
				(myIds == null && otherIds == null)
				|| (myIds != null && myIds.equals(otherIds));
		
		return compatibilityEquals && neighbourhoodEquals && interestIdsEquals && idsEquals;
	}
	
}
