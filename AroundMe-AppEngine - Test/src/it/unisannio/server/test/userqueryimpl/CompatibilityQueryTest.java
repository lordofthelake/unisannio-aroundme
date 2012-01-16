package it.unisannio.server.test.userqueryimpl;

import it.unisannio.aroundme.model.Compatibility;
import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;
import it.unisannio.aroundme.server.InterestImpl;
import it.unisannio.aroundme.server.ServerModelFactory;
import it.unisannio.aroundme.server.UserImpl;
import it.unisannio.aroundme.server.UserQueryImpl;

import java.util.Collection;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;

import junit.framework.TestCase;

/**
 * Verifica che {@link UserQueryImpl} impostato per eseguire una query sulla compatibilit&agrave;,
 * restituisca risultati concordi ai dati presenti sul Datastore
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class CompatibilityQueryTest extends TestCase{
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private User queryingUser;
	
	
	@Override
	protected void setUp() throws Exception {
		
		helper.setUp();

		ModelFactory.setInstance(new ServerModelFactory());
		
		try{
			ObjectifyService.register(UserImpl.class);
			ObjectifyService.register(InterestImpl.class);
		}catch(IllegalArgumentException e){}
		
		queryingUser = ModelFactory.getInstance().createUser(123, "Danilo", null);
		User user1 = ModelFactory.getInstance().createUser(124, "Michele", null);
		User user2 = ModelFactory.getInstance().createUser(125, "Marco", null);
		User user3 = ModelFactory.getInstance().createUser(126, "Giuseppe", null);
		User user4 = ModelFactory.getInstance().createUser(127, "Jessica", null);
		User user5 = ModelFactory.getInstance().createUser(128, "Samantha", null);

		Interest intFootball = ModelFactory.getInstance().createInterest(1, "Calcio", "Sport");
		Interest intBasket = ModelFactory.getInstance().createInterest(2, "Basket", "Sport");
		Interest intReading = ModelFactory.getInstance().createInterest(3, "Lettura", "Hobbies");
		Interest intLedZep = ModelFactory.getInstance().createInterest(4, "Led Zeppelin", "Musica");
		Interest intTheWho = ModelFactory.getInstance().createInterest(5, "The Who", "Musica");
		Interest intAndroid = ModelFactory.getInstance().createInterest(6, "Android", "Tecnologia");
		Interest intCooking = ModelFactory.getInstance().createInterest(7, "Cucina", "Hobbies");

		
		((UserImpl) queryingUser).addInterest(intAndroid);
		((UserImpl) queryingUser).addInterest(intTheWho);
		((UserImpl) queryingUser).addInterest(intReading);
		((UserImpl) queryingUser).addInterest(intFootball);
		
		((UserImpl) user1).addInterest(intAndroid);
		((UserImpl) user1).addInterest(intReading);
		((UserImpl) user1).addInterest(intLedZep);
		((UserImpl) user1).addInterest(intTheWho);
		
		((UserImpl) user2).addInterest(intFootball);
		((UserImpl) user2).addInterest(intReading);
		((UserImpl) user2).addInterest(intLedZep);
		((UserImpl) user2).addInterest(intAndroid);
		((UserImpl) user2).addInterest(intCooking);
		
		((UserImpl) user3).addInterest(intBasket);
		((UserImpl) user3).addInterest(intReading);
		((UserImpl) user3).addInterest(intAndroid);
		((UserImpl) user3).addInterest(intLedZep);
		
		((UserImpl) user4).addInterest(intAndroid);
		((UserImpl) user4).addInterest(intTheWho);
		((UserImpl) user4).addInterest(intReading);
		((UserImpl) user4).addInterest(intFootball);
		((UserImpl) user4).addInterest(intCooking);
		
		((UserImpl) user5).addInterest(intAndroid);
		((UserImpl) user5).addInterest(intTheWho);
		((UserImpl) user5).addInterest(intReading);
		((UserImpl) user5).addInterest(intFootball);

		ObjectifyService.begin().put(queryingUser, user1, user2, user3, user4, user5);
	}
	
	public void tearDown(){
		helper.tearDown();
	}

	
	/**
	 * Metodo di convenienza che esegue una query sulla compatibilit&agrave; restiturndone
	 * i risultati
	 * @param compatibility la {@link Compatibility} richiesta
	 * @return gli {@link User} risultanti dalla query
	 * @throws Exception se si verificano errori durante l'esecuzione della query
	 */
	public Collection<User> executeCompatibilityQuery(Compatibility compatibility) throws Exception{
		UserQuery query = ModelFactory.getInstance().createUserQuery();
		query.setCompatibility(compatibility);
		return query.call();
	}
	
	/**
	 * Verifica che una query sulla compatibilit&agrave; al 50&#37;
	 * restituisca una collezione di User che non contenga l'esecutore della
	 * query stesso e che contenga solo utenti con compatibilit&agrave;
	 * maggiore o uguale a quella richiesta.
	 * @throws Exception se si verificano errori durante l'esecuzione della query
	 */
	public void testCompatibilityFiftyPercent() throws Exception{
		float requiredRank = 0.5f;
		Compatibility compatibility = new Compatibility(queryingUser.getId(), requiredRank);
		Collection<User> users = executeCompatibilityQuery(compatibility);
		for(User u: users){
			assertTrue(queryingUser.getCompatibilityRank(u) >= requiredRank);
		}
		assertFalse(users.contains(queryingUser));
		
	}
	
	/**
	 * Verifica che una query sulla compatibilit&agrave; al 60&#37;
	 * restituisca una collezione di User che non contenga l'esecutore della
	 * query stesso e che contenga solo utenti con compatibilit&agrave;
	 * maggiore o uguale a quella richiesta.
	 * @throws Exception se si verificano errori durante l'esecuzione della query
	 */
	public void testCompatibilitySixtyPercent() throws Exception{
		float requiredRank = 0.6f;
		Compatibility compatibility = new Compatibility(queryingUser.getId(), requiredRank);
		Collection<User> users = executeCompatibilityQuery(compatibility);
		for(User u: users){
			assertTrue(queryingUser.getCompatibilityRank(u) >= requiredRank);
		}
		assertFalse(users.contains(queryingUser));
	}
	
	/**
	 * Verifica che una query sulla compatibilit&agrave; al 70&#37;
	 * restituisca una collezione di User che non contenga l'esecutore della
	 * query stesso e che contenga solo utenti con compatibilit&agrave;
	 * maggiore o uguale a quella richiesta.
	 * @throws Exception se si verificano errori durante l'esecuzione della query
	 */
	public void testCompatibilitySeventyPercent() throws Exception{
		float requiredRank = 0.7f;
		Compatibility compatibility = new Compatibility(queryingUser.getId(), requiredRank);
		Collection<User> users = executeCompatibilityQuery(compatibility);
		for(User u: users){
			assertTrue(queryingUser.getCompatibilityRank(u) >= requiredRank);
		}
		assertFalse(users.contains(queryingUser));
		
	}
	
	/**
	 * Verifica che una query sulla compatibilit&agrave; al 80&#37;
	 * restituisca una collezione di User che non contenga l'esecutore della
	 * query stesso e che contenga solo utenti con compatibilit&agrave;
	 * maggiore o uguale a quella richiesta.
	 * @throws Exception se si verificano errori durante l'esecuzione della query
	 */
	public void testCompatibilityEightyPercent() throws Exception{
		float requiredRank = 0.8f;
		Compatibility compatibility = new Compatibility(queryingUser.getId(), requiredRank);
		Collection<User> users = executeCompatibilityQuery(compatibility);
		for(User u: users){
			assertTrue(queryingUser.getCompatibilityRank(u) >= requiredRank);
		}
		assertFalse(users.contains(queryingUser));
		
	}
	
	/**
	 * Verifica che una query sulla compatibilit&agrave; al 100&#37;
	 * restituisca una collezione di User che non contenga l'esecutore della
	 * query stesso e che contenga solo utenti con compatibilit&agrave;
	 * maggiore o uguale a quella richiesta.
	 * @throws Exception se si verificano errori durante l'esecuzione della query
	 */
	public void testCompatibilityOneHundredPercent() throws Exception{
		float requiredRank = 1f;
		Compatibility compatibility = new Compatibility(queryingUser.getId(), requiredRank);
		Collection<User> users = executeCompatibilityQuery(compatibility);
		for(User u: users){
			assertTrue(queryingUser.getCompatibilityRank(u) >= requiredRank);
		}
		assertFalse(users.contains(queryingUser));
		
	}
	
}
