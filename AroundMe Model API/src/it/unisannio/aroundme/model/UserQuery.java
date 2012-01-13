package it.unisannio.aroundme.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.Callable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Modello per rappresentare un'interrogazione su un insieme di utenti.
 * 
 * Si possono combinare pi&ugrave; criteri di ricerca in AND logico tra loro.
 * <ul>
 * 	<li><strong>Ricerca per ID</strong>: vengono restituiti solo gli utenti aventi l'ID specificato</li>
 *  <li><strong>Ricerca per interesse</strong>: vengono restituiti solo gli utenti che hanno tra gli interessi quelli con l'ID indicato</li>
 *  <li><strong>Ricerca per area geografica</strong>: vengono restituiti solo gli utenti che si trovano entro un certo raggio da un punto prefissato</li>
 *  <li><strong>Ricerca per compatibilit&agrave;</strong>: vengono restituiti solo gli utenti che abbiano un rank di compatibilit&agrave; almeno pari a quello 
 *    indicato, rispetto ad un utente fissato</li>
 * </ul>
 * 
 * <p>I risultati dell'interrogazione possono essere ottenuti tramite una chiamata a {@link #call()}. L'implementazione di questo metodo
 * &egrave; strettamente dipendente dalla piattaforma utilizzata: in ambito server potrebbe tradursi in un'interrogazione su un database,
 * mentre lato client potrebbe risultare in una chiamata a delle API remote.</p>
 * 
 * @see User
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public abstract class UserQuery implements Callable<Collection<User>>, Model {
	private static final long serialVersionUID = 1L;

	/**
	 * Serializzatore di questo modello.
	 * 
	 * Il formato utilizzato nella codifica &egrave;:
	 * <pre><code>
	 * &lt;query&gt;
	 * 	&lt;compatibility rank="0.0" userid="123" /&gt;
	 * 	&lt;neighbourhood radius="0.0"&gt;
	 * 		&lt;position lat="0.0" lon="0.0" /&gt;
	 * 	&lt;/neighbourhood&gt;
	 * 	&lt;interest-ids&gt;
	 * 		&lt;id&gt;123&lt;/id&gt;
	 * 		&lt;id&gt;123&lt;/id&gt;
	 *  	&lt;!-- ... --&gt;
	 * 	&lt;/interest-ids&gt;
	 *  &lt;ids&gt;
	 * 		&lt;id&gt;123&lt;/id&gt;
	 * 		&lt;id&gt;123&lt;/id&gt;
	 *  	&lt;!-- ... --&gt;
	 * 	&lt;/ids&gt;
	 * &lt;/query&gt;
	 * </code></pre>
	 * 
	 * @see Serializer
	 */
	public static final Serializer<UserQuery> SERIALIZER = new Serializer<UserQuery>() {

		@Override
		public UserQuery fromXML(Element node) {
			validateTagName(node, "query");
			
			UserQuery obj = ModelFactory.getInstance().createUserQuery();

			Element compatibility = getSingleElementByTagName(node, "compatibility");
			if(compatibility != null) 
				obj.setCompatibility(Compatibility.SERIALIZER.fromXML(compatibility));
			
			Element neighbourhood = getSingleElementByTagName(node, "neighbourhood");
			if(neighbourhood != null) 
				obj.setNeighbourhood(Neighbourhood.SERIALIZER.fromXML(neighbourhood));
			
			Element interestIds = getSingleElementByTagName(node, "interest-ids");
			if(interestIds != null) {
				NodeList ids = interestIds.getElementsByTagName("id");
				for(int i = 0, len = ids.getLength(); i < len; ++i) {
					Element id = (Element) ids.item(i);
					obj.addInterestId(Long.parseLong(id.getTextContent()));
				}
			}
			
			Element userIds = getSingleElementByTagName(node, "ids");
			if(userIds != null) {
				NodeList ids = userIds.getElementsByTagName("id");
				for(int i = 0, len = ids.getLength(); i < len; ++i) {
					Element id = (Element) ids.item(i);
					obj.addId(Long.parseLong(id.getTextContent()));
				}
			}
			
			return obj;
		}

		@Override
		public Element toXML(UserQuery obj) {
			Document document = getDocumentBuilder().newDocument();
			
			Element container = document.createElement("query");
			
			Neighbourhood n = obj.getNeighbourhood();
			if(n != null)
				container.appendChild(document.adoptNode(Neighbourhood.SERIALIZER.toXML(n)));
			
			Compatibility c = obj.getCompatibility();
			if(c != null)
				container.appendChild(document.adoptNode(Compatibility.SERIALIZER.toXML(c)));
			
			Collection<Long> iids = obj.getInterestIds();
			if(iids.size() > 0) {
				Element interests = document.createElement("interest-ids");
				
				for(long l : obj.getInterestIds()) {
					Element e = document.createElement("id");
					e.setTextContent(String.valueOf(l));
					interests.appendChild(e);
				}
				
				container.appendChild(interests);
			}
			
			Collection<Long> ids = obj.getIds();
			if(ids.size() > 0) {
				Element ids1 = document.createElement("ids");
				
				for(long l : ids) {
					Element e = document.createElement("id");
					e.setTextContent(String.valueOf(l));
					ids1.appendChild(e);
				}
				
				container.appendChild(ids1);
			}
			
			return container;
		}
		
	};
	
	/**
	 * Restituisce un'istanza di UserQuery che compie un'interrogazione solo sugli ID utente.
	 * 
	 * @param ids una lista di ID utente su cui effettuare l'interrogazione
	 * @return un'istanza di UserQuery, utilizzante l'implementazione in uso sulla piattaforma, che effettua un'interrogazione per ID
	 * 
	 * @see #addId(long...)
	 * @see ModelFactory#createUserQuery()
	 */
	public static UserQuery byId(long... ids) {
		return ModelFactory.getInstance().createUserQuery().addId(ids);
	}
	
	/**
	 * Restituisce un'istanza di UserQuery che compie un'interrogazione solo sugli ID utente.
	 * 
	 * @param ids una lista di ID utente su cui effettuare l'interrogazione
	 * @return un'istanza di UserQuery, utilizzante l'implementazione in uso sulla piattaforma, che effettua un'interrogazione per ID
	 * 
	 * @see #addId(Collection)
	 * @see ModelFactory#createUserQuery()
	 */
	public static UserQuery byId(Collection<Long> ids) {
		return ModelFactory.getInstance().createUserQuery().addId(ids);
	}
	
	/**
	 * Restituisce un oggetto di tipo {@link java.util.concurrent.Callable} che effettua un'interrogazione su un singolo ID.
	 * 
	 * Una chiamata al metodo {@code call()} dell'oggetto fornito restituir&agrave; l'utente richiesto o sollever&agrave; una {@code NoSuchElementException}
	 * nel caso in cui questo non sia presente nell'insieme su cui viene effettuata l'interrogazione.
	 * 
	 * @param id l'ID dell'utente desiderato
	 * @return un'oggetto che implementa Callable in grado di effettuare un'interrogazione su un singolo ID
	 */
	public static Callable<User> single(final long id) {
		return new Callable<User>() {

			@Override
			public User call() throws Exception {
				Collection<? extends User> c = byId(id).call();
				User[] u = c.toArray(new User[0]);
				
				if(u.length == 0)
					throw new NoSuchElementException("User with id #" + id + "doesn't exist");
				
				return u[0];
			}
			
		};
	}
	
	private Neighbourhood neighbourhood;
	private Set<Long> interestIds = new HashSet<Long>();
	private Compatibility compatibility;
	private Set<Long> ids = new HashSet<Long>();
	
	/**
	 * Effettua un'interrogazione sull'insieme di utenti in uso usando i parametri fissati nella query.
	 * 
	 * @return una collezione, possibilmente vuota, degli utenti che rispettano i parametri di ricerca fissati
	 * @throws Exception nel caso in cui si verifichino problemi durante l'interrogazione
	 */
	public abstract Collection<User> call() throws Exception;
	
	/**
	 * Restituisce l'intorno (area geografica) usato come criterio d'interrogazione.
	 * 
	 * @return l'intorno che viene usato per effettuare l'interrogazione, o {@code null} se non fissato
	 * @see Neighbourhood
	 * @see #setNeighbourhood(Neighbourhood)
	 */
	public Neighbourhood getNeighbourhood() {
		return neighbourhood;
	}
	
	/**
	 * Imposta l'intorno (area geografica) da usare come criterio d'interrogazione.
	 * 
	 * @param n l'intorno entro cui si desidera che gli utenti si trovino
	 * @return la stessa istanza di UserQuery, per permettere la concatenazione di pi&ugrave; metodi.
	 */
	public UserQuery setNeighbourhood(Neighbourhood n) {
		neighbourhood = n;
		
		return this;
	}
	
	/**
	 * Aggiunge uno o pi&ugrave; ID utente da usare come criterio d'interrogazione.
	 * 
	 * @param id la lista degli ID degli utenti desiderati
	 * @return la stessa istanza di UserQuery, per permettere la concatenazione di pi&ugrave; metodi.
	 * @see #getIds()
	 */
	public UserQuery addId(long... id) {
		for(long i : id)
			ids.add(i);
		
		return this;
	}
	
	/**
	 * Rimuove uno o pi&ugrave; ID utente dai criteri usati per l'interrogazione.
	 * 
	 * @param id la lista degli ID degli utenti da rimuovere
	 * @return la stessa istanza di UserQuery, per permettere la concatenazione di pi&ugrave; metodi.
	 * @see #getIds()
	 */
	public UserQuery removeId(long... id) {
		for(long i : id)
			ids.remove(i);
				
	    return this;
	}
	
	/**
	 * Aggiunge una collezione di ID utente a quelli da usare come criterio d'interrogazione.
	 * 
	 * @param id una collezione degli ID degli utenti desiderati
	 * @return la stessa istanza di UserQuery, per permettere la concatenazione di pi&ugrave; metodi.
	 * @see #getIds()
	 */
	public UserQuery addId(Collection<Long> id) {
		ids.addAll(id);
		return this;
	}
	
	/**
	 * Rimuove una collezione di ID utente dai criteri usati per l'interrogazione.
	 * 
	 * @param id una collezione degli ID degli utenti da rimuovere
	 * @return la stessa istanza di UserQuery, per permettere la concatenazione di pi&ugrave; metodi.
	 * @see #getIds()
	 */
	public UserQuery removeId(Collection<Long> id) {
		ids.removeAll(id);
		
		return this;
	}
	
	/**
	 * Restituisce gli ID utente usati come criterio di interrogazione.
	 * 
	 * @return una collezione di ID, possibilmente vuota nel caso in cui non sia stato usato questo criterio di interrogazione
	 */
	public Collection<Long> getIds() {
		return ids;
	}
	
	/**
	 * Aggiunge uno o pi&ugrave; ID di interessi da usare come criterio d'interrogazione.
	 * 
	 * @param id la lista degli ID degli interessi che si desidera che gli utenti possiedano
	 * @return la stessa istanza di UserQuery, per permettere la concatenazione di pi&ugrave; metodi.
	 * @see #getInterestIds()
	 */
	public UserQuery addInterestId(long... id) {
		for(long i : id)
			interestIds.add(i);
		return this;
	}
	
	/**
	 * Rimuove uno o pi&ugrave; ID di interessi dai criteri usati per l'interrogazione.
	 * 
	 * @param id la lista degli ID degli interesti da rimuovere
	 * @return la stessa istanza di UserQuery, per permettere la concatenazione di pi&ugrave; metodi.
	 * @see #getInterestIds()
	 */
	public UserQuery removeInterestId(long... id) {
		for(long i : id)
			interestIds.remove(i);
		return this;
	}
	
	/**
	 * Aggiunge una collezione di ID di interessi da usare come criterio d'interrogazione.
	 * 
	 * @param id una collezione degli ID degli interessi che si desidera che gli utenti possiedano
	 * @return la stessa istanza di UserQuery, per permettere la concatenazione di pi&ugrave; metodi.
	 * @see #getInterestIds()
	 */
	public UserQuery addInterestId(Collection<Long> id) {
		interestIds.addAll(id);
		return this;
	}
	
	/**
	 * Rimuove una collezione di ID di interessi dai criteri usati per l'interrogazione.
	 * 
	 * @param id una collezione degli ID degli interessi da rimuovere
	 * @return la stessa istanza di UserQuery, per permettere la concatenazione di pi&ugrave; metodi.
	 * @see #getInterestIds()
	 */
	public UserQuery removeInterestId(Collection<Long> id) {
		interestIds.removeAll(id);
		return this;
	}
	
	/**
	 * Restituisce gli ID degli interessi usati come criteri di interrogazione.
	 * 
	 * @return una collezione di ID, possibilmente vuota nel caso in cui non sia stato usato questo criterio di interrogazione.
	 */
	public Collection<Long> getInterestIds() {
		return interestIds;
	}
	
	/**
	 * Imposta la compatibilit&agrave; minima da usare come criterio d'interrogazione.
	 * 
	 * @param c il grado di compatibilit&agrave; minimo che si desidera che gli utenti posseggano
	 * @return la stessa istanza di UserQuery, per permettere la concatenazione di pi&ugrave; metodi.
	 * @see #getCompatibility()
	 * @see Compatibility
	 */
	public UserQuery setCompatibility(Compatibility c) {
		compatibility = c;	
		return this;
	}
	
	/**
	 * Restituisce il grado i compatibilit&agrave; minimo usato come criterio d'interrogazione.
	 * 
	 * @return il grado di compatibilit&agrave; minimo usato per effettuare l'interrogazione, o {@code null} se non fissato
	 * @see Compatibility
	 * @see #setCompatibility(Compatibility)
	 */
	public Compatibility getCompatibility() {
		return compatibility;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null && !(obj instanceof UserQuery))
			return false;
		
		UserQuery other = (UserQuery) obj;
		
		Compatibility myCompatibility = getCompatibility();
		Compatibility otherCompatibility = other.getCompatibility();
		
		boolean compatibilityEquals = 
				(myCompatibility == null && otherCompatibility == null)
				|| (myCompatibility != null && myCompatibility.equals(otherCompatibility));
		
		Neighbourhood myNeighbourhood = getNeighbourhood();
		Neighbourhood otherNeighbourhood = other.getNeighbourhood();

		boolean neighbourhoodEquals = 
				(myNeighbourhood == null && otherNeighbourhood == null)
				|| (myNeighbourhood != null && myNeighbourhood.equals(otherNeighbourhood));
		
		Collection<Long> myInterestIds = getInterestIds();
		Collection<Long> otherInterestIds = other.getInterestIds();
		
		boolean interestIdsEquals = 
				(myInterestIds == null && otherInterestIds == null)
				|| (myInterestIds != null && myInterestIds.equals(otherInterestIds));
		
		Collection<Long> myIds = getIds();
		Collection<Long> otherIds = other.getIds();

		boolean idsEquals = 
				(myIds == null && otherIds == null)
				|| (myIds != null && myIds.equals(otherIds));
		
		return compatibilityEquals && neighbourhoodEquals && interestIdsEquals && idsEquals;
	}
	
}
