package it.unisannio.aroundme.client.model;

import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.InterestQuery;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Picture;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class ModelFactoryImpl extends ModelFactory {
	static {
		ModelFactory.setInstance(new ModelFactoryImpl());
	}
	
	private ModelFactoryImpl() {}
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
		return new InterestQueryImpl();
	}

	@Override
	public UserQuery createUserQuery() {
		// TODO Auto-generated method stub
		return null;
	}

}
