package it.unisannio.aroundme.middleware;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public interface Interest extends Entity {
	
	/**
	 * <interest id="123">
	 * 	<name>Name</name>
	 * 	<picture>http://url.com/123</picture>
	 * </interest>
	 */
	public static final Serializer<Interest> SERIALIZER = new Serializer<Interest>() {

		@Override
		public Interest fromXML(Node xml) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Node toXML(Interest obj) {
			Document d = SerializerUtils.newDocument();
			Element interest = d.createElement("interest");
			interest.setAttribute("id", String.valueOf(obj.getId()));
			
			Element name = d.createElement("name");
			name.appendChild(d.createTextNode(obj.getName()));
			interest.appendChild(name);
			
			Picture<?> p = obj.getPicture();
			if(p != null) 
				interest.appendChild(SerializerUtils.toXML(p));
			
			return interest;
		}
		
	};
	
	String getName();
	<U> Picture<U> getPicture();
	long getId();
}
