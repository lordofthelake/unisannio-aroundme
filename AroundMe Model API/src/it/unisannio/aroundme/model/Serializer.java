package it.unisannio.aroundme.model;

import org.w3c.dom.Node;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 * @param <T>
 */
public interface Serializer<T> {
	
	T fromXML(Node xml);
	Node toXML(T obj);
}
