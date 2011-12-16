package it.unisannio.aroundme.client.model;

import java.util.Collection;

import it.unisannio.aroundme.client.DataService;
import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.InterestQuery;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class ClientModelFactory extends ModelFactory {

	private DataService service;
	
	public ClientModelFactory(DataService service) {
		this.service = service;
	}
	
	@Override
	public User createUser(long id, String name, Collection<Interest> interests) {
		return new UserImpl(id, name, interests);
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
	public InterestQuery createInterestQuery() {
		return new InterestQueryImpl(service);
	}

	@Override
	public UserQuery createUserQuery() {
		return new UserQueryImpl(service);
	}

}
