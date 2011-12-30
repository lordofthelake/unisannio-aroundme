package it.unisannio.aroundme.server;

import java.util.Collection;

import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.Preferences;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServletContextListenerImpl implements ServletContextListener{

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		ModelFactory.setInstance(new ModelFactory() {
			
			@Override
			public User createUser(long id, String name, Collection<Interest> interests) {
				UserImpl userImpl = new UserImpl(id, name);
				for(Interest i: interests){
					userImpl.addInterest(i);
				}
				return userImpl;
			}

			@Override
			public Interest createInterest(long id, String name, String category) {
				Interest interest = new InterestImpl(id, name, category);
				return interest;
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
				// TODO Auto-generated method stub
				return null;
			}
		});
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {}

}
