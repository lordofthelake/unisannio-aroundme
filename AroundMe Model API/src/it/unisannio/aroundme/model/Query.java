package it.unisannio.aroundme.model;

import java.util.Collection;

public interface Query<T extends Model> {
	
	public void perform(DataListener<Collection<T>> l);
}
