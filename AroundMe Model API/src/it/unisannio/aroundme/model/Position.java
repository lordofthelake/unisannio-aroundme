package it.unisannio.aroundme.model;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public interface Position extends Model {
	
	/**
	 * <position lat="0.0" lon="0.0" />
	 */
	public static final Serializer<Position> SERIALIZER = new Serializer<Position>() {

		@Override
		public Position fromXML(Node xml) {
			if(!(xml instanceof Element)) 
				throw new IllegalArgumentException();
			
			Element position = (Element) xml;
			double lat = Double.parseDouble(position.getAttribute("lat"));
			double lon = Double.parseDouble(position.getAttribute("lon"));
			
			Position obj = ModelFactory.getInstance().createPosition();
			obj.setLatitude(lat);
			obj.setLongitude(lon);
			
			return obj;
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

	void setLongitude(double lon);

	void setLatitude(double lat);

	double getLatitude();

	double getLongitude();
}
