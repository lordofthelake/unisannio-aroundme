package it.unisannio.aroundme.model;

/**
 * 
 * @author m
 *
 * @param <T>
 */
public interface DataListener<T> {
	void onData(T object);
	void onError(Exception e);
}
