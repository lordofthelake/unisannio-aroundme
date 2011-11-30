package it.unisannio.aroundme.middleware;

import org.w3c.dom.Node;

public interface Serializer<T> {
	
	T fromXML(Node xml);
	Node toXML(T obj);
}
