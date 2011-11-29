package it.unisannio.aroundme.middleware;

import java.io.Serializable;
import java.util.Collection;

import org.w3c.dom.Node;

public interface Entity extends Serializable {
	public static final Serializer<? extends Collection<? extends Entity>> COLLECTION_SERIALIZER = new Serializer<Collection<? extends Entity>>() {

		@Override
		public <U extends Collection<? extends Entity>> U fromXML(Node xml,
				U obj) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Node toXML(Collection<? extends Entity> obj) {
			// TODO Auto-generated method stub
			return null;
		}
		
	};
}
