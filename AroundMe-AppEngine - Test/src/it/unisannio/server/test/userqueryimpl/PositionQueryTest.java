package it.unisannio.server.test.userqueryimpl;

import java.util.Collection;

import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Neighbourhood;
import it.unisannio.aroundme.model.Position;
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
 * Verifica che {@link UserQueryImpl} impostato per eseguire una query sulle Posizioni,
 * restituisca risultati concordi ai dati presenti sul Datastore
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class PositionQueryTest extends TestCase{
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private User user1, user2, user3, user4;
	Position cGaribaldi,rcost, sSofia, sea, palazzoGiannone, paris;
	
	
	@Override
	protected void setUp() throws Exception {
		
		helper.setUp();

		ModelFactory.setInstance(new ServerModelFactory());
		
		try{
			ObjectifyService.register(UserImpl.class);
			ObjectifyService.register(InterestImpl.class);
		}catch(IllegalArgumentException e){}
		
		user1 = ModelFactory.getInstance().createUser(123, "Danilo", null);
		user2 = ModelFactory.getInstance().createUser(124, "Michele", null);
		user3 = ModelFactory.getInstance().createUser(125, "Marco", null);
		user4 = ModelFactory.getInstance().createUser(126, "Giuseppe", null);

		cGaribaldi = ModelFactory.getInstance().createPosition(41.1312275, 14.7778049); // Corso Galibardi altezza piazza Roma
		rcost = ModelFactory.getInstance().createPosition(41.1315992, 14.7779900); // RCOST
		sSofia = ModelFactory.getInstance().createPosition(41.1304275, 14.7809672);// S. Sofia
		sea = ModelFactory.getInstance().createPosition(41.12787, 14.78165); // SEA
		palazzoGiannone = ModelFactory.getInstance().createPosition(41.1309285, 14.7775555); //Giannone
		paris = ModelFactory.getInstance().createPosition(48.85801, 2.29494); //Tour Eiffel
		
		user1.setPosition(sSofia); 
		user2.setPosition(rcost);
		user3.setPosition(cGaribaldi); 
		user4.setPosition(sea);  

		ObjectifyService.begin().put(user1, user2, user3, user4);
	}
	
	public void tearDown(){
		helper.tearDown();
	}
	
	
	/**
	 * Verifica che il risultato di una query sulla posizione, restituisca effettivamente
	 * User presenti all'interno del raggio fornito
	 * @throws Exception se si verificano errori durante l'esecuzione della query
	 */
	public void testPositionQuery() throws Exception {
		int radius = 100;
		Neighbourhood neighbourhood = new Neighbourhood(palazzoGiannone, radius);
		UserQuery query = ModelFactory.getInstance().createUserQuery();
		query.setNeighbourhood(neighbourhood);
		Collection<User> users = query.call();
		
		for(User u: users){
			assertTrue(u.getPosition().getDistance(palazzoGiannone) <= radius);
		}
	}
	
	/**
	 * Verifica che il risultato di una query su di una posizione remota,
	 * restituisca una collezione di User vuota
	 * @throws Exception se si verificano errori durante l'esecuzione della query
	 */
	public void testPositionQueryEmpty() throws Exception {
		int radius = 100;
		Neighbourhood neighbourhood = new Neighbourhood(paris, radius);
		UserQuery query = ModelFactory.getInstance().createUserQuery();
		query.setNeighbourhood(neighbourhood);
		Collection<User> users = query.call();
		assertTrue(users.isEmpty());
	}
	
	/**
	 * Testa la precisione di {@link UserQueryImpl} eseguendo
	 * una query tale da porre due utenti ai margini dell'area
	 * di ricerca verificando che siano entrambi presenti nei risultati 
	 * @throws Exception se si verificano errori durante l'esecuzione della query
	 */
	public void testPositionQueryRightOnBorders() throws Exception{
		Collection<User> users = positionQueryOnBorders(0);
		assertTrue(users.contains(user1) && users.contains(user2));
	}

	/**
	 * Testa la precisione di {@link UserQueryImpl} eseguendo
	 * una query tale da porre due utenti ai margini dell'area
	 * di ricerca verificando che, riducendo il raggio di appena un metro,
	 * uno dei due utenti non sia presente nei risultati 
	 * @throws Exception se si verificano errori durante l'esecuzione della query
	 */
	public void testPositionQueryJustOutsideBorder() throws Exception{
		Collection<User> users = positionQueryOnBorders(-1);
		
		assertTrue(users.contains(user1));
		assertFalse(users.contains(user2));
	}

	
	/**
	 * Metodo di convenienza che esegue una query su posizione tra due utenti
	 * ponendo come centro la posizione di uno degli utenti e come raggio
	 * la distanza che li separa.
	 * Al raggio verr&agrave inoltre aggiunto un l'offset passato come parametro
	 * @param radiusOffset L'offset da aggiungere/sottrarre al raggio
	 * @return Una collezione di {@link User} risultate dalla query
	 * @throws Exception se si verificano errori durante l'esecuzione della query
	 */
	public Collection<User> positionQueryOnBorders(int radiusOffset) throws Exception {
		int radius = rcost.getDistance(sSofia);
		user1.setPosition(sSofia);
		user2.setPosition(rcost);
		ObjectifyService.begin().put(user1, user2);
		Neighbourhood neighbourhood = new Neighbourhood(user1.getPosition(), radius + radiusOffset);
		UserQuery query = ModelFactory.getInstance().createUserQuery();
		query.setNeighbourhood(neighbourhood);
		return query.call();
	}
}
