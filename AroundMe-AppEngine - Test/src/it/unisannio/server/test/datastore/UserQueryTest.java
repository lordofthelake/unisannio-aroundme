package it.unisannio.server.test.datastore;

import it.unisannio.aroundme.model.Compatibility;
import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Neighbourhood;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;
import it.unisannio.aroundme.server.InterestImpl;
import it.unisannio.aroundme.server.ServerModelFactory;
import it.unisannio.aroundme.server.UserImpl;

import java.util.Collection;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

/**
 * 
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
public class UserQueryTest extends TestCase{
	//Helper che permette di testare il Datastore in locale
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private static Objectify ofy = ObjectifyService.begin();

	public static Test suite() {
		return new TestSetup(new TestSuite(UserQueryTest.class)) {
			protected void setUp() throws Exception {
				//Le istruzioni qui contenute verrano eseguite una volta sola
				ModelFactory.setInstance(new ServerModelFactory());
				ObjectifyService.register(UserImpl.class);
				ObjectifyService.register(InterestImpl.class);
			}
		};
	}


	public void setUp() {
		helper.setUp();
	}

	public void tearDown(){
		helper.tearDown();
	}
	
	/**
	 * Testa la query che restituisce gli utenti con un certo id
	 * @throws Exception nel caso di errori durante l'esecuzione della query
	 */
	public void testUserIdsQuery() throws Exception{
		User user1 = ModelFactory.getInstance().createUser(123, "Danilo", null);
		User user2 = ModelFactory.getInstance().createUser(124, "Michele", null);
		User user3 = ModelFactory.getInstance().createUser(125, "Marco", null);
		User user4 = ModelFactory.getInstance().createUser(126, "Giuseppe", null);
		
		ofy.put(user1, user2, user3, user4);
		
		UserQuery query = ModelFactory.getInstance().createUserQuery();
		query.addId(user1.getId(), user3.getId());
		
		Collection<User> users = query.call();
		assertTrue(users.contains(user1) && users.contains(user3));
		assertFalse(users.contains(user2));
		assertFalse(users.contains(user4));
		
	}
	
	
	/**
	 * Testa la query che restituisce gli utenti con degli interessi dati
	 * @throws Exception nel caso di errori durante l'esecuzione della query
	 */
	public void testIterestIdsQuery() throws Exception{
		
		User user1 = ModelFactory.getInstance().createUser(123, "Danilo", null);
		User user2 = ModelFactory.getInstance().createUser(124, "Michele", null);
		User user3 = ModelFactory.getInstance().createUser(125, "Marco", null);
		User user4 = ModelFactory.getInstance().createUser(126, "Giuseppe", null);
		
		Interest intFootball = ModelFactory.getInstance().createInterest(1, "Calcio", "Sport");
		Interest intBasket = ModelFactory.getInstance().createInterest(2, "Basket", "Sport");
		Interest intReading = ModelFactory.getInstance().createInterest(3, "Lettura", "Hobbies");
		Interest intLedZep = ModelFactory.getInstance().createInterest(4, "Led Zeppelin", "Musica");
		Interest intTheWho = ModelFactory.getInstance().createInterest(5, "The Who", "Musica");
		Interest intAndroid = ModelFactory.getInstance().createInterest(6, "Android", "Tecnologia");
		
		((UserImpl) user1).addInterest(intAndroid);
		((UserImpl) user1).addInterest(intTheWho);
		((UserImpl) user2).addInterest(intAndroid);
		((UserImpl) user2).addInterest(intReading);
		
		((UserImpl) user3).addInterest(intAndroid);
		((UserImpl) user3).addInterest(intReading);
		((UserImpl) user3).addInterest(intLedZep);
		
		((UserImpl) user4).addInterest(intBasket);
		((UserImpl) user4).addInterest(intFootball);
		((UserImpl) user4).addInterest(intReading);
	
		ofy.put(user1, user2, user3, user4);
		
		//Creazione ed esecuzione di una query con un solo interesse
		UserQuery query1 = ModelFactory.getInstance().createUserQuery();
		query1.addInterestId(intAndroid.getId());
		Collection<User> users1 = query1.call();
		
		assertTrue(users1.contains(user1) && users1.contains(user2) && users1.contains(user3));
		assertFalse(users1.contains(user4));
		
		//Creazione ed esecuzione di una query con due interessi
		UserQuery query2 = ModelFactory.getInstance().createUserQuery();
		query2.addInterestId(intAndroid.getId(), intReading.getId());
		Collection<User> users2 = query2.call();
		
		assertTrue(users2.contains(user2) && users2.contains(user3));
		assertFalse(users2.contains(user1));
		assertFalse(users2.contains(user4));
	}
	
	/**
	 * Testa la query che restituisce gli utenti con un certo grado di compatibilit&agrave;
	 * @throws Exception nel caso di errori durante l'esecuzione della query
	 */
	public void testCompatibilityQuery() throws Exception{

		User user1 = ModelFactory.getInstance().createUser(123, "Danilo", null);
		User user2 = ModelFactory.getInstance().createUser(124, "Michele", null);
		User user3 = ModelFactory.getInstance().createUser(125, "Marco", null);
		User user4 = ModelFactory.getInstance().createUser(126, "Giuseppe", null);
		User user5 = ModelFactory.getInstance().createUser(127, "Jessica", null);
		User user6 = ModelFactory.getInstance().createUser(128, "Samantha", null);

		Interest intFootball = ModelFactory.getInstance().createInterest(1, "Calcio", "Sport");
		Interest intBasket = ModelFactory.getInstance().createInterest(2, "Basket", "Sport");
		Interest intReading = ModelFactory.getInstance().createInterest(3, "Lettura", "Hobbies");
		Interest intLedZep = ModelFactory.getInstance().createInterest(4, "Led Zeppelin", "Musica");
		Interest intTheWho = ModelFactory.getInstance().createInterest(5, "The Who", "Musica");
		Interest intAndroid = ModelFactory.getInstance().createInterest(6, "Android", "Tecnologia");
		Interest intCooking = ModelFactory.getInstance().createInterest(7, "Cucina", "Hobbies");

		
		((UserImpl) user1).addInterest(intAndroid);
		((UserImpl) user1).addInterest(intTheWho);
		((UserImpl) user1).addInterest(intReading);
		((UserImpl) user1).addInterest(intFootball);
		
		((UserImpl) user2).addInterest(intAndroid);
		((UserImpl) user2).addInterest(intReading);
		((UserImpl) user2).addInterest(intLedZep);
		((UserImpl) user2).addInterest(intTheWho);
		
		((UserImpl) user3).addInterest(intFootball);
		((UserImpl) user3).addInterest(intReading);
		((UserImpl) user3).addInterest(intLedZep);
		((UserImpl) user3).addInterest(intAndroid);
		((UserImpl) user3).addInterest(intCooking);
		
		((UserImpl) user4).addInterest(intBasket);
		((UserImpl) user4).addInterest(intReading);
		((UserImpl) user4).addInterest(intAndroid);
		((UserImpl) user4).addInterest(intLedZep);
		
		((UserImpl) user5).addInterest(intAndroid);
		((UserImpl) user5).addInterest(intTheWho);
		((UserImpl) user5).addInterest(intReading);
		((UserImpl) user5).addInterest(intFootball);
		((UserImpl) user5).addInterest(intCooking);
		
		((UserImpl) user6).addInterest(intAndroid);
		((UserImpl) user6).addInterest(intTheWho);
		((UserImpl) user6).addInterest(intReading);
		((UserImpl) user6).addInterest(intFootball);

		ofy.put(user1, user2, user3, user4, user5, user6);

		//Creazione ed esecuzione di una query che richiede un grado di compatibilità del 60%
		UserQuery query1 = ModelFactory.getInstance().createUserQuery();
		query1.setCompatibility(new Compatibility(user1.getId(), 0.6f));
		Collection<User> users1 = query1.call();
		assertTrue(users1.contains(user2) && users1.contains(user3) && users1.contains(user5) && users1.contains(user6));
		assertFalse(users1.contains(user1));
		assertFalse(users1.contains(user4));
				
		//Creazione ed esecuzione di una query che richiede un grado di compatibilità del 50%
		UserQuery query2 = ModelFactory.getInstance().createUserQuery();
		query2.setCompatibility(new Compatibility(user1.getId(), 0.5f));
		Collection<User> users2 = query2.call();
		assertTrue(users2.contains(user2) && users2.contains(user3) && users2.contains(user4) 
				&& users2.contains(user5) && users1.contains(user6));
		assertFalse(users1.contains(user1));
		
		//Creazione ed esecuzione di una query che richiede un grado di compatibilità del 70%
		UserQuery query3 = ModelFactory.getInstance().createUserQuery();
		query3.setCompatibility(new Compatibility(user1.getId(), 0.7f));
		Collection<User> users3 = query3.call();
		assertTrue(users3.contains(user2) && users3.contains(user5) && users3.contains(user6));
		assertFalse(users3.contains(user1));
		assertFalse(users3.contains(user3));
		assertFalse(users3.contains(user4));
		
		//Creazione ed esecuzione di una query che richiede un grado di compatibilità del 80%
		UserQuery query4 = ModelFactory.getInstance().createUserQuery();
		query4.setCompatibility(new Compatibility(user1.getId(), 0.8f));
		Collection<User> users4 = query4.call();
		assertTrue(users4.contains(user5) && users4.contains(user6));
		assertFalse(users4.contains(user1));
		assertFalse(users4.contains(user2));
		assertFalse(users4.contains(user3));
		assertFalse(users4.contains(user4));
		
		//Creazione ed esecuzione di una query che richiede un grado di compatibilità del 100%
		UserQuery query5 = ModelFactory.getInstance().createUserQuery();
		query5.setCompatibility(new Compatibility(user1.getId(), 1));
		Collection<User> users5 = query5.call();
		assertTrue(users5.contains(user6));
		assertFalse(users5.contains(user1));
		assertFalse(users5.contains(user2));
		assertFalse(users5.contains(user3));
		assertFalse(users5.contains(user4));
		assertFalse(users5.contains(user5));
	}
	

	public void testPositionQuery() throws Exception {
		User user1 = ModelFactory.getInstance().createUser(123, "Danilo", null);
		User user2 = ModelFactory.getInstance().createUser(124, "Michele", null);
		User user3 = ModelFactory.getInstance().createUser(125, "Marco", null);
		User user4 = ModelFactory.getInstance().createUser(126, "Giuseppe", null);

		Position cGaribaldi = ModelFactory.getInstance().createPosition(41.1312275, 14.7778049); // Corso Galibardi altezza piazza Roma
		Position rcost = ModelFactory.getInstance().createPosition(41.1315992, 14.7779900); // RCOST
		Position sSofia = ModelFactory.getInstance().createPosition(41.1304275, 14.7809672);// S. Sofia
		Position sea = ModelFactory.getInstance().createPosition(41.12787, 14.78165); // SEA
		user1.setPosition(sSofia); 
		user2.setPosition(rcost);
		user3.setPosition(cGaribaldi); 
		user4.setPosition(sea);  
		ofy.put(user1, user2, user3, user4);

		Position giannone = ModelFactory.getInstance().createPosition(41.1309285, 14.7775555); //Giannone
		Neighbourhood neighbourhood = new Neighbourhood(giannone, 100);
		UserQuery query = ModelFactory.getInstance().createUserQuery();
		query.setNeighbourhood(neighbourhood);

		Collection<User> users = query.call();
		assertTrue(users.contains(user2) && users.contains(user3));
		assertFalse(users.contains(user1));
		assertFalse(users.contains(user4));
	}

	public void testPositionQueryRightOnBorders() throws Exception{
		positionQueryOnBorders(0);
	}

	public void testPositionQueryJustOutsideBorder() throws Exception{
		positionQueryOnBorders(-1);
	}

	public void positionQueryOnBorders(int radiusOffset) throws Exception {
		Position rcost = ModelFactory.getInstance().createPosition(41.1315992, 14.7779900);
		Position sSofia = ModelFactory.getInstance().createPosition(41.1304275, 14.7809672);
		double radius = rcost.getDistance(sSofia);
		User user1 = ModelFactory.getInstance().createUser(123, "Danilo", null);
		User user2 = ModelFactory.getInstance().createUser(124, "Michele", null);
		user1.setPosition(sSofia);
		user2.setPosition(rcost);
		ofy.put(user1, user2);
		Neighbourhood neighbourhood = new Neighbourhood(rcost, radius + radiusOffset);
		UserQuery query = ModelFactory.getInstance().createUserQuery();
		query.setNeighbourhood(neighbourhood);
		Collection<User> users = query.call();
		if(radiusOffset < 0){
			assertTrue(users.contains(user2));
			assertFalse(users.contains(user1));
		}
		else
			assertTrue(users.contains(user1) && users.contains(user2));
	}
	
	

}