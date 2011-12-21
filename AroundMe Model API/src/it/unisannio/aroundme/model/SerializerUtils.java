package it.unisannio.aroundme.model;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class SerializerUtils {
	private SerializerUtils() {}
	
	/**
	 * <collection>
	 * 	<entity />
	 * 	<entity />
	 * 	<entity />
	 * </collection>
	 */
	public static <T extends Model> Serializer<Collection<? extends T>> getCollectionSerializer(final Class<T> clazz) {
		return new Serializer<Collection<? extends T>>() {
			@Override
			public Collection<? extends T> fromXML(Node xml) {
				Collection<T> obj = new LinkedList<T>();
				Serializer<T> serializer = SerializerUtils.getSerializer(clazz);
				
				if(!(xml instanceof Element))
					throw new IllegalArgumentException();
				
				NodeList list = xml.getChildNodes();
				for(int i = 0, len = list.getLength(); i < len; ++i) {
					Node n = list.item(i);
					obj.add(serializer.fromXML(n));
				}
				
				return obj;
			}
	
			@Override
			public Node toXML(Collection<? extends T> obj) {
				Document d = SerializerUtils.newDocument();
				Element container = d.createElement("collection");

				for(Model e : obj) {
					container.appendChild(SerializerUtils.toXML(e));
				}
				
				return container;
			}
		};
		
	}
	

	private static DocumentBuilder documentBuilder = null;
	
	public static DocumentBuilder getDocumentBuilder() {
		if(documentBuilder == null) {
			try {
				documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return documentBuilder;
	}
	
	public static Document newDocument() {
		return documentBuilder.newDocument();
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Model> Serializer<T> getSerializer(Class<T> clazz) {
		try {
			Field f = clazz.getDeclaredField("SERIALIZER");
			return (Serializer<T>) f.get(null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static <T extends Model> Node toXML(T e) {
		@SuppressWarnings("unchecked")
		Serializer<T> s = (Serializer<T>) SerializerUtils.getSerializer(e.getClass());
		return s.toXML((T) e);
	}
	
	/**
	 * Scrive un DOM XML su un OutputStream
	 * @param node Il DOM XML da scrivere
	 * @param out L'OutputStream su cui scrivere
	 * 
	 * @throws TransformerException Se occorre un errore durante la scrittura 
	 */
	public static void writeXML(Node node, OutputStream out) throws TransformerException{
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(node);
		StreamResult result = new StreamResult(out);
		transformer.transform(source, result);
	}
}
