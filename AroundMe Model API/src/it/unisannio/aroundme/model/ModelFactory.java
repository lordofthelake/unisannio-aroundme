package it.unisannio.aroundme.model;


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
	
	public abstract User createUser();
	public abstract Interest createInterest();
	public abstract Position createPosition();
	public abstract InterestQuery createInterestQuery();
	public abstract UserQuery createUserQuery();

	public Neighbourhood createNeighbourhood() {
		return new NeighbourhoodImpl();
	}

	public Compatibility createCompatibility() {
		return new CompatibilityImpl();
	}
	
}
