package it.unisannio.aroundme.client.model;

import org.w3c.dom.Node;

import it.unisannio.aroundme.model.DataListener;
import it.unisannio.aroundme.model.Identifiable;
import it.unisannio.aroundme.model.Model;
import it.unisannio.aroundme.model.Serializer;
import it.unisannio.aroundme.model.SerializerUtils;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class Backend {
	public static interface Descriptor {
		String getPathFor(Class<? extends Model> c);
	}
	
	private XmlClient client;
	private Descriptor desc;
	
	public Backend(XmlClient client, Descriptor desc) {
		this.client = client;
		this.desc = desc;
	}
	
	public XmlClient getXmlClient() {
		return client;
	}
	
	<T extends Model> void get(Class<? extends T> model, long id, final DataListener<T> listener) {
		final Serializer<? extends T> serializer = SerializerUtils.getSerializer(model);
		client.get(desc.getPathFor(model) + id, new DataListener<Node>() {

			@Override
			public void onData(Node xml) {
				listener.onData(serializer.fromXML(xml));
			}

			@Override
			public void onError(Exception e) {
				listener.onError(e);
			}
			
		});

	}
	
	@SuppressWarnings("unchecked")
	<T extends Model & Identifiable> void post(T obj, DataListener<T> listener) {
		this.post(obj, (Serializer<? extends T>) SerializerUtils.getSerializer(obj.getClass()), listener);
	}
	
	@SuppressWarnings("unchecked")
	<U, T extends Model & Identifiable> void post(T obj, final Serializer<? extends U> retSerializer, final DataListener<U> retListener) {
		Serializer<T> objSerializer = (Serializer<T>)SerializerUtils.getSerializer(obj.getClass());
		
		client.post(desc.getPathFor(obj.getClass()) + obj.getId(), objSerializer.toXML(obj), new DataListener<Node>() {

			@Override
			public void onData(Node object) {
				retListener.onData(retSerializer.fromXML(object));
				
			}

			@Override
			public void onError(Exception e) {
				retListener.onError(e);
			}
			
		});	
	}
	
	
}
