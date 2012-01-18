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
package it.unisannio.server.test.entities;

import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Preferences;
import it.unisannio.aroundme.server.InterestImpl;
import it.unisannio.aroundme.server.ServerModelFactory;
import it.unisannio.aroundme.server.UserImpl;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

import junit.framework.TestCase;

/**
 * {@link TestCase} che verifica che un oggetto di tipo {@link UserImpl} sia regolarmente
 * reso persistente sul Datastore
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class UserImplTest extends TestCase{
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private static Objectify ofy = ObjectifyService.begin();
	private UserImpl user;
	
	@Override
	public void setUp() {
		helper.setUp();

		ModelFactory.setInstance(new ServerModelFactory());
		
		try{
			ObjectifyService.register(UserImpl.class);
			ObjectifyService.register(InterestImpl.class);
		}catch(IllegalArgumentException e){}
		
		//L'User da persistere nel datastore
		user = (UserImpl) ModelFactory.getInstance().createUser(123, "Danilo", null);
		user.setPosition(ModelFactory.getInstance().createPosition(41.1315992, 14.7779900));
		Interest intTheWho = ModelFactory.getInstance().createInterest(5, "The Who", "Musica");
		Interest intAndroid = ModelFactory.getInstance().createInterest(6, "Android", "Tecnologia");
		user.addInterest(intTheWho);
		user.addInterest(intAndroid);
		String facebookAuthToken = "123456";
		user.setAuthToken(facebookAuthToken);
		Preferences pr = ModelFactory.getInstance().createPreferences();
		pr.put("Prova", "pippo");
		user.setPreferences(pr);
	}
	
	@Override
	public void tearDown(){
		helper.tearDown();
	}
	
	/**
	 * Testa che un {@link UserImpl} reso persistente sul datastore, conservi tutti i suoi attributi
	 */
	public void testUserPersistence(){
				
		ofy.put(user); //Salva un User sul datastore
		
		UserImpl retrievedUser = ofy.get(UserImpl.class, user.getId()); //Recupera l'user dal Datastore
		
		assertEquals(user, retrievedUser);
		assertEquals(user.getPosition(), retrievedUser.getPosition());
		assertEquals(user.getInterests(), retrievedUser.getInterests());
		assertEquals(user.getAuthToken(), retrievedUser.getAuthToken());
		assertEquals(user.getPreferences(), retrievedUser.getPreferences());
		
	
	}
	
	/**
	 * Testa che un la cancellazione di un {@link UserImpl} dal datastore vada a buon fine.
	 * Verifica che sia stata lanciata l'eccezione {@link NotFoundException} se si tenta di accedere
	 * ad un {@link UserImpl} precedentemente eliminato
	 */
	public void testUserDelete(){
		ofy.put(user); //Salva un User sul datastore
	
		ofy.delete(user); //Cancella l'User dal datastore
		
		Exception notFoundException = null;
		try{ 
			ofy.get(UserImpl.class, user.getId());
		}catch (NotFoundException e) {
			notFoundException = e;
		}
		assertNotNull(notFoundException);
	}
	
	
}
