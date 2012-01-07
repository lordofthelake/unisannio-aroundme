package it.unisannio.aroundme.server;

import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.Preferences;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;

import java.util.Collection;

public class ServerModelFactory extends ModelFactory{

	@Override
	public User createUser(long id, String name, Collection<Interest> interests) {
		UserImpl userImpl = new UserImpl(id, name);
		if(interests != null){
			for(Interest i: interests){
				userImpl.addInterest(i);
			}
		}
		return userImpl;
	}

	@Override
	public Interest createInterest(long id, String name, String category) {
		return new InterestImpl(id, name, category);
	}

	@Override
	public Position createPosition(double lat, double lon) {
		return new PositionImpl(lat, lon);
	}

	@Override
	public UserQuery createUserQuery() {
		return new UserQueryImpl();
	}

	@Override
	public Preferences createPreferences() {
		return new PreferencesImpl();
	}
}
