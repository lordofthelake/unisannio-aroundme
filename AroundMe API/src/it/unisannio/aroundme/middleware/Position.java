package it.unisannio.aroundme.middleware;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public interface Position extends Entity {
	
	/**
	 * <position lat="0.0" lon="0.0" />
	 */
	public static final Serializer<Position> SERIALIZER = new Serializer<Position>() {

		@Override
		public Position fromXML(Node xml) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Node toXML(Position obj) {
			DocumentBuilder b = SerializerUtils.getDocumentBuilder();
			Document d = b.newDocument();
			
			Element e = d.createElement("position");
			e.setAttribute("lat", String.valueOf(obj.getLatitude()));
			e.setAttribute("lon", String.valueOf(obj.getLongitude()));
			
			return e;
		}
		
	};
	
	double getDistance(Position p);

	double getLatitude();

	double getLongitude();
}
