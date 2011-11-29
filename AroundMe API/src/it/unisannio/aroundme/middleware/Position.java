package it.unisannio.aroundme.middleware;

import org.w3c.dom.Node;

public interface Position extends Entity {
	public static final Serializer<Position> SERIALIZER = new Serializer<Position>() {

		@Override
		public <U extends Position> U fromXML(Node xml, U obj) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Node toXML(Position obj) {
			// TODO Auto-generated method stub
			return null;
		}
		
	};
	
	double getDistance(Position p);
}
