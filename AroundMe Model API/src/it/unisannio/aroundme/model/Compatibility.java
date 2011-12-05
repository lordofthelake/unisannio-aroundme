package it.unisannio.aroundme.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public interface Compatibility extends Model {
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
			if(!(xml instanceof Element))
				throw new IllegalArgumentException();
			
			Element compatibility = (Element) xml;
			float rank = Float.parseFloat(compatibility.getAttribute("rank"));
			long userId = Long.parseLong(compatibility.getAttribute("userid"));
			
			Compatibility obj = ModelFactory.getInstance().createCompatibility();
			obj.setRank(rank);
			obj.setUserId(userId);
			
			return obj;
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
	void setUserId(long userId);
	void setRank(float rank);
	float getRank();
}
