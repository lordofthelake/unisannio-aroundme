package it.unisannio.aroundme.middleware;

import javax.xml.parsers.DocumentBuilder;

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
		public <U extends Neighbourhood> U fromXML(Node xml, U obj) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Node toXML(Neighbourhood obj) {
			DocumentBuilder b = Factory.getDocumentBuilder();
			Document d = b.newDocument();
			
			Element e = d.createElement("neighbourhood");
			e.setAttribute("radius", String.valueOf(obj.getRadius()));
			
			return e;
		}
		
	};
	
	Position getPosition();
	double getRadius();
}
