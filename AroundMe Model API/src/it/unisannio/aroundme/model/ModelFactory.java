package it.unisannio.aroundme.model;

import java.util.Collection;


/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public abstract class ModelFactory {
	private static ModelFactory instance;
	
	
	public static void setInstance(ModelFactory e) {
		instance = e;
	}
	
	public static ModelFactory getInstance() {
		if(instance == null)
			throw new IllegalStateException("No concrete factory set.");
		
		return instance;
	}
	
	public abstract User createUser(long id, String name, Collection<Interest> interests);
	public abstract Interest createInterest(long id, String name, String category);
	public abstract Position createPosition(double lat, double lon);
	public abstract UserQuery createUserQuery();
	
}
