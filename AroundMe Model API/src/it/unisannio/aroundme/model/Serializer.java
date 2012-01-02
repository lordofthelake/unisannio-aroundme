package it.unisannio.aroundme.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Legge o scrive oggetti in XML.
 * 
 * Ogni classe che voglia supportare la serializzazione automatica via XML dovrebbe dichiarare un campo statico 
 * chiamato {@code SERIALIZER} che mantenga il riferimento ad un'appropriata istanza di {@code Serializer}, 
 * in grado di serializzare e deserializzare l'oggetto stesso.
 *
 * @param <T> Il tipo di oggetto che la classe pu&ograve; gestire
 * 
 * @see Model
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public abstract class Serializer<T> {
	private static DocumentBuilder documentBuilder;
	
	/**
	 * Restituisce un'istanza di {@code Serializer} in grado di gestire una collezione di oggetti a loro
	 * volta serializzabili.
	 * 
	 * <p>Il formato utilizzato nella codifica &egrave;:
	 * <pre><code>
	 * &lt;collection&gt;
	 *   &lt;entity /&gt;
	 *   &lt;entity /&gt;
	 *   &lt;entity /&gt;
	 * &lt;/collection&gt;
	 * </code></pre>
	 * </p>
	 * 
	 * @param clazz La classe degli oggetti da serializzare. Deve essere provvista di serializzatore
	 * @return Un serializzatore in grado di gestire una collezione di istanze di {@code clazz}
	 * @throws RuntimeException Se la classe non &egrave; provvista di serializzatore
	 * 
	 * @see #of(Class)
	 */
	public static <T extends Model> Serializer<Collection<? extends T>> ofCollection(final Class<T> clazz) {
		final Serializer<T> serializer = Serializer.of(clazz);
		
		return new Serializer<Collection<? extends T>>() {
			@Override
			public Collection<? extends T> fromXML(Element xml) {
				Collection<T> obj = new LinkedList<T>();
				
				NodeList list = xml.getChildNodes();
				for(int i = 0, len = list.getLength(); i < len; ++i) {
					Node n = list.item(i);
					if(n instanceof Element)
						obj.add(serializer.fromXML((Element) n));
				}
				
				return obj;
			}
	
			@Override
			public Element toXML(Collection<? extends T> obj) {
				Document document = getDocumentBuilder().newDocument();
				Element container = document.createElement("collection");
	
				for(T e : obj) 
					container.appendChild(document.importNode(Serializer.of(clazz).toXML(e), true));
				
				return container;
			}
		};
		
	}
	
	/**
	 * Restituisce il Serializer associato alla classe tramite il campo {@code SERIALIZER}.
	 * 
	 * @return Il Serializer della classe.
	 * 
	 * @throws ClassCastException Se il campo esiste ma la sua tipizzazione &egrave; sbagliata. 
	 * @throws RuntimeException Se la classe non ha dichiarato un campo {@code SERIALIZER}.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Model> Serializer<T> of(Class<T> clazz) {
		try {
			return (Serializer<T>) clazz.getField("SERIALIZER").get(null);
		} catch (Exception e) {
			throw new RuntimeException("Class " + clazz + " doesn't have a serializer", e);
		}
	}
	
	
	/**
	 * Metodo helper per scrivere elementi XML su uno stream.
	 * 
	 * @param node L'elemento da scrivere
	 * @param out Lo stream su cui scrivere
	 * 
	 * @throws TransformerException Se si verifica un errore durante la scrittura 
	 */
	public static void writeXML(Element node, StreamResult out) throws TransformerException{
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.transform(new DOMSource(node), out);
	}
	
	/**
	 * Metodo helper per leggere elementi XML da uno stream.
	 * 
	 * @param in Lo stream da cui leggere
	 * @return L'elemento letto
	 * 
	 * @throws SAXException
	 * @throws IOException 
	 */
	public static Element readXML(InputSource in) throws SAXException, IOException {
		return getDocumentBuilder().parse(in).getDocumentElement();
	}
	
	/**
	 * Deserializza un oggetto a partire da un elemento XML.
	 * 
	 * @param e L'elemento che verr&agrave; utilizzato per la deserializzazione
	 * @return L'oggetto deserializzato
	 * 
	 * @throws IllegalArgumentException Se l'XML in input non &egrave; in un formato valido
	 */
	public abstract T fromXML(Element e);
	
	/**
	 * Serializza un oggetto nella sua rappresentazione XML.
	 * 
	 * @param obj L'oggetto da serializzare
	 * @return L'elemento radice dell'XML serializzato
	 */
	public abstract Element toXML(T obj);
	
	/**
	 * Legge un oggetto da uno stream.
	 * 
	 * @param in Lo stream da cui leggere
	 * @return L'oggetto letto.
	 * 
	 * @throws SAXException
	 * @throws IOException
	 * 
	 * @see #fromXML(Element)
	 * @see #readXML(InputSource)
	 */
	public T read(InputSource in) throws SAXException, IOException {
		return fromXML(readXML(in));
	}
	
	/**
	 * Legge un oggetto da un InputStream.
	 * 
	 * @param in Lo stream da cui leggere
	 * @return L'oggetto letto
	 * 
	 * @throws SAXException
	 * @throws IOException
	 * 
	 * @see #read(InputSource)
	 */
	public T read(InputStream in) throws SAXException, IOException {
		return read(new InputSource(in));
	}
	
	/**
	 * Scrive un oggetto su uno stream di output.
	 * 
	 * @param obj L'oggetto da scrivere
	 * @param out Lo stream di output
	 * 
	 * @throws TransformerException
	 * 
	 * @see #writeXML(Element, StreamResult)
	 * @see #toXML(Object)
	 */
	public void write(T obj, StreamResult out) throws TransformerException {
		writeXML(toXML(obj), out);
	}
	
	/**
	 * Scrive un oggetto su un OutputStream.
	 * 
	 * @param obj L'oggetto da scrivere
	 * @param out Lo stream di output
	 * 
	 * @throws TransformerException
	 * 
	 * @see #write(T, StreamResult)
	 */
	public void write(T obj, OutputStream out) throws TransformerException {
		writeXML(toXML(obj), new StreamResult(out));
	}
	
	/**
	 * Restituisce una rappresentazione XML dell'oggetto sottoforma di stringa.
	 * 
	 * @param obj L'oggetto di cui si desidera la rappresentazione
	 * @return Una stringa contenente l'XML risultato dalla serializzazione dell'oggetto
	 * 
	 * @throws TransformerException
	 * 
	 * @see #toXML(T)
	 */
	public String toString(T obj) throws TransformerException {
		StringWriter w = new StringWriter();
		Serializer.writeXML(toXML(obj), new StreamResult(w));
		
		return w.toString();
	}
	
	/**
	 * Ricostruisce un oggetto a partire dall'XML contenuto in una stringa.
	 * 
	 * @param str La stringa contenente l'XML
	 * @return L'oggetto ricostruito
	 * 
	 * @throws SAXException
	 * @throws IOException
	 * 
	 * @see #fromXML(Element)
	 * @see #toString(Object)
	 */
	public T fromString(String str) throws SAXException, IOException {
		return read(new InputSource(new StringReader(str)));
	}

	/**
	 * Metodo helper che restituisce un {@link DocumentBuilder}.
	 * 
	 * @return Un'istanza di DocumentBuilder
	 * 
	 * @throws RuntimeException se non &egrave; istanziare l'oggetto
	 */
	protected static DocumentBuilder getDocumentBuilder() {
		if(documentBuilder == null) {
			try {
				documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			} catch (Exception e) {
				throw new RuntimeException("Can't instantiate a DocumentBuilder", e);
			}
		}
		
		return documentBuilder;
	}
	
	/**
	 * Metodo helper che si assicura che l'elemento sia di un certo tipo.
	 * 
	 * @param node L'elemento da controllare
	 * @param name Il nome del tag che ci si aspetta
	 * 
	 * @throws IllegalArgumentException Se l'elemento non ha il nome richiesto
	 */
	protected void validateTagName(Element node, String name) {
		if(!node.getTagName().equals(name))
			throw new IllegalArgumentException("Format mismatch. Expected <" + name +">, got <" + node.getTagName() + "> instead.");
	}
	
	/**
	 * Metodo helper che restituisce un singolo elemento con il nome specificato.
	 * 
	 * @param parent Il nodo genitore in cui effettuare la ricerca
	 * @param name Il nome dell'elemento da cercare
	 * @return L'elemento richiesto, {@code null} se non presente
	 * 
	 * @throws IllegalArgumentException Se esiste pi&ugrave; di un elemento con lo stesso nome
	 */
	protected Element getSingleElementByTagName(Element parent, String name) {
		NodeList children = parent.getElementsByTagName(name);
		switch(children.getLength()) {
		case 0:
			return null;
		case 1:
			return (Element) children.item(0);
		default:
			throw new IllegalArgumentException("Cannot have more than one <" + name +"> element.");
		}
	}
	
	/**
	 * Metodo helper che restituisce il valore di un attributo, validandone la presenza.
	 * 
	 * @param node L'elemento a cui l'attributo appartiene
	 * @param attr Il nome dell'attributo da leggere
	 * @return Il valore dell'attributo
	 * 
	 * @throws IllegalArgumentException Se l'attributo non &egrave; presente.
	 */
	protected String getRequiredAttribute(Element node, String attr) {
		Attr attribute = node.getAttributeNode(attr);
		if(attribute == null) 
			throw new IllegalArgumentException("Expected attribute '" + attr + "' in <" + node.getTagName() + "> element");
		
		return attribute.getValue();
	}
	
}
