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
			e.appendChild(document.adoptNode(Position.SERIALIZER.toXML(obj.getPosition())));
			
			return e;
		}
		
	};
	
	private static final long serialVersionUID = 1L;
	
	private final Position position;
	private final double radius;
	
	public Neighbourhood(Position position, double radius) {
		if(position == null)
			throw new IllegalArgumentException("Position cannot be null");
		
		this.position = position;
		this.radius = radius;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public double getRadius() {
		return radius;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof Neighbourhood))
			return false;
		
		Neighbourhood other = (Neighbourhood) obj;
		return other.getPosition().equals(getPosition()) && other.getRadius() == getRadius();
	}
}
