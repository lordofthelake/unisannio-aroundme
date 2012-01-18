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

import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.server.ServerModelFactory;
import it.unisannio.aroundme.server.c2dm.C2DMConfig;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

import junit.framework.TestCase;

/**
 * {@link TestCase} che verifica che un oggetto di tipo {@link C2DMConfig} sia regolarmente
 * reso persistente sul Datastore
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 */
public class C2DMConfigTest extends TestCase{
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private static Objectify ofy = ObjectifyService.begin();
	private C2DMConfig c2dmConfig;
	
	@Override
	public void setUp() {
		helper.setUp();

		ModelFactory.setInstance(new ServerModelFactory());
		
		try{
			ObjectifyService.register(C2DMConfig.class);
		}catch(IllegalArgumentException e){}
		
		//Il C2DMConfig da persistere nel datastore
		c2dmConfig = new C2DMConfig();
		c2dmConfig.setAuthKey("anAuthKey");
		
	}
	
	@Override
	public void tearDown(){
		helper.tearDown();
	}
	
	/**
	 * Testa che un {@link C2DMConfigImpl} reso persistente sul datastore, conservi tutti i suoi attributi
	 */
	public void testC2DMConfigPersistence(){
				
		ofy.put(c2dmConfig); //Salva un C2DMConfig sul datastore
		
		C2DMConfig retrievedC2DMConfig = ofy.get(C2DMConfig.class, c2dmConfig.getKey()); //Recupera il C2DMConfig dal Datastore
		
		assertEquals(c2dmConfig, retrievedC2DMConfig);
		assertEquals(c2dmConfig.getAuthKey(), retrievedC2DMConfig.getAuthKey());
	}
	
	/**
	 * Testa che un la cancellazione di un {@link C2DMConfig} dal datastore vada a buon fine.
	 * Verifica che sia stata lanciata l'eccezione {@link NotFoundException} se si tenta di accedere
	 * ad un {@link C2DMConfig} precedentemente eliminato
	 */
	public void testC2DMConfigDelete(){
		ofy.put(c2dmConfig); //Salva un C2DMConfig sul datastore
	
		ofy.delete(c2dmConfig); //Cancella il C2DMConfig dal datastore
		
		Exception notFoundException = null;
		try{ 
			ofy.get(C2DMConfig.class, c2dmConfig.getKey());
		}catch (NotFoundException e) {
			notFoundException = e;
		}
		assertNotNull(notFoundException);
	}
}