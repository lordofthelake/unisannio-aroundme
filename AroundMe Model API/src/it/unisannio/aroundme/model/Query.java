package it.unisannio.aroundme.model;

import java.util.Collection;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 * @param <T>
 */
public interface Query<T extends Model> {
	
	public void perform(DataListener<Collection<? extends T>> l);
}
