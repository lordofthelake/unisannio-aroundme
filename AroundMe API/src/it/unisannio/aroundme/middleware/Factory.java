package it.unisannio.aroundme.middleware;

public abstract class Factory {
	private static Factory instance;
	
	
	public static void setInstance(Factory e) {
		instance = e;
	}
	
	public static Factory getInstance() {
		if(instance == null)
			throw new IllegalStateException("No concrete factory set.");
		
		return instance;
	}
	
	public abstract User createUser();
	public abstract Interest createInterest();
	public abstract Position createPosition();
	public abstract Picture<?> createPicture();
	public abstract InterestQuery createInterestQuery();
	public abstract UserQuery createUserQuery();

	public abstract Neighbourhood createNeighbourhood();

	public abstract Compatibility createCompatibility();
	
}
