/* AroundMe - Social Network mobile basato sulla geolocalizzazione
 * Copyright (C) 2012 AroundMe Working Group
 *   
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unisannio.server.test.userqueryimpl;

import java.util.Collection;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;

import junit.framework.TestCase;

import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;
import it.unisannio.aroundme.server.InterestImpl;
import it.unisannio.aroundme.server.ServerModelFactory;
import it.unisannio.aroundme.server.UserImpl;
import it.unisannio.aroundme.server.UserQueryImpl;

/**
 * Verifica che {@link UserQueryImpl} impostato per eseguire una query sugli interessi,
 * restituisca risultati concordi ai dati presenti sul Datastore
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class InterestQueryTest extends TestCase{
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private User user1, user2, user3, user4;
	private Interest requiredInterest1, requiredInterest2, unusedInterest;
	
	
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
		
		Interest intFootball = ModelFactory.getInstance().createInterest(1, "Calcio", "Sport");
		Interest intBasket = ModelFactory.getInstance().createInterest(2, "Basket", "Sport");
		requiredInterest2 = ModelFactory.getInstance().createInterest(3, "Lettura", "Hobbies");
		Interest intLedZep = ModelFactory.getInstance().createInterest(4, "Led Zeppelin", "Musica");
		Interest intTheWho = ModelFactory.getInstance().createInterest(5, "The Who", "Musica");
		requiredInterest1 = ModelFactory.getInstance().createInterest(6, "Android", "Tecnologia");
		unusedInterest = ModelFactory.getInstance().createInterest(7, "House", "Musica");
		
		((UserImpl) user1).addInterest(requiredInterest1);
		((UserImpl) user1).addInterest(intTheWho);
		((UserImpl) user2).addInterest(requiredInterest1);
		((UserImpl) user2).addInterest(requiredInterest2);
		
		((UserImpl) user3).addInterest(requiredInterest1);
		((UserImpl) user3).addInterest(requiredInterest2);
		((UserImpl) user3).addInterest(intLedZep);
		
		((UserImpl) user4).addInterest(intBasket);
		((UserImpl) user4).addInterest(intFootball);
		((UserImpl) user4).addInterest(requiredInterest2);
		
		ObjectifyService.begin().put(user1, user2, user3, user4);
	}
	
	public void tearDown(){
		helper.tearDown();
	}

	/**
	 * Testa che la query restituisca gli utenti che abbiano tutti gli interessi richiesti
	 * @throws Exception nel caso di errori durante l'esecuzione della query
	 */
	public void testIterestIdsQuery() throws Exception{
		UserQuery query = ModelFactory.getInstance().createUserQuery();
		query.addInterestId(requiredInterest1.getId(), requiredInterest2.getId());
		Collection<User> users = query.call();
		
		assertTrue(users.contains(user2) && users.contains(user3));
		assertFalse(users.contains(user1));
		assertFalse(users.contains(user4));
	}
	
	/**
	 * Testa che una query su in {@link InterestImpl} non associato a nessuno
	 * restutuisca una collezione di {@link User} vuota 
	 * @throws Exception nel caso di errori durante l'esecuzione della query
	 */
	public void testResultsQuery() throws Exception{
		UserQuery query1 = ModelFactory.getInstance().createUserQuery();
		query1.addInterestId(unusedInterest.getId());
		Collection<User> users = query1.call();
		assertTrue(users.isEmpty());
	}
	
}
