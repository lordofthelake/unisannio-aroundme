package it.unisannio.aroundme.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class Compatibility implements Model {
	/**
	 * <compatibility rank="0.0" userid="123" />
	 */
	public static final Serializer<Compatibility> SERIALIZER = new Serializer<Compatibility>() {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Compatibility fromXML(Element node) {
			validateTagName(node, "compatibility");
			
			float rank = Float.parseFloat(getRequiredAttribute(node, "rank"));
			long userId = Long.parseLong(getRequiredAttribute(node, "userid"));
			
			Compatibility obj = new Compatibility(userId, rank);
			
			return obj;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Element toXML(Compatibility obj) {
			Document document = getDocumentBuilder().newDocument();
			
			Element e = document.createElement("compatibility");
			e.setAttribute("rank", String.valueOf(obj.getRank()));
			e.setAttribute("userid", String.valueOf(obj.getUserId()));
			
			return e;
		}
		
	};
	
	private static final long serialVersionUID = 1L;
	
	private long userId;
	private float rank;
	
	public Compatibility(long userId, float rank) {
		this.userId = userId;
		this.rank = rank;
	}
	
	public long getUserId() {
		return userId;
	}
	
	public float getRank() {
		return rank;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof Compatibility))
			return false;
		
		Compatibility c = (Compatibility) obj;
		return getUserId() == c.getUserId() && getRank() == c.getRank();
	}
}
