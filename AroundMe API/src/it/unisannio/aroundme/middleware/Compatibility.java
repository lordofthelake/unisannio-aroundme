package it.unisannio.aroundme.middleware;

import org.w3c.dom.Node;

public interface Compatibility extends Entity {
	static final Serializer<Compatibility> SERIALIZER = new Serializer<Compatibility>() {

		@Override
		public <U extends Compatibility> U fromXML(Node xml, U obj) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Node toXML(Compatibility obj) {
			// TODO Auto-generated method stub
			return null;
		}
		
	};
	
	long getUserId();
	float getRank();
}
