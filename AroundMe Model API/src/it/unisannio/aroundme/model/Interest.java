package it.unisannio.aroundme.model;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public abstract class Interest implements Model {
	private static final long serialVersionUID = 1L;
	
	/**
	 * <interest id="123" name="Name" category="Category" />
	 */
	public static final Serializer<Interest> SERIALIZER = new Serializer<Interest>() {

		@Override
		public Interest fromXML(Element node) {
			validateTagName(node, "interest");
			
			long id = Long.parseLong(node.getAttribute("id"));
			String name = node.getAttribute("name");
			String category = node.getAttribute("category");
			
			return ModelFactory.getInstance().createInterest(id, name, category);
		}

		@Override
		public Element toXML(Interest obj) {
			Document document = getDocumentBuilder().newDocument();
			Element interest = document.createElement("interest");
			
			interest.setAttribute("id", String.valueOf(obj.getId()));
			interest.setAttribute("name", obj.getName());
			interest.setAttribute("category", obj.getCategory());
			
			return interest;
		}
		
	};
	
	public abstract String getName();
	public abstract String getCategory();
	public abstract long getId();
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof Interest))
			return false;
		
		Interest i = (Interest) obj;
		return getId() == i.getId() && getName().equals(i.getName()) && getCategory().equals(i.getCategory());
	}
	
	@Override
	public String toString() {
		return "\"" + getName() + "\" (#" + getId() + ")";
	}
}
