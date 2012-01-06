package it.unisannio.server.test.datastore;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.server.InterestImpl;
import it.unisannio.aroundme.server.ServerModelFactory;
import it.unisannio.aroundme.server.UserImpl;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class SimpleDatastoreTest extends TestCase{
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private static Objectify ofy = ObjectifyService.begin();

	public static Test suite() {
		return new TestSetup(new TestSuite(SimpleDatastoreTest.class)) {

			protected void setUp() throws Exception {
				ModelFactory.setInstance(new ServerModelFactory());
				ObjectifyService.register(UserImpl.class);
				ObjectifyService.register(InterestImpl.class);
			}
		};
	}
 
	
	@Override
	public void setUp() {
		helper.setUp();
	}
	
	@Override
	public void tearDown(){
		helper.tearDown();
	}
		
	public void testUserPersistence() throws Exception{
		UserImpl user = (UserImpl) ModelFactory.getInstance().createUser(123, "Danilo", null);
		user.setPosition(ModelFactory.getInstance().createPosition(41.1315992, 14.7779900));
		Interest intTheWho = ModelFactory.getInstance().createInterest(5, "The Who", "Musica");
		Interest intAndroid = ModelFactory.getInstance().createInterest(6, "Android", "Tecnologia");
		user.addInterest(intTheWho);
		user.addInterest(intAndroid);
		String facebookAuthToken = "123456";
		user.setAuthToken(facebookAuthToken);
		
		ofy.put(user);
		
		User userRetrieved = ofy.get(User.class, 123);
		assertEquals(user, userRetrieved);
		assertEquals(user.getPosition(), userRetrieved.getPosition());
		assertEquals(user.getInterests(), userRetrieved.getInterests());
		assertEquals(facebookAuthToken, ((UserImpl)userRetrieved).getAuthToken());
	}
	
	public void testInterestPersistence() {
		// TODO testInterestPersistence
	}
	
	public void testC2DMConfigPersistence() {
		// TODO testC2DMPersistence
	}
	
	

	
}