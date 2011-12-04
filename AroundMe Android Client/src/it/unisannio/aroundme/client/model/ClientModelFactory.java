package it.unisannio.aroundme.client.model;

import java.net.URL;

import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.InterestQuery;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Picture;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;

public class ClientModelFactory extends ModelFactory {
	private URL endpoint;
	
	public ClientModelFactory(URL endpoint) {
		this.endpoint = endpoint;
	}
	
	@Override
	public User createUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Interest createInterest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Position createPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Picture<?> createPicture() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InterestQuery createInterestQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserQuery createUserQuery() {
		// TODO Auto-generated method stub
		return null;
	}

}
