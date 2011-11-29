package it.unisannio.aroundme.middleware;

import java.net.URL;

public interface Picture<T> extends Entity {
	URL getURL();
	void load(DataListener<T> l);
}
