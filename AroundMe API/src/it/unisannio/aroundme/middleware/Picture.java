package it.unisannio.aroundme.middleware;

import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public interface Picture<T> extends Entity {
	
	/**
	 * <picture>http://url.com/123</picture>
	 */
	public static final Serializer<Picture<?>> SERIALIZER = new Serializer<Picture<?>>() {

		@Override
		public <U extends Picture<?>> U fromXML(Node xml, U obj) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Node toXML(Picture<?> obj) {
			Document d = SerializerUtils.newDocument();
			
			Element e = d.createElement("picture");
			e.appendChild(d.createTextNode(obj.getURL().toString()));
			
			return e;
		}
		
	};
	
	URL getURL();
	void load(DataListener<T> l);
}
