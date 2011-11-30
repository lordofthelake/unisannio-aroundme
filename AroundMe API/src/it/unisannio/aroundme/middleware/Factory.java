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
	
	public abstract User newUser();
	public abstract Interest newInterest();
	public abstract Position newPosition();
	public abstract Picture<?> newPicture();
	public abstract InterestQuery createInterestQuery();
	public abstract UserQuery createUserQuery();
	
}
