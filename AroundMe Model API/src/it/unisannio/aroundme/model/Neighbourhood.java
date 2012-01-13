package it.unisannio.aroundme.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Modello per descrivere un "intorno" rispetto ad una posizione.
 * 
 * Usato in un'interrogazione, specifica il raggio entro cui si vogliono filtrare i risultati rispetto ad una certa posizione geografica.
 * 
 * @see UserQuery#setNeighbourhood(Neighbourhood)
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class Neighbourhood implements Model {
	
	/**
	 * Serializzatore di questo modello.
	 * 
	 * Il formato utilizzato nella codifica &egrave;:
	 * <pre><code>
	 * &lt;neighbourhood radius="0.0"&gt;
	 * 	&lt;position lat="0.0" lon="0.0" /&gt;
	 * &lt;/neighbourhood&gt;
	 * </code></pre>
	 * 
	 * @see Serializer
	 */
	public static final Serializer<Neighbourhood> SERIALIZER = new Serializer<Neighbourhood>() {

		@Override
		public Neighbourhood fromXML(Element node) {
			validateTagName(node, "neighbourhood");
			
			int radius = Integer.parseInt(getRequiredAttribute(node, "radius"));
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
	private final int radius;
	
	/**
	 * Inizializza un'istanza della classe con i parametri forniti.
	 * 
	 * @param position la posizione che si vuole utilizzare come centro
	 * @param radius il raggio, in metri, dell'area
	 * 
	 * @throws IllegalArgumentException se la posizione &egrave; {@code null}
	 */
	public Neighbourhood(Position position, int radius) {
		if(position == null)
			throw new IllegalArgumentException("Position cannot be null");
		
		this.position = position;
		this.radius = radius;
	}
	
	/**
	 * Restituisce il punto che viene utilizzato come centro per l'intorno.
	 * 
	 * @return la posizione usata come centro dell'intorno
	 */
	public Position getPosition() {
		return position;
	}
	
	/**
	 * Restituisce il raggio dell'intorno.
	 * 
	 * @return il raggio dell'intorno, espresso in metri
	 */
	public int getRadius() {
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
