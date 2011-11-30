package it.unisannio.aroundme.middleware;

import org.w3c.dom.Node;

public interface Serializer<T> {
	
	<U extends T> U fromXML(Node xml, U obj);
	Node toXML(T obj);
}
