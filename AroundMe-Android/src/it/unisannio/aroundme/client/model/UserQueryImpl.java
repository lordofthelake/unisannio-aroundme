package it.unisannio.aroundme.client.model;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.w3c.dom.Node;

import it.unisannio.aroundme.client.Callback;
import it.unisannio.aroundme.client.DataService;
import it.unisannio.aroundme.client.Transformer;
import it.unisannio.aroundme.model.DataListener;
import it.unisannio.aroundme.model.SerializerUtils;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
class UserQueryImpl extends UserQuery {
	private static final long serialVersionUID = 1L;
	private DataService service;
	private URL endpoint = null; // FIXME
	
	UserQueryImpl(DataService service) {
		this.service = service;
	}

	@Override
	public void perform(DataListener<Collection<? extends User>> listener) {
		service.asyncHttpRequest(endpoint, "POST", 
				new Transformer<InputStream, Collection<? extends User>>() {
		
					@Override
					public Collection<? extends User> transform(InputStream input) throws Exception {
						Node xml = SerializerUtils.getDocumentBuilder().parse(input); 
						return SerializerUtils.getCollectionSerializer(User.class).fromXML(xml);
					}
				}, new Callback<OutputStream>() {
	
					@Override
					public void handle(OutputStream obj) {
						// TODO
					}
				}, listener);

	}

}
