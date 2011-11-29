package it.unisannio.aroundme.middleware;

import org.w3c.dom.Node;

public interface Neighbourhood extends Entity {
	public static final Serializer<Neighbourhood> SERIALIZER = new Serializer<Neighbourhood>() {

		@Override
		public <U extends Neighbourhood> U fromXML(Node xml, U obj) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Node toXML(Neighbourhood obj) {
			// TODO Auto-generated method stub
			return null;
		}
		
	};
	
	Position getPosition();
	double getRadius();
}
