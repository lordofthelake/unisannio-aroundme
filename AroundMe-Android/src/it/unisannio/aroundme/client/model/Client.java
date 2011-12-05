/**
 * 
 */
package it.unisannio.aroundme.client.model;

import it.unisannio.aroundme.model.DataListener;

/**
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public interface Client<T> {
	void get(String path, DataListener<T> listener);
	void put(String path, T data, DataListener<T> listener);
	void post(String path, T data, DataListener<T> listener);
	void delete(String path, DataListener<T> listener);
}
