package it.unisannio.aroundme.model;

import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public interface Picture<T> extends Model {
	
	/**
	 * <picture>http://url.com/123</picture>
	 */
	public static final Serializer<Picture<?>> SERIALIZER = new Serializer<Picture<?>>() {

		@Override
		public Picture<?> fromXML(Node xml) {
			if(!(xml instanceof Element))
				throw new IllegalArgumentException();
			
			Element picture = (Element) xml;
			try {
				URL url = new URL(picture.getTextContent());
				Picture<?> obj = ModelFactory.getInstance().createPicture();
				obj.setURL(url);
				
				return obj;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
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
	void setURL(URL url);
	void load(DataListener<T> l);
}
