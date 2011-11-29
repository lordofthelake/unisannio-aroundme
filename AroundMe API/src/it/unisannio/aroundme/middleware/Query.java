package it.unisannio.aroundme.middleware;

import java.util.Collection;

public interface Query<T extends Entity> {
	
	public void perform(DataListener<Collection<T>> l);
}
