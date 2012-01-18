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
package it.unisannio.aroundme.model.test.helpers;


import java.util.Collection;
import java.util.HashSet;

import static junit.framework.TestCase.*;

import it.unisannio.aroundme.model.Interest;
import it.unisannio.aroundme.model.ModelFactory;
import it.unisannio.aroundme.model.Position;
import it.unisannio.aroundme.model.Preferences;
import it.unisannio.aroundme.model.User;
import it.unisannio.aroundme.model.UserQuery;


/** 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public abstract class ModelFactoryTestHelper {
	
	public static void testCreateUser(ModelFactory factory) {
		long id = Math.round(Math.random()*10000);
		String name = String.valueOf(Math.random()*1000000);
		Collection<Interest> interests = new HashSet<Interest>();
		
		int num = Math.round(Math.round(Math.random()*20));
		while(num--> 0) {
			long iid = Math.round(Math.random() * 10000);
			interests.add(factory.createInterest(iid, "Interest #" + iid, "Category #" + iid));
		}
		
		User user = factory.createUser(id, name, interests);
		
		assertNotNull(user);
		assertEquals(user.getId(), id);
		assertEquals(user.getName(), name);
		assertEquals(user.getInterests().size(), interests.size());
		assertNull(user.getPosition());
		assertTrue(user.getInterests().containsAll(interests));
	}

	public static void testCreateInterest(ModelFactory factory) {
		long id = Math.round(Math.random()*10000);
		String name = String.valueOf(Math.random()*1000000);
		String category = String.valueOf(Math.random()*1000000);
		
		Interest i = factory.createInterest(id, name, category);
		
		assertNotNull(i);
		assertEquals(i.getId(), id);
		assertEquals(i.getName(), name);
		assertEquals(i.getCategory(), category);
	}

	public static void testCreatePosition(ModelFactory factory) {
		double latitude = Math.random() * 10;
		double longitude = Math.random() * 10;
		
		Position p = factory.createPosition(latitude, longitude);
		
		assertNotNull(p);
		
		assertEquals(p.getLatitude(), latitude, 0);
		assertEquals(p.getLongitude(), longitude, 0);
	}

	public static void testCreateUserQuery(ModelFactory factory) {
		UserQuery query = factory.createUserQuery();
		
		assertNotNull(query);
		assertNull(query.getCompatibility());
		assertNull(query.getNeighbourhood());
		assertTrue(query.getInterestIds().isEmpty());
		assertTrue(query.getIds().isEmpty());
	}
	
	public static void testCreatePreferences(ModelFactory factory) {
		Preferences preferences = factory.createPreferences();
		
		assertNotNull(preferences);
		assertTrue(preferences.getAll().isEmpty());
	}

}
