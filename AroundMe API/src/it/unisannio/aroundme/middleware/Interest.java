package it.unisannio.aroundme.middleware;

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
		public <U extends Interest> U fromXML(Node xml, U obj) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Node toXML(Interest obj) {
			return null;
			// TODO Auto-generated method stub
			
		}
		
	};
	
	String getName();
	<U> Picture<U> getPicture();
	long getId();
}
