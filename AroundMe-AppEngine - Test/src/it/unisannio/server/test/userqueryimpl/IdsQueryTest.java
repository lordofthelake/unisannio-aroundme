package it.unisannio.server.test.userqueryimpl;

import java.util.Collection;

import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;
import it.unisannio.aroundme.server.InterestImpl;
import it.unisannio.aroundme.server.ServerModelFactory;
import it.unisannio.aroundme.server.UserImpl;
import it.unisannio.aroundme.server.UserQueryImpl;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;

import junit.framework.TestCase;

/**
 * Verifica che {@link UserQueryImpl} impostato per eseguire una query su id utente,
 * restituisca risultati concordi ai dati presenti sul Datastore
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class IdsQueryTest extends TestCase{
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private User requiredUser1, requiredUser2, unsavedUser1, unsavedUser2, unrequiredUser;


	@Override
	protected void setUp() throws Exception {

		helper.setUp();

		ModelFactory.setInstance(new ServerModelFactory());

		try{
			ObjectifyService.register(UserImpl.class);
			ObjectifyService.register(InterestImpl.class);
		}catch(IllegalArgumentException e){}
		
		requiredUser1 = ModelFactory.getInstance().createUser(123, "Danilo", null);
		requiredUser2 = ModelFactory.getInstance().createUser(124, "Michele", null);
		unrequiredUser = ModelFactory.getInstance().createUser(127, "Giovanni", null);
		
		ObjectifyService.begin().put(requiredUser1, requiredUser2, unrequiredUser);
		unsavedUser1 = ModelFactory.getInstance().createUser(125, "Marco", null);
		unsavedUser2 = ModelFactory.getInstance().createUser(126, "Giuseppe", null);
	}
	
	public void tearDown(){
		helper.tearDown();
	}
	
	/**
	 * Testa la query che restituisce gli utenti solo con un certo id
	 * e che siano presenti sul Datastore
	 * @throws Exception nel caso di errori durante l'esecuzione della query
	 */
	public void testUserIdsQuery() throws Exception{
		
		UserQuery query = ModelFactory.getInstance().createUserQuery();
		query.addId(requiredUser1.getId(), requiredUser2.getId(), unsavedUser1.getId());
		
		Collection<User> users = query.call();
		assertTrue(users.contains(requiredUser1) && users.contains(requiredUser2));
		assertFalse(users.contains(unrequiredUser));
		assertFalse(users.contains(unsavedUser1));
	}
	
	/**
	 * Testa la query che restituisca una collezione vuota se
	 * gli id richiesti non corrispondono a nessun User sul Datastore
	 * @throws Exception nel caso di errori durante l'esecuzione della query
	 */
	public void testUserIdsQueryEmpty() throws Exception{
		
		UserQuery query = ModelFactory.getInstance().createUserQuery();
		query.addId(unsavedUser1.getId(), unsavedUser2.getId());
		
		Collection<User> users = query.call();
		assertTrue(users.isEmpty());
	
	}
	
	
}
