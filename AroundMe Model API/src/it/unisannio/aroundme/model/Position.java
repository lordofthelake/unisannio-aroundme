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
public abstract class Position implements Model {
	private static final long serialVersionUID = 1L;
	
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
			
			Position obj = ModelFactory.getInstance().createPosition(lat, lon);
			
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
	
	/**
	 * Metodo testato e funzionante sul calcolo della distanza tra due punti.
	 * Il risutato è concorde con le misure effettuate con altri software.
	 * Restituisce la distanza in metri tra due punti.
	 * 
	 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
	 */
	public double getDistance(Position p){
		double lat1 = Math.toRadians(getLatitude());
		double lat2 = Math.toRadians(p.getLatitude());
		double lon1 = Math.toRadians(getLongitude());
		double lon2 = Math.toRadians(p.getLongitude());
		double dist =  Math.cos(lon1 -lon2) * Math.cos(lat1) * Math.cos(lat2) +  Math.sin(lat1) * Math.sin(lat2);
		dist = Math.acos(dist) * 6378;
		return Math.round(dist * 1000);
	}

	public abstract double getLatitude();

	public abstract double getLongitude();
}
