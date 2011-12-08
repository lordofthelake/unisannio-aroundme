package it.unisannio.aroundme.client.model;

import it.unisannio.aroundme.ClientApplication;
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
	public static void install(ClientApplication app) {
		ModelFactory.setInstance(new ModelFactoryImpl(app));
	}
	
	private ClientApplication app;
	
	private ModelFactoryImpl(ClientApplication app) {
		this.app = app;
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
		return new InterestQueryImpl(app.getXmlClient());
	}

	@Override
	public UserQuery createUserQuery() {
		return new UserQueryImpl(app.getXmlClient());
	}

}
