package it.unisannio.aroundme.model;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SerializerUtils {
	private SerializerUtils() {}
	
	/**
	 * <collection>
	 * 	<entity />
	 * 	<entity />
	 * 	<entity />
	 * </collection>
	 */
	public static <T extends Model> Serializer<? extends Collection<T>> getCollectionSerializer(Collection<T> collection, final Class<T> clazz) {
		return new Serializer<Collection<T>>() {
			@Override
			public Collection<T> fromXML(Node xml) {
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
			public Node toXML(Collection<T> obj) {
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
}
