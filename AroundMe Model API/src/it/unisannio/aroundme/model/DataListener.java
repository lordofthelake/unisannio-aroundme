package it.unisannio.aroundme.model;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 * @param <T>
 */
public interface DataListener<T> {
	void onData(T object);
	void onError(Exception e);
}
