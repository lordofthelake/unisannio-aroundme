package it.unisannio.aroundme.middleware;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public interface Compatibility extends Entity {
	/**
	 * <compatibility rank="0.0" userid="123" />
	 */
	static final Serializer<Compatibility> SERIALIZER = new Serializer<Compatibility>() {

		@Override
		/**
		 * 
		 * @param xml
		 * @param obj
		 * @return
		 */
		public Compatibility fromXML(Node xml) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Node toXML(Compatibility obj) {
			Document d = SerializerUtils.newDocument();
			
			Element e = d.createElement("compatibility");
			e.setAttribute("rank", String.valueOf(obj.getRank()));
			e.setAttribute("userid", String.valueOf(obj.getUserId()));
			
			return e;
		}
		
	};
	
	long getUserId();
	float getRank();
}
