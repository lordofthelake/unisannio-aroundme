package it.unisannio.aroundme.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
		public Neighbourhood fromXML(Element node) {
			validateTagName(node, "neighbourhood");
			
			double radius = Double.parseDouble(getRequiredAttribute(node, "radius"));
			Element position = getSingleElementByTagName(node, "position");
			
			if(position == null) {
				throw new IllegalArgumentException("A <position> element is required.");
			}
			
			return new Neighbourhood(Position.SERIALIZER.fromXML(position), radius);
		}

		@Override
		public Element toXML(Neighbourhood obj) {
			Document document = getDocumentBuilder().newDocument();
			
			Element e = document.createElement("neighbourhood");
			e.setAttribute("radius", String.valueOf(obj.getRadius()));
			e.appendChild(document.importNode(Position.SERIALIZER.toXML(obj.getPosition()), true));
			
			return e;
		}
		
	};
	
	private static final long serialVersionUID = 1L;
	
	private final Position position;
	private final double radius;
	
	public Neighbourhood(Position position, double radius) {
		this.position = position;
		this.radius = radius;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public double getRadius() {
		return radius;
	}
}
