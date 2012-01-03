package it.unisannio.aroundme.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public abstract class UserQuery implements Callable<Collection<User>>, Model {
	private static final long serialVersionUID = 1L;

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
			
			NodeList idList = query.getElementsByTagName("ids");
			if(idList.getLength() > 0) {
				NodeList ids = ((Element) idList.item(0)).getElementsByTagName("id");
				for(int i = 0, len = ids.getLength(); i < len; ++i) {
					Element id = (Element) ids.item(i);
					obj.addId(Long.parseLong(id.getTextContent()));
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
			
			Collection<Long> ids = obj.getIds();
			if(ids.size() > 0) {
				Element ids1 = d.createElement("ids");
				
				for(long l : ids) {
					Element e = d.createElement("id");
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
}
