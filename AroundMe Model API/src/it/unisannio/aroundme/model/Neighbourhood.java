package it.unisannio.aroundme.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class Neighbourhood implements Model {
	
	/**
	 * <neighbourhood radius="0.0">
	 * 	<position lat="0.0" lon="0.0" />
	 * </neighbourhood>
	 */
	public static final Serializer<Neighbourhood> SERIALIZER = new Serializer<Neighbourhood>() {

		@Override
		public Neighbourhood fromXML(Node xml) {
			if(!(xml instanceof Element))
				throw new IllegalArgumentException();
			
			Element neighbourhood = (Element) xml;
			if(!neighbourhood.getTagName().equals("neighbourhood")) 
				throw new IllegalArgumentException();
			
			double radius = Double.parseDouble(neighbourhood.getAttribute("radius"));
			Element position = (Element) neighbourhood.getElementsByTagName("position").item(0);
			Position p = Position.SERIALIZER.fromXML(position);
			
			Neighbourhood obj = new Neighbourhood();
			obj.setRadius(radius);
			obj.setPosition(p);
			
			return obj;
		}

		@Override
		public Node toXML(Neighbourhood obj) {
			Document d = SerializerUtils.newDocument();
			
			Element e = d.createElement("neighbourhood");
			e.setAttribute("radius", String.valueOf(obj.getRadius()));
			e.appendChild(SerializerUtils.toXML(obj.getPosition()));
			
			return e;
		}
		
	};
	
	private static final long serialVersionUID = 1L;
	
	private Position position;
	private double radius;
	
	public Position getPosition() {
		return position;
	}
	
	public void setPosition(Position p) {
		this.position = p;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public void setRadius(double radius) {
		this.radius = radius;
	}
}
