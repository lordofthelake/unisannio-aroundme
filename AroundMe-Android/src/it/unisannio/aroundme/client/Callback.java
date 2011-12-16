package it.unisannio.aroundme.client;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 * @param <T>
 */
public interface Callback<T> {
	void handle(T obj);
}
