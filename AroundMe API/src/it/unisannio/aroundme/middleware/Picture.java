package it.unisannio.aroundme.middleware;

import java.net.URL;

import org.w3c.dom.Node;

public interface Picture<T> extends Entity {
	public static final Serializer<Picture<?>> SERIALIZER = new Serializer<Picture<?>>() {

		@Override
		public <U extends Picture<?>> U fromXML(Node xml, U obj) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Node toXML(Picture<?> obj) {
			// TODO Auto-generated method stub
			return null;
		}
		
	};
	
	URL getURL();
	void load(DataListener<T> l);
}
