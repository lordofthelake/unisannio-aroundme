package it.unisannio.aroundme.middleware;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public interface Neighbourhood extends Entity {
	
	/**
	 * <neighbourhood radius="0.0">
	 * 	<position lat="0.0" lon="0.0" />
	 * </neighbourhood>
	 */
	public static final Serializer<Neighbourhood> SERIALIZER = new Serializer<Neighbourhood>() {

		@Override
		public Neighbourhood fromXML(Node xml) {
			// TODO Auto-generated method stub
			return null;
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
	
	Position getPosition();
	double getRadius();
}
