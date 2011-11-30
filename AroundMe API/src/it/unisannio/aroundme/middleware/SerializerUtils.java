package it.unisannio.aroundme.middleware;

import java.lang.reflect.Field;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SerializerUtils {
	private SerializerUtils() {}
	
	/**
	 * <collection entity="Class">
	 * 	<entity />
	 * 	<entity />
	 * 	<entity />
	 * </collection>
	 */
	public static final Serializer<? extends Collection<? extends Entity>> COLLECTION_SERIALIZER = new Serializer<Collection<? extends Entity>>() {

		@Override
		public Collection<? extends Entity> fromXML(Node xml) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Node toXML(Collection<? extends Entity> obj) {
			Document d = SerializerUtils.newDocument();
			Element container = d.createElement("collection");
			// FIXME attribute
			for(Entity e : obj) {
				container.appendChild(SerializerUtils.toXML(e));
			}
			
			return container;
		}
		
	};
	

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
	public static <T extends Entity> Serializer<T> getSerializer(Class<T> clazz) {
		try {
			Field f = clazz.getDeclaredField("SERIALIZER");
			return (Serializer<T>) f.get(null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static <T extends Entity> Node toXML(T e) {
		@SuppressWarnings("unchecked")
		Serializer<T> s = (Serializer<T>) SerializerUtils.getSerializer(e.getClass());
		return s.toXML((T) e);
	}
}
