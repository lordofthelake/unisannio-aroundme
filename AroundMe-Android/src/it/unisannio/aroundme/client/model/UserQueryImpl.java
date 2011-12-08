package it.unisannio.aroundme.client.model;

import java.util.Collection;

import org.w3c.dom.Node;

import it.unisannio.aroundme.model.*;

class UserQueryImpl extends UserQuery {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private XmlClient client;
	
	UserQueryImpl(XmlClient client) {
		this.client = client;
	}
	
	@Override
	public void perform(final DataListener<Collection<User>> l) {
		client.post("/query/user", SERIALIZER.toXML(this), new DataListener<Node>() {

			@Override
			public void onData(Node object) {
				Serializer<? extends Collection<User>> s = SerializerUtils.getCollectionSerializer(User.class);
				l.onData(s.fromXML(object));
				
			}

			@Override
			public void onError(Exception e) {
				l.onError(e);
			}
			
		});
	}

}
