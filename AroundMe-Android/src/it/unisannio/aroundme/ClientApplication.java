package it.unisannio.aroundme;

import android.app.Application;
import it.unisannio.aroundme.client.model.*;
import it.unisannio.aroundme.model.Model;

public class ClientApplication extends Application {
	private XmlClient xmlClient;
	private Backend backend;
	
	@Override
	public void onCreate() {
		xmlClient = new XmlClient(null); // FIXME Unimplemented
		ModelFactoryImpl.install(this);
		backend = new Backend(xmlClient, new Backend.Descriptor() {
			
			@Override
			public String getPathFor(Class<? extends Model> c) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		super.onCreate();
	}
	
	public XmlClient getXmlClient() {
		return xmlClient;
	}

	public Backend getBackend() {
		return backend;
	}
}
