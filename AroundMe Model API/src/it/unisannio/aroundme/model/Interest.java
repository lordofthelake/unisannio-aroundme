package it.unisannio.aroundme.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public interface Interest extends Model {
	
	/**
	 * <interest id="123" name="Name" category="Category">
	 * 	<picture>http://url.com/123</picture>
	 * </interest>
	 */
	public static final Serializer<Interest> SERIALIZER = new Serializer<Interest>() {

		@Override
		public Interest fromXML(Node xml) {
			if(!(xml instanceof Element)) 
				throw new IllegalArgumentException();
			
			Element interest = (Element) xml;
			long id = Long.parseLong(interest.getAttribute("id"));
			String name = interest.getAttribute("name");
			String category = interest.getAttribute("category");
			
			Interest obj = ModelFactory.getInstance().createInterest();
			obj.setId(id);
			obj.setName(name);
			obj.setCategory(category);
			
			return obj;
		}

		@Override
		public Node toXML(Interest obj) {
			Document d = SerializerUtils.newDocument();
			Element interest = d.createElement("interest");
			interest.setAttribute("id", String.valueOf(obj.getId()));
			interest.setAttribute("name", obj.getName());
			interest.setAttribute("category", obj.getCategory());
			
			Picture<?> p = obj.getPicture();
			if(p != null) 
				interest.appendChild(SerializerUtils.toXML(p));
			
			return interest;
		}
		
	};
	
	String getName();
	String getCategory();
	void setCategory(String category);
	void setName(String name);
	void setId(long id);
	<U> Picture<U> getPicture();
	long getId();
}
